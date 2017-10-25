package com.perfecto.reporting.sample;

import com.perfecto.reportium.WebDriverProvider;
import com.perfecto.reportium.testng.ReportiumTestNgListener;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;

@Listeners(ReportiumTestNgListener.class)
public class TodoMvcWithListenerTest implements WebDriverProvider {

    private WebDriver driver;
    private TodoMvcService todoMvcService;
    public static final String IS_LOCAL_DRIVER = "is-local-driver";
    private static final String SELENIUM_GRID_URL_KEY = "selenium-grid-url";
    private static final String SELENIUM_GRID_USERNAME_KEY = "selenium-grid-username";
    private static final String SELENIUM_GRID_PASSWORD_KEY = "selenium-grid-password";

    @SuppressWarnings("Duplicates")
    @BeforeClass
    public void setupDriver() throws MalformedURLException {

        // Define target mobile device
        String browserName = "mobileOS";
        DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
        String seleniumGridUsername = System.getProperty(SELENIUM_GRID_USERNAME_KEY, "MYUSER");
        String seleniumGridPassword = System.getProperty(SELENIUM_GRID_PASSWORD_KEY, "MYPASSWORD");
        capabilities.setCapability("user", seleniumGridUsername);
        capabilities.setCapability("password", seleniumGridPassword);
        // Define device allocation timeout, in minutes
        capabilities.setCapability("openDeviceTimeout", 5);

        // Name of script
        capabilities.setCapability("scriptName", this.getClass().getName());

        // Create Remote WebDriver
        Reporter.log("Allocating Mobile device per specified capabilities");
        if (!Boolean.parseBoolean(System.getProperty(IS_LOCAL_DRIVER))) {
            String seleniumGridUrl = System.getProperty(SELENIUM_GRID_URL_KEY, "https://MYCOMPANY.perfectomobile.com/nexperience/perfectomobile/wd/hub");
            driver = new RemoteWebDriver(new URL(seleniumGridUrl), capabilities);
        } else {
            driver = new FirefoxDriver();
        }
        todoMvcService = new TodoMvcService(driver);
    }

    @AfterClass
    public void quitDriver() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }

    @BeforeMethod
    public void navigateToApp() {
        String url = "http://todomvc.com/examples/vanillajs/";
        Reporter.log("Navigating to " + url);
        driver.get(url);
    }

    @Test(description = "Create a new todo")
    public void createTodo() {
        Reporter.log("Create new todo with");
        String namePrefix = "createTodo";
        String todoName = todoMvcService.createUniqueTodo(namePrefix);
        Reporter.log("Created new todo called " + todoName);
        Assert.assertTrue(todoName.startsWith(namePrefix), "Unique todo name is expected to start with the given prefix " + namePrefix);

        Reporter.log("Verify todo with name " + todoName + " was added to list");
        todoMvcService.verifyAddedTodo(todoName);
    }

    @Test(description = "Create a new todo, make it as complete and then delete it")
    public void completeTodo() {
        Reporter.log("Create new todo");
        String todoName = todoMvcService.createUniqueTodo("deleteTodo");

        Reporter.log("Complete todo called " + todoName);
        todoMvcService.completeTodo(todoName);

        Reporter.log("Verify todo called " + todoName + " is completed");
        todoMvcService.verifyCompletedTodo(todoName);

        Reporter.log("Remove created todo called " + todoName);
        todoMvcService.removeTodo();
    }

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }
}
