String git_folder = 'sampathr'

folder("/POC") {
  displayName('POC')
  description("Folder for POC Jobs")
}

def envManage = [
  [environment: 'dev', services:
    [
      [springName: 'address', branch: 'develop'],
      [springName: 'auth', branch: 'develop'],
      [springName: 'branding', branch: 'develop'],
      [springName: 'elastic', branch: 'develop']
    ]
  ],
  [environment: 'qa', services:
    [
      [springName: 'address', branch: 'test'],
      [springName: 'auth', branch: 'test'],
      [springName: 'branding', branch: 'test'],
      [springName: 'elastic', branch: 'test']
    ]
  ]
]

envManage.each { config ->
  String chefEnvironment = config.environment

  for ( apps in config.services ) {
    String springName = apps.springName
    String githubBranch = apps.branch
    String baseFolderName = "/POC/${chefEnvironment}/Spring/"
    String deployJob = "${baseFolderName}/${springName}/${chefEnvironment}Deploy"
    String qadeployJob = "/POC/qa/Spring//${springName}/qaDeploy"


    folder("/POC/${chefEnvironment}") {
      displayName(chefEnvironment)
      description("Folder for ${chefEnvironment} env Spring app jobs")
    }

    folder("/POC/${chefEnvironment}/Spring") {
      displayName('Spring')
      description("Folder for all Spring app jobs in ${chefEnvironment}")
    }

    folder("/POC/${chefEnvironment}/Spring/${springName}") {
      displayName(springName)
      description("Folder for all jobs related to ${springName} in ${chefEnvironment}")
    }

    job("${baseFolderName}${springName}/Restart Services") {
      properties {
        buildDiscarder {
          strategy {
            logRotator {
              numToKeepStr('4')
              daysToKeepStr('')
              artifactDaysToKeepStr('')
              artifactNumToKeepStr('')
            }
          }
        }
      }
      steps {
        shell("echo this is restart job for ${springName}")
      }
    }

    if ("${chefEnvironment}" == "dev") {
      job("${baseFolderName}${springName}/Build") {
        properties {
          buildDiscarder {
            strategy {
              logRotator {
                numToKeepStr('4')
                daysToKeepStr('')
                artifactDaysToKeepStr('')
                artifactNumToKeepStr('')
              }
            }
          }
        }
        steps {
          shell("echo this is Build job for ${springName}")
        }
        publishers {
          downstream(deployJob, 'SUCCESS')
        }
      }
    }

    job("${deployJob}") {
      properties {
        buildDiscarder {
          strategy {
            logRotator {
              numToKeepStr('4')
              daysToKeepStr('')
              artifactDaysToKeepStr('')
              artifactNumToKeepStr('')
            }
          }
        }
      }
      steps {
        shell("echo this is deploy job for ${springName} in ${chefEnvironment}")
      }
      if ("${chefEnvironment}" == "dev") {
        publishers {
          downstream(qadeployJob, 'SUCCESS')
        }
      }
    }
  }
}
