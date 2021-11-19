pipeline{

     agent any

    environment
    {
        IMG_NAME = "order_service"
        AWS_ID = "419106922284"

    }

  tools
  {
            maven 'maven'
            jdk 'java'
  }

  stages
  {
       
    //    stage("Build")
    //    {
    //         steps {
    //              sh 'mvn clean install'
    //         }
    //    }
       
    //    stage("Test")
    //    {
    //             steps
    //             {
    //                 sh 'mvn test'
    //                 junit '**/target/surefire-reports/*.xml'
    //             }
    //    } 
         
    //    stage('Code Analysis: Sonarqube')
    //    {
    //                steps {
    //                    withSonarQubeEnv('sonarqube') {
    //                        sh 'mvn sonar:sonar'
    //                    }
    //                }
    //    }
    //    stage('Await Quality Gateway') 
    //    {
    //         steps {
    //             waitForQualityGate abortPipeline: false
    //         }
    //    }
      stage("Package")
      {
            steps
            {
                sh 'mvn clean package'
            }
      }
      stage("Docker Build") {

          steps {
              echo "Docker Build...."
              withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'jenkins_credentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh "aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin ${AWS_ID}.dkr.ecr.us-east-2.amazonaws.com"
              }
              sh "docker build -t ${IMG_NAME} ."
               sh "docker tag ${IMG_NAME}:latest ${AWS_ID}.dkr.ecr.us-east-2.amazonaws.com/${IMG_NAME}:latest"
              echo "Docker Push..."
               sh "docker push ${AWS_ID}.dkr.ecr.us-east-2.amazonaws.com/${IMG_NAME}:latest"
          }
      }
    }
  post
  {
          always
          {
              sh 'mvn clean'
          }
  }

}