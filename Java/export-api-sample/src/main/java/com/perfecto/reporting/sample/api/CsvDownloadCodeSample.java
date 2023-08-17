package com.perfecto.reporting.sample.api;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvDownloadCodeSample {

    // ***************************************************************************************
    // Set your "CQL_NAME" and the "PERFECTO_SECURITY_TOKEN" in the ReportiumExportUtils file.
    // ***************************************************************************************

    public static void main(String[] args) throws Exception {
        Path tempDir = Files.createTempDirectory("reporting_csv_sample_");

        // TODO put your required file name here
        String csvFileName = "Report_Library_Sample";

        // Download a CSV report - create a "task" for CSV generation and download the CSV on task completion
        Path testsCsvPath = Paths.get(tempDir.toString(), csvFileName + ".csv");
        // To get data about all the executions from the last 24 hours:
        ReportiumExportUtils.downloadCsvReport(testsCsvPath, ReportiumExportUtils.createRequestBody());

        // To get csv file for particular execution:
        // You can get executionId value from your driver like:
        // String executionId = (String) driver.getCapabilities().getCapability("executionId");
        String executionId = "executionId";
        Path testCsvPath = Paths.get(tempDir.toString(), csvFileName + "_" + executionId + ".csv");
        ReportiumExportUtils.downloadCsvReport(testCsvPath, ReportiumExportUtils.createRequestBody(executionId));

        try {
            Desktop.getDesktop().open(tempDir.toFile());
        } catch (Exception e) {
            System.out.println("Data was saved in: " + tempDir.toString());
        }
    }
}
