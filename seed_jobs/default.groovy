import groovy.json.JsonSlurper

def configFile = new File('${WORKSPACE}/config/pipelines.json')
def config = new JsonSlurper().parseText(configFile.text)


config.pipelines.each {

    job("${it.name}-${it.branch}-build") {
        description "Building the ${it.branch} branch."
        parameters {
            stringParam('COMMIT', 'HEAD', 'Commit to build')
        }
        scm {
            git {
                remote {
                    url '${config.gitPrefix}/${it.git}'
                    branch '${COMMIT}'
                }
                extensions {
                    wipeOutWorkspace()
                    localBranch it.branch
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