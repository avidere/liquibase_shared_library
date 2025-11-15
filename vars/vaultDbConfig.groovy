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
            "password": "${username}",
            "max_open_connections": 4,
            "max_idle_connections": 0,
            "max_connection_lifetime": "0s"
        }' \
        ${VAULT_ADDR}/v1/database/config/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_${username} | jq -r . > mysql_config_out.json
    """

    return readJSON(file: "mysql_config_out.json")
}

def mysql() {

    // Run curl and store response in variable
    def response = sh(
        script: """
            curl -sk \
            -H "X-Vault-Namespace: ${namespace}" \
            -H "X-Vault-Token: ${VAULT_TOKEN}" \
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
            ${VAULT_ADDR}/v1/database/config/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_${username}
        """,
        returnStdout: true
    ).trim()

    echo "RAW VAULT RESPONSE: ${response}"

    // If empty → fail with clear message
    if (!response || response.trim() == "") {
        error("Vault returned EMPTY response")
    }

    // Try parsing JSON safely
    try {
        def parsed = readJSON text: response
        return parsed
    } catch (Exception e) {
        error("Vault returned INVALID JSON:\n${response}")
    }
}
