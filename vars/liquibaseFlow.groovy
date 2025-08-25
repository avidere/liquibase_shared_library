def appci(String flowfile) {
        def flowfiles = libraryResource("config/flowfiles/${flowfile}")
        writeFile file: 'flowfile.yaml', text: flowfiles

    
        sh """
            liquibase --defaultsFile=liquibase.properties \
                flow --flowfile=flowfile.yaml 
        """
    

}

def appcd(String flowfile) {
     def flowfiles = libraryResource("config/flowfiles/${flowfile}")
     writeFile file: 'liquibase-flowfile.yaml', text: flowfiles

    
        sh """
            liquibase --defaultsFile=liquibase.properties \
                flow --flowfile=liquibase-cd.flowfile.yaml
        """
    

}