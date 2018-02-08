package com.perfecto.reporting.sample.api;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.client.ReportiumImportClientFactory;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.imports.model.command.Command;
import com.perfecto.reportium.imports.model.command.CommandParameter;
import com.perfecto.reportium.imports.model.command.CommandStatus;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import java.net.URI;

public class ImportTestCommand {
    private static final String COMPANY_ID = "my-company-id"; //TODO put your company ID here
    private static final String PERFECTO_SECURITY_TOKEN = "my-security-token"; //TODO put your security token here

    private static final String REPORTIUM_URL = "https://" + System.getProperty("company-id", COMPANY_ID) + ".reporting.perfectomobile.com"; // "https://[COMPANY_ID].reporting.perfectomobile.com";
    private static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    public static void main(String[] args) throws Exception {
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();
        Connection connection = new Connection(new URI(REPORTIUM_URL), SECURITY_TOKEN);
        ReportiumImportClient reportiumClient = new ReportiumImportClientFactory().createReportiumImportClient(connection, executionContext);

        reportiumClient.testStart("my test name", new TestContext());

        reportiumClient.stepStart("my step");

        long commandStartTime = System.currentTimeMillis();
        long commandEndTime = commandStartTime + 100;
        Command command = new Command.Builder()
                .withName("my command name")
                .withMessage("my command message")
                .withStatus(CommandStatus.SUCCESS)
                .withStartTime(commandStartTime)
                .withEndTime(commandEndTime)
                .addParameter(new CommandParameter("name1", "value1"))
                .addParameter(new CommandParameter("name2", "value2"))
                .build();

        reportiumClient.command(command);

        reportiumClient.stepEnd();

        reportiumClient.testStop(TestResultFactory.createSuccess());

        System.out.println(reportiumClient.getReportUrl());
    }
}
