def call (comment) {
    def content = "$comment\\n\\n"

    sh """
        set +xe
        if [ ! -f ServiceNow_PipelineSummary.txt ];
        then
            touch ServiceNow_PipelineSummary.txt
        fi

        echo '$content' >> ServiceNow_PipelineSummary.txt
    """
}
