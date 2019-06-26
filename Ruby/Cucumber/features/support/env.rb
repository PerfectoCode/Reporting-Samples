require_relative '../lib/utils/perfecto-utils'

# Do before each scenario
#
# saves scenario (test instance) in utils module
# that way it's available in the test
#
# Create a new webdriver instance
# Create a new reporting client
# Log a new test with scenario name
Before do |scenario|

  Utils::Cucumber.scenario = scenario

  host = 'demo.perfectomobile.com'
  securityToken =''

  capabilities = {
      :platformName => 'Android',
      :model => '',
      :platformVersion => '',
      :browserName => '',
      :browserVersion => '',
      :deviceName => '',
      :securityToken => securityToken
  }

  Utils::Device.create_device host, capabilities
  Utils::Reporting.create_reporting_client(Utils::Device.driver, 'Ruby', 'Demo', 'Perfecto') # Optional, add more tags 
  Utils::Reporting.start_new_test scenario.name, 'RubyTest' # Optional, add more tags 

end

# Do after each scenarios
#
# check for scenario status
# if scenario success generates a successful test report,
# otherwise generates failure test report
#
# unless the driver nil quiting the session
After do |scenario|

  cfe1 = CustomField.new('Ruby', 'Demo')
  cfe2 = CustomField.new('CustomField', 'Demo')
  tec = TestContext::TestContextBuilder
         .withTestExecutionTags('PerfectoEndTag1' , 'PerfectoEndTag2')
         .withCustomFields(cfe1, cfe2)
         .build()
  if scenario.failed?
    Utils::Reporting.reportiumClient.testStop(TestResultFactory.createFailure(scenario.exception.message, scenario.exception, nil), tec)
  else
    Utils::Reporting.reportiumClient.testStop(TestResultFactory.createSuccess(), tec)
  end

  puts '========================================================================================'
  puts 'report-url: ' + Utils::Reporting.reportiumClient.getReportUrl
  puts '========================================================================================'

  unless Utils::Device.driver.nil?
    Utils::Device.driver.quit
  end

end