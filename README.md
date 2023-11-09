
## The solution

This project uses a lot of Google Client library. So, I decided to use Google Cloud Platform to host the project.  
I want to emphasize this is a personal project. That means the high cost is not acceptable.    
So, the first priority in choosing a service is lower cost. :) 

### Services I chose is below:
1. Cloud Function to call Google Photo APIs
2. Cloud Run to upload the youtube video
3. Cloud scheduler to trigger these function at 00:00 AM
4. Thinking about data storage...


### Reference link
[Configure a Cloud Run job to execute on a schedule](https://cloud.google.com/run/docs/execute/jobs-on-schedule#using-scheduler)  
[Triggering Cloud Run Jobs with Cloud Scheduler](https://codelabs.developers.google.com/cloud-run-jobs-and-cloud-scheduler#0)