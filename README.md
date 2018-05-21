SDK Release Notes
=============

### Java SDK (https://repository-perfectomobile.forge.cloudbees.com/public/com/perfecto/reporting-sdk/reportium-java)
##### 1.2.4
Required cloud version - 18.2  
New features:
- Ability to upload text attachments and get them via export-api
- When using asynchronous command upload method, you are able to wait for upload completion by using the "close()" method

Compiled with selenium 3.12.0

Compiled with appium 5.0.4
##### 1.2.0
Required cloud version - 10.12  
New features - Ability to upload commands with screenshot attachments
##### 1.1.22
Required cloud version - 10.11  
New features - Support custom fields in PerfectoExecutionContext and ImportExecutionContext in addition to TestContext
##### 1.1.21
Required cloud version - 10.9  
New features - Support feature branch and custom fields on tests
##### 1.1.19
Required cloud version - 10.3  
New features - Ability to import executions directly to Reportium using ReportiumImportClient (without using RemoteWebDriver)
##### 1.1.17
Required cloud version - 10.3  
New features - Ability to extend ReportiumTestNgListener
##### 1.1.16
Required cloud version - 10.3  
New features - Remove dependency on specific selenium version   
##### 1.1.15
Required cloud version - 10.3  
New features - assert and step end commands  

##### 1.1.10  
Stable version
   
***
### Javascript SDK (https://www.npmjs.com/package/perfecto-reporting)
##### 2.4.1
Fix: handle undefined in Tags and Customfields

##### 2.4.0
PerfectoExecutionContext support jobBranch in job object

##### 2.2.1
Validation on `status` param in `reportiumClient.reportiumAssert(message, status)` was added.

##### 2.2.0
*New features* - Support custom fields in PerfectoExecutionContext and in PerfectoTestContext

*Changes* - `reportingClient.testStart(testName, tags)` is now deprecated,

use instead `reportingClient.testStart(testName, PerfectoTestContext<tags, customFields>)`
##### 2.1.0  
The following methods updated and now returning Promise:
testStart, testStop, testStep, stepStart, stepEnd, reportiumAssert
To handle cases such as synchronization between invoking testEnd and driver.close() commands.
Update selenium-driver to 3.6.0, which means in turn that the required version of node is >= 6.9.0.


##### 2.0.0  
Required cloud version - 10.3  
New features - assert and step end commands  
  
***
### Ruby SDK (https://rubygems.org/gems/perfecto-reporting)
##### 2.0.2
Maintenance release

##### 2.0.1  
Documentation changes 

##### 2.0.0  
Required cloud version - 10.3  
New features - assert and step end commands  

##### 1.1.3  
Stable version  
  
***
### C# SDK (https://www.nuget.org/packages/Perfecto-Reporting)
##### 2.0.0  
Required cloud version - 10.3  
New features - assert and step end commands  

##### 1.0.1  
Stable version  
  
***
### Python SDK (https://pypi.python.org/pypi/perfecto)
(python 2.7.X only).
##### 2.0.0.1  
Maintenance release

##### 2.0.0.0  
Maintenance release

##### 0.1.1.4  
Required cloud version - 10.3  
New features - assert and step end commands 

##### 0.1.1  
Stable version  
