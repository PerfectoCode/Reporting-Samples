using System;
using NUnit.Framework;
using OpenQA.Selenium.Remote;
using Reportium.Client;
using Reportium.Model;
using Reportium.Test.Result;
using Reportium.Test;
using TestContext = Reportium.Test.TestContext;
using NTestContext = NUnit.Framework.TestContext;

namespace ReportingTests.NUnit
{
    /// <summary>
    /// Test base class
    /// </summary>
    /// <remarks> 
    /// Create RemoteWebDriver and Reportium client object.
    /// Configure here what happensed before and after each test and
    /// before and after all tests.
    /// </remarks>
    [TestFixture]
    class PerfectoTestBox
    {
        //Perfecto lab username, password and host.
        private const string PERFECTO_USER = "MY_USER";
        private const string PERFECTO_TOKEN = "MY_TOKEN";
        private const string PERFECTO_HOST = "MY_HOST.perfectomobile.com";

        internal static RemoteWebDriver driver;
        internal static ReportiumClient reportiumClient;

        /// <summary>
        /// Setup once what happens before all tests
        /// </summary>
        [OneTimeSetUp]
        public void OneTimeSetUp()
        {
            //DesiredCapabilities capabilities = new DesiredCapabilities(browserName, string.Empty, new Platform(PlatformType.Any));
            DesiredCapabilities capabilities = new DesiredCapabilities();

            //Provide your Perfecto lab user and pass
            //capabilities.SetCapability("user", PERFECTO_USER);
            capabilities.SetCapability("securityToken", PERFECTO_TOKEN);

            //Device capabilities
            capabilities.SetCapability("platformName", "Android");

            //Create RemoteWebDriver
            var url = new Uri(string.Format("http://{0}/nexperience/perfectomobile/wd/hub/fast", PERFECTO_HOST));
            driver = new RemoteWebDriver(url, capabilities);
            driver.Manage().Timeouts().ImplicitWait = TimeSpan.FromSeconds(15);

            //Initialize driver
            reportiumClient = clientCreator();
        }

        /// <summary>
        /// Initialize ReportiumClient with perfecto execution context. 
        /// </summary>
        /// <returns> PerfectoReportiumClient object </returns>
        private static ReportiumClient clientCreator()
        {
            PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .WithProject(new Project("My project", "2.1")) //optional 
                .WithContextTags(new[] { "sample tag1", "sample tag2", "c#" }) //optional 
                .WithCustomFields(new[] { new CustomField("tester", "john") }) //optional 
                .WithJob(new Job("Job name", 12345, "master")) //optional 
                .WithWebDriver(driver)
                .Build();
             
            return PerfectoClientFactory.CreatePerfectoReportiumClient(perfectoExecutionContext);
        }

        /// <summary>
        /// Setup what happens before each test
        /// </summary>
        [SetUp]
        public void SetUp()
        {
            //Start a new reporting for the test with the full test name
            reportiumClient.TestStart(NTestContext.CurrentContext.Test.FullName , new TestContext("My First NUnit test" , "Tag1" , "Tag2"));
        }

        /// <summary>
        /// TearDown the test (each one of them)
        /// </summary>
        [TearDown]
        public void afterTest()
        {
            try
            {
                var status = NTestContext.CurrentContext.Result.Outcome.Status.ToString();

                //test success, generates successful reporting
                if (status.Equals("Passed"))
                {
                    reportiumClient.TestStop(TestResultFactory.CreateSuccess());
                }
                //test fail, generates failure repostiung
                else
                {
                    var Message = NTestContext.CurrentContext.Result.Message;
                    var trace = NTestContext.CurrentContext.Result.StackTrace;
                    Message = Message + ". Stack Trace:" + trace;
                    reportiumClient.TestStop(TestResultFactory.CreateFailure(Message, null));
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }
        }

        /// <summary>
        /// Class cleanup (after all tests)
        /// </summary>
        /// <remarks> 
        /// This method runs after all the tests in the class are finished 
        /// Opens a browser with the report url and quit the driver
        /// </remarks>
        [OneTimeTearDown]
        public static void tearDown()
        {
            //retrieve the test report 
            try
            {
                driver.Close();
                var url = reportiumClient.GetReportUrl();
                Console.WriteLine(url);

                //Optional open browser after test finished: 
                System.Diagnostics.Process.Start(url.ToString());
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }

            //Close connection and ends the test
            driver.Quit();
        }

    }
}
