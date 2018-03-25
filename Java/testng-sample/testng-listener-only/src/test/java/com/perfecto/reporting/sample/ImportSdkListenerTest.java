package com.perfecto.reporting.sample;

import com.perfecto.reportium.client.ReportiumClientProvider;
import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.testng.ReportiumImportTestNgListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;
import selenium.PerfectoEventFiringWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Listeners(ReportiumImportTestNgListener.class)
public class ImportSdkListenerTest {

    public static final String IS_LOCAL_DRIVER = "is-local-driver";
    private static final String SELENIUM_GRID_URL_KEY = "selenium-grid-url";
    private static final String SELENIUM_GRID_USERNAME_KEY = "selenium-grid-username";
    private static final String SELENIUM_GRID_PASSWORD_KEY = "selenium-grid-password";

    private static final String PERFECTO_SECURITY_TOKEN = "my-security-token"; //TODO put your security token here
    private static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    private static final String CQL_NAME = System.getProperty("CQL_NAME", "my-company-id"); // TODO put your Continuous Quality Lab name here
    private static final String REPORTIUM_URL = "https://" + CQL_NAME + ".reporting.perfectomobile.com"; // "https://[COMPANY_ID].reporting.perfectomobile.com";

    private WebDriver driver;
    private TodoMvcService todoMvcService;

    @SuppressWarnings("Duplicates")
    @BeforeClass
    public void setupDriver() throws MalformedURLException, URISyntaxException {
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();
        Connection connection = new Connection(new URI(REPORTIUM_URL), SECURITY_TOKEN);
        ReportiumImportClient reportiumClient = new ReportiumImportClientFactory().createReportiumImportClient(connection, executionContext);
        ReportiumClientProvider.set(reportiumClient);

        // Define target mobile device
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String seleniumGridUsername = System.getProperty(SELENIUM_GRID_USERNAME_KEY, "ci@reportium.com");
        String seleniumGridPassword = System.getProperty(SELENIUM_GRID_PASSWORD_KEY, "reportium");
        capabilities.setCapability("user", seleniumGridUsername);
        capabilities.setCapability("password", seleniumGridPassword);
        // Define device allocation timeout, in minutes
        capabilities.setCapability("openDeviceTimeout", 5);

        // Name of script
        capabilities.setCapability("scriptName", this.getClass().getName());

        capabilities.setCapability("deviceType", "Web");
        capabilities.setCapability("platformName", "Windows");
        capabilities.setCapability("platformVersion", "10");
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        capabilities.setCapability("resolution", "1920x1080");
//        capabilities.setCapability("outputVideo", false);
        capabilities.setCapability("location", "US East");


        // Create Remote WebDriver
        Reporter.log("Allocating Mobile device per specified capabilities");
        if (!Boolean.parseBoolean(System.getProperty(IS_LOCAL_DRIVER))) {
            String seleniumGridUrl = System.getProperty(SELENIUM_GRID_URL_KEY, "https://demo.perfectomobile.com/nexperience/perfectomobile/wd/hub");
            driver = new RemoteWebDriver(new URL(seleniumGridUrl), capabilities);
        } else {
            driver = new FirefoxDriver();
        }

        driver = new PerfectoEventFiringWebDriver(driver, reportiumClient);
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
}
