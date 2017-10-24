package com.perfecto.reporting.sample.api;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import java.net.URI;

public class ImportTestSimple {

    private static final String REPORTIUM_URL = "https://[MY_COMPANY_ID].reporting.perfectomobile.com";
    private static final String SECURITY_TOKEN = "[MY_SECURITY_TOKEN]";

    public static void main(String[] args) throws Exception {
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();
        Connection connection = new Connection(new URI(REPORTIUM_URL), SECURITY_TOKEN);
        ReportiumImportClient reportiumClient = new ReportiumImportClientFactory().createReportiumImportClient(connection, executionContext);

        reportiumClient.testStart("my test name", new TestContext());

        reportiumClient.stepStart("my step");

        reportiumClient.stepEnd();

        reportiumClient.testStop(TestResultFactory.createSuccess());

        System.out.println(reportiumClient.getReportUrl());
    }
}
