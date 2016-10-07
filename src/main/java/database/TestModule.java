package database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;

import java.util.HashMap;


/* TestModule object */
@DynamoDBTable(tableName = "TestModule")
public class TestModule {
    private String name;
    private S3Link executableFile;
    private S3Link testFile;
    private S3Link scriptFile;
    private String latestTestJobName;
    private String latestTestJobTimestamp;

    public HashMap<String, String> filePathMap = new HashMap<>();


    // Partition key
    @DynamoDBHashKey(attributeName = "TestModuleName")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "ExecutableFile")
    public S3Link getExecutableFile() {
        return executableFile;
    }

    public void setExecutableFile(S3Link executableFile) {
        this.executableFile = executableFile;
    }

    @DynamoDBAttribute(attributeName = "TestFile")
    public S3Link getTestFile() {
        return testFile;
    }

    public void setTestFile(S3Link testFile) {
        this.testFile = testFile;
    }

    @DynamoDBAttribute(attributeName = "ScriptFile")
    public S3Link getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(S3Link scriptFile) {
        this.scriptFile = scriptFile;
    }

    @DynamoDBAttribute(attributeName = "LatestTestJobName")
    public String getLatestTestJobName() {
        return latestTestJobName;
    }

    public void setLatestTestJobName(String latestTestJobName) {
        this.latestTestJobName = latestTestJobName;
    }

    @DynamoDBAttribute(attributeName = "LatestTestJobTimestamp")
    public String getLatestTestJobTimestamp() {
        return latestTestJobTimestamp;
    }

    public void setLatestTestJobTimestamp(String latestTestJobTimestamp) {
        this.latestTestJobTimestamp = latestTestJobTimestamp;
    }
}