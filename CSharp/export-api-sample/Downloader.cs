using System;
using System.IO;
using System.Linq;
using System.Net;
using Newtonsoft.Json.Linq;

namespace ExportAPI
{
    public static class Downloader
    {
        public static void downloadFile(string fileName, System.Net.HttpWebRequest request, string suffix, string description)
        {
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            try
            {
                if (HttpStatusCode.OK == response.StatusCode)
                {
                    string path = fileName + suffix;
                    using (Stream output = File.OpenWrite(path))
                    using (Stream input = response.GetResponseStream())
                    {
                        input.CopyTo(output);
                    }
                    Console.WriteLine("Saved " + description + " to: " + Environment.CurrentDirectory + path);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
        }

        public static void downloadVideo(JToken testExecution)
        {
            var videos = testExecution["videos"];
            //var temp = videos[0];
            string downloadURL = videos[0]["downloadUrl"].ToString();
            string format = "." + videos[0]["format"].ToString();
            string id = testExecution["id"].ToString();
            HttpWebRequest request =
                (HttpWebRequest)WebRequest.Create(downloadURL);
            request.Headers.Add("PERFECTO_AUTHORIZATION", vars.Default.OFFLINE_TOKEN);
            downloadFile(id, request, format, "Test Execution Video");
        }

        internal static void downloadAttachments(JToken testExecution)
        {
            try
            {
                var artifacts = testExecution["artifacts"];
                if (artifacts.Count() > 0)
                {
                    foreach (var currArtifact in artifacts)
                    {
                        string type = currArtifact["type"].ToString();
                        if (type.Equals("DEVICE_LOGS"))
                        {
                            string testId = testExecution["id"].ToString();
                            string path = currArtifact["path"].ToString();
                            HttpWebRequest request =
                                (HttpWebRequest) WebRequest.Create(path);
                            request.Headers.Add("PERFECTO_AUTHORIZATION", vars.Default.OFFLINE_TOKEN);
                            downloadFile(testId, request, ".zip", "Device Logs");
                        }
                    }
                }
            }
            catch (Exception ex) { Console.WriteLine(ex.ToString());}
        }
    }
}