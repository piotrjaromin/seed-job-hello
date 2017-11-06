import groovy.json.JsonSlurper

def configFile = new File("${WORKSPACE}/config/pipelines.json")
def config = new JsonSlurper().parseText(configFile.text)


config.pipelines.each { pipeline ->

    pipelineJob("${pipeline.name}-${pipeline.branch}-build") {
        description "Building the ${pipeline.branch} branch."
        parameters {
            stringParam('COMMIT', 'HEAD', 'Commit to build')
        }
        definition {
            cpsScm {
                scm {
                    git {
                        branch pipeline.branch
                        remote {
                            url "${config.gitPrefix}/${pipeline.git}"
                        }
                    }
                }
                scriptPath "Jenkinsfile"
            }
        }
        triggers {
            scm('H/15 * * * *')
        }
    }
}