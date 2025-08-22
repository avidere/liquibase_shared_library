def appci() {
    // def flowfile = libraryResource('config/liquibase-flowfile.yaml')
    // writeFile file: 'liquibase-flowfile.yaml', text: flowfile

    ansiColor('xterm') {
        bat """
            cat liquibase-flowfile.yaml
            liquibase --defaultsFile=liquibase.properties \
                changelog-file=changelog/changelog.xml \
                flow --flowfile=liquibase-ci.flowfile.yaml
        """
    }

}