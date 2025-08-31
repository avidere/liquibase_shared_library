def call() {
    try {
        //COMMIT_ID=$(git -c ${WORKSPACE} rev-parse --short=11 HEAD)
        sh '''
            
            echo changelogName=${changelog} >> artifact.properties
            echo branch=${gitBranch} >> artifact.properties
            echo artifactGroup=${groupId} >> artifact.properties
            echo validationFile=${validationFile} >> artifact.properties

            if [[ ${changelog} =~ "xml"]]
            then
                scriptPath=\$(cat ${changelog} | grep -o 'path="[^"]*"' | cut -d'"' -f2 | tr -d '"')
            elif [[ ${changelog} =~ "yaml" ]]
            then
                scriptPath=\$(cat ${changelog} | grep -w "path:" | cut -d: -f2 | tr -d " "))")
            fi

            echo \$scriptPath
            cp -f liquibase.properties_from_source config/liquibase.properties
            zip -r ${WORKSPACE}/{artifact}-${BUILD_NUMBER}.zip  ${changelog} config/liquibase.properties artifact.properties config/* \$validationfile
        '''

    } catch (e) {
        echo "Error: An exception during zip worspace stage: ${e}"
        error("zip workspace failed")
        currentBuild.result = 'FAILURE'
    }
}
