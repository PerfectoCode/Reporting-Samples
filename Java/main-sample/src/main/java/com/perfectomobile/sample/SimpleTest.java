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

public class SimpleTest {

    // TODO put your Continuous Quality Lab name here
    private static final String LAB_NAME = "my-lab.perfectomobile.com";

    // TODO put your Continuous Quality username here
    private static final String USERNAME = "my-lab-username";

    // TODO put your Continuous Quality password here
    private static final String PASSWORD = "my-lab-password";

    private static final String SOURCE_FILE_ROOT_PATH = "Java/main-sample/src/main/java";

    public static void main(String[] args) throws IOException {
        WebDriver driver = getDriver();
        ReportiumClient reportiumClient = createReportingClient(driver);
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
            TestResult testResult = TestResultFactory.createSuccess();
            reportiumClient.testStop(testResult);

        } catch (Exception e) {
            TestResult testResult = TestResultFactory.createFailure("Test stop failure", e, "ApplicationNotFound-1542806871");  //Add here the failure reason name as appear in the failure reasons admin tab
            reportiumClient.testStop(testResult);
            e.printStackTrace();
        } finally {
            driver.close();
            driver.quit();
        }
    }

    private static ReportiumClient createReportingClient(WebDriver driver) {

        // Custom fields
        CustomField teamCustomField = new CustomField("team", "devOps");
        CustomField departmentCustomField = new CustomField("department", "engineering");
        CustomField[] customFields = VcsUtils.addVcsFields(SOURCE_FILE_ROOT_PATH, teamCustomField, departmentCustomField);

        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withJob(new Job("my-custom-job-name", 123).withBranch("my-branch"))
                .withProject(new Project("Sample Reportium project", "1.0"))
                .withContextTags("simpleSeleniumTests")
                .withCustomFields(customFields)
                .withWebDriver(driver)
                .build();
        System.out.println("end of reporting client setup");
        return new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }

    private static WebDriver getDriver() throws MalformedURLException {
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
        System.out.println("end of init driver");
        return remoteWebDriver;
    }
}
