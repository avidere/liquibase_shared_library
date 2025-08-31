def call() {
    withCredentials([usernamePassword(credentialsId: ServiceNow, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            if ("${REQUEST_NUMBER}".contains('CHG')) {
                table_type = 'change_request'
            } else if ("${REQUEST_NUMBER}".contains('REQ')) {
                table_type = 'sc_request'
            } else {
                table_type = 'sc_req_item'
            }
            def curlResponse = sh(returnStdout: true, script: "curl -s -k -u ${USERNAME}:'${PASSWORD}' '${SNApi}/table/${table_type}?sysparam_query=number=${REQUEST_NUMBER}&sysparam_display_value=true&' | jq -r .result[]")

            def json = readJSON(text: curlResponse)
            sn_request_sys_id = json.sys_id

            content = sh(returnStdout: true, script: "echo \$(cat ${WORKSPACE}/ServiceNow_PipelineSummary.txt)")
            comment = content.trim()

            def status = "{ \"comments\": \"${comment}\" }"

            def sn_comment = sh(returnStdout: true, script:curl -X POST -s -k -u "admin:A@vinash2412" "https://dev313863.service-now.com/api/now/table/sys_journal_field" -H "Content-Type: application/json" \
                -d '{
                        "element_id": '${sn_request_sys_id}',
                        "element": "comments",
                        "value": '${status}'
                    }')

          //  def sn_comment = sh(returnStdout: true, script: """curl -X PATCH -s -k -u $USERNAME:'$PASSWORD' ${SNApi}/table/${table_type}/${sn_request_sys_id} -H 'Content-Type: application/json' -d '${status}' """)
    }
}
