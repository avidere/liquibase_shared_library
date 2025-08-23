def appci(String flowfile) {
        def flowfile = libraryResource('config/liquibase-ci.flowfile.yaml')
        writeFile file: 'liquibase-ci.flowfile.yaml', text: flowfile

    
        sh """
            liquibase --defaultsFile=liquibase.properties \
                flow --flowfile=${flowfile}
        """
    

}

def appcd(String) {
     def flowfile = libraryResource('config/liquibase-flowfile.yaml')
     writeFile file: 'liquibase-flowfile.yaml', text: flowfile

    
        sh """
            liquibase --defaultsFile=liquibase.properties \
                flow --flowfile=liquibase-cd.flowfile.yaml
        """
    

}