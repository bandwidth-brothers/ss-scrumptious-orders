pipeline{
	agent any

  environment
  {
          DB_ENDPOINT = credentials('DB_ENDPOINT')
          DB_USERNAME = credentials('DB_USERNAME')
          DB_PASSWORD = credentials('DB_PASSWORD')
          STRIPE_SECRET_KEY = credentials('STRIPE_SECRET_KEY')
  }

      tools
      {
                maven 'maven'
                jdk 'java'
      }

	stages{

            stage('Analysis'){
                steps {
                    withSonarQubeEnv('jenkins-sonar') {
                        sh 'mvn clean verify sonar:sonar'
                    }
                }
            }


	}
}
