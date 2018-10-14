package com.perfecto.reporting.sample.api;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import selenium.PerfectoEventFiringWebDriver;

import java.net.URI;
import java.net.URL;

public class ImportTestSeleniumListener {
    public static final String SELENIUM_GRID_URL_KEY = "selenium-grid-url";
    public static final String IS_LOCAL_DRIVER = "is-local-driver";

    private static final String PERFECTO_SECURITY_TOKEN = "my-security-token"; //TODO put your security token here
    private static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    private static final String CQL_NAME = System.getProperty("CQL_NAME", "my-company-id"); // TODO put your Continuous Quality Lab name here
    private static final String REPORTIUM_URL = "https://" + CQL_NAME + ".reporting.perfectomobile.com"; // "https://[COMPANY_ID].reporting.perfectomobile.com";

    public static void main(String[] args) throws Exception {
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();
        Connection connection = new Connection(new URI(REPORTIUM_URL), SECURITY_TOKEN);
        ReportiumImportClient reportiumImportClient = new ReportiumImportClientFactory().createReportiumImportClient(connection, executionContext);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        // Define device allocation timeout, in minutes
        capabilities.setCapability("openDeviceTimeout", 5);

        // Name of script
        capabilities.setCapability("scriptName", ImportTestSeleniumListener.class.getSimpleName());

        capabilities.setCapability("deviceType", "Web");
        capabilities.setCapability("platformName", "Windows");
        capabilities.setCapability("platformVersion", "10");
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        capabilities.setCapability("resolution", "1920x1080");
//        capabilities.setCapability("outputVideo", false);
        capabilities.setCapability("location", "US East");
        capabilities.setCapability("securityToken", SECURITY_TOKEN);

        WebDriver originalDriver;
        if (!Boolean.parseBoolean(System.getProperty(IS_LOCAL_DRIVER))) {
            String seleniumGridUrl = System.getProperty(SELENIUM_GRID_URL_KEY, "https://" + CQL_NAME + ".perfectomobile.com/nexperience/perfectomobile/wd/hub");
            originalDriver = new RemoteWebDriver(new URL(seleniumGridUrl), capabilities);
        } else {
            originalDriver = new ChromeDriver();
        }

        // wrap the selenium driver in order to capture selenium events and send them as commands to perfecto reporting
        EventFiringWebDriver perfectoDriver = new PerfectoEventFiringWebDriver(originalDriver, reportiumImportClient);

        try {
            reportiumImportClient.testStart("Selenium listener test", new TestContext.Builder().build());

            reportiumImportClient.stepStart("Go to google.com");
            perfectoDriver.get("https://www.google.com/");
            perfectoDriver.getKeyboard().sendKeys("perfecto mobile");
            perfectoDriver.getScreenshotAs(OutputType.FILE);
            reportiumImportClient.stepEnd();

            reportiumImportClient.stepStart("Click on About");
            WebElement aboutLink = perfectoDriver.findElement(By.xpath("//a[text() = 'About']"));
            perfectoDriver.getScreenshotAs(OutputType.BYTES);
            Actions rightClickAction = new Actions(perfectoDriver).contextClick(aboutLink);
            rightClickAction.build().perform();
            aboutLink.click();
            reportiumImportClient.stepEnd();

            reportiumImportClient.stepStart("Take screenshot");
            perfectoDriver.getScreenshotAs(OutputType.BASE64);
            reportiumImportClient.stepEnd();

            reportiumImportClient.testStop(TestResultFactory.createSuccess());
        } catch (Exception e) {
            reportiumImportClient.testStop(TestResultFactory.createFailure(e, "Application not found"));  //Add here the failure reason name as appear in the failure reasons admin tab
        } finally {
            if (perfectoDriver != null) {
                perfectoDriver.close();
                perfectoDriver.quit();
            }
            reportiumImportClient.close();
        }
        System.out.println(reportiumImportClient.getReportUrl());
    }
}
