
//Test1
fdescribe('Protractor Perfecto Demo', function () {

  it('should pass test', function () {
    browser.reportingClient.stepStart('Step 1: Navigate Google');
    browser.driver.get('https://www.google.com'); //Navigate to google.com
    browser.reportingClient.stepEnd();
    //Locate the search box element and insert text
    //Click on search button
    browser.reportingClient.stepStart('Step 2: Send Keys');
    browser.driver.findElement(by.name('q')).sendKeys('PerfectoCode GitHub');
    browser.reportingClient.stepEnd();
    browser.reportingClient.stepStart('Step 3: Click');
    browser.driver.findElement(by.css('#tsbb > div')).click();
    browser.reportingClient.stepEnd();

  });

  //This test should fail 
  it('should fail test', function () {
    browser.reportingClient.stepStart('Step 1: Navigate Google');
    browser.driver.get('https://www.google.com'); //Navigate to google.com
    browser.reportingClient.stepEnd();
    //Locate the search box element and insert text
    //Click on search button
    browser.reportingClient.stepStart('Step 2: Send Keys');
    browser.driver.findElement(by.name('q')).sendKeys('PerfectoCode GitHub');
    browser.reportingClient.stepEnd();
    browser.reportingClient.stepStart('Step 3: Click');
    browser.driver.findElement(by.css('#tsbbbsdasd > div')).click();
    browser.reportingClient.stepEnd();
  });

});
