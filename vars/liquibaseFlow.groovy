def appci() {
        def flowfiles = libraryResource('config/flowfiles/liquibase-ci.flowfile.yaml')
        writeFile file: 'flowfile.yaml', text: flowfiles

    
        sh """
            liquibase --defaultsFile=liquibase.properties \
                flow --flowfile=flowfile.yaml 
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