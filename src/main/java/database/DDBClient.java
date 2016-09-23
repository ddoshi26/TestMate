package database;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.s3.model.Region;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Do NOT call these methods directly, use the
 * methods in DDBManager to handle all cases
 */

public class DDBClient {


    AWSCredentialsProvider awsCredentialsProvider;
    AmazonDynamoDBClient client;
    DynamoDB dynamoDB;
    DynamoDBMapper mapper;

    DDBClient() {
        /* initializing aws-related service providers */
        awsCredentialsProvider = new ProfileCredentialsProvider();
        client = new AmazonDynamoDBClient(awsCredentialsProvider);
        dynamoDB = new DynamoDB(new AmazonDynamoDBClient(new ProfileCredentialsProvider()));
        mapper = new DynamoDBMapper(client, awsCredentialsProvider);

        /* create tables if they do not exist */
        createTables();
    }


    /*
     * Create Dynamo tables : TestModules and TestJobs
     * if tables do not exist.
     */
    public void createTables() {

        String[] tableNames = {"TestModules", "TestJobs"};
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
                System.out.println(tableName + " exists and is active");

            } catch (ResourceNotFoundException rnfe) {
                switch (tableName) {
                    case "TestModules":
                        createTable("TestModules", 10L, 5L, "TestModuleName", "S");
                        break;
                    case "TestJobs":
                        /* TO DO : create secondary index for TestJobs if needed */
                        createTable("TestJobs", 10L, 5L, "TestJobName", "S");
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

            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.out.println("Waiting for " + tableName
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
        TestModules testModule = new TestModules();
        testModule.setName(testModuleName);

        S3Link s3Link_executableFile = mapper.createS3Link(Region.US_Standard, "testmate", testModule.getName() +
                "/executableFile");
        s3Link_executableFile.uploadFrom(new File(executableFilePath));
        testModule.setExecutableFile(s3Link_executableFile);

        S3Link s3Link_testFile = mapper.createS3Link(Region.US_Standard, "testmate", testModule.getName() +
                "/testFile");
        s3Link_testFile.uploadFrom(new File(testFilePath));
        testModule.setTestFile(s3Link_testFile);

        S3Link s3Link_scriptFile = mapper.createS3Link(Region.US_Standard, "testmate", testModule.getName() +
                "/scriptFile");
        s3Link_scriptFile.uploadFrom(new File(scriptFilePath));
        testModule.setScriptFile(s3Link_scriptFile);

        mapper.save(testModule);
    }


    /**
     * The executablefile, testfile and scriptfile are downloaded to TestModuleFiles folder
     * and can be accessed using the filePathMap in the returned TestModules object
     * @param testModuleName
     * @return
     */
    public TestModules getTestModule(String testModuleName) {

        TestModules getTestModule = mapper.load(TestModules.class, testModuleName);

        /* check if the item exists */
        if(getTestModule == null) {
            System.err.println("item does not exist\n");
            return null;
        }

        if(!getTestModule.filePathMap.isEmpty()) {
            getTestModule.filePathMap.clear();
        }

        String projectFilePath = new java.io.File("").getAbsolutePath() + "/src/main/java/database/TestModuleFiles/";

        S3Link s3Link_executableFile = getTestModule.getExecutableFile();
        String executableFilePath = projectFilePath + testModuleName + "/executableFile";
        getTestModule.filePathMap.put("executableFile", executableFilePath);
        s3Link_executableFile.downloadTo(new File(executableFilePath));

        S3Link s3Link_testFile = getTestModule.getTestFile();
        String testFilePath = projectFilePath + testModuleName + "/testFile";
        getTestModule.filePathMap.put("testFile", testFilePath);
        s3Link_testFile.downloadTo(new File(testFilePath));

        S3Link s3Link_scriptFile = getTestModule.getScriptFile();
        String scriptFilePath = projectFilePath + testModuleName + "/testFile";
        getTestModule.filePathMap.put("scriptFile", scriptFilePath);
        s3Link_scriptFile.downloadTo(new File(scriptFilePath));

        return getTestModule;
    }


    // TO DO
    public Map<String, AttributeValue> getAllTestModules() {
        return null;
    }

    // TO DO
    public void createNewTestJob() {

    }

    // TO DO
    public TestJobs getTestJob() {
        return null;
    }


}
