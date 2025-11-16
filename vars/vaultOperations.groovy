def generateToken(String vaultNS) {
    withCredentials([usernamePassword(credentialsId: 'vaultcred', passwordVariable: 'secretID', usernameVariable: 'roleID')]) {
       
        sh """
            curl -k \
            -H "X-Vault-Namespace: ${vaultNS}" \
            --request POST \
            --data '{
                "role_id": "${roleID}",
                "secret_id": "${secretID}"
            }' \
            ${VAULT_ADDR}/v1/auth/approle/login | jq -r .auth > token.json
        """
        
    }
    def authProps = readJSON file: 'token.json'
    def token = authProps['client_token']
    return token
}
