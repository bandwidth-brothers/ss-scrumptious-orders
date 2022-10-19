pipeline{
	agent any

  environment
  {
          DB_ENDPOINT = credentials('DB_ENDPOINT')
          DB_USERNAME = credentials('DB_USERNAME')
          DB_PASSWORD = credentials('DB_PASSWORD')
          STRIPE_SECRET_KEY = credentials('STRIPE_SECRET_KEY')
  }

      

	stages{

                stage('test'){
		tools{
                    maven 'maven'
                    jdk 'jdk8'
      		}
    	        steps{

                    script{
                    def files = findFiles(glob: '**/main/resources/application-product.properties')
                    echo """name ${files[0].name}; path:  ${files[0].path}; directory: ${files[0].directory}; length: ${files[0].length}; modified:  ${files[0].lastModified}"""

                    def readContent = readFile "${files[0].path}"
                    writeFile file: "${files[0].path}", text: readContent+"""\r\nspring.datasource.username=${DB_USERNAME}
                                                                                              \r\nspring.datasource.password=${DB_PASSWORD}
                                                                                              \r\nspring.datasource.url=${DB_ENDPOINT}
                                                                                              \r\nSTRIPE_SECRET_KEY=${STRIPE_SECRET_KEY}
                                                                                              """
                    def str=readFile file: "${files[0].path}"
                    echo str
                    }
		    sh 'mvn clean test'
                }

    	   }

		stage('Package'){
			tools{
	                    maven 'maven'
                	    jdk 'jdk11'
      			}
			steps{
				sh 'mvn clean package -Dmaven.test.skip'
			}
		}

		stage('Deploy'){
			steps{
				sh "docker build -t order-service-john ."
				script{
					docker.withRegistry("https://419106922284.dkr.ecr.us-east-2.amazonaws.com/","ecr:us-east-2:ecr_credentials"){
						docker.image("order-service-john").push()
					}
				}
				sh "docker system prune -fa"
			}
		}
	}
}
