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
import java.util.concurrent.TimeUnit;

public class PdfDownloadCodeSample {

    // The Perfecto Continuous Quality Lab you work with
    public static final String CQL_NAME = "demo";

    public static final String REPORTING_SERVER_URL = "https://" + CQL_NAME + ".reporting.perfectomobile.com";

    // See http://developers.perfectomobile.com/display/PD/DigitalZoom+Reporting+Public+API on how to obtain a Security Token
    public static final String SECURITY_TOKEN = "MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN";

    public static final int PDF_DOWNLOAD_ATTEMPTS = 5;

    static HttpClient httpClient = HttpClientBuilder.create().build();
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws Exception {

        // TODO put your driver execution ID here
        String driverExecutionId = "MY_DRIVER_EXECUTION_ID";

        // TODO put your reportium test execution ID here
        String testId = "MY_TEST_ID";

        // Download an execution summary PDF report of an execution (may contain several tests)
        downloadExecutionSummaryReport(driverExecutionId);

        // Download a PDF report of a single test - create a "task" for PDF generation and download the PDF on task completion
        AsyncTask task = startTestReportGeneration(testId);
        downloadTestReport(task, testId);
    }

    private static void downloadExecutionSummaryReport(String driverExecutionId) throws URISyntaxException, IOException {
        System.out.println("Downloading PDF for driver execution ID: " + driverExecutionId);
        URIBuilder uriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v1/test-executions/pdf");
        uriBuilder.addParameter("externalId[0]", driverExecutionId);
        downloadPdfFileToFS(uriBuilder.build(), driverExecutionId, "_summary.pdf");
    }

    private static AsyncTask startTestReportGeneration(String testId) throws URISyntaxException, IOException {
        System.out.println("Starting PDF generation for test ID: " + testId);
        URIBuilder taskUriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v2/test-executions/pdf/task");
        taskUriBuilder.addParameter("testExecutionId", testId);
        HttpPost httpPost = new HttpPost(taskUriBuilder.build());
        addDefaultRequestHeaders(httpPost);

        AsyncTask task = null;
        for (int attempt = 1; attempt <= PDF_DOWNLOAD_ATTEMPTS; attempt++) {

            HttpResponse response = httpClient.execute(httpPost);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    task = gson.fromJson(EntityUtils.toString(response.getEntity()), AsyncTask.class);
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
        return task;
    }

    private static void downloadTestReport(AsyncTask task, String testId) throws URISyntaxException, IOException {
        System.out.println("Downloading PDF for test ID: " + testId);
        long startTime = System.currentTimeMillis();
        int maxWaitMin = 10;
        long maxGenerationTime = TimeUnit.MINUTES.toMillis(maxWaitMin);
        String taskId = task.getTaskId();

        AsyncTask updatedTask;
        do {
            updatedTask = getUpdatedAsyncTask(taskId);
            try {
                if (updatedTask.getStatus() != TaskStatus.COMPLETE) {
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (updatedTask.getStatus() != TaskStatus.COMPLETE && startTime + maxGenerationTime < System.currentTimeMillis());

        if (updatedTask.getStatus() == TaskStatus.COMPLETE) {
            downloadPdfFileToFS(new URI(updatedTask.getUrl()), testId, "_report.pdf");
        } else {
            throw new RuntimeException("The task is still in " + updatedTask.getStatus() + " status after waiting " + maxWaitMin + " min");
        }
    }

    private static AsyncTask getUpdatedAsyncTask(String taskId) throws URISyntaxException, IOException {
        AsyncTask task;
        URIBuilder taskUriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v2/test-executions/pdf/task/" + taskId);
        HttpGet httpGet = new HttpGet(taskUriBuilder.build());
        addDefaultRequestHeaders(httpGet);
        HttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK == statusCode) {
            task = gson.fromJson(EntityUtils.toString(response.getEntity()), AsyncTask.class);
        } else {
            throw new RuntimeException("Error while getting AsyncTask: " + response.getStatusLine().toString());
        }
        return task;
    }

    private static void downloadPdfFileToFS(URI uri, String fileName, String suffix) throws IOException {
        boolean downloadComplete = false;
        HttpGet httpGet = new HttpGet(uri);
        addDefaultRequestHeaders(httpGet);
        for (int attempt = 1; attempt <= PDF_DOWNLOAD_ATTEMPTS && !downloadComplete; attempt++) {

            HttpResponse response = httpClient.execute(httpGet);
            FileOutputStream fileOutputStream = null;

            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    Path file = Files.createTempFile(fileName, suffix);
                    fileOutputStream = new FileOutputStream(file.toFile());
                    IOUtils.copy(response.getEntity().getContent(), fileOutputStream);
                    System.out.println("\nSaved downloaded file to: " + file.toFile().getAbsolutePath());
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
        request.addHeader("PERFECTO_AUTHORIZATION", SECURITY_TOKEN);
    }

    private enum TaskStatus {
        IN_PROGRESS, COMPLETE
    }

    private static class AsyncTask {
        private String taskId;
        private TaskStatus status;
        private String url;

        public AsyncTask() {
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
