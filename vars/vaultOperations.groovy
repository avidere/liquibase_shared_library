def generateToken(String namespace) {
    withCredentials([
        usernamePassword(
            credentialsId: 'vaultcred',
            passwordVariable: 'secretID',
            usernameVariable: 'roleID'
        )
    ]) {
        bat """
        "C:\\Program Files\\Git\\bin\\bash.exe" -c '
          curl -s -k \
            -H "X-Vault-Namespace: ${namespace}" \
            --request POST \
            --data "{\\\\\\"role_id\\\\\\": \\\\\\"${roleID}\\\\\\", \\\\\\"secret_id\\\\\\": \\\\\\"${secretID}\\\\\\"}" \
            ${VAULT_ADDR}/v1/auth/approle/login \
          | jq -r .auth.client_token > token.json
        '
        """
        def token = readFile('token.json').trim()
        return token
    }
}
