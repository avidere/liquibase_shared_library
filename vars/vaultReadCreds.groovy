def usernamePassword(){
    maskPasswords(varMaskRegexes: [[value: master_pass, VAULT_TOKEN]]) {
        sh """
            curl \
            -H "X-Vault-Token: ${VAULT_TOKEN}" \
            -H "X-Vault-Namespace: ${namespace}" \
            $VAULT_ADDR/v1/${VaultPath} | jq -r .data.data > credentials.json
        """
        def credProps = readJSON file: 'credentials.json'
        
        user = credProps['username']
        pass = credProps['password']

        return [user, pass]
    }
}