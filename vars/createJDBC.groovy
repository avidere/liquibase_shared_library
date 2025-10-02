def call() {
    def failFile = "secret.txt"
    try {
        def JDBCurl = ""
        def comment = ""

        if ("${DBType}" == "MySQL") {
            def masteraddress = params.MASTER_IP ?: getUserInput.masterIP("Please select Master IP to proceed")
            if ("${ENVIRONMENT}" == "PROD") {
                JDBCurl = sh(returnStdout: true, script: "echo jdbc:mysql://${masteraddress}:30306/${DB_NAME}?useSSL=true").trim()
            } else {
                JDBCurl = sh(returnStdout: true, script: "echo jdbc:mysql://${masteraddress}:1521/${DB_NAME}?useSSL=true").trim()
            }
        } else if ("${DBType}" == "Cassandra") {
            def inputURL = params.CASSANDRA_JDBC ?: getUserInput.stringValue("Please enter JDBC_URL to create secret (Example: jdbc:cassandra://host:9042/test_db)")
            JDBCurl = inputURL + "?requesttimeout=10000&compliancemode=Liquibase&localdatacenter=DAL&enablessl=true"
        } else {
            JDBCurl = params.JDBC_URL ?: getUserInput.stringValue("Please enter the JDBC URL")
        }

        println "Created JDBC URL: ${JDBCurl}"
        return JDBCurl

    } catch (e) {
        def comment = "Failed to create JDBC URL due to exception: ${e.message}"
        println comment
        // Safely write to failFile
        sh """#!/bin/sh
echo "[ERROR] ${comment}" >> ${failFile}
"""
        currentBuild.result = "FAILURE"
        error("Pipeline failed due to: ${e.message}")
    }
}
