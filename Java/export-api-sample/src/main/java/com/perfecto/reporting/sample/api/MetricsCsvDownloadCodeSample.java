package com.perfecto.reporting.sample.api;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MetricsCsvDownloadCodeSample {

    // ***************************************************************************************
    // Set your "CQL_NAME" and the "PERFECTO_SECURITY_TOKEN" in the ReportiumExportUtils file.
    // ***************************************************************************************

    public static void main(String[] args) throws Exception {
        Path tempDir = Files.createTempDirectory("metrics_csv_sample_");

        // TODO put your required file name here
        String csvFileName = "exported_metrics_sample";

        // Download metrics CSV report - create a "task" for CSV generation and download the CSV on task completion
        Path testCsvPath = Paths.get(tempDir.toString(), csvFileName + ".csv");

        // You can get executionId value from your driver like:
        // String executionId = (String) driver.getCapabilities().getCapability("executionId");
        String executionId = "executionId";
        ReportiumExportUtils.downloadMetricsCsvReport(testCsvPath, ReportiumExportUtils.createRequestBody(executionId));

        try {
            Desktop.getDesktop().open(tempDir.toFile());
        } catch (Exception e) {
            System.out.println("Data was saved in: " + tempDir);
        }
    }
}
