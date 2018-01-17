package com.perfecto.reporting.sample.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class PdfDownloadCodeSample {

    // The Perfecto Continuous Quality Lab you work with
    public static final String CQL_NAME = "demo"; // TODO put your Continuous Quality Lab name here

    // See http://developers.perfectomobile.com/display/PD/DigitalZoom+Reporting+Public+API on how to obtain a Security Token
    private static final String PERFECTO_SECURITY_TOKEN = "MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN"; // TODO put your security token here


    public static final String REPORTING_SERVER_URL = "https://" + CQL_NAME + ".reporting.perfectomobile.com";
    public static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);

    static HttpClient httpClient = HttpClientBuilder.create().build();
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final int PDF_DOWNLOAD_ATTEMPTS = 5;

    public static void main(String[] args) throws Exception {
        String tempDir = Files.createTempDirectory("reporting_pdf_sample_").toString();

        // TODO put your driver execution ID here
        String driverExecutionId = "MY_DRIVER_EXECUTION_ID";

        // TODO put your reportium test execution ID here
        String testId = "MY_TEST_ID";

        // Download an execution summary PDF report of an execution (may contain several tests)
        Path summaryPdfPath = Paths.get(tempDir, driverExecutionId + ".pdf");
        downloadExecutionSummaryReport(summaryPdfPath, driverExecutionId);

        // Download a PDF report of a single test - create a "task" for PDF generation and download the PDF on task completion
        CreatePdfTask task = startTestReportGeneration(testId);
        Path testPdfPath = Paths.get(tempDir, testId + ".pdf");
        downloadTestReport(testPdfPath, task, testId);
    }

    private static void downloadExecutionSummaryReport(Path summaryPdfPath, String driverExecutionId) throws URISyntaxException, IOException {
        System.out.println("Downloading PDF for driver execution ID: " + driverExecutionId);
        URIBuilder uriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v1/test-executions/pdf");
        uriBuilder.addParameter("externalId[0]", driverExecutionId);
        downloadPdfFileToFS(summaryPdfPath, uriBuilder.build());
    }

    private static CreatePdfTask startTestReportGeneration(String testId) throws URISyntaxException, IOException {
        System.out.println("Starting PDF generation for test ID: " + testId);
        URIBuilder taskUriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v2/test-executions/pdf/task");
        taskUriBuilder.addParameter("testExecutionId", testId);
        HttpPost httpPost = new HttpPost(taskUriBuilder.build());
        addDefaultRequestHeaders(httpPost);

        CreatePdfTask task = null;
        for (int attempt = 1; attempt <= PDF_DOWNLOAD_ATTEMPTS; attempt++) {

            HttpResponse response = httpClient.execute(httpPost);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    task = gson.fromJson(EntityUtils.toString(response.getEntity()), CreatePdfTask.class);
                    break;
                } else if (HttpStatus.SC_NO_CONTENT == statusCode) {

                    // if the execution is being processed, the server will respond with empty response and status code 204
                    System.out.println("\nThe server responded with 204 (no content). " +
                            "The execution is still being processed. Attempting again in 5 sec (" + attempt + "/" + PDF_DOWNLOAD_ATTEMPTS + ")");
                    Thread.sleep(5000);
                } else {
                    String errorMsg = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                    System.err.println("Error downloading file. Status: " + response.getStatusLine() + ".\nInfo: " + errorMsg);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
        if (task == null) {
            throw new RuntimeException("Unable to create a CreatePdfTask");
        }
        return task;
    }

    private static void downloadTestReport(Path testPdfPath, CreatePdfTask task, String testId) throws URISyntaxException, IOException {
        System.out.println("Downloading PDF for test ID: " + testId);
        long startTime = System.currentTimeMillis();
        int maxWaitMin = 10;
        long maxGenerationTime = TimeUnit.MINUTES.toMillis(maxWaitMin);
        String taskId = task.getTaskId();

        CreatePdfTask updatedTask;
        do {
            updatedTask = getUpdatedTask(taskId);
            try {
                if (updatedTask.getStatus() != TaskStatus.COMPLETE) {
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (updatedTask.getStatus() != TaskStatus.COMPLETE && startTime + maxGenerationTime > System.currentTimeMillis());

        if (updatedTask.getStatus() == TaskStatus.COMPLETE) {
            downloadPdfFileToFS(testPdfPath, new URI(updatedTask.getUrl()));
        } else {
            throw new RuntimeException("The task is still in " + updatedTask.getStatus() + " status after waiting " + maxWaitMin + " min");
        }
    }

    private static CreatePdfTask getUpdatedTask(String taskId) throws URISyntaxException, IOException {
        CreatePdfTask task;
        URIBuilder taskUriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v2/test-executions/pdf/task/" + taskId);
        HttpGet httpGet = new HttpGet(taskUriBuilder.build());
        addDefaultRequestHeaders(httpGet);
        HttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK == statusCode) {
            task = gson.fromJson(EntityUtils.toString(response.getEntity()), CreatePdfTask.class);
        } else {
            throw new RuntimeException("Error while getting AsyncTask: " + response.getStatusLine().toString());
        }
        return task;
    }

    private static void downloadPdfFileToFS(Path pdfPath, URI uri) throws IOException {
        boolean downloadComplete = false;
        HttpGet httpGet = new HttpGet(uri);
        addDefaultRequestHeaders(httpGet);
        for (int attempt = 1; attempt <= PDF_DOWNLOAD_ATTEMPTS && !downloadComplete; attempt++) {

            HttpResponse response = httpClient.execute(httpGet);
            FileOutputStream fileOutputStream = null;

            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    fileOutputStream = new FileOutputStream(pdfPath.toFile());
                    IOUtils.copy(response.getEntity().getContent(), fileOutputStream);
                    System.out.println("\nSaved downloaded file to: " + pdfPath.toString());
                    downloadComplete = true;
                } else if (HttpStatus.SC_NO_CONTENT == statusCode) {

                    // if the execution is being processed, the server will respond with empty response and status code 204
                    System.out.println("\nThe server responded with 204 (no content). " +
                            "The execution is still being processed. Attempting again in 5 sec (" + attempt + "/" + PDF_DOWNLOAD_ATTEMPTS + ")");
                    Thread.sleep(5000);
                } else {
                    String errorMsg = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                    System.err.println("Error downloading file. Status: " + response.getStatusLine() + ".\nInfo: " + errorMsg);
                    downloadComplete = true;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                EntityUtils.consumeQuietly(response.getEntity());
                IOUtils.closeQuietly(fileOutputStream);
            }
        }
        if (!downloadComplete) {
            System.err.println("The execution is still being processed. No more download attempts");
        }
    }

    private static void addDefaultRequestHeaders(HttpRequestBase request) {
        if (SECURITY_TOKEN == null || SECURITY_TOKEN.equals("MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN")) {
            throw new RuntimeException("Invalid security token '" + SECURITY_TOKEN + "'. Please set a security token");
        }
        request.addHeader("PERFECTO_AUTHORIZATION", SECURITY_TOKEN);
    }

    private enum TaskStatus {
        IN_PROGRESS, COMPLETE
    }

    private static class CreatePdfTask {
        private String taskId;
        private TaskStatus status;
        private String url;

        public CreatePdfTask() {
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
