import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

def call(def ServiceNow, def REQUEST_NUMBER, def HttpProxy, def SNApi) {
    try {
        println "Proceeding with Change Request Validation"

        withCredentials([usernamePassword(credentialsId: ServiceNow, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            def curlResponse = sh(returnStdout: true, script: "curl -s -k -u ${USERNAME}:'${PASSWORD}' -x ${HttpProxy} '${SNApi}'/v2/table/change_request?sysparm_query=number=${CR_NUMBER}&sysparamdisplay_value=true$' | jq -r '.result[]")

            if (curlResponse) {
                def json = readJSON text: curlResponse

                def currentTime = new Date()
                def plannedStartDateTime = json.u_disired_start_date //start time from servicenow
                def plannedEndDateTime = json.u_disired_end_date //end time from servicenow

                //print Change Request curl response values
                println("Change Request Current State: " + json.state)
                println("Planned Start Date : " + json.u_disired_start_date)
                println("Planned End Date : " + json.u_disired_end_date)
                println("Change Request Environment: " + json.u_environment)
                println("Change Request Service: " + json.u_service.replaceAll("\\s",""))
                println("Change Request Sub Service: " + json.u_sub_service.toLowerCase().replaceAll("\\s",""))
                pinrtln("Change Request Type: " +json.type)

                if ("${json.state}".contans("Closed") || "${json.state}".contains("Implemented")) {
                    comment = "change request is in ${json.satate}.\\n"
                    sh "echo '[ERROR] $comment' >> $failFile"
                    throw new Exception(comment)
                }

                if (json.u_service != "To - Database Ops" && json.u_service.replaceAll("\\s","") != "TE-PLatform") {
                    comment = "Change Request Service ${json.u_service} does not match with Database Request Catagory.\\n\\n"
                    sh "echo '[ERROR] $comment' >> $failFile"
                    throw new Exception(comment)
                }

                if (!"${envir}".toLowerCase().contains("prod") && json.u_environment("Production")) {
                    comment = "Change Request Environment ${json.u_environment} does not match with the selected environment ${envir}\\n\\n"
                    sh "echo '[ERROR] $comment' >> $failFile"
                    throw new Exception(comment)
                }

                if ("${envir}".toLowerCase().contains("prod") && !"${json.u_environment}".contains("Production")) {
                    comment = "Change Request Environment ${json.u_environment} does not match with the selected environment ${envir}\\n\\n"
                    sh "echo '[ERROR] $comment' >> $failFile"
                    throw new Exception(comment)
                }
                println("Change Request Type :" + json.type)

                if (json.u_sub_service.toLowerCase().contains("${DBType}".toLowerCase())) {
                    println "CR is Valid ${json.u_sub_service.toLowerCase()}  Database Request. Proceeding with Further Validation."

                    if (json.type == "Emergency - Break Fix" && !json.state == "Implement") {

                        printf "Note: Change Request has been Classified as Emergency - Break Fix request in ${json.state} state"
                        comment = "Note: Change Request has been Classified as Emergency - Break Fix request in ${json.state} state\\n\\n"
                        sh"echo '[INFO] $comment' >> $successFile"

                    } else if (json.state == 'Implement') {
                        println "Change Request is in Implement state. Proceeding for timeframe validation"
                        comment = "Change Request is in Implement state. Proceeding for timeframe validation\\n\\n"
                        sh"echo '[INFO] $comment' >> $successFile"
                        sh"echo '[INFO] $comment' >> $failFile"

                        //define the date format for parsing

                        def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        def currentHour = currentTime.format('H',TimeZone.getTimeZone("America/New_York")).toInteger()

                        println("Current time : " + currentTime)

                        def startTime = dateFormat.parse(plannedStartDateTime)
                        def endTime = dateFormat.parse(plannedEndDateTime)

                        //compare current time with start and end time

                        if (currentTime.after(startTime) && currentTime.before(endTime)) {
                            println "Current time is with in the scheduled change Window"
                            comment = "Current time is with in the scheduled change Window\\n\\n"
                            sh"echo '[INFO] $comment' >> $successFile"
                            sh"echo '[INFO] $comment' >> $failFile"

                            if ("${envir}".toLowerCase().contains("prod") && "${REQUEST_TYPE}" == "DEPLOYMENT") {

                                int businesHourStart = 6
                                int businesHourEnd = 12

                                    if (currentHour >= businesHourStart || currentHour <= businesHourEnd) {
                                        println("\033[1;32mThis change is being triggered in business Hour...\033[0m")
                                        timeout(time: 2, unit: "HOURS") {
                                            input id: 'Deploy', message: '\033mYou are trying to deploy this change during business hours. Please approve if you wish to [roceed with deployment\033[0m', ok: 'Approve'
                                        }
                                        comment = "This change is being triggered in business Hours\\n\\n"
                                        sh"echo '[INFO] $comment' >> $successFile"
                                        sh"echo '[INFO] $comment' >> $failFile"
                                    }
                            }

                        } else {
                            println "Current time is outside the scheduled Change Window"
                            comment = "Current time is outside the scheduled Change Window\\n\\n"
                            sh"echo '[ERROR] $comment' >> $failFile"
                        }
                    } else {
                        comment = "Change Requet is in ${json.state}.\\n"
                        sh"echo '[INFO] $comment' >> $failFile"
                        throw new Exception(comment)
                    }
                } else {
                        println "${DBType} Pipeline cannot be used to deploy ${json.u_sub_service} change request"
                        comment = "${DBType} Pipeline cannot be used to deploy ${json.u_sub_service} change request\\n\\n"
                        sh"echo '[ERROR] $comment' >> $failFile"
                        throw new Exception(comment)
                     }

            } else {
                println "The provided Change Request is not valid. Please provide valid change request"
                comment = "The provided Change Request is not valid. Please provide valid change request\\n\\n"
                sh"echo '[ERROR] $comment' >> $failFile"
                throw new Exception(comment)
            }

        }
    } catch (Exception e) {
        comment = "An exception occurred during change Request validation\\n\\n"
        sh"echo '[ERROR] $comment' >> $failFile"
        currentBuild.result = 'FAILURE'
        throw new Exception(comment)
    }
}
