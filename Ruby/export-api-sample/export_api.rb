require 'rest-client'
require 'json'

# The Perfecto Continuous Quality Lab you work with
CQL_NAME = 'branchtest'

# The reporting Server address depends on the location of the lab. Please refer to the documentation at
# http://developers.perfectomobile.com/display/PD/Reporting#Reporting-ReportingserverAccessingthereports
# to find your relevant address
# For example the following is used for US:
REPORTING_SERVER_URL = 'https://' + CQL_NAME + '.reporting.perfectomobile.com'

# See http://developers.perfectomobile.com/display/PD/Using+the+Reporting+Public+API on how to obtain an Offline Token
# In this case the offline token is stored as a env variable
OFFLINE_TOKEN = ENV['token']

CQL_SERVER_URL = 'http://' + CQL_NAME + '.perfectomobile.com'

HEADER = {'PERFECTO_AUTHORIZATION' => OFFLINE_TOKEN}

def retrieve_tests_executions
  url = REPORTING_SERVER_URL + '/export/api/v1/test-executions'
  payload = {
      :'startExecutionTime[0]' => ((Time.now.to_f * 1000).to_i - (30 * 24 * 60 * 60 * 1000)),
      :'endExecutionTime[0]' => (Time.now.to_f * 1000).to_i
  }
  RestClient::Request.execute(method: :get, url: url,
                              payload: payload, headers: HEADER)
end

def retrieve_test_commands test_id
  url = REPORTING_SERVER_URL + '/export/api/v1/test-executions/' + test_id + '/commands'
  RestClient::Request.execute(method: :get, url: url, headers: HEADER)
end

def download_execution_summary_report driver_execution_id
  url = REPORTING_SERVER_URL + '/export/api/v1/test-executions/pdf?externalId[0]=' + driver_execution_id
  download_file_routine driver_execution_id + '.pdf', url
end

def download_test_report test_id
  url = REPORTING_SERVER_URL + '/export/api/v1/test-executions/pdf/' + test_id
  download_file_routine test_id + '.pdf', url
end

def download_video test_execution
  videos = test_execution['videos']
  if videos.size > 0
    video = videos[0]
    download_url = video['downloadUrl']
    video_format = '.' + video['format']
    test_id = test_execution['id']
    download_file_routine test_id + video_format, download_url
  end
end

def download_attachments test_execution
  artifacts = test_execution['artifacts']
  if artifacts.size > 0
    artifacts.each {|artifact|
      type = artifact['type']
      if type == 'DEVICE_LOGS'
        test_id = test_execution['id']
        path = artifact['path']
        download_file_routine test_id + '.zip', path
      end
    }
  end
end

def download_file_routine name, url
  File.open(name, 'w') {|f|
    block = proc {|response|
      response.read_body do |chunk|
        f.write chunk
      end
    }
    RestClient::Request.execute(method: :get, url: url, headers: HEADER, block_response: block)
  }
end

begin
  # Retrieve a list of the test executions in your lab (as a json)
  resources = (JSON.parse retrieve_tests_executions)['resources']
  test_execution = resources[1] # retrieve a test execution
  driver_execution_id = test_execution['externalId'] # retrieve the execution id
  test_id = test_execution['id'] # retrieve a single test id

  test_commands = retrieve_test_commands test_id # retrieve a list of test commands
  # Do something with the test commands ...

  # download execution report in pdf format
  download_execution_summary_report driver_execution_id

  # downloads the test report in pdf format
  download_test_report test_id

  # downloads video
  download_video test_execution

  # Download attachments such as device logs, vitals or network files (relevant for Mobile tests only)
  download_attachments test_execution

rescue Exception => e
  puts 'Error: ' + e.to_s
  puts e.backtrace
end