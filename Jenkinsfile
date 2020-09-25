pipeline {

	agent any
	stages {
		stage('Build Docker Image') {
			steps {
				sh './mvnw package -Pprod verify -DskipTests jib:dockerBuild'
			}
			post {
				success {
					echo "Docker Image has been built"
				}
			}
		}
   
		stage('Publish Docker Image to Azure Container Registry') {
                        steps {
                                withCredentials([string(credentialsId: 'Azure-Container-Registry', variable: 'SECRET')]) {
					sh 'az acr login --name $SECRET'
					sh 'docker tag auth-service $SECRET".azurecr.io/auth-service-dev:${BUILD_NUMBER}"'
					sh 'docker push $SECRET".azurecr.io/auth-service-dev:${BUILD_NUMBER}"'
				}

                        }
                        post {
                                success {
                                        echo "Published Docker Image to Azure Container Registry"
                                }
                        }
                }
 
                stage('Deploy Application in AKS') {
                        steps {
				sh 'kubectl apply -f kubernetes/deployment.yml'
                                sh 'kubectl apply -f kubernetes/service.yml'
				sh 'kubectl apply -f kubernetes/ingress.yml'
				sh 'kubectl apply -f kubernetes/autoscale.yml'
				withCredentials([string(credentialsId: 'Azure-Container-Registry', variable: 'SECRET')]) {
                                        sh 'kubectl set image deployment/auth-service-dev  auth-service-dev=$SECRET".azurecr.io/auth-service-dev:${BUILD_NUMBER}" -n authentication'
                                }
                        }
                        post {
                                success {
                                        echo "Deployed to AKS"
                                }
                        }
                }

		
	}
}
