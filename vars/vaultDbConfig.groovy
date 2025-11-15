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

    // Run curl and save RAW response
    sh """
        echo "----- RAW VAULT RESPONSE START -----"
        
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
        ${VAULT_ADDR}/v1/admin/database/config/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_${username} \
        | tee curl_raw_output.txt

        echo "----- RAW VAULT RESPONSE END -----"
    """

    // Show raw response
    sh "cat curl_raw_output.txt"

    // If JSON is empty or invalid, fail early
    sh """
        if ! jq -e . curl_raw_output.txt > mysql_config_out.json; then
            echo "Vault returned invalid JSON. Printing raw output:"
            cat curl_raw_output.txt
            exit 1
        fi
    """

    return readJSON(file: "mysql_config_out.json")
}
