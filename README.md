If there is a problem with deployment, create a deploy_dir directory.
Then, build the WAR file using:

mvn clean package


Next, convert this WAR file into a ZIP file and copy all files, including the app file, into deploy_dir.
Finally, run the following command in deploy_dir:

gcloud app deploy --project <YOUR_GCP_PROJECT_ID>
