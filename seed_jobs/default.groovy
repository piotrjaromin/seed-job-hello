import groovy.json.JsonSlurper

def configFile = new File("${WORKSPACE}/config/pipelines.json")
def config = new JsonSlurper().parseText(configFile.text)


config.pipelines.each { pipeline ->

    job("${pipeline.name}-${pipeline.branch}-build") {
        description "Building the ${pipeline.branch} branch."
        parameters {
            stringParam('COMMIT', 'HEAD', 'Commit to build')
        }
        scm {
            git {
                remote {
                    url "${config.gitPrefix}/${branch.git}"
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
            shell "Look! I'm building ${BRANCH}!"
        }
    }
}