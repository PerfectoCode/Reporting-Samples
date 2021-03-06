from perfecto import TestResultFactory
from selenium.common.exceptions import NoSuchElementException

from Conf import TestConf
import unittest
import time
from selenium.webdriver.common.by import By


class ReportingTests(TestConf):

    def test_navigation(self):

        self.reporting_client.step_start("sanity")
        self.reporting_client.step_end()
        self.reporting_client.step_start('Step1: Navigate to google')
        self.driver.get('https://google.com')
        self.reporting_client.step_end()
        self.reporting_client.step_start('Step2: Search PerfectoCode GitHub repo')

        try:
            element = self.driver.find_element(By.NAME, 'q')
            element.send_keys('PerfectoCode GitHub')
            element.submit()
            time.sleep(2)
            print "end of sleep"
            self.reporting_client.step_end()
            self.reporting_client.step_start('Step3: Navigate to the first search result')
            element = self.driver.find_element_by_xpath('//*[text()="PerfectoCode/Samples: Product Samples - GitHub"]')#
            element.click()

            self.reporting_client.step_end()
            self.reporting_client.step_start('Step4: Validate title contains the work Perfecto')
            assert 'Perfecto' in self.driver.title
            self.reporting_client.step_end()
        except NoSuchElementException as ex:
            self.reporting_client.step_end()
            self.reporting_client.test_stop(TestResultFactory.create_failure("NoSuchElementException", ex))
            print ex
        self.reporting_client.test_stop(TestResultFactory.create_success())


if __name__ == '__main__':
    unittest.main()
