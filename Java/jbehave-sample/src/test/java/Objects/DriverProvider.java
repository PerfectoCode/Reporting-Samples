package Objects;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverProvider implements WebDriverProvider {

    private WebDriver driver;
    final String HOST_KEY = "host";
    final String SELENIUM_GRID_USERNAME_KEY = "selenium-grid-username";
    String SELENIUM_GRID_PASSWORD_KEY = "selenium-grid-password";
    String myHost = System.getProperty(HOST_KEY);
    String myPerfectoUser = System.getProperty(SELENIUM_GRID_USERNAME_KEY);
    String myPerfectoPassword = System.getProperty(SELENIUM_GRID_PASSWORD_KEY);
    private static final String PERFECTO_SECURITY_TOKEN = "my-security-token"; //TODO put your security token here
    private static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    @Override
    public WebDriver get() {
        return driver;
    }

    @Override
    public void initialize() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("model", "Galaxy S9");
        capabilities.setCapability("securityToken", SECURITY_TOKEN);

        //Other capabilities ...

        try {
            driver = new RemoteWebDriver(new URL("http://" + myHost + "/nexperience/perfectomobile/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean saveScreenshotTo(String s) {
        return false;
    }

    @Override
    public void end() {
        driver.close();
        driver.quit();
    }
}
