import unittest
from selenium import webdriver
from perfecto import PerfectoExecutionContext,TestResultFactory,TestContext,PerfectoReportiumClient


class TestConf(unittest.TestCase):
    def __init__(self, *args, **kwargs):
        self.user = 'My_User'
        self.password = 'My_Pass'
        self.host = 'My_Host.perfectomobile.com'
        self.driver = None
        self.reporting_client = None

        super(TestConf, self).__init__(*args, **kwargs)

    def setUp(self):
        capabilities = {
            'platformName': 'Android',
            'deviceName': '',
            'user': self.user,
            'password': self.password
        }
        self.driver = webdriver.Remote('https://' + self.host + '/nexperience/perfectomobile/wd/hub', capabilities)
        self.create_reporting_client()
        cf2 = CustomField('key1', 'Tvalue1')
        cf2 = CustomField('key2', 'Tvalue2')
        self.reporting_client.test_start(self.id(),
                                         TestContext(cusFields=[cf1, cf2], test_tags=['Tag1', 'Tag2', 'Tag3']))
 
    def run(self, result=None):
        self.currentResult = result  # remember result for use in tearDown
        unittest.TestCase.run(self, result)  # call superclass run method

    def tearDown(self):
        try:
            if self.currentResult.wasSuccessful():
                self.reporting_client.test_stop(TestResultFactory.create_success())
            else:
                self.reporting_client.test_stop(TestResultFactory.create_failure(self.currentResult.errors,
                                                                                 self.currentResult.failures))
            # Print report's url
            print 'Report-Url: ' + self.reporting_client.report_url() + '\n'
        except Exception as e:
            print e.message

        self.driver.quit()

    def create_reporting_client(self):
        cf1 = CustomField('key1','Evalue1')
        cf2 = CustomField('key3', 'Evalue3'])

        perfecto_execution_context = PerfectoExecutionContext(webdriver=self.driver,
                                                              context_tags=['Etag0', 'Etag1', 'Etag2', 'Etag3'],
                                                              job=Job('Jobname', '12', 'branch_name'),
                                                              project=Project('project_name', '2.0'),
                                                              cusFields=[cf1, cf2])
        
        self.reporting_client = PerfectoReportiumClient(perfecto_execution_context)
