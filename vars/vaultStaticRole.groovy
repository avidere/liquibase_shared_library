def deploy() {

    sh """
          curl -sk \
            -H "X-Vault-Namespace: $namespace" \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "Content-Type: application/json" \
            --request POST \
            --data '{
              "db_name": "${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}",
              "username": "liquibase_deploy",
              "rotation_period": "2160h"
            }' \
            $VAULT_ADDR/v1/database/static-roles/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_liquibase_deploy_role?namespace=${namespace}

    """
}

def dare() {

    sh """
          curl -sk \
            -H "X-Vault-Namespace: $namespace" \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "Content-Type: application/json" \
            --request POST \
            --data '{
              "db_name": "${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}",
              "username": "liquibase_dare",
              "rotation_period": "2160h"
            }' \
            $VAULT_ADDR/v1/database/static-roles/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_liquibase_dare_role?namespace=${namespace}

    """
}

def dba() {

    sh """
          curl -sk \
            -H "X-Vault-Namespace: $namespace" \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "Content-Type: application/json" \
            --request POST \
            --data '{
              "db_name": "${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}",
              "username": "liquibase_dba",
              "rotation_period": "2160h"
            }' \
            $VAULT_ADDR/v1/database/static-roles/${APP_CIID}_${AWS_ACCOUNT}_${DB_TYPE}_${DB_IDENTIFIER}_liquibase_dba_role?namespace=${namespace}

    """
}