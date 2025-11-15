def call() {
    sh """
        curl -sk --fail\
        -H "X-Vault-Namespace: $namespace" \
        -H "X-Vault-Token: $VAULT_TOKEN" \
        -H "Content-Type: application/json" \
        --request POST \
        --data '{
            "plugin_name": "mysql-database-plugin",
            "connection_url": "{{username}}:{{password}}@tcp(${GLOBAL_ENDPOINT})/",
            "username": "${username}",
            "password": "${password}",
            "max_open_connections": 4,
            "max_idle_connections": 0,
            "max_connection_lifetime": "0s"
        }' \
        ${VAULT_ADDR}/v1/database/config/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_${username} | jq -r . > mysql_config_out.json
    """

    return readJSON(file: "mysql_config_out.json")
}

def mysql() {
    // Generate token at runtime
    def tokenJson = sh(script: """
        curl -sk -H "X-Vault-Namespace: admin" \
        --request POST \
        --data '{"role_id":"cdbc4a62-b7ad-ac45-d663-436041dd6bd5","secret_id":"be5bb9fc-a92a-09aa-07e8-ca369b10c16f"}' \
        ${VAULT_ADDR}/v1/auth/approle/login
    """, returnStdout: true).trim()

    def vaultToken = new groovy.json.JsonSlurper().parseText(tokenJson).auth.client_token
    echo "Vault Token: ${vaultToken}"

    // Configure MySQL in Vault
    def dbRaw = sh(script: """
        curl -sk -H "X-Vault-Namespace: admin" \
        -H "X-Vault-Token: ${vaultToken}" \
        -H "Content-Type: application/json" \
        --request POST \
        --data '{
            "plugin_name": "mysql-database-plugin",
            "connection_url": "{{username}}:{{password}}@tcp(demo-mysql-db.cr24o06285ta.ap-south-1.rds.amazonaws.com:3306)/",
            "username": "admin",
            "password": "8F8%?YbhSDp?uQOw",
            "max_open_connections": 4,
            "max_idle_connections": 0,
            "max_connection_lifetime": "0s"
        }' \
        ${VAULT_ADDR}/v1/database/config/503027034_654654373515_aurora_demo-mysql-db_admin?namespace=admin
    """, returnStdout: true).trim()

    echo "RAW VAULT RESPONSE:\n${dbRaw}"

    // Attempt JSON parse safely
    try {
        def dbJson = new groovy.json.JsonSlurper().parseText(dbRaw)
        return dbJson
    } catch (Exception e) {
        error("Vault returned invalid or empty JSON. Likely permission, namespace, or connection issue:\n${dbRaw}")
    }
}
