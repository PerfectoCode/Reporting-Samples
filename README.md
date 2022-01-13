SDK Release Notes
=============
Licensed under the Apache License, Version 2.0

### Java SDK (https://repo1.perfectomobile.com/public/repositories/maven/com/perfecto/reporting-sdk/reportium-java)
##### 2.3.4
- Updating project dependencies to fix log4j vulnerability
##### 2.3
- SDK is open source: https://github.com/PerfectoCode/reporting-java-sdk
##### 2.2.13
- Added single report for multiple executions support
##### 2.2.10
- Added license information
##### 2.2.9
- Support any type of test attachment
##### 2.2.8
- Added support for generic attachment upload in external data sdk
##### 2.2.4
- Support selenium 3.141.5
##### 2.2.3
- Bug fixes
##### 2.2.1
- Support for providing a failure reason
##### 2.2.0
- Artifact upload changes. Bug fixes
##### 2.0.2
- Fixed text artifacts upload issues
##### 2.0.1
Required cloud version - 18.10  
New features:
- Support sending tags and custom fields on reportiumClient.testEnd
##### 1.2.8
New features:
- Support Selenium version 3.13.0 and Appium version 6.1.0
##### 1.2.7
New features:
- Support custom proxy configuration
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
##### 2.5.3
Add validation on tags - only non empty string values are allowed.

##### 2.5.2
reporting-node-sdk repository is now an open source.

##### 2.5.1
- Update license to Apache2

##### 2.5.0
- Support for providing a failure reason

##### 2.4.4
removing unused code

##### 2.4.3
Update selenium-webdriver dependency to support anything above 2.0.0 

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
##### 3.0.0  
- Support sending tags and custom fields on reportiumClient.testEnd
- Support for providing a failure reason

##### 2.0.5  
New features  
Support jobBranch in job object  
Support custom fields  

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
##### 3.0.4  
- Bug fixes

##### 3.0.3  
- Bug fixes

##### 3.0.2  
- Test stop api fixes

##### 3.0.0  
- Support sending tags and custom fields on reportiumClient.testEnd
- Support for providing a failure reason

##### 2.0.0.1  
Maintenance release

##### 2.0.0.0  
Maintenance release

##### 0.1.1.4  
Required cloud version - 10.3  
New features - assert and step end commands 

##### 0.1.1  
Stable version  
