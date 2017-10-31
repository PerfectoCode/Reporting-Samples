package com.perfecto.reporting.sample.api;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.imports.model.platform.BrowserInfo;
import com.perfecto.reportium.imports.model.platform.BrowserType;
import com.perfecto.reportium.imports.model.platform.DeviceType;
import com.perfecto.reportium.imports.model.platform.Platform;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import java.net.URI;

public class ImportTestWeb {

    private static final String MY_COMPANY_ID = "company-id";
    private static final String PERFECTO_SECUIRTY_TOKEN_KEY = "security-token";
    private static final String REPORTIUM_URL = "https://" + System.getProperty(MY_COMPANY_ID) + ".reporting.perfectomobile.com"; // "https://[MY_COMPANY_ID].reporting.perfectomobile.com";
    private static final String SECURITY_TOKEN = System.getProperty(PERFECTO_SECUIRTY_TOKEN_KEY);


    public static void main(String[] args) throws Exception {
        System.out.println(REPORTIUM_URL);
        BrowserInfo browserInfo = new BrowserInfo.Builder()
                .withBrowserType(BrowserType.CHROME)
                .withBrowserVersion("60")
                .build();

        Platform platform = new Platform.Builder()
                .withBrowserInfo(browserInfo)
                .withOs("Windows")
                .withOsVersion("10")
                .withDeviceType(DeviceType.DESKTOP)
                .withScreenResolution("1366x768")
                .build();

        ImportExecutionContext executionContext = new ImportExecutionContext.Builder()
                .withJob(new Job("my job name", 123))
                .withProject(new Project("my project name", "ver 123"))
                .withContextTags("tag1", "tag2", "tag3")
                .withPlatforms(platform)
                .build();

        Connection connection = new Connection(new URI(REPORTIUM_URL), SECURITY_TOKEN);
        ReportiumImportClient reportiumClient = new ReportiumImportClientFactory().createReportiumImportClient(connection, executionContext);

        reportiumClient.testStart("my test name", new TestContext("tag4", "tag5"));

        reportiumClient.stepStart("my step");

        reportiumClient.stepEnd();

        reportiumClient.testStop(TestResultFactory.createFailure("it was a failure"));

        System.out.println(reportiumClient.getReportUrl());
    }
}
