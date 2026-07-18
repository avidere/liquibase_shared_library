def call() {
    randomVal = sh(returnStdout: true, script:"""set +xv
        letters=\$(tr -dc 'a-z' < /dev/urandom | head -c 3)
        lettersUC=\$(tr -dc 'A-Z' < /dev/urandom | head -c 2)
        digits=\$(tr -dc '0-9' < /dev/urandom | head -c 2)
        special=\$(tr -dc '!#%*+,-/:=?_' < /dev/urandom | head -c 2)
        concat=\${letters:2:1}\${digits:0:1}\${special:0:1}
        value=\${letters:0:2}\${digits:1:1}\${special:1:1}\${lettersUC:0:1}\$(echo \${concat} | fold -w 1 | shuf | tr -d "\n")\${lettersUC:1:1}
        echo \$value
    """).trim()

    return randomVal
}