def call() {
            withCredentials([usernamePassword(credentialsId: ServiceNow, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            if ( "${REQUEST_NUMBER}".contains("CHG")) {
                table_type = "change_request"
            } else {
                table_type = "sc_request" 
            }
            def curlResponse = sh(returnStdout: true, script: "curl -s -k -u admin:A@vinash2412 '${SNApi}/table/${table_type}?sysparam_query=number=number=${REQUEST_NUMBER}&sysparam_display_value=true&' | jq -r .result[]")
            
            def json = readJSON(text: curlResponse)
            sn_request_sys_id = json.sys_id

            content = sh(returnStdout: true, script: "echo \$(cat ${WORKSPACE}/serviceNow_PipelineSummary.txt)")
            comment = content.trim()

            def status = "{ \"comments\": \"${comment}\" }"

            def sn_comment = sh(returnStdout: true, script: """curl -X PATCH -s -k -u $USERNAME:'$PASSWORD' ${SNApi}/table/${table_type}/${sn_request_sys_id} -H 'Content-Type: application/json' -d '${status}' """)

        }
}