package com.perfecto.reporting.sample;

import com.perfecto.reportium.testng.ReportiumTestNgListener;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

/**
 * TestNG listener using custom credentials for connecting to Reportium
 */
public class CustomReportingTestListener extends ReportiumTestNgListener {

    @Override
    protected WebDriver getWebDriver(ITestResult testResult) {
        return null; // take WebDriver from somewhere
    }
}
