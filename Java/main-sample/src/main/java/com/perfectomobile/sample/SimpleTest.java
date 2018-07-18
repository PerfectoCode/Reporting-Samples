package com.perfectomobile.sample;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.CustomField;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yuvals on 7/18/2018.
 */
public class SimpleTest {


    public static void main(String[] args) throws IOException {
        //boolean test passed = true; // assume true until failure
        TestResult testResult = TestResultFactory.createFailure("Test stop failure");// assume failure until proven passed
        WebDriver driver = getDriver();
        ReportiumClient reportiumClient = setReportingClient(driver);
        try {
            reportiumClient.testStart("simpleTest", new TestContext.Builder()
                    .withTestExecutionTags("google")
                    .withCustomFields(new CustomField("developer", "John"))
                    .build());
            reportiumClient.stepStart("browser navigate to google");
            driver.get("http://www.google.com");
            reportiumClient.stepEnd();
            reportiumClient.stepStart("browser navigate to ebay");
            driver.get("http://www.ebay.com");
            reportiumClient.stepEnd();

            //STOP TEST
            testResult = TestResultFactory.createSuccess();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reportiumClient.testStop(testResult);
            driver.close();
            driver.quit();
        }


    }

    private static ReportiumClient setReportingClient(WebDriver driver) {
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withJob(new Job("my-custom-job-name", 123).withBranch("my-branch"))
                .withProject(new Project("Sample Reportium project", "1.0"))
                .withContextTags("AndroidSeleniumTests")
                .withCustomFields(new CustomField("team", "devOps"))
                .withWebDriver(driver)
                .build();
        System.out.println("end of reporting client setup");
        return new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }

    private static WebDriver getDriver() throws MalformedURLException {
        final String HOST = "host";
        final String SELENIUM_GRID_USERNAME_KEY = "selenium-grid-username";
        String SELENIUM_GRID_PASSWORD_KEY = "selenium-grid-password";
        String seleniumGridUsername = System.getProperty(SELENIUM_GRID_USERNAME_KEY);
        String seleniumGridPassword = System.getProperty(SELENIUM_GRID_PASSWORD_KEY);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("user", seleniumGridUsername);
        capabilities.setCapability("password", seleniumGridPassword);

        //Other capabilities ...
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(new URL("http://" + System.getProperty(HOST) + "/nexperience/perfectomobile/wd/hub"), capabilities);
        System.out.println("end of init driver");
        return remoteWebDriver;
    }

}
