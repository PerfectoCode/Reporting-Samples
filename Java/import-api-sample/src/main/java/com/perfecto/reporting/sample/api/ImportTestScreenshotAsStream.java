package com.perfecto.reporting.sample.api;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.imports.model.attachment.ScreenshotAttachment;
import com.perfecto.reportium.imports.model.command.Command;
import com.perfecto.reportium.imports.model.command.CommandStatus;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import java.io.InputStream;
import java.net.URI;

public class ImportTestScreenshotAsStream {

    private static final String PERFECTO_SECURITY_TOKEN = "my-security-token"; //TODO put your security token here
    private static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    private static final String CQL_NAME = System.getProperty("CQL_NAME", "my-company-id"); // TODO put your Continuous Quality Lab name here
    private static final String REPORTIUM_URL = "https://" + CQL_NAME + ".reporting.perfectomobile.com"; // "https://[COMPANY_ID].reporting.perfectomobile.com";

    public static void main(String[] args) throws Exception {
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();
        Connection connection = new Connection(new URI(REPORTIUM_URL), SECURITY_TOKEN);
        ReportiumImportClient reportiumClient = new ReportiumImportClientFactory().createReportiumImportClient(connection, executionContext);

        reportiumClient.testStart("my test name", new TestContext());

        reportiumClient.stepStart("my step");

        long commandStartTime = System.currentTimeMillis();
        long commandEndTime = commandStartTime + 100;
        InputStream screenshotInputStream = ImportTestScreenshotAsStream.class.getClassLoader().getResourceAsStream("screenshots/digital-zoom.jpg");
        Command command = new Command.Builder()
                .withName("my command name")
                .withStatus(CommandStatus.SUCCESS)
                .withStartTime(commandStartTime)
                .withEndTime(commandEndTime)
                .addScreenshotAttachment(new ScreenshotAttachment.Builder()
                        .withInputStream(screenshotInputStream)
                        .withContentType(ScreenshotAttachment.IMAGE_JPEG)
                        .build())
                .build();

        reportiumClient.command(command);

        reportiumClient.stepEnd();

        reportiumClient.testStop(TestResultFactory.createSuccess());

        System.out.println(reportiumClient.getReportUrl());
    }
}
