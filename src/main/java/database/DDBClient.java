package database;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.s3.model.Region;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class DDBClient {

    final static String executableFile = "executableFile";
    final static String testFile = "testFile";
    final static String scriptFile = "scriptFile";

    AWSCredentialsProvider awsCredentialsProvider;
    AmazonDynamoDBClient client;
    DynamoDB dynamoDB;
    DynamoDBMapper mapper;

    public DDBClient() {
        /* initializing aws-related service providers */
        awsCredentialsProvider = new ProfileCredentialsProvider();
        client = new AmazonDynamoDBClient(awsCredentialsProvider);
        dynamoDB = new DynamoDB(new AmazonDynamoDBClient(new ProfileCredentialsProvider()));
        mapper = new DynamoDBMapper(client, awsCredentialsProvider);

        /* create tables if they do not exist */
        createTables();
    }


    /*
     * Create Dynamo tables : TestModule and TestJob
     * if tables do not exist.
     */
    public void createTables() {

        String[] tableNames = {"TestModule", "TestJob"};
        String tableName;

        for (int i = 0; i < tableNames.length; i++) {

            tableName = tableNames[i];

            try {
                // throws ResourceNotFoundException when table does not exist
                TableDescription table = client.describeTable(new DescribeTableRequest(tableName))
                        .getTable();

                // check if table has ACTIVE status
                boolean tableStatus = TableStatus.ACTIVE.toString().equals(table.getTableStatus());
                assert (tableStatus = true);
                System.err.println(tableName + " exists and is active");

            } catch (ResourceNotFoundException rnfe) {
                switch (tableName) {
                    case "TestModule":
                        createTable("TestModule", 10L, 5L, "TestModuleName", "S");
                        break;
                    case "TestJob":
                        /* TO DO : create secondary index for TestJob if needed */
                        createTable("TestJob", 10L, 5L, "TestModuleName", "S", "TestJobName", "S");
                        break;
                    default:
                        System.err.println("Attempted to create table -> " + tableName + "\n");
                        break;
                }
            }
        }
    }


    /* constructor for tables with only partition key */
    private void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits,
                             String partitionKeyName, String partitionKeyType) {
        createTable(tableName, readCapacityUnits, writeCapacityUnits,
                partitionKeyName, partitionKeyType, null, null);
    }


    /* constructor for tables with both partition and sort keys */
    private void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits,
                             String partitionKeyName, String partitionKeyType, String sortKeyName, String sortKeyType) {

        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(partitionKeyName)
                    .withKeyType(KeyType.HASH)); //Partition key

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(partitionKeyName)
                    .withAttributeType(partitionKeyType));

            if (sortKeyName != null) {
                keySchema.add(new KeySchemaElement()
                        .withAttributeName(sortKeyName)
                        .withKeyType(KeyType.RANGE)); //Sort key
                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName(sortKeyName)
                        .withAttributeType(sortKeyType));
            }

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));

            request.setAttributeDefinitions(attributeDefinitions);

            System.err.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.err.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
            table.waitForActive();

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }


    /**
     * To create a new test module
     * @param testModuleName is unique for every testModule
     * @param executableFilePath full file path for executable/binary
     * @param testFilePath full file path for test case file
     * @param scriptFilePath full file path for test/run script
     */
    public void createNewTestModule(String testModuleName, String executableFilePath, String testFilePath,
                                    String scriptFilePath) {
        TestModule testModule = new TestModule();
        testModule.setName(testModuleName);

        S3Link s3Link_executableFile = mapper.createS3Link(Region.US_Standard, "testmate", testModule.getName() +
                "/" + executableFile);
        s3Link_executableFile.uploadFrom(new File(executableFilePath));
        testModule.setExecutableFile(s3Link_executableFile);

        S3Link s3Link_testFile = mapper.createS3Link(Region.US_Standard, "testmate", testModule.getName() +
                "/" + testFile);
        s3Link_testFile.uploadFrom(new File(testFilePath));
        testModule.setTestFile(s3Link_testFile);

        S3Link s3Link_scriptFile = mapper.createS3Link(Region.US_Standard, "testmate", testModule.getName() +
                "/" + scriptFile);
        s3Link_scriptFile.uploadFrom(new File(scriptFilePath));
        testModule.setScriptFile(s3Link_scriptFile);

        testModule.setLatestTestJobName("NA");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date currentDate = new Date();
        testModule.setLatestTestJobTimestamp(dateFormat.format(currentDate));

        mapper.save(testModule);
    }


    /**
     * The executablefile, testfile and scriptfile are downloaded to TestModuleFiles folder
     * and can be accessed using the filePathMap in the returned TestModule object
     * @param testModuleName
     * @return
     */
    public TestModule getTestModule(String testModuleName) {

        TestModule getTestModule = mapper.load(TestModule.class, testModuleName);

        /* check if the item exists */
        if(getTestModule == null) {
            System.err.println("TestModule " + testModuleName + "does not exist\n");
            return null;
        }

        if(!getTestModule.filePathMap.isEmpty()) {
            getTestModule.filePathMap.clear();
        }

        String projectFilePath = new java.io.File("").getAbsolutePath() + "/src/main/java/database/TestModuleFiles/";

        S3Link s3Link_executableFile = getTestModule.getExecutableFile();
        String executableFilePath = projectFilePath + testModuleName + "/" + executableFile;
        getTestModule.filePathMap.put(executableFile, executableFilePath);
        s3Link_executableFile.downloadTo(new File(executableFilePath));

        S3Link s3Link_testFile = getTestModule.getTestFile();
        String testFilePath = projectFilePath + testModuleName + "/" + testFile;
        getTestModule.filePathMap.put(testFile, testFilePath);
        s3Link_testFile.downloadTo(new File(testFilePath));

        S3Link s3Link_scriptFile = getTestModule.getScriptFile();
        String scriptFilePath = projectFilePath + testModuleName + "/" + scriptFile;
        getTestModule.filePathMap.put(scriptFile, scriptFilePath);
        s3Link_scriptFile.downloadTo(new File(scriptFilePath));

        return getTestModule;
    }


    /**
     *
     * @return List of TestModule
     * NOTE : files cannot be accessed. You need to call getTestModule with the
     * name of the TestModule whose files you want to access.
     */
    public List<TestModule> getAllTestModules() {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<TestModule> scanResult = mapper.scan(TestModule.class, scanExpression);

        for(TestModule testModule: scanResult) {
            System.err.println(testModule.getName());
            System.err.println();
        }

        return scanResult;
    }


    /**
     * Each testModule keeps track of its latest job by calling this
     * @param testModuleName
     * @param testJobName
     * @param testTimestamp
     */
    public void updateTestModuleWithNewTestJob(String testModuleName, String testJobName, String testTimestamp) {
        TestModule updateTestModule = mapper.load(TestModule.class, testModuleName);
        if(updateTestModule == null) {
            System.err.println("Couldn't update testModule : " + testModuleName);
            return;
        }

        updateTestModule.setLatestTestJobName(testJobName);
        updateTestModule.setLatestTestJobTimestamp(testTimestamp);

        mapper.save(updateTestModule);
    }


    /**
     * To create a new test job using the name of the test module and other params
     * @param testModuleName
     * @param noOfTotalTests
     * @param testsPassed
     * @param testsFailed
     * @return the name of the testJob.
     */
    public String createNewTestJob(String testModuleName, int noOfTotalTests, int testsPassed, int testsFailed) {
        TestJob testJob = new TestJob();

        testJob.setTestModuleName(testModuleName);

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date currentDate = new Date();
        String testJobName = testModuleName + "_job_" + dateFormat.format(currentDate);
        testJob.setTestJobName(testJobName);


        assert(noOfTotalTests == testsPassed + testsFailed);
        testJob.setTotalTests(noOfTotalTests);
        testJob.setTestsPassed(testsPassed);
        testJob.setTestsFailed(testsFailed);

        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        testJob.setTimestamp(dateFormat.format(currentDate));

        mapper.save(testJob);

        return testJobName;
    }


    /**
     * Retrieves the test job given its name
     * @param testJobName
     * @return
     */
    public TestJob getTestJob(String testJobName) {

        /* Parse the testJobName to get the testModuleName*/
        String[] splitTestJobName = testJobName.split("_");
        String testModuleName = splitTestJobName[0];
        System.err.println("TestModuleName for the TestJob : " + testModuleName);

        /* Get the TestJob */
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(testModuleName));
        eav.put(":val2", new AttributeValue().withS(testJobName));

        DynamoDBQueryExpression<TestJob> queryExpression = new DynamoDBQueryExpression<TestJob>()
                .withKeyConditionExpression("TestModuleName = :val1 and TestJobName = :val2")
                .withExpressionAttributeValues(eav);

        List<TestJob> requiredTestJobs = mapper.query(TestJob.class, queryExpression);

        assert (requiredTestJobs.size() <= 1);

        if(requiredTestJobs.size() == 0) {
            System.err.println("Test Job not found");
            return null;
        } else {
            System.err.println("Test Job found :" + requiredTestJobs.get(0).getTestJobName());
            return requiredTestJobs.get(0);
        }
    }


    /**
     * Returns Test Jobs with the same Test Module
     * @param testModuleName : name of the test module
     * @return list of Test Jobs
     */
    public List<TestJob> getAllTestJobsForATestModule(String testModuleName) {

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(testModuleName));

        DynamoDBQueryExpression<TestJob> queryExpression = new DynamoDBQueryExpression<TestJob>()
                .withKeyConditionExpression("TestModuleName = :val1")
                .withExpressionAttributeValues(eav);

        List<TestJob> jobsWithGivenTestModuleName = mapper.query(TestJob.class, queryExpression);

        System.err.println("Listing jobs for TestModule : " + testModuleName);
        for(TestJob testJob: jobsWithGivenTestModuleName) {
            System.err.println("TestJob : " + testJob.getTestJobName());
        }

        return jobsWithGivenTestModuleName;
    }

}
