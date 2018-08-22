package com.perfecto.reporting.sample;

import com.perfecto.reportium.testng.ReportiumTestNgListener;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

/*
 * You can extend the listener in order to provide the driver from a custom location
 */

public class CustomReportingTestListener extends ReportiumTestNgListener {

    @Override
    protected WebDriver getWebDriver(ITestResult testResult) {
        return null; // take WebDriver from somewhere
    }
}
