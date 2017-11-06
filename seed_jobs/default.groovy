import groovy.json.JsonSlurper

def configFile = new File("${WORKSPACE}/config/pipelines.json")
def config = new JsonSlurper().parseText(configFile.text)


config.pipelines.each { pipeline ->

    pipelineJob("${pipeline.name}-${pipeline.branch}-build") {
        parameters {
            stringParam('COMMIT', 'HEAD', 'Commit to build')
        }
        definition {
            cps {
                    script "Jenkinsfile"
                }
        }
        description "Building the ${pipeline.branch} branch."
        scm {
            git {
                remote {
                    url "${config.gitPrefix}/${pipeline.git}"
                    branch pipeline.branch
                }
                extensions {
                    wipeOutWorkspace()
                    localBranch pipeline.branch
                }
            }
        }
        triggers {
            scm('H/15 * * * *')
        }
        steps {
            shell "Look! I'm building!"
        }
    }
}