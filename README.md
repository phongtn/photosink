
## The solution

This project uses a lot of Google Client library. So, I decided to use Google Cloud Platform to host the project.  
I want to emphasize this is a personal project. That means the high cost is not acceptable.    
So, the first priority in choosing a service is lower cost. :) 

### Services I chose is below:
1. Cloud Function to call Google Photo APIs
2. Cloud Run to upload the youtube video
3. Cloud scheduler to trigger these function at 00:00 AM
4. Thinking about data storage...

### Guideline for developer
The following guides illustrate how to use some features concretely:

### Environment

##### The programming language based on Java Platform
```shell
openjdk 17.0.6 2023-01-17 LTS
OpenJDK Runtime Environment Corretto-17.0.6.10.1 (build 17.0.6+10-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.6.10.1 (build 17.0.6+10-LTS, mixed mode, sharing)
```

##### Gradle to build
```shell
------------------------------------------------------------
Gradle 8.2.1
------------------------------------------------------------

Build time:   2023-07-10 12:12:35 UTC
Revision:     a38ec64d3c4612da9083cc506a1ccb212afeecaa

Kotlin:       1.8.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          17.0.6 (Amazon.com Inc. 17.0.6+10-LTS)
OS:           Mac OS X 14.1.1 aarch64
```

#### Database using Google Sheet


#### Application configuration
```
gg_sheet_id=Google Sheet IP
data_ranges=A2:E10
row_begin=2
column_status=E
column_link=F
```

#### How to build the service?
```shell
gradle clean build
```

```shell
docker build -t ptube:1.0 .
```

```shell
docker run --rm -it -p 8081:8081 ptube:1.0

```


### Reference link
[Configure a Cloud Run job to execute on a schedule](https://cloud.google.com/run/docs/execute/jobs-on-schedule#using-scheduler)  
[Triggering Cloud Run Jobs with Cloud Scheduler](https://codelabs.developers.google.com/cloud-run-jobs-and-cloud-scheduler#0)