import unittest
import urllib3
import os
from selenium import webdriver
from perfecto import PerfectoExecutionContext,TestResultFactory,TestContext,PerfectoReportiumClient,model


class TestConf(unittest.TestCase):
    def __init__(self, *args, **kwargs):
        #Suppress InsecureRequestWarning: Unverified HTTPS request is being made 
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        
        self.securityToken = os.environ['OFFLINE_TOKEN']
        self.host = os.environ['LAB'] + '.perfectomobile.com'
        self.driver = None
        self.reporting_client = None

        super(TestConf, self).__init__(*args, **kwargs)

    def setUp(self):
        capabilities = {
            'platformName': 'Android',
            'model': 'Galaxy S9',
            'securityToken' : self.securityToken
        }
        print ('capabilities:' + capabilities);
        self.driver = webdriver.Remote('https://' + self.host + '/nexperience/perfectomobile/wd/hub', capabilities)
        self.create_reporting_client()
        cf1 = model.CustomField('key1', 'Tvalue1')
        cf2 = model.CustomField('key2', 'Tvalue2')
        self.reporting_client.test_start(self.id(),
                                         TestContext(customFields=[cf1, cf2], tags=['Tag1', 'Tag2', 'Tag3']))
 
    def run(self, result=None):
        self.currentResult = result  # remember result for use in tearDown
        unittest.TestCase.run(self, result)  # call superclass run method

    def tearDown(self):
        cf1 = model.CustomField('key1', 'val1')
        cf2 = model.CustomField('key2', 'val2')
        tec = TestContext(customFields=[cf1, cf2], tags=['test1', 'test2'])
        try:
            if self.currentResult.wasSuccessful():
                self.reporting_client.test_stop(TestResultFactory.create_success())
            else:
                self.reporting_client.test_stop(TestResultFactory.create_failure(self.currentResult.errors,
                                                                                 self.currentResult.failures),
                                                                                 tec)
            # Print report's url
            print 'Report-Url: ' + self.reporting_client.report_url() + '\n'
        except Exception as e:
            print e.message

        self.driver.quit()

    def create_reporting_client(self):
        cf1 = model.CustomField('key1','Evalue1')
        cf2 = model.CustomField('key3', 'Evalue3')

        perfecto_execution_context = PerfectoExecutionContext(webdriver=self.driver,
                                                              tags=['Etag0', 'Etag1', 'Etag2', 'Etag3'],
                                                              job=model.Job('Jobname', 12, 'branch_name'),
                                                              project=model.Project('project_name', 2.0),
                                                              customFields=[cf1, cf2])
        
        self.reporting_client = PerfectoReportiumClient(perfecto_execution_context)
