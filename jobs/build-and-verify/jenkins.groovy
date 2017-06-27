node {
    try {
     
        // configure Tools and Environment.
        def buildNumber = "$env.BUILD_NUMBER"


        def buildCfg = "builddocker"
        def depCfg = "builddocker"


        // Stage Build
        stage 'build'

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: OSE_CREDENTIAL_ID, passwordVariable: 'openshift_password', usernameVariable: 'openshift_username']]) {
            
            openshiftBuild apiURL: OSE_MASTER, authToken: env.openshift_password, bldCfg: OSE_BUILDCONFIG, buildName: '', checkForTriggeredDeployments: 'true', commitID: "", namespace: OSE_NAMESPACE, showBuildLogs: 'true', verbose: 'false', waitTime: ''
            
            openshiftVerifyDeployment apiURL: OSE_MASTER, authToken: env.openshift_password, depCfg: OSE_BUILDCONFIG, namespace: OSE_NAMESPACE, replicaCount: '1', verbose: 'false', verifyReplicaCount: 'true'
        }

        // Stage smoke Tests
        stage 'smoke tests'
        def waittime = params.OSE_DEPLOYWAITTIME ?: "20"
        sleep waittime.toInteger()

        sh("""curl -s -o /dev/null -w "%{http_code}" $OSE_APPURL > code.txt""")
        status = readFile('code.txt')
        if (status != "200") {
            throw new Exception("Result code is not 200: " + status);
        }


    } catch (Exception e) {
        // Notify
        echo "send error mail to $OSE_EMAIL_TO"
        mail subject: "OpenShift Deployment Job failed with ${e.message}", to: "$OSE_EMAIL_TO", body: "Job failed: ${env.BUILD_URL} \n\n${e.stackTrace}"
        throw e;
    }
}
