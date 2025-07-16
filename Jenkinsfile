// This Jenkinsfile defines a declarative pipeline for a CI/CD job.
// It polls a Git repository, pulls the latest changes, builds a JAR file,
// and then copies the built JAR to a specified directory.

pipeline {
    // Define the agent where the pipeline will run.
    // 'any' means Jenkins will run the pipeline on any available agent.
    agent any

    // Define environment variables that can be used throughout the pipeline.
    // Here, we define the target directory for the JAR file.
    environment {
        // Replace with your desired target directory path on the Jenkins agent.
        // For example: '/var/lib/jenkins/jars' or 'C:\\Jenkins\\Jars'
        TARGET_DIR = '/opt/jenkins/builds'
    }

    // Define the stages of the pipeline.
    stages {
        // Stage 1: Checkout the Git repository.
        stage('Checkout') {
            steps {
                // Clean the workspace before checking out to ensure a fresh build.
                cleanWs()
                // Checkout the source code from the Git repository.
                // Replace 'your-repo-url' with the actual URL of your Git repository.
                // Replace 'main' with the branch you want to poll and build (e.g., 'master', 'develop').
                git branch: 'main', url: 'https://github.com/cjremmett/spring-api.git'
            }
        }

        // Stage 2: Build the JAR file using Maven or Gradle.
        stage('Build') {
            steps {
                // This step assumes you are using Maven to build your project.
                // 'mvn clean package' cleans the project and then packages it,
                // typically creating a JAR or WAR file in the 'target' directory.
                // If you are using Gradle, replace this with: sh './gradlew clean build'
                sh 'mvn clean package'
            }
        }

        // Stage 3: Copy the built JAR file to the target directory.
        stage('Copy JAR') {
            steps {
                // Find the JAR file in the 'target' directory (or 'build/libs' for Gradle).
                // '**/*.jar' matches any JAR file in the 'target' directory and its subdirectories.
                // The 'findFiles' step returns a list of files that match the pattern.
                script {
                    def jarFiles = findFiles(glob: 'target/**/*.jar')
                    // Check if any JAR files were found.
                    if (jarFiles.length > 0) {
                        // Get the path of the first found JAR file.
                        def jarPath = jarFiles[0].path
                        echo "Found JAR: ${jarPath}"
                        // Create the target directory if it doesn't exist.
                        sh "mkdir -p ${TARGET_DIR}"
                        // Copy the JAR file to the target directory.
                        sh "cp ${jarPath} ${TARGET_DIR}/"
                        echo "Copied ${jarPath} to ${TARGET_DIR}/"
                    } else {
                        // If no JAR file is found, fail the build.
                        error "No JAR file found in target directory. Build failed."
                    }
                }
            }
        }
    }

    // Define post-build actions, such as sending notifications.
    post {
        // Always execute this block, regardless of the build result.
        always {
            // Clean up the workspace after the build is complete.
            deleteDir()
        }
        // Execute this block if the build is successful.
        success {
            echo 'Pipeline finished successfully!'
            // You can add notifications here, e.g., email, Slack.
            // mail to: 'your-email@example.com',
            //      subject: "Jenkins Build Success: ${env.JOB_NAME}",
            //      body: "Build ${env.BUILD_NUMBER} for ${env.JOB_NAME} was successful."
        }
        // Execute this block if the build fails.
        failure {
            echo 'Pipeline failed!'
            // You can add notifications here for failures.
            // mail to: 'your-email@example.com',
            //      subject: "Jenkins Build Failed: ${env.JOB_NAME}",
            //      body: "Build ${env.BUILD_NUMBER} for ${env.JOB_NAME} failed. Check logs."
        }
    }
}

// How to configure Git Polling in Jenkins:
// 1. In your Jenkins job configuration, go to the "Build Triggers" section.
// 2. Check the "Poll SCM" option.
// 3. In the "Schedule" field, enter a cron-like syntax to define the polling frequency.
//    Examples:
//    - `H/5 * * * *` (every 5 minutes)
//    - `H H(0-23) * * 1-5` (every hour on weekdays)
//    - `0 * * * *` (every hour on the hour)
// 4. Jenkins will then automatically check your Git repository for new commits
//    at the specified interval and trigger a build if changes are detected.