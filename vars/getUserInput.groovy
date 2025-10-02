def stringValue(String message) {
    try {
        def userInput = ''
        while (userInput.trim() == '') {
            userInput = input(
                id: 'userInput',
                message: "${message}",
                parameters: [String(name: 'USER_INPUT')]).trim()
        } 
        return userInput
    } catch (Exception e) {
        comment "Failed to get user input due to exception $e\\n\\n"
        sh"echo '[ERROR] $comment ' >> $failFile"
        currentBuild.result = "FAILURE"

    }
}