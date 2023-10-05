diff --git a/Java/export-api-sample/src/main/java/com/perfecto/reporting/sample/api/ReportiumExportUtils.java b/Java/export-api-sample/src/main/java/com/perfecto/reporting/sample/api/ReportiumExportUtils.java
index 3647fd0..a478b55 100644
--- a/Java/export-api-sample/src/main/java/com/perfecto/reporting/sample/api/ReportiumExportUtils.java
+++ b/Java/export-api-sample/src/main/java/com/perfecto/reporting/sample/api/ReportiumExportUtils.java
@@ -1,433 +1,450 @@
 package com.perfecto.reporting.sample.api;
 
 import com.google.gson.*;
+import org.apache.commons.io.FilenameUtils;
 import org.apache.commons.io.IOUtils;
 import org.apache.http.HttpResponse;
 import org.apache.http.HttpStatus;
+import org.apache.http.NameValuePair;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.config.RequestConfig;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.utils.URIBuilder;
+import org.apache.http.client.utils.URLEncodedUtils;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
 import org.apache.http.impl.client.HttpClientBuilder;
 import org.apache.http.util.EntityUtils;
 
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.nio.charset.Charset;
 import java.nio.file.Files;
 import java.nio.file.Path;
+import java.util.List;
+import java.util.Optional;
 import java.util.concurrent.TimeUnit;
 
 /**
  * This class is the utils class for exporting Reportium data
  */
 public class ReportiumExportUtils {
 
     // The Perfecto Continuous Quality Lab you work with
     private static final String CQL_NAME = "demo"; // TODO put your Continuous Quality Lab name here
 
     // See http://developers.perfectomobile.com/display/PD/DigitalZoom+Reporting+Public+API on how to obtain a Security Token
     private static final String PERFECTO_SECURITY_TOKEN = "MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN"; // TODO put your security token here
 
 
     private static final int TIMEOUT_MILLIS = 60000;
     private static final int DOWNLOAD_ATTEMPTS = 12;
     private static final String REPORTING_SERVER_URL = "https://" + CQL_NAME + ".app.perfectomobile.com";
     private static final String CSV_TASK_CREATION_URL = REPORTING_SERVER_URL + "/export/api/v3/test-executions/csv";
     private static final String METRICS_CSV_TASK_CREATION_URL = REPORTING_SERVER_URL + "/export/api/v3/metrics/csv";
     private static final String PDF_DOWNLOAD_URL = REPORTING_SERVER_URL + "/export/api/v3/test-executions/pdf/task/";
     private static final String CSV_DOWNLOAD_URL = REPORTING_SERVER_URL + "/export/api/v3/test-executions/csv/";
     private static final String SECURITY_TOKEN = System.getProperty("security-token", PERFECTO_SECURITY_TOKEN);
+    private static final String FILENAME_QUERY_PARAM = "fileName";
     private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
     private static HttpClient httpClient = HttpClientBuilder.create()
             .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
             .setDefaultRequestConfig(RequestConfig.custom()
                     .setSocketTimeout(TIMEOUT_MILLIS)
                     .setConnectTimeout(TIMEOUT_MILLIS)
                     .setConnectionRequestTimeout(TIMEOUT_MILLIS)
                     .build())
             .build();
 
     /**
      * Returns a JSON instance containing commands of a single test
      *
      * @param testId the ID of the test
      * @return
      * @throws URISyntaxException
      * @throws IOException
      */
     public static JsonObject retrieveTestCommands(String testId) throws URISyntaxException, IOException {
         URIBuilder uriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v3/test-executions/" + testId + "/commands");
 
         HttpGet getCommands = new HttpGet(uriBuilder.build());
         JsonObject commandsJson = getJson(getCommands);
         System.out.println("\nList of commands response:\n" + gson.toJson(commandsJson));
 
         return commandsJson;
     }
 
     /**
      * Returns a JSON instance containing information regarding the execution: tests, artifacts.
      * For more info
      *
      * @param executionId
      * @return
      * @throws URISyntaxException
      * @throws IOException
      */
     public static JsonObject retrieveTestExecutions(String executionId) throws URISyntaxException, IOException {
         URIBuilder uriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v3/test-executions");
         uriBuilder.addParameter("externalId[0]", executionId);
 
         HttpGet getTestExecutions = new HttpGet(uriBuilder.build());
         JsonObject testExecutionsJson = getJson(getTestExecutions);
         System.out.println("\nList of test executions response:\n" + gson.toJson(testExecutionsJson));
 
         return testExecutionsJson;
     }
 
     /**
      * Downloads the driver execution summary PDF report
      *
      * @param summaryPdfPath    local path that the downloaded report will be saved to
      * @param driverExecutionId the driver execution ID of the report
      * @throws URISyntaxException
      * @throws IOException
      */
     public static void downloadExecutionSummaryReport(Path summaryPdfPath, String driverExecutionId) throws URISyntaxException, IOException {
         System.out.println("Downloading PDF for driver execution ID: " + driverExecutionId);
         URIBuilder uriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v3/test-executions/pdf");
         uriBuilder.addParameter("externalId[0]", driverExecutionId);
 
         downloadPdfOrCsvFileToFS(summaryPdfPath, uriBuilder.build());
     }
 
     /**
      * Downloads a single test execution PDF report
      *
      * @param testPdfPath local path that the downloaded report will be saved to
      * @param testId      the test ID
      * @throws URISyntaxException
      * @throws IOException
      */
     public static void downloadTestReport(Path testPdfPath, String testId) throws URISyntaxException, IOException {
         System.out.println("Starting PDF generation for test ID: " + testId);
         URIBuilder taskUriBuilder = new URIBuilder(REPORTING_SERVER_URL + "/export/api/v3/test-executions/pdf/task");
         taskUriBuilder.addParameter("testExecutionId", testId);
         HttpPost httpPost = new HttpPost(taskUriBuilder.build());
         addDefaultRequestHeaders(httpPost);
 
         CreateTask task = null;
         for (int attempt = 1; attempt <= DOWNLOAD_ATTEMPTS; attempt++) {
 
             HttpResponse response = httpClient.execute(httpPost);
             try {
                 int statusCode = response.getStatusLine().getStatusCode();
                 if (HttpStatus.SC_OK == statusCode) {
                     task = gson.fromJson(EntityUtils.toString(response.getEntity()), CreateTask.class);
                     break;
                 } else if (HttpStatus.SC_NO_CONTENT == statusCode) {
 
                     // if the execution is being processed, the server will respond with empty response and status code 204
                     System.out.println("The server responded with 204 (no content). " +
                             "The execution is still being processed. Attempting again in 5 sec (" + attempt + "/" + DOWNLOAD_ATTEMPTS + ")");
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
             throw new RuntimeException("Unable to create a CreateTask");
         }
 
         downloadTestReport(testPdfPath, task, testId);
     }
 
     /**
      * Downloads a CSV report
      *
      * @param testCsvPath local path that the downloaded report will be saved to
      * @throws URISyntaxException
      * @throws IOException
      */
     public static void downloadCsvReport(Path testCsvPath, JsonObject requestBody) throws URISyntaxException, IOException {
         downloadCsvReport(testCsvPath, requestBody, CSV_TASK_CREATION_URL);
     }
 
     /**
      * Downloads metrics CSV report
      *
      * @param metricsCsvPath local path that the downloaded report will be saved to
      * @throws URISyntaxException
      * @throws IOException
      */
     public static void downloadMetricsCsvReport(Path metricsCsvPath, JsonObject requestBody) throws URISyntaxException, IOException {
         downloadCsvReport(metricsCsvPath, requestBody, METRICS_CSV_TASK_CREATION_URL);
     }
 
     private static void downloadCsvReport(Path testCsvPath, JsonObject requestBody, String csvTaskCreationUrl) throws URISyntaxException, IOException {
         System.out.println("Starting CSV generation from url: " + csvTaskCreationUrl);
         URIBuilder taskUriBuilder = new URIBuilder(csvTaskCreationUrl);
         HttpPost httpPost = new HttpPost(taskUriBuilder.build());
         addDefaultRequestHeaders(httpPost);
         addRequestBody(httpPost, requestBody);
 
         CreateTask task = null;
         for (int attempt = 1; attempt <= DOWNLOAD_ATTEMPTS; attempt++) {
 
             HttpResponse response = httpClient.execute(httpPost);
             int statusCode = response.getStatusLine().getStatusCode();
             if (HttpStatus.SC_OK == statusCode) {
                 task = gson.fromJson(EntityUtils.toString(response.getEntity()), CreateTask.class);
                 break;
             } else {
                 String errorMsg = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                 System.err.println("Error downloading file. Status: " + response.getStatusLine() + ".\nInfo: " + errorMsg);
             }
             EntityUtils.consumeQuietly(response.getEntity());
         }
         if (task == null) {
             throw new RuntimeException("Unable to create a CreateTask");
         }
         downloadCsvReport(testCsvPath, task);
     }
 
     public static void writeJsonToFile(Path path, JsonObject jsonObject) throws IOException {
         Files.write(path, gson.toJson(jsonObject).getBytes());
     }
 
     private static JsonObject getJson(HttpGet httpGet) throws IOException {
         JsonObject result;
         addDefaultRequestHeaders(httpGet);
         HttpClient httpClient = HttpClientBuilder.create().build();
 
         HttpResponse getExecutionsResponse = httpClient.execute(httpGet);
 
         try (InputStreamReader inputStreamReader = new InputStreamReader(getExecutionsResponse.getEntity().getContent())) {
             String response = IOUtils.toString(inputStreamReader);
             try {
                 result = gson.fromJson(response, JsonObject.class);
             } catch (JsonSyntaxException e) {
                 throw new RuntimeException("Unable to parse response: " + response);
             }
         }
         return result;
     }
 
     private static void downloadTestReport(Path testPdfPath, CreateTask task, String testId) throws URISyntaxException, IOException {
         System.out.println("Downloading PDF for test ID: " + testId);
         long startTime = System.currentTimeMillis();
         int maxWaitMin = 10;
         long maxGenerationTime = TimeUnit.MINUTES.toMillis(maxWaitMin);
         String taskId = task.getTaskId();
 
         CreateTask updatedTask;
         do {
             updatedTask = getUpdatedTask(taskId, PDF_DOWNLOAD_URL);
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
             downloadPdfOrCsvFileToFS(testPdfPath, new URI(updatedTask.getUrl()));
         } else {
             throw new RuntimeException("The task is still in " + updatedTask.getStatus() + " status after waiting " + maxWaitMin + " min");
         }
     }
 
     private static void downloadCsvReport(Path testCsvPath, CreateTask task) throws URISyntaxException, IOException {
         System.out.println("Downloading CSV");
         long startTime = System.currentTimeMillis();
         int maxWaitMin = 10;
         long maxGenerationTime = TimeUnit.MINUTES.toMillis(maxWaitMin);
         String taskId = task.getTaskId();
 
         CreateTask updatedTask;
         do {
             updatedTask = getUpdatedTask(taskId, CSV_DOWNLOAD_URL);
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
             downloadPdfOrCsvFileToFS(testCsvPath, new URI(updatedTask.getUrl()));
         } else {
             throw new RuntimeException("The task is still in " + updatedTask.getStatus() + " status after waiting " + maxWaitMin + " min");
         }
     }
 
     private static CreateTask getUpdatedTask(String taskId, String downloadUrl) throws URISyntaxException, IOException {
         CreateTask task;
         URIBuilder taskUriBuilder = new URIBuilder(downloadUrl + taskId);
         HttpGet httpGet = new HttpGet(taskUriBuilder.build());
         addDefaultRequestHeaders(httpGet);
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (HttpStatus.SC_OK == statusCode) {
             task = gson.fromJson(EntityUtils.toString(response.getEntity()), CreateTask.class);
         } else {
             throw new RuntimeException("Error while getting AsyncTask: " + response.getStatusLine().toString());
         }
         return task;
     }
 
     public static void downloadFileToFS(Path path, URI uri) throws IOException {
         HttpGet httpGet = new HttpGet(uri);
         addDefaultRequestHeaders(httpGet);
         HttpResponse response = httpClient.execute(httpGet);
         try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
             if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                 IOUtils.copy(response.getEntity().getContent(), fileOutputStream);
             } else {
                 String errorMsg = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                 System.err.println("Error downloading file. Status: " + response.getStatusLine() + ".\nInfo: " + errorMsg);
             }
         } finally {
             EntityUtils.consumeQuietly(response.getEntity());
         }
     }
 
     private static void downloadPdfOrCsvFileToFS(Path filePath, URI uri) throws IOException {
         boolean downloadComplete = false;
         HttpGet httpGet = new HttpGet(uri);
         ReportiumExportUtils.addDefaultRequestHeaders(httpGet);
         for (int attempt = 1; attempt <= DOWNLOAD_ATTEMPTS && !downloadComplete; attempt++) {
 
             HttpResponse response = httpClient.execute(httpGet);
             FileOutputStream fileOutputStream = null;
 
             try {
                 int statusCode = response.getStatusLine().getStatusCode();
                 if (HttpStatus.SC_OK == statusCode) {
                     fileOutputStream = new FileOutputStream(filePath.toFile());
                     IOUtils.copy(response.getEntity().getContent(), fileOutputStream);
                     System.out.println("Saved downloaded file to: " + filePath.toString());
                     downloadComplete = true;
                 } else if (HttpStatus.SC_NO_CONTENT == statusCode) {
 
                     // if the execution is being processed, the server will respond with empty response and status code 204
                     System.out.println("The server responded with 204 (no content). " +
                             "The execution is still being processed. Attempting again in 5 sec (" + attempt + "/" + DOWNLOAD_ATTEMPTS + ")");
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
 
     public static JsonObject createRequestBody() {
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
 
     public static JsonObject createRequestBody(String executionId) throws URISyntaxException, IOException {
         JsonObject jsonBody = new JsonObject();
         JsonObject filterJson = new JsonObject();
         JsonObject fieldsJson = new JsonObject();
         JsonArray testExecutions = retrieveTestExecutions(executionId).getAsJsonArray("resources");
         JsonObject testExecution = gson.fromJson(testExecutions.get(0), JsonObject.class);
         JsonArray values = new JsonArray();
         values.add(testExecution.get("id"));
         fieldsJson.add("testExecutionId", values);
         filterJson.add("fields", fieldsJson);
         jsonBody.add("filter", filterJson);
         return jsonBody;
     }
 
     private static void addDefaultRequestHeaders(HttpRequestBase request) {
         if (SECURITY_TOKEN == null || SECURITY_TOKEN.equals("MY_CONTINUOUS_QUALITY_LAB_SECURITY_TOKEN")) {
             throw new RuntimeException("Invalid security token '" + SECURITY_TOKEN + "'. Please set a security token");
         }
         request.addHeader("PERFECTO_AUTHORIZATION", SECURITY_TOKEN);
     }
 
 
     private static void addRequestBody(HttpPost request, JsonObject requestBodyJson) {
         try {
             StringEntity requestBody = new StringEntity(requestBodyJson.toString());
             request.setEntity(requestBody);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
 
+    public static String getFileName(String downloadUrl) throws URISyntaxException {
+        URI uri = new URI(downloadUrl);
+        List<NameValuePair> params = URLEncodedUtils.parse(new URI(downloadUrl), "UTF-8");
+        Optional<NameValuePair> fileName = params.stream().filter(param -> FILENAME_QUERY_PARAM.equals(param.getName())).findFirst();
+        if (fileName.isPresent()) {
+            return fileName.get().getValue();
+        } else {
+            return FilenameUtils.getName(downloadUrl);
+        }
+    }
+
     private enum TaskStatus {
         IN_PROGRESS, COMPLETE
     }
 
     private static class CreateTask {
         private String taskId;
         private TaskStatus status;
         private String url;
 
         public CreateTask() {
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
