package database;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


/* TestJob object */
@DynamoDBTable(tableName = "TestJob")
public class TestJob {
    private String testModuleName;
    private String testJobName;
    private String timestamp;
    private int totalTests;
    private int testsPassed;
    private int testsFailed;



    /* Hash Key : TestModuleName */
    @DynamoDBHashKey(attributeName = "TestModuleName")
    public String getTestModuleName() {
        return testModuleName;
    }

    public void setTestModuleName(String testModuleName) {
        this.testModuleName = testModuleName;
    }

    /* Range Key : TestJobName is of the form TestModuleName_TimeStamp */
    @DynamoDBRangeKey(attributeName = "TestJobName")
    public String getTestJobName() {
        return testJobName;
    }

    public void setTestJobName(String testJobName) {
        this.testJobName = testJobName;
    }

    @DynamoDBAttribute(attributeName = "Timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute(attributeName = "TotalTestsRun")
    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    @DynamoDBAttribute(attributeName = "TestsPassed")
    public int getTestsPassed() {
        return testsPassed;
    }

    public void setTestsPassed(int testsPassed) {
        this.testsPassed = testsPassed;
    }

    @DynamoDBAttribute(attributeName = "TestsFailed")
    public int getTestsFailed() {
        return testsFailed;
    }

    public void setTestsFailed(int testsFailed) {
        this.testsFailed = testsFailed;
    }

}
