using NUnit.Framework;

namespace ReportingTests.NUnit
{
    /// <summary>
    /// Test class
    /// </summary>
    /// <remarks> This class inherit from <see cref="PerfectoTestBox"/> </remarks>
    [TestFixture]
    class MyTestClass: PerfectoTestBox
    {
        /// <summary>
        /// Test 1 - Should Success
        /// </summary>
        /// <remarks> Navigate to google and search "PerfectoCode GitHub" and assert keyword is in the title </remarks>
        [Test]
        public void should_success()
        {

            reportiumClient.TestStep("Navigate to google and search PerfectoCode GitHub"); //Test step will be shown on the report ui
            driver.Navigate().GoToUrl("https://www.google.com");

            //locate the search bar and sendkeys
            driver.FindElementByName("q").SendKeys("PerfectoCode GitHub");

            //click on the search button
            driver.FindElementByXPath("//*[@aria-label='Google Search']").Click();

            reportiumClient.TestStep("Choose first result and validate title"); //Add as many test steps as you want
                                                                               
            var keyword = "Perfecto"; //a keyword to validate
            Assert.IsTrue(driver.FindElementByPartialLinkText(keyword).Displayed);

        }

        /// <summary>
        /// Test 2- Should fail
        /// </summary>
        /// <remarks> Asserting failure </remarks>
        [Test]
        public void should_fail()
        {
            Assert.Fail("Asserting fail");
        }

    }
}
