The system consists of 4 functional components which cover all the use cases, namely, user interface, wayfinding algorithm (routing backend), offline Bluetooth communication (P2P network), and the server. 

# Architecture
![alt_text](https://github.com/the-sagar/Dynamic-Sustainable-Wayfinding/blob/master/images/technical%20diagram.png "image_tooltip")

# The system is developed using eXtreme programming approaches 
- Development and deployment in iterations
- Pair programming
- Test-driven development
- Continuous integration/Continuous deployment
- Collective code ownership
- Refactoring and code standards


# Compile and deploy instructions
# Android Application
- Prepare
- Install nvm, then run
- nvm install v15.6.0
or alternatively, download and install node.js v15.6.0 manually.
- Install yarn v1.22.10
- Install VSCode
- Install Android Studio

The recommended plugins are listed in .vscode/extensions.json.
When you open the workspace, a message box will appear in the lower right corner and click "Install All Workspace Recommended Extensions" to install them all, then, confirm each plugin is in the enabled state.
- Install Android SDK from Android Studio
- Tools > SDK Manager > SDK Platforms > Android 11.0 (R) API Level 30
- Install Android NDK from Android Studio
- Tools > SDK Manager > SDK Tools, tick the followings
- Android SDK Build-Tools
- NDK (Side by side)
- Android SDK Command-line Tools (latest)
- CMake
- Android SDK Platform-Tools

Installation

After downloading the project, you need to execute the following command, which will install all the dependencies.
# run at root directory of this project
- rm -rf node_modules yarn.lock
- yarn install

After all modules installed, you can connect your Android phone with USB debugging enabled or start an Android emulator to run the app.
- yarn android
Or run the react native manually, then debug the app in Android Studio.
- yarn start
# Metro bundler
- adb reverse tcp:8081 tcp:8081
# Spring boot server
- adb reverse tcp:8090 tcp:8090
# Routing backend
- adb reverse tcp:9000 tcp:9000

To build the release app, run the following commands manually.
- yarn build:android
- cd android
- ./gradlew assembleRelease

# Server Deployment
# Manual

Install the following tools on the machine - 
- Java
- Go
- NodeJS
- Maven
- MySQL

Create a new database (with any name) on your MySQL instance.

Open the file 
- SpringServer/src/main/resources/application.properties

Replace the following properties according to the SQL server - 
- spring.datasource.url - Provide the port number and database name 
- spring.datasource.username - Provide username for SQL database
- spring.datasource.password - Provide password for SQL database


Now go inside the code directory - 
- cd SpringServer

Now for building the components run the following commands - 
- ./build_routing.sh
- ./build_admPanel.sh

Finally run the following commands to build and deploy the server - 
- mvn clean install -DskipTests = true
- cd target
- java -jar server-0.0.1-SNAPSHOT.war

# Automatic
Following tools are required for automatic deployment - 
- Jenkins
- Docker
- SonarQube (stand-alone or dockerized)

Download and install Jenkins - 
- brew install jenkins-lts
- brew services start jenkins-lts  (This will start the jenkins server)

Alternatively you can install jenkins from the following url - 
- https://www.jenkins.io/download/

Open the jenkins server on http://localhost:8080/

Configure the jenkins with following plugins - 
- Pipeline
- Docker (Configure the path properly)
- Maven
- Java
- NodeJS
- Go

Configure the Jenkins server with SonarQube by following the instructions from below - 
- https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-jenkins/#:~:text=Configure%20your%20SonarQube%20server(s,a%20'Secret%20Text'%20credential.

On the jenkins dashboard - 
- Create a new item
- Name the item (any name)
- Select “Pipeline” as the type of job

Create the Pipeline with the following configurations - 
- Provide github link
- Choose pipeline script from SCM
- Set Script-path as "JenkinsFile"

Click on save

Now go on the pipeline and select “Build Now"

The pipeline will automatically build and deploy the server in a docker

