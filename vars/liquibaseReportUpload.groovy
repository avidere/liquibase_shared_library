def app() {
    def table_type

    withCredentials([usernamePassword(credentialsId: 'ServiceNow', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        if ("${SN_NUMBER}".contains("CHG")) {
            table_type = "change_request"
        } else {
            table_type = "sc_req_item"
        }

        //fetching the sys_id of the ticket
        def curlResponse = sh(returnStdout: true, script: "curl -s -k -u ${USERNAME}:'${PASSWORD}' -X ${HttpProxy} '${SNApi}/v2/table/${table_type}?sysparm_query=number=${SN_NUMBER}&sysparam_display_value=true' | jq -r .result[]")
        def json = readJSON text: curlResponse
        sn_request_sys_id = json.sys_id
          println sn_request_sys_id

          liquibasesqlfile = "drift_rport.html"
          println liquibasesqlfile

          def sn_upload = sh(returnStdout: true, script: """curl -X POST -u ${USERNAME}:'${PASSWORD}' -X ${HttpProxy} -H 'Content-Type: text/html' '$${SNAPiupload}?table_name=${table_type}&table_sys_id=${sn_request_sys_id}&file_name=${liquibasesqlfile}' --data-binary '@${WORKSPACE}/${liquibasesqlfile}' """)
            println sn_upload
         def sn_outputValidationupload = sh(returnStdout: true, script: """curl -X POST -u ${USERNAME}:'${PASSWORD}' -X ${HttpProxy} -H 'Content-Type: text/html' '$${SNAPiupload}?table_name=${table_type}&table_sys_id=${sn_request_sys_id}&file_name=outputValidationReport.txt' --data-binary '@${WORKSPACE}/outputValidationReport.txt' """)
            println sn_outputValidationupload
    }
}

def errorLog() {
    def table_type

    withCredentials([usernamePassword(credentialsId: 'ServiceNow', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        if ("${SN_NUMBER}".contains("CHG")) {
            table_type = "change_request"
        } else {
            table_type = "sc_req_item"
        }

        //fetching the sys_id of the ticket
        def curlResponse = sh(returnStdout: true, script: "curl -s -k -u ${USERNAME}:'${PASSWORD}' -X ${HttpProxy} '${SNApi}/v2/table/${table_type}?sysparm_query=number=${SN_NUMBER}&sysparam_display_value=true' | jq -r .result[]")
        def json = readJSON text: curlResponse
        sn_request_sys_id = json.sys_id
          println sn_request_sys_id

        if (DBType == 'oracle') {
            liquibasesqlfile = "spoolError_${REQUEST_NUMBER}.txt"
            println liquibasesqlfile

            def spool_upload = sh(returnStdout: true, script: """curl -X POST -u ${USERNAME}:'${PASSWORD}' -X ${HttpProxy} -H 'Content-Type: text/plain' '$${SNAPiupload}?table_name=${table_type}&table_sys_id=${sn_request_sys_id}&file_name=${liquibasesqlfile}' --data-binary '@${WORKSPACE}/temp/${liquibasesqlfile}' """)
            println spool_upload
            }

          liquibasesqlfile = "errorLog_${BUILD_NUMBER}.txt"
          println liquibasesqlfile

          def sn_upload = sh(returnStdout: true, script: """curl -X POST -u ${USERNAME}:'${PASSWORD}' -X ${HttpProxy} -H 'Content-Type: text/plain' '$${SNAPiupload}?table_name=${table_type}&table_sys_id=${sn_request_sys_id}&file_name=${liquibasesqlfile}' --data-binary '@${WORKSPACE}/${liquibasesqlfile}' """)
            println sn_upload
    }
}
