# Test-Mate

Pre-requisites:
* Oracle Java SDK
* Unix/Bash Environment
* Maven
* AWS account and AWS configured command line

# How to run the server? 
    1. tar xfv Test-Mate.tar
    2. cd Test-Mate/bin
    3. ./Test-Mate

  The server will take a couple of minutes to spool up. Expected messages are:
    **** TestMate - Server Initializing... ****
    TestModule exists and is active
    TestJob exists and is active
    bucket location = US 

# How to run the client?

    java - jar TestMate-1.0-SNAPSHOT-jar-with-dependencies.jar

The servlet will load up and connect to the server. Now to access the client, open
your browser and go to: http:localhost:8080. You should see the TestMate-Web
page on your browser.
