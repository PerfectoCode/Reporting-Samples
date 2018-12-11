package com.perfecto.reporting.sample.api;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.imports.model.platform.DeviceType;
import com.perfecto.reportium.imports.model.platform.MobileInfo;
import com.perfecto.reportium.imports.model.platform.Platform;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import java.net.URI;

public class ImportTestMobile {

    private static final String PERFECTO_SECURITY_TOKEN = "my-security-token"; //TODO put your security token here
    private static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    private static final String CQL_NAME = System.getProperty("CQL_NAME", "my-company-id"); // TODO put your Continuous Quality Lab name here
    private static final String REPORTIUM_URL = "https://" + CQL_NAME + ".reporting.perfectomobile.com"; // "https://[COMPANY_ID].reporting.perfectomobile.com";

    public static void main(String[] args) throws Exception {
        MobileInfo mobileInfo = new MobileInfo.Builder()
                .withManufacturer("Samsung")
                .withModel("Galaxy Note 8")
                .withOperator("Sprint")
                .build();

        Platform platform = new Platform.Builder()
                .withMobileInfo(mobileInfo)
                .withOs("Android")
                .withDeviceType(DeviceType.MOBILE)
                .withScreenResolution("1440x2960")
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

        reportiumClient.testStop(TestResultFactory.createFailure("it was a failure", null, "ApplicationNotFound-1542806871"));  //Add here the failure reason id as appear in the failure reasons admin tab

        reportiumClient.close();

        System.out.println(reportiumClient.getReportUrl());

    }
}