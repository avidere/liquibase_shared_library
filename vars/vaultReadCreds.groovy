def usernamePassword(){
    sh """
        curl \
        -H "X-Vault-Token: ${VAULT_TOKEN}" \
        -H "X-Vault-Namespace: ${namespace}" \
        $VAULT_ADDR/v1/${path} | jq -r .data.data > credentials.json
    """
    def credProps = readJSON file: 'credentials.json'
    
    user = credProps['username']
    pass = credProps['password']

    return [user, pass]
}