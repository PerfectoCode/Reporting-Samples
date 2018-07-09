require_relative 'PerfectoTest.rb'

# Test class
#
# Add here more tests here.
# configuration for the tests can be found in PerfectoTest class.
class MyTests < PerfectoTest

  def test_navigation_to_perfecto_code_should_fail
    begin
      @reportiumClient.stepStart('Step1: navigate to google')
      @driver.get 'https://google.com'
	  @reportiumClient.stepEnd()

      @reportiumClient.stepStart('Step2: Searching for PerfectoCode')
      @driver.find_element(:name => 'q').send_keys('PerfectoCode GitHub')

      #click search button
      # @driver.find_element(:id => 'tsbb').click
	  @driver.find_element(:class => 'Tg7LZd').click

      #click the first search result
      @driver.find_element(:css => '#rso > div:nth-of-type(1) > div.ZINbbc.xpd > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(1) > a.C8nzq.JTuIPc').click
	  @reportiumClient.stepEnd()

      @reportiumClient.stepStart('Step3: Asserting page title contains keyword')
      assert(@driver.title.include? 'Perfecto')
	  @reportiumClient.stepEnd()

    # Logging the exception into the reporting client
    rescue Exception => exception
      @exception = exception
      raise exception
    end
  end

end