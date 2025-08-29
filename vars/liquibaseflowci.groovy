def call () {
    sh """
        liquibase --defaultsFile=liquibase.properties flow --flowfile=flowfile.yaml --output-file=output.txt --log-file-liquibase.log
        """
}