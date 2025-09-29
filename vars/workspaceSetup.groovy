def call () {
    try {
        def liquibaseproperty = liquibaseResource "config/liquibase_props/${DBType}_liquibase.properties"
        writeFile file: 'liquibase.properties', text: liquibaseproperty

        def liquibasesqlconf = liquibaseResource "config/check_conf/liquibase.checks-execute-sql.conf"
        writeFile file: 'liquibase.checks-execute-sql.conf' text: liquibasesqlconf

        liquibaseconf = liquibaseResource "config/checks_conf/liquibase.${DBType}-checks-settings.conf"
        writeFile file: 'liquibase.checks.settings.conf' text: liquibaseconf

        if("${DBType}" == "Oracle") {
            def rootchange = liquibaseResource "config/root-changelog-oracle.xml"
            writeFile file: 'root-changelog.xml' text: rootchange
        } else {
            def rootchange = liquibaseResource "config/root-changelog.xml"
            writeFile file: 'root-changelog.xml' text: rootchange
        }

            sh """
                    set +xv
                    envsubst < config/liquibase.properties > liquibase_updated.properties
                    mv config/liquibase.properties liquibase.properties_from_source
                    mv liquibase_updated.properties config/liquibase.properties
                    mkdir -p config/${envir}
                    mv liquibase.properties config/${envir}/liquibase.properties
               """

               def consoleOutput = currentBuild.getBuildCause()
               env.SSOID = (consoleOutput =~ /userId:(\d+)/)[0][1]
               env.USERNAME = (consoleOutput =~ /username:\s*([^(]*)/)[0][1]

               echo "SSOID: ${SSOID}"
               echo "USERNAME: ${USERNAME}"

               withFileParameter('FILE') {
                liquibasesqlfile = sh(returnStdout: true, script: "echo $FILE_FILENAME | grep -oP '^[^.]+' || true").trim()
                env.sql_file = FILE_FILENAME
                sh """
                    mkdir DBScript
                    cp $FILE DBScript/"${liquibasesqlfile}.sql"
                    ls -l DBScript

                    if [$ACCESS_REQUEST == 'true' ]
                    then 
                        sed -i "s/{ACCESS}/ACCESS_REQUEST : $ACCESS_TYPE access for $USER_ID on $DB_Name/g" $changelog
                    else
                        sed -i "s/{ACCESS}//g" $changelog
                    fi

                    envsubst < $changelog > ${changelog}_updated
                    mv $changelog ${changelog}_from_source
                    mv ${changelog}_updated $changelog
                """

                if ("${REQUEST_TYPE}" == "DEPLOYMENT") {
                    captureFile = sh(returnStdout: true, script: "cat DBScript/${liquibasesqlfile}.sql | grep -w -Ei 'grant|lock|revoke|create user|alter user|drop user|flush|delete user|update user'|| true").trim()
                        
                        if (captureFile.toLowerCase() != "" & "${ACCESS_REQUEST}" != 'true' ) {
                            def accessurl = "${JOB_URL}${BUILD_NIMBER}/execution/node/10/ws/updatesql.txt"
                            def accessmessage = "<a></ href='${accessurl}'>"
                            timeout(time: 30, unit: "MINUTES") {
                                input(
                                    id: 'Deploy'
                                    message: "\033[1;33m Access Request is selected as NO, but access related statements found in script. \\nReview the script and abort the pipeline if there are access related statements and re-trigger the build by selecting Access Request as Yes.\\nApprove to contine the build if no access related statements are present and finding is false.: ${accessmessage}\033[0m",
                                    ok: 'Approve')
                                comment = "Access Request is selected as NO, but access related statements found in script. \\nReview the script and abort the pipeline if there are access related statements and re-trigger the build by selecting Access Request as Yes.\\nApprove to contine the build if no access related statements are present and finding is false.\\n\\n"
                                println "Access Request is selected as NO, but access related statements found in script. \\nReview the script and abort the pipeline if there are access related statements and re-trigger the build by selecting Access Request as Yes.\\nApprove to contine the build if no access related statements are present and finding is false."
                                sh"echo '[INFO] $comment' >> $successFile"
                                sh"echo '[INFO] $comment' >> $failFile"
                            }
                        }
                    }
               }
    } catch(Exception e) {
        comment = "Workspace setup failed with exception $e\\n\\n"
        sh"echo '[ERROR] $comment' >> $failFile"
        currentBuild.result = "FAILURE"
        error(e)
    }
}
