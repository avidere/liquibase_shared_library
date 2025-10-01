def call () {
    try {
        if ("${DBType}" == "MySQL") {
            def message = "Please select Masterip to proceed"
            def masteraddress = getUserInput.masterIP(message)

            if ("${ENVIRONMENT}" == "PROD") {
                def JDBCurl = sh(returnStdout: true, script: "echo jdbc:mysql://${masteraddress}:30306/${DB_NAME}?useSSL=true || true").trim()
                println JDBCurl
                return JDBCurl
            } else {
                def JDBCurl = sh(returnStdout: true, script: "echo jdbc:mysql://${masteraddress}:1521/${DB_NAME}?useSSL=true || true").trim()
                println JDBCurl
                return JDBCurl
            }
        } else if ("${DBType}" == "Cassandra") {
            def message = "Please  enter JDBC_URL to create secret (Example: jdbc://cassandra://host:9042/test_db)"
            def JDBCurl = getUserInput.StringValue(message).concat("?requesttimeout=10000'&compliancemode=Liquibase&localdatacenter=DAL&enablessl=true'")
            println JDBCurl
            return JDBCurl
        } else {
            def message = "Please enter the JDBC URL"
            def JDBCurl = getUserInput.StringValue(message)
            println JDBCurl
            return JDBCurl
        }
    } catch(e) {
        comment = "Failed to create JDBC URL due to exception $e\\n\\n"
        sh"echo ['ERROR'] $comment' >> $failFile"
        currentBuild.result = "FAILURE"
        error(e)
    }
}