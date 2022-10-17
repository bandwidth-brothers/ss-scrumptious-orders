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
                    }
			step{
         		sh 'mvn clean test'
			}
                }
           }

            stage('Analysis'){
                tools{
                    maven 'maven'
                    jdk 'jdk11'
                }
                
		    steps {
	            withSonarQubeEnv('jenkins-sonar') {
                        sh 'mvn sonar:sonar -Dsonar.java.source=1.8 -Dsonar.java.jdkHome=/usr/lib/jvm/java-11-openjdk'
                    }
                }
            }


	}
}
