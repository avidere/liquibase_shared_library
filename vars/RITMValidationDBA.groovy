def call(def ServiceNow, def REQUEST_NUMBER, def SNApi) {
    def comment = ''
    try {
        println 'Proceeding with RITM Validation....'
        withCredentials([usernamePassword(credentialsId: ServiceNow, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            def curlResponse = sh(returnStdout: true, script: "curl -s -k -u ${USERNAME}:'${PASSWORD}' '${SNApi}/table/sc_request?sysparam_display_value=true&sysparam_query=number=number=${REQUEST_NUMBER}' | jq -r .result[]")

            if (curlResponse) {
                def json = readJSON(text: curlResponse)

                def currentTime = new Date()

                //print curl response of RITM request
                println('Requested Item: ' + json.short_description)
                println('Request Current State: ' + json.request_state)
                println('Request Approval State: ' + json.approval)
               // println("RITM Request display Value: " + json.cat_item.display_value)

                // print devops-control RITM display_value value for application
                if (json.short_description.contains('Database Service Request')) {
                    //if (json.cat_item.display_value.contains(display_value)) { its in real request
                    println 'RITM is a valid Database Request. Proceeding with state validation.'
                    comment = 'RITM is a valid Database Request. Proceeding with state validation.\\n\\n'
                    sh "echo '[INFO]  ${comment}' >> $successFile"
                    sh "echo '[INFO]  ${comment}' >> $failFile"

                    if (json.approval == 'approved') {
                        // if (json.state == 'Active') { for DBA the commented one is working
                        println 'RITM is in Active state'
                        comment = 'RITM is in Active state\\n\\n'
                        sh "echo '[INFO]  ${comment}' >> $successFile"
                        sh "echo '[INFO]  ${comment}' >> $failFile"
                        println('Current time: ' + currentTime)
                    //  def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    //  def currentHour = currentTime.format('H',TimeZone.getTimeZone('America/New_York')).toInteger()

                        if ("${envir}".toLowerCase().contains('prod') && "${REQUEST_TYPE}" == 'DEPLOYMENT') {
                                int businesHourStart = 6
                                int businesHourEnd = 12

                                    if (currentHour >= businesHourStart || currentHour <= businesHourEnd) {
                                        println("\033[1;32mThis RITM is being triggered in business Hour...\033[0m")
                                        timeout(time: 2, unit: 'HOURS') {
                                            input id: 'Deploy', message: '\033mYou are trying to deploy this change during business hours. Please approve if you wish to [roceed with deployment\033[0m', ok: 'Approve'
                                        }
                                        comment = 'This RITM is being triggered in business Hours\\n\\n'
                                        sh"echo '[INFO] $comment' >> $successFile"
                                        sh"echo '[INFO] $comment' >> $failFile"
                                    }
                        }
                    } else {
                        comment = 'RITM is not Active, Please provide Active RITM for deployment'
                        sh "echo '[INTO] $comment >> $failFile"
                        throw new Exception(comment)
                        }
                } else {
                        comment = 'The Provided RITM is not a Database Request, Please provide a valid RITM\\n\\n'
                        sh "echo '[INTO] $comment >> $failFile"
                        throw new Exception(comment)
                    }
            }   else {
                        comment = 'The Provided RITM is not a valid Request, Please provide a valid RITM\\n\\n'
                        sh "echo '[INTO] $comment >> $failFile"
                        throw new Exception(comment)
                    }
                }
        } catch (Exception e) {
        comment = 'An excetion Occured during RITM validation.\\n\\n'
        sh"echo '[ERROR] $comment' >> $failFile"
        currentBuild.result = 'FAILURE'
        error(e)
            }
        }

