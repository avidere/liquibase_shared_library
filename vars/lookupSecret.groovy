def call () {
    try {
        path = "kv/liquibase/$DBType/${envir}"
        vaultSearchSecret(vault_ns, path)
        checkSecret = sh(returnStdout: true, script: "grep -w ${DB_NAME} secret.txt || true").trim()
        println "Display value of checkSecret"+checkSecret

        def chekMaterIPCount = "0"

        if (DBType == "MySQL") {
            def masterIPDetails = libraryResource "${DBType}_DB_Details/mysqlDbDetails.csv"
            writeFile: 'mysqlDbDetails.csv' text: masterIPDetails

            def envInLowerCase = envir.toLowerCase()
            env.CheckMasterIP = sh(returnStdout: true, script: "grep -w ${envInLowerCase} mysqlDbDetails.csv | grep -w ${DB_NAME} | cut -d'|'-f6 || true").trim()
            println CheckMasterIP 

            env.CheckMasterIPCount = sh(returnStdout: true, script: "grep -w ${envInLowerCase} mysqlDbDetails.csv | grep -wc ${DB_NAME} | cut -d'|'-f6 || true").trim()
            println CheckMasterIPCount
        }

        if (!checkSecret.equals(params.DB_NAME)) {
            println "Secret not found in vault"
            url = createJDBC()
            def path = "kv/data/liquibase/$DBType/$envir/$DB_NAME"
            vaultWriteCreds.dbUrl(vault_ns, path, url)
            comment = "JDBC url updated in vault\\n\\n"
            sh"echo '[INFO] $comment' >> $successFile"
        } else if (checkSecret.equals(params.DB_NAME) && chekMaterIPCount > "1") {
            println "Multiple IP found for selected Database"
            url = createJDBC()
            def path = "kv/data/liquibase/$DBType/$envir/$DB_NAME"
            vaultWriteCreds.dbUrl(vault_ns, path, url)
            comment = "JDBC url updated in vault\\n\\n"
            sh"echo '[INFO] $comment' >> $successFile"
        } else {
            println "Secret Found in Vault"
        }
    } catch (Exception e) {
        comment = "$e\\n\\n"
        sh"echo '[ERROR] $comment' >> $failFile"
        currentBuild.result = "FAILURE"
        error(e)
    }
}