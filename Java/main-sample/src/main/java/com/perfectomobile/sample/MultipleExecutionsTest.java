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
import java.util.Objects;

public class MultipleExecutionsTest {

    // TODO put your Continuous Quality Lab name here
    private static final String LAB_NAME = "my-lab.perfectomobile.com";

    // TODO put your Continuous Quality username here
    private static final String USERNAME = "my-lab-username";

    // TODO put your Continuous Quality password here
    private static final String PASSWORD = "my-lab-password";


    private static final String SOURCE_FILE_ROOT_PATH = "Java/main-sample/src/main/java";

    public static void main(String[] args) throws IOException {
        // Create the drivers
        WebDriver webDriver1 = acquireDriver();
        WebDriver webDriver2 = acquireDriver();
        WebDriver webDriver3 = acquireDriver();

        // Create the reporting client
        ReportiumClient reportiumClient = createReportingClient(webDriver1, "D1", webDriver2, "D2", webDriver3, "D3");

        try {
            // Test start
            reportiumClient.testStart("MultipleExecutionsTest", new TestContext.Builder()
                    .withTestExecutionTags("google")
                    .withCustomFields(new CustomField("developer", "John"))
                    .build());

            // Step 1
            reportiumClient.stepStart("Browser navigation - 1");
            webDriver1.get("http://www.google.com");
            reportiumClient.stepEnd();

            // Step 2
            reportiumClient.stepStart("Browser navigation - 2");
            webDriver2.get("http://www.ebay.com");
            reportiumClient.stepEnd();

            // Step 3
            reportiumClient.stepStart("Browser navigation - 3");
            webDriver3.get("http://www.cnn.com");
            reportiumClient.stepEnd();



            // TODO write your test code here



            // Test stop
            TestResult testResult = TestResultFactory.createSuccess();
            reportiumClient.testStop(testResult);
        } catch (Exception e) {
            // Error handling
            TestResult testResult = TestResultFactory.createFailure("Test stop failure", e, "ApplicationNotFound-1542806871");  // Add here the failure reason name as appear in the failure reasons admin tab
            reportiumClient.testStop(testResult);
            e.printStackTrace();
        } finally {
            // Close the drivers
            closeDriver(webDriver1);
            closeDriver(webDriver2);
            closeDriver(webDriver3);
        }
        System.out.println("Test completed");
    }

    private static void closeDriver(WebDriver webDriver) {
        // Close the driver
        webDriver.close();
        webDriver.quit();
    }

    private static ReportiumClient createReportingClient(WebDriver webDriver1, String alias1,
                                                         WebDriver webDriver2, String alias2,
                                                         WebDriver webDriver3, String alias3) {
        // Custom fields
        CustomField teamCustomField = new CustomField("team", "devOps");
        CustomField departmentCustomField = new CustomField("department", "engineering");
        CustomField[] customFields = VcsUtils.addVcsFields(SOURCE_FILE_ROOT_PATH, teamCustomField, departmentCustomField);

        // Create the execution context
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withJob(new Job("my-custom-job-name", 123).withBranch("my-branch"))
                .withProject(new Project("Sample Reportium project", "1.0"))
                .withContextTags("simpleSeleniumTests")
                .withCustomFields(customFields)
                .withWebDriver(webDriver1, alias1)
                .withWebDriver(webDriver2, alias2)
                .withWebDriver(webDriver3, alias3)
                .build();

        // Create and return the reporting client
        System.out.println("End of reporting client setup");
        return new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }

    private static WebDriver acquireDriver() throws MalformedURLException {
        String host = System.getProperty("host", LAB_NAME);
        if (Objects.equals("my-lab.perfectomobile.com", host)) {
            throw new RuntimeException("Please set the lab name");
        }

        String userName = System.getProperty("selenium-grid-username", USERNAME);
        if (Objects.equals("my-lab-username", userName)) {
            throw new RuntimeException("Please set the username");
        }

        String password = System.getProperty("selenium-grid-password", PASSWORD);
        if (Objects.equals("my-lab-password", password)) {
            throw new RuntimeException("Please set the password");
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("user", userName);
        capabilities.setCapability("password", password);

        // Create the driver
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
        System.out.println("End of drivers init");
        return remoteWebDriver;
    }
}
