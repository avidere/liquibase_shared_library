def getAddr(String vaultNS) {
    if (vaultNS.contains(vaultdev) || vaultNS.contains(smdev)) {
        print 'DEV VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (vaultNS.contains(vaultqa) || vaultNS.contains(smqa)) {
        print 'QA VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (vaultNS.contains(vaultuat) || vaultNS.contains(smuat)) {
        print 'UAT VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (vaultNS.contains(vaultprod) || vaultNS.contains(smprod)) {
        print 'PROD VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    }

    if (vaultNS.count("/") == 2) {
        prefix = vaultNS.split('/')[1]+'_'+vaultNS.split('/')[2]
    } else {
        prefix = vaultNS.split('/')[1]
    }

    if (vaultNS.contains('token')){
        env.cred = prefix+'_token_'+(vaultNS.split('/')[0]).split(':')[1]
    } else {
        env.cred = prefix+'_approle_'+(vaultNS.split('/')[0])
    }
}
def generateToken(String vaultNS) {
    getAddr(vaultNS)
    withCredentials([usernamePassword(credentialsId: 'cred', passwordVariable: 'secretID', usernameVariable: 'roleID')]) {
       
        sh """
            curl -k \
            -H "X-Vault-Namespace: admin/${vaultNS}" \
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
