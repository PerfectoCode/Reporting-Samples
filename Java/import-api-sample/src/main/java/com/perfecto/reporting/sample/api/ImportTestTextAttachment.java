package com.perfecto.reporting.sample.api;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.imports.model.attachment.TextAttachment;
import com.perfecto.reportium.imports.model.command.Command;
import com.perfecto.reportium.imports.model.command.CommandStatus;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

public class ImportTestTextAttachment {
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

        reportiumClient.command(new Command.Builder()
                .withName("my command name")
                .withStatus(CommandStatus.SUCCESS)
                .build());

        reportiumClient.stepEnd();

        File xmlFile = new File(ImportTestScreenshotAsFile.class.getClassLoader().getResource("attachments/xml_file.xml").getFile());
        TextAttachment xmlAttachment = new TextAttachment.Builder()
                .withAbsolutePath(xmlFile.getAbsolutePath())
                .build(); // no need to pass content type and file name, since we can guess them from the provided path

        InputStream textFileInputStream = ImportTestScreenshotAsStream.class.getClassLoader().getResourceAsStream("attachments/text_file.txt");
        TextAttachment txtAttachment = new TextAttachment.Builder()
                .withInputStream(textFileInputStream)
                .withContentType(TextAttachment.TEXT_PLAIN)
                .withFileName("text_file.txt")
                .build();

        reportiumClient.testStop(TestResultFactory.createSuccess(), xmlAttachment, txtAttachment);

        reportiumClient.close();

        System.out.println(reportiumClient.getReportUrl());
    }
}
