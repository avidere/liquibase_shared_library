def appci() {
    // def flowfile = libraryResource('config/liquibase-flowfile.yaml')
    // writeFile file: 'liquibase-flowfile.yaml', text: flowfile

    
        bat """
            liquibase --defaultsFile=liquibase.properties \
                flow --flowfile=liquibase-ci.flowfile.yaml
        """
    

}