pipeline {
    agent any  // 모든 Jenkins 에이전트에서 실행 가능

    stages {
        stage('Checkout') {
            steps {
                // GitHub에서 코드 체크아웃
                checkout scm
                // Gradle 래퍼에 실행 권한 부여 (Linux 환경에서 필요)
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                // Gradle을 사용하여 애플리케이션 빌드
                // clean: 이전 빌드 결과물 삭제
                // build: 새로운 빌드 실행
                sh './gradlew clean build'
            }
        }

        stage('Docker Build') {
            steps {
                // Docker 이미지 빌드
                // -t: 태그 지정
                // .: 현재 디렉토리의 Dockerfile 사용
                sh 'docker build -t b2b:latest .'
            }
        }

        stage('Deploy') {
            steps {
                // 이전 컨테이너와 네트워크 정리
                // --remove-orphans: 관련 없는 컨테이너도 함께 제거
                sh 'docker-compose down --remove-orphans'
                // 새로운 컨테이너 시작
                // -d: 백그라운드에서 실행
                sh 'docker-compose up -d'
            }
        }
    }

    post {
        always {
            // 빌드 완료 후 항상 실행
            // 작업 공간 정리
            cleanWs()
        }
    }
}