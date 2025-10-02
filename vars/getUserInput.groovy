def stringValue(String message) {
    try {
        def userInput = ''
        while (userInput.trim() == '') {
            userInput = input(
                id: 'userInput',
                message: "${message}",
                parameters: [string(name: 'USER_INPUT')]
            ).trim()
        }
        return userInput
    } catch (Exception e) {
        def comment = "Failed to get user input due to exception: ${e}"
        echo "[ERROR] ${comment}"  // use echo to log errors
        currentBuild.result = "FAILURE"
        return null
    }
}
