def call(){
    sh """
            curl -sk \
            -H "X-Vault-Namespace: $namespace" \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "Content-Type: application/json" \
            --request POST \
            --data '{
                "plugin_name": "mysql-database-plugin",
                "connection_url": "{{username}}:{{password}}@tcp(${GLOBAL_ENDPOINT}:${PORT})/",
                "username": "${master_user}",
                "password": "${master_pass}",
                "allowed_roles":"${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_liquibase_deploy_role, ${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_liquibase_dare_role, ${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_liquibase_dba_role",
                "max_open_connections": 4,
                "max_idle_connections": 0,
                "max_connection_lifetime": "0s"
            }' \
            $VAULT_ADDR/v1/database/config/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}
"""
}