package database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;

import java.util.HashMap;


/* TestModuleObject */
@DynamoDBTable(tableName="TestModules")
public class TestModules {
    private String name;
    private S3Link executableFile;
    private S3Link testFile;
    private S3Link scriptFile;
    public HashMap<String, String> filePathMap = new HashMap<>();

    // Partition key
    @DynamoDBHashKey(attributeName="TestModuleName")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName="executableFile")
    public S3Link getExecutableFile() {
        return executableFile;
    }

    public void setExecutableFile(S3Link executableFile) {
        this.executableFile = executableFile;
    }

    @DynamoDBAttribute(attributeName="testFile")
    public S3Link getTestFile() {
        return testFile;
    }

    public void setTestFile(S3Link testFile) {
        this.testFile = testFile;
    }

    @DynamoDBAttribute(attributeName="scriptFile")
    public S3Link getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(S3Link scriptFile) {
        this.scriptFile = scriptFile;
    }
}