def call() {
    echo 'Cleaning Jenkins workspace...'
    deleteDir()
    echo 'Workspace cleaned successfully.'
}
