package com.perfecto.reporting.sample.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
        Path testCsvPath = Paths.get(tempDir.toString(), csvFileName + ".csv");
        ReportiumExportUtils.downloadCsvTestReport(testCsvPath,createRequestBody());

        try {
            Desktop.getDesktop().open(tempDir.toFile());
        } catch (Exception e) {
            System.out.println("Data was saved in: " + tempDir.toString());
        }
    }

    private static JsonObject createRequestBody() {
        JsonObject jsonBody = new JsonObject();
        JsonObject filerJson = new JsonObject();
        JsonObject fieldsJson = new JsonObject();
        JsonArray values = new JsonArray();
        values.add(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        fieldsJson.add("startExecutionTime", values);
        filerJson.add("fields", fieldsJson);
        jsonBody.add("filter", filerJson);
        return jsonBody;
    }
}
