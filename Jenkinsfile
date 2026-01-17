pipeline {
    agent any

    tools {
        maven 'Maven-3.9.4'
        jdk 'JDK-17'
    }

    environment {
        PROJECT_NAME = 'DZ16'
        BRANCH = 'main'
        ALLURE_RESULTS = 'target/allure-results'
        HTML_REPORT = 'target/surefire-reports'
        SCREENSHOTS_DIR = 'target/screenshots'
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    echo '📦 Получение кода из Git...'
                    checkout scm
                }
            }
        }

        stage('Clean') {
            steps {
                script {
                    echo '🧹 Очистка проекта...'
                    sh 'mvn clean'
                }
            }
        }

        stage('Compile') {
            steps {
                script {
                    echo '🔨 Компиляция проекта...'
                    sh 'mvn compile'
                }
            }
        }

        stage('Run UI Tests') {
            steps {
                script {
                    echo '🎨 Запуск UI тестов...'
                    sh 'mvn test -Dtest="*UITest,*PageTest,*Download*"'
                }
            }
            post {
                always {
                    // Сохраняем скриншоты при падениях
                    archiveArtifacts artifacts: 'target/screenshots/*.png',
                    allowEmptyArchive: true
                }
            }
        }

        stage('Run API Tests') {
            steps {
                script {
                    echo '🌐 Запуск API тестов...'
                    sh 'mvn test -Dtest="*ApiTest,*UserPatchTest,*TokenAuthenticationTest"'
                }
            }
        }

        stage('Generate Reports') {
            steps {
                script {
                    echo '📊 Генерация отчетов...'

                    sh 'mvn allure:report'

                    sh 'mvn surefire-report:report'

                    sh 'mkdir -p ${ALLURE_RESULTS} ${HTML_REPORT} ${SCREENSHOTS_DIR}'
                }
            }
        }
    }

    post {
        always {
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']],
                reportBuildPolicy: 'ALWAYS',
                properties: []
            ])

            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'target/site',
                reportFiles: 'surefire-report.html',
                reportName: 'HTML Test Report',
                reportTitles: 'Unit Test Results'
            ])

            cleanWs()
        }

        success {
            echo '✅ Все тесты пройдены успешно!'
        }

        failure {
            echo '❌ Тесты упали!'
        }

        unstable {
            echo '⚠️  Тесты нестабильны'
        }
    }
}