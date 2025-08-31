import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

def call(def ServiceNow, def REQUEST_NUMBER, def SNApi) {
    try {
        println "Proceeding with RITM Validation...."
        withCredentials([usernamePassword(credentialsId: ServiceNow, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            def curlResponse = sh(returnStdout: true, script: "curl -s -k -u ${USERNAME}:'${PASSWORD}' '${SNApi}/v2/table/sc_re_item?sysparam_display_value=true&sysparam_query=number=number=${RITM_NUMBER}' | jq -r .result[]")

            if (curlResponse) {
                def json = readJSON(text: curlResponse)

                def currentTime = new Date()

                //print curl response of RITM request
                println("Requested Item: " + json.short_description)
                println("Request Current State: " + json.state)
                println("RITM Request display Value: " + json.cat_item.display_value)

                // print devops-control RITM display_value value for application

                if (json.cat_item.display_value.contains(display_value)) {
                    println "RITM is a valid Database Request. Proceeding with state validation."
                    comment = "RITM is a valid Database Request. Proceeding with state validation.\\n\\n"
                  //  sh "echo '[INFO] $comment' >> $successFile"
                 //   sh "echo '[INFO] $comment' >> $failFile"

                    if (json.state == 'Active') {
                        println "RITM is in Acti state"
                        comment = "RITM is in Acti state\\n\\n"
                    //    sh "echo '[INFO] $comment' >> $successFile"
                    //    sh "echo '[INFO] $comment' >> $failFile"
                        println("Current time: " + currentTime)
                        def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        def currentHour = currentTime.format('H',TimeZone.getTimeZone('America/New_York')).toInteger()

                    } else {
                        comment = "RITM is not Active, Please provide Active RITM for deployment"
                      //  sh "echo '[INTO] $comment >> $failFile"
                        throw new Exeception(comment)
                    }
                } else {
                        comment = "The Provided RITM is not a Database Request, Please provide a valid RITM\\n\\n"
                     //   sh "echo '[INTO] $comment >> $failFile"
                        throw new Exeception(comment)
                }
            }   else {
                        comment = "The Provided RITM is not a valid Request, Please provide a valid RITM\\n\\n"
                  //      sh "echo '[INTO] $comment >> $failFile"
                        throw new Exeception(comment)
            }
        }
        } catch (Exception e) {
            comment = "An excetion Occured during RITM validation.\\n\\n"
         //   sh"echo '[ERROR] $comment' >> $failFile"
            currentBuild.result = 'FAILURE'
            error(e)

    }
}    

    