using NUnit.Framework;
using TechTalk.SpecFlow;

namespace PerfectoSpecFlow
{
    [Binding]
    public class PerfectoFeaturesSteps: PerfectoHooks
    {
        [Given(@"I navigate to (.*) search page")]
        public void GivenINavigateToGoogleSearchPage(string site)
        {
            var url = string.Format("https://{0}.com", site);
            driver.Navigate().GoToUrl(url);
        }
        
        [Given(@"I search for (.*)")]
        public void GivenISearchForPerfectoCodeGitHub(string valueToSearch)
        {
            //locate the search bar and sendkeys
            driver.FindElementByName("q").SendKeys(valueToSearch);

            //click on the search button
            driver.FindElementByXPath("//*[@aria-label='Google Search']").Click();
            
        }
        
        
        [Then(@"I validate that (.*) is in the page")]
        public void ThenIValidateThatPerfectoIsInThePageSTitle(string val)
        {
            
            var keyword = "Perfecto"; //a keyword to validate
            Assert.IsTrue(driver.FindElementByPartialLinkText(val).Displayed);
        } 
    }
}
