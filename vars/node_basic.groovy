#!/usr/bin/env groovy

def call(Map config=[:], Closure body) {
    pipeline {
      agent {
    label 'docker' 
  }

   agent {
        docker {
            image 'node:12.18.3-alpine'
            args '-p 3000:3000 -p 5000:5000'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'npm install'
            }
        }
        stage('Test') {
            environment {
                NODE_ENV = "test"
            }
            steps {
                sh 'npm test'
            }
        }
        stage('Deliver for development') {
            when {
                branch 'development' 
            }
            environment {
                NODE_ENV = "development"
            }
            steps {
                sh "docker build -t ${config.image_name}:development ."
            }
        }
        stage('Deploy for production') {
            when {
                branch 'production'  
            }
             environment {
                NODE_ENV = "production"
            }
             steps {
                sh "docker build -t ${config.image_name}:production ."
            }
        
}
    }
     
}
}


