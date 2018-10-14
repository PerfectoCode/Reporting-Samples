package com.perfecto.reporting.sample.api;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfDownloadCodeSample {

    // ***************************************************************************************
    // Set your "CQL_NAME" and the "PERFECTO_SECURITY_TOKEN" in the ReportiumExportUtils file.
    // ***************************************************************************************

    public static void main(String[] args) throws Exception {
        Path tempDir = Files.createTempDirectory("reporting_pdf_sample_");

        // TODO put your driver execution ID here
        String driverExecutionId = "Galaxy S6";

        // TODO put your reportium test execution ID here (you have to know it in advance)
        String testId = "MY_TEST_ID";

        // Download an execution summary PDF report of an execution (may contain several tests)
        Path summaryPdfPath = Paths.get(tempDir.toString(), driverExecutionId + ".pdf");
        ReportiumExportUtils.downloadExecutionSummaryReport(summaryPdfPath, driverExecutionId);

        // Download a PDF report of a single test - create a "task" for PDF generation and download the PDF on task completion
        Path testPdfPath = Paths.get(tempDir.toString(), testId + ".pdf");
        ReportiumExportUtils.downloadTestReport(testPdfPath, testId);

        try {
            Desktop.getDesktop().open(tempDir.toFile());
        } catch (Exception e) {
            System.out.println("Data was saved in: " + tempDir.toString());
        }
    }
}
