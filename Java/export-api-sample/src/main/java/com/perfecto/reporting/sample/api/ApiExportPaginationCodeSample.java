package com.perfecto.reporting.sample.api;

import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ApiExportPaginationCodeSample {

    // The Perfecto Continuous Quality Lab you work with
    public static final String CQL_NAME = "demo"; // TODO put your Continuous Quality Lab name here

    // See http://developers.perfectomobile.com/display/PD/DigitalZoom+Reporting+Public+API on how to obtain a Security Token
    public static final String PERFECTO_SECURITY_TOKEN = "MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN"; // TODO put your security token here


    public static final String REPORTING_SERVER_URL = "https://" + CQL_NAME + ".app.perfectomobile.com";
    public static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        boolean halt = false;
        int page = 1;
        while (!halt) {
            URIBuilder uriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v1/test-executions");
            // In this example: retrieve test executions of the past 24 hours (result may contain tests of multiple driver executions)
            uriBuilder.addParameter("startExecutionTime[0]", Long.toString(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)));
            uriBuilder.addParameter("endExecutionTime[0]", Long.toString(System.currentTimeMillis()));
            uriBuilder.addParameter("_page", Long.toString(page));

            HttpGet getExecutions = new HttpGet(uriBuilder.build());
            addDefaultRequestHeaders(getExecutions);
            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpResponse getExecutionsResponse = httpClient.execute(getExecutions);
            JsonArray executions;
            JsonObject metadata;
            try (InputStreamReader inputStreamReader = new InputStreamReader(getExecutionsResponse.getEntity().getContent())) {
                String response = IOUtils.toString(inputStreamReader);
                try {
                    JsonObject json = gson.fromJson(response, JsonObject.class);
                    executions = json.getAsJsonArray("resources");
                    metadata = json.getAsJsonObject("metadata");
                } catch (JsonSyntaxException e) {
                    throw new RuntimeException("Unable to parse response: " + response);
                }
            }
            System.out.println("Received " + executions.size() + " test executions for page " + page);

            // if truncated is true, it means there are more results to retrieve for the current filter
            halt = !metadata.getAsJsonPrimitive("truncated").getAsBoolean();
            page++;
        }
        System.out.println("Done, retrieved " + (page-1) + " pages in total");
    }

    private static void addDefaultRequestHeaders(HttpRequestBase request) {
        if (SECURITY_TOKEN == null || SECURITY_TOKEN.equals("MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN")) {
            throw new RuntimeException("Invalid security token '" + SECURITY_TOKEN + "'. Please set a security token");
        }
        request.addHeader("PERFECTO_AUTHORIZATION", SECURITY_TOKEN);
    }
}

