def call(){
    try {
        sh """
            liquibase --defaultsFile="${liquibasePropFile}" --changelogFile="${changelog}" --output-file=updatesql.txt
        """
    } catch (Exception e){

    }
}