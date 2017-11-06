import groovy.json.JsonSlurper

def configFile = new File("${WORKSPACE}/config/pipelines.json")
def config = new JsonSlurper().parseText(configFile.text)


config.pipelines.each { pipeline ->

    pipelineJob("${pipeline.name}-${pipeline.branch}-build") {
        description "Building the ${pipeline.branch} branch."
        parameters {
            stringParam('COMMIT', 'HEAD', 'Commit to build')

            pipeline?.parameters.each { param ->
                activeChoiceParam(param.name) {
                    description(param?.description)
                    filterable()
                    choiceType('SINGLE_SELECT')
                    groovyScript {
                        script("[${param.values.join(',')}]")
                        fallbackScript('"fallback choice"')
                    }
                }
            }
        }
        definition {
            cpsScm {
                scm {
                    git {
                        branch pipeline.branch?: "master"
                        remote {
                            url getRepoUrl(config, pipeline)
                            credentials config.gitCredentials
                        }
                    }
                }
                scriptPath getScriptPath(pipeline)
            }
        }
        triggers {
            scm('H/15 * * * *')
        }
    }
}


String getScriptPath(pipeline) {
    return pipeline.scriptPath?: "Jenkinsfile"
}

String getRepoUrl(config, pipeline) {
    if ( !pipeline?.git ) {
        return "${config.gitPrefix}/${config.defaultGitProject}"
    }
    return "${config.gitPrefix}/${pipeline.git}"
}
