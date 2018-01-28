package com.perfecto.reporting.sample.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class ExportAllDataCodeSample {
    // The Perfecto Continuous Quality Lab you work with
    public static final String CQL_NAME = "demo"; // TODO put your Continuous Quality Lab name here

    // See http://developers.perfectomobile.com/display/PD/DigitalZoom+Reporting+Public+API on how to obtain a Security Token
    private static final String PERFECTO_SECURITY_TOKEN = "MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN"; // TODO put your security token here


    public static final String REPORTING_SERVER_URL = "https://" + CQL_NAME + ".reporting.perfectomobile.com";
    public static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    public static void main(String[] args) throws Exception {
        // TODO replace with your export directory
        Path exportRoot = Files.createTempDirectory("perfecto_reporting_exports_");

        // TODO put your driver execution ID here
        String executionId = "MY_DRIVER_EXECUTION_ID";

        // get all the tests of the execution and stores them as JSON to a file
        JsonObject testExecutionsJson = retrieveTestExecutions(executionId);
        Path testExecutionsJsonPath = Paths.get(exportRoot.toString(), "test_executions_export.json");
        writeJsonToFile(testExecutionsJsonPath, testExecutionsJson);


    }

    private static void writeJsonToFile(Path path, JsonObject content) {
    }

    private static JsonObject retrieveTestExecutions(String executionId) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v1/test-executions");

         uriBuilder.addParameter("externalId[0]", executionId);

        HttpGet getExecutions = new HttpGet(uriBuilder.build());
        addDefaultRequestHeaders(getExecutions);
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse getExecutionsResponse = httpClient.execute(getExecutions);
        JsonObject executions;
        try (InputStreamReader inputStreamReader = new InputStreamReader(getExecutionsResponse.getEntity().getContent())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String response = IOUtils.toString(inputStreamReader);
            try {
                executions = gson.fromJson(response, JsonObject.class);
            } catch (JsonSyntaxException e) {
                throw new RuntimeException("Unable to parse response: " + response);
            }
            System.out.println("\nList of test executions response:\n" + gson.toJson(executions));
        }
        return executions;
    }

    private static void addDefaultRequestHeaders(HttpRequestBase request) {
        if (SECURITY_TOKEN == null || SECURITY_TOKEN.equals("MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN")) {
            throw new RuntimeException("Invalid security token '" + SECURITY_TOKEN + "'. Please set a security token");
        }
        request.addHeader("PERFECTO_AUTHORIZATION", SECURITY_TOKEN);
    }
}