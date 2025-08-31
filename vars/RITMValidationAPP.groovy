import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

def call(def ServiceNow, def REQUEST_NUMBER, def SNApi) {
    try {
        println "Proceeding with RITM Validation...."

        withCredentials([usernamePassword(credentialsId: ServiceNow, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            
            // Call ServiceNow API
            def curlResponse = sh(
                returnStdout: true, 
                script: """
                    curl -s -k -u ${USERNAME}:'${PASSWORD}' \
                    '${SNApi}/v2/table/sc_req_item?sysparam_display_value=true&sysparam_query=number=${REQUEST_NUMBER}' | jq -r .
                """
            ).trim()

            if (curlResponse) {
                def json = readJSON(text: curlResponse).result[0]  // Assuming result is array

                def currentTime = new Date()

                // Print details
                println("Requested Item: " + json.short_description)
                println("Request Current State: " + json.state)
                println("RITM Request display Value: " + json.cat_item.display_value)

                // Define what valid display_value looks like
                def expectedValue = "Database Request"

                if (json.cat_item.display_value.contains(expectedValue)) {
                    println "RITM is a valid Database Request. Proceeding with state validation."
                    def comment = "RITM is a valid Database Request. Proceeding with state validation.\n\n"

                    if (json.state == 'Active') {
                        println "RITM is in Active state"
                        def comment2 = "RITM is in Active state\n\n"
                        println("Current time: " + currentTime)

                        def currentHour = currentTime.format('H', TimeZone.getTimeZone('America/New_York')).toInteger()
                        println("Current Hour (EST): " + currentHour)

                    } else {
                        def comment = "RITM is not Active, Please provide Active RITM for deployment"
                        throw new Exception(comment)
                    }
                } else {
                    def comment = "The Provided RITM is not a Database Request, Please provide a valid RITM\n\n"
                    throw new Exception(comment)
                }
            } else {
                def comment = "The Provided RITM is not a valid Request, Please provide a valid RITM\n\n"
                throw new Exception(comment)
            }
        }

    } catch (Exception e) {
        def comment = "An exception occurred during RITM validation: ${e.getMessage()}\n\n"
        println(comment)
        currentBuild.result = 'FAILURE'
        error(comment)  // ✅ pass string, not Exception object
    }
}
