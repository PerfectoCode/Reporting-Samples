# Protractor + Jasmine
The project demonstrates adding Reportium calls to [Protractor](http://www.protractortest.org/#/) tests written with [Jasmine](http://jasmine.github.io/).

:information_source: Click [here](http://developers.perfectomobile.com/display/PD/Simple+Browsing+Protractor+Code+Sample) for a guide to get started with Protractor.

## Getting started
Install NodeJS dependencies with this command:

> npm install

## Running the test
Update your Perfecto credentials in [conf.js](conf.js).

You can run [spec.js](spec.js) with this command:

> npm test

## What's in the box?
A [custom Jasmine reporter](http://jasmine.github.io/2.4/custom_reporter.html) in configured in conf.js.

This reporter automatically reports the start and end of test executions, 
to provide seamless integration and remove boilerplate code from your test scripts.

Test scripts can then be enriched with reporting of functional test steps by using 
> browser.reportingClient.testStep('Step description comes here');

## 💡 important note
to be able to report back test status in specDone or suiteDone, Jasmine callbacks
please add this code at end of any describe block

```
afterAll(function(done){
    process.nextTick(done); // let all current waiting events to complete
});
```
for more details see: // https://github.com/angular/protractor/issues/1938

