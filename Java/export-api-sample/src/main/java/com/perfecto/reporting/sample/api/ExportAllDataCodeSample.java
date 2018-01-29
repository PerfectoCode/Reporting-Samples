package com.perfecto.reporting.sample.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This sample downloads all the data that is related to a given driver execution ID
 */
public class ExportAllDataCodeSample {

    // ********************************************************************************************
    // Set your "CQL_NAME" and the "PERFECTO_SECURITY_TOKEN" in the ReportiumExportUtils file.
    // ********************************************************************************************

    public static void main(String[] args) throws Exception {
        // TODO replace with your export root directory
        Path exportRoot = Files.createTempDirectory("perfecto_reporting_exports_");

        // TODO put your driver execution ID here
        String executionId = "MY_DRIVER_EXECUTION_ID";

        // get all the tests of the execution as JSON
        JsonObject testExecutionsJson = ReportiumExportUtils.retrieveTestExecutions(executionId);

        JsonArray testExecutionsArray = testExecutionsJson.getAsJsonArray("resources");
        if (testExecutionsArray.size() == 0) {
            System.out.println("There are no test executions for that driver execution ID");
        } else {
            int testCounter = 1;

            // store each test's data in a separate folder
            for (JsonElement testExecutionElement : testExecutionsArray) {
                JsonObject testExecution = testExecutionElement.getAsJsonObject();
                String testId = testExecution.get("id").getAsString();

                Path testFolder = Paths.get(exportRoot.toString(), String.format("%03d", testCounter) + "-" + testId);
                Files.createDirectory(testFolder);

                // write test's data to a file
                Path testExecutionsJsonPath = Paths.get(testFolder.toString(), "test-execution-" + testId + ".json");
                ReportiumExportUtils.writeJsonToFile(testExecutionsJsonPath, testExecutionsJson);

                // get all the commands of a specific test and store them as a JSON to file
                JsonObject commandsJson = ReportiumExportUtils.retrieveTestCommands(testId);
                Path commandsJsonPath = Paths.get(testFolder.toString(), "commands-" + testId + ".json");
                ReportiumExportUtils.writeJsonToFile(commandsJsonPath, commandsJson);

                // get PDF of the specific test and store it to a file


                // TODO write test execution to file
                // TODO retrieve commands
                // TODO download PDF
                // TODO download video
                // TODO download attachments

            }

            // TODO download summary PDF
        }


        Path testExecutionsJsonPath = Paths.get(exportRoot.toString(), "test_executions_export.json");
        ReportiumExportUtils.writeJsonToFile(testExecutionsJsonPath, testExecutionsJson);


    }
}