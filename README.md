## Installing and running

0.1. Download and install `gradle` >= 5.2.1 (IDEA may offer install it automatically) [#Install and setup Gradle](see this);
0.2. Download and install `open-jdk` 1.8 (IDEA may offer install it automatically) [#Install and setup Open-JDK](see this);
1. Open IDEA and import downloaded `speech-to-text-demo` project;
2. Open `build.gradle` project and click button on emerged button (top right corner) `Load Gradle Changes`;
3. After building project by `gradle` open `DemoServer.java` file and try run it from code listing;
4. Into `Run/Debug Configurations` has been created new run configuration with name `DemoServer`;
5. Need add path (the best way specify absolut path) to `config/local.json` file as argument into `Program arguments` field;
6. However, you must add Env-var `GOOGLE_APPLICATION_CREDENTIALS={path-to-project}/nodal-thunder-279319-7a4d2b6d03a6.json` into `Environment variables` field;
7. Open `config/local.json` file and specify path (the best way specify absolut path) to `static-content` directory into `staticContentDir` field;
8. You can change other fields into this file if you really wanna it.


### Install and setup Gradle

To install and setup gradle follow these steps:
1. Download the latest Gradle distribution from [https://gradle.org/releases](link)
2. Unpack the distribution `mkdir /opt/gradle && unzip -d /opt/gradle gradle-{version}-bin.zip`
3. Configure your system environment `export PATH=$PATH:/opt/gradle/gradle-{version}}/bin`

To install and setup gradle from Intellij IDEA try these steps:

1. File -> Settings -> "Build, Execution, Deployment" -> Build Tools -> Gradle;
2. Into "Gradle user home" specify path to your gradle.

Another way! Look at bottom of current "Settings" window and find gradle frame. Select into "Build and run using" field - Intellij IDEA


### Install and setup Open-JDK

To install JDK yourself just need:

1. Download JDK kit needed version from this [http://www.oracle.com/technetwork/java/javase/downloads/index.html](link) and run installing;
2. Run this command in terminal `sudo apt-get update && sudo apt-get install openjdk-{version}-jdk`;

To install JDK from Intellij IDEA try to do these steps:

1. File -> Project Structure -> Platform Settings -> SDKs;
2. Click on "+" and select "Download SDK";
3. Into opened window select:
   Version: 1.8
   Vendor: Amazon Corretto 1.8.0_302
   Location: default


## Test user credentials

username: `T3st0v`
password: `T3st0v`


## REST API of Speechpad Handler

First important stage is login as test user!

### 1. Create -> POST 

Creates new archive and returns id, name and status. 

request:
`http://{address:port}/1/speechpad/create?model=default&name=test`

response:
```json
{
    "speechpadId": "{id}",
    "speechPadName": "test",
    "success": true
}
```

P.S.: I dont really know whf is it - POST request but passing params into URL... OMG!

### 2. getAll -> GET

Returns all archives.

request:
`http://{address:port}/1/speechpad/getAll`

response:

```json
{
	"allSpeechpad": [
		{
			"name": "test",
			"id": "{id}",
			"transcriber": {
				"model": "default",
				"ready": false,
				"transcribeResults": [],
				"logger": {
					"name": "RealtimeTranscriber"
				}
			}
		}
	],
	"success": true
}
```

### 3. get -> GET

Returns archive object by specified id.

request:
`http://{address:port}/1/speechpad/get?speechpad_id={id}`

response:
```json
{
	"speechpad": {
		"name": "test",
		"id": "{id}",
		"transcriber": {
			"model": "default",
			"ready": false,
			"transcribeResults": [],
			"logger": {
				"name": "RealtimeTranscriber"
			}
		}
	},
	"success": true
}
```

### 4. rename -> POST

Returns renamed archive object.

request:
`http://{address:port}/1/speechpad/rename?speechpad_id={id}&new_name=test_new_name`

response:
```json
{
	"newName": "test_new_name",
	"speechpadId": "{id}",
	"success": true
}
```

### 5. remove -> POST

Removes archive object by specified id.

request:
`http://{address:port}/1/speechpad/remove?speechpad_id={id}`

response:
```json
{
	"success": true
}
```
