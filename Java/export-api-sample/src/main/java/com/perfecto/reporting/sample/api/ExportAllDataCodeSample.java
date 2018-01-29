package com.perfecto.reporting.sample.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This sample downloads all the data that is related to a given driver execution ID
 */
public class ExportAllDataCodeSample {

    // ***************************************************************************************
    // Set your "CQL_NAME" and the "PERFECTO_SECURITY_TOKEN" in the ReportiumExportUtils file.
    // ***************************************************************************************

    public static void main(String[] args) throws Exception {
        // TODO replace with your export root directory
        Path exportRoot = Files.createTempDirectory("perfecto_reporting_exports_");

        // TODO put your driver execution ID here
        String executionId = "MY_DRIVER_EXECUTION_ID";

        // get all the tests of the execution as JSON
        JsonObject testExecutionsJson = ReportiumExportUtils.retrieveTestExecutions(executionId);
        Path testExecutionsJsonPath = Paths.get(exportRoot.toString(), "test_executions_export.json");
        ReportiumExportUtils.writeJsonToFile(testExecutionsJsonPath, testExecutionsJson);

        JsonArray testExecutionsArray = testExecutionsJson.getAsJsonArray("resources");
        if (testExecutionsArray.size() == 0) {
            System.out.println("There are no test executions for that driver execution ID");
        } else {

            int testCounter = 1;

            // store each test's data in a separate folder
            for (JsonElement testExecutionElement : testExecutionsArray) {
                JsonObject testJson = testExecutionElement.getAsJsonObject();
                String testId = testJson.get("id").getAsString();
                String testName = testJson.get("name").getAsString();

                Path testFolder = Paths.get(exportRoot.toString(), "test-" + String.format("%03d", testCounter) + "-" + FilenameUtils.normalize(testName));
                Files.createDirectory(testFolder);

                // write test's data to a file
                Path testJsonPath = Paths.get(testFolder.toString(), "test.json");
                ReportiumExportUtils.writeJsonToFile(testJsonPath, testJson);

                // get all the commands of a specific test and store them as a JSON to file
                JsonObject commandsJson = ReportiumExportUtils.retrieveTestCommands(testId);
                Path commandsJsonPath = Paths.get(testFolder.toString(), "commands.json");
                ReportiumExportUtils.writeJsonToFile(commandsJsonPath, commandsJson);

                // get PDF of the specific test and store it to a file
                Path testPdfPath = Paths.get(testFolder.toString(), "report.pdf");
                ReportiumExportUtils.downloadTestReport(testPdfPath, testId);

                downloadAttachments(testJson, testFolder);

                downloadVideos(testJson, testFolder);

                testCounter++;
            }

            // get execution summary PDF
            Path summaryPdfPath = Paths.get(exportRoot.toString(), "summary-report.pdf");
            ReportiumExportUtils.downloadExecutionSummaryReport(summaryPdfPath, executionId);
        }

        try {
            Desktop.getDesktop().open(exportRoot.toFile());
        } catch (Exception e) {
            System.out.println("Data was saved in: " + exportRoot.toString());
        }
    }

    private static void downloadAttachments(JsonObject testJson, Path testFolder) throws IOException, URISyntaxException {
        String testName = testJson.get("name").getAsString();
        JsonArray attachmentsArray = testJson.getAsJsonArray("artifacts");
        if (attachmentsArray.size() > 0) {
            Path attachmentsDir = Paths.get(testFolder.toString(), "attachments");
            Files.createDirectory(attachmentsDir);

            for (JsonElement attachmentElement : attachmentsArray) {
                JsonObject artifactJson = attachmentElement.getAsJsonObject();
                String type = artifactJson.get("type").getAsString();
                Path attachmentDir = Paths.get(attachmentsDir.toString(), type.toLowerCase());
                Files.createDirectory(attachmentDir);
                String path = artifactJson.get("path").getAsString();
                Path artifactPath = Paths.get(attachmentDir.toString(), FilenameUtils.getName(path));
                ReportiumExportUtils.downloadFileToFS(artifactPath, new URI(path));
            }
        } else {
            System.out.println("\nNo attachments found for test execution '" + testName + "'");
        }
    }

    private static void downloadVideos(JsonObject testJson, Path testFolder) throws IOException, URISyntaxException {
        String testName = testJson.get("name").getAsString();
        JsonArray videosArray = testJson.getAsJsonArray("videos");
        if (videosArray.size() > 0) {
            Path videosDir = Paths.get(testFolder.toString(), "videos");
            Files.createDirectory(videosDir);

            for (JsonElement videosElement : videosArray) {
                JsonObject videoJson = videosElement.getAsJsonObject();
                String downloadUrl = videoJson.get("downloadUrl").getAsString();
                Path videoPath = Paths.get(videosDir.toString(), FilenameUtils.getName(downloadUrl));
                ReportiumExportUtils.downloadFileToFS(videoPath, new URI(downloadUrl));
            }
        } else {
            System.out.println("\nNo videos found for test execution '" + testName + "'");
        }
    }
}