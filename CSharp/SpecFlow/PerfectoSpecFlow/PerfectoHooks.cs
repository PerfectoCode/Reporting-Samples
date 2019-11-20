using OpenQA.Selenium;
using OpenQA.Selenium.Appium;
using OpenQA.Selenium.Appium.Android;
using OpenQA.Selenium.Appium.iOS;
using OpenQA.Selenium.Remote;
using Reportium.Client;
using Reportium.Model;
using Reportium.Test;
using Reportium.Test.Result;
using System;
using TechTalk.SpecFlow;

namespace PerfectoSpecFlow
{
    [Binding]
    public class PerfectoHooks
    {
        protected static AppiumDriver<IWebElement> driver;
        private static ReportiumClient reportingClient;

        //Perfecto Lab credentials
        const string PerfectoUser = "My_User";
        const string PerfectoToken = "MySecurity_Token";
        const string PerfectoHost = "My_Host.perfectomobile.com"; 
        
        /**
         * BeforeFeature method 
         * Creating appium driver and reporting client.
         **/  
        [BeforeFeature]
        public static void beforeFeature()
        {

           
            var desiredCaps = new AppiumOptions
            {
                PlatformName = "Android"
               // PlatformName = "iOS"
            };
            //Credentials from setting file named properties
            desiredCaps.AddAdditionalCapability("user", PerfectoUser);
            desiredCaps.AddAdditionalCapability("securityToken", PerfectoToken);


            desiredCaps.AddAdditionalCapability("platformName", "Android");
            desiredCaps.AddAdditionalCapability("model", "Galaxy S6");

            Uri url = new Uri(string.Format("http://{0}/nexperience/perfectomobile/wd/hub/fast", PerfectoHost));

            if (desiredCaps.ToCapabilities().GetCapability("platformName").Equals("Android"))
            {
                driver = new AndroidDriver<IWebElement>(url, desiredCaps);
               
            }
            else
            {
                driver = new IOSDriver<IWebElement>(url, desiredCaps);
            }
            driver.Manage().Timeouts().ImplicitWait = TimeSpan.FromSeconds(15);
           
            reportingClient = CreateReportingClient();
        }

        /**
         * BeforeScenario
         * Starting a new test agiants reporting server.
         **/ 
        [BeforeScenario]
        public static void beforeScenario()
        {
            reportingClient.TestStart(ScenarioContext.Current.ScenarioInfo.Title, new TestContext(ScenarioContext.Current.ScenarioInfo.Tags));
        }

        /**
         * BeforeStep
         * Logging a new test step to reporting client.
         **/ 
        [BeforeStep]
        public static void beforeStep()
        {
            reportingClient.TestStep(ScenarioStepContext.Current.StepInfo.Text);
        }

        /**
         * AfterScenario
         * Stoping the test and report to reporting server the test's status.
         * If test failed providing the exception.
         **/
        [AfterScenario]
        public static void afterScenario()
        {
            Exception scenarioExpection = ScenarioContext.Current.TestError;

            if (scenarioExpection == null)
            {
                reportingClient.TestStop(TestResultFactory.CreateSuccess());
            }
            else
            {
                reportingClient.TestStop(TestResultFactory.CreateFailure(scenarioExpection.Message, scenarioExpection));
            }
        }

        /**
         * Closing the driver and providing the test's URL for review. 
         **/ 
        [AfterFeature]
        public static void afterFeature()
        {
            Console.WriteLine("Report-Url: " + reportingClient.GetReportUrl());
            driver.Close();
            driver.Quit();
        }

        private static ReportiumClient CreateReportingClient()
        {
            PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .WithProject(new Project("My first project", "v1.0")) //optional 
                .WithContextTags(new[] { "tag1", "tag2", "tag3" }) //optional 
                .WithJob(new Job("Job name", 12345)) //optional 
                .WithWebDriver(driver)
                .Build();
            return PerfectoClientFactory.CreatePerfectoReportiumClient(perfectoExecutionContext);
        }

    }
}
