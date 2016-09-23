package controllers;

import java.util.ArrayList;
import java.util.Iterator;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class mainController {

        static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(
                new ProfileCredentialsProvider()));

        static String tableName = "ExampleTable";

        /*public static void main(String[] args) throws Exception {

            System.out.println("Hello everyone! Welcome to TestMate ! \n");
            createExampleTable();
            System.out.println();
            System.out.println();
            listMyTables();
            //getTableInformation();
            //updateExampleTable();
            dynamoDB.

            //deleteExampleTable();
        }*/

        static void createExampleTable() {

            try {

                ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName("Id")
                        .withAttributeType("N"));

                ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
                keySchema.add(new KeySchemaElement()
                        .withAttributeName("Id")
                        .withKeyType(KeyType.HASH)); //Partition key

                CreateTableRequest request = new CreateTableRequest()
                        .withTableName(tableName)
                        .withKeySchema(keySchema)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withProvisionedThroughput(new ProvisionedThroughput()
                                .withReadCapacityUnits(5L)
                                .withWriteCapacityUnits(6L));

                System.out.println("Issuing CreateTable request for " + tableName);
                Table table = dynamoDB.createTable(request);

                System.out.println("Waiting for " + tableName
                        + " to be created...this may take a while...");
                table.waitForActive();

                getTableInformation();

            } catch (Exception e) {
                System.err.println("CreateTable request failed for " + tableName);
                System.err.println(e.getMessage());
            }

        }

        static void listMyTables() {

            TableCollection<ListTablesResult> tables = dynamoDB.listTables();
            Iterator<Table> iterator = tables.iterator();

            System.out.println("Listing table names");

            while (iterator.hasNext()) {
                Table table = iterator.next();
                System.out.println(table.getTableName());
            }
        }

        static void getTableInformation() {

            System.out.println("Describing " + tableName);

            TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
            System.out.format("Name: %s:\n" + "Status: %s \n"
                            + "Provisioned Throughput (read capacity units/sec): %d \n"
                            + "Provisioned Throughput (write capacity units/sec): %d \n",
                    tableDescription.getTableName(),
                    tableDescription.getTableStatus(),
                    tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
                    tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
        }

        static void updateExampleTable() {

            Table table = dynamoDB.getTable(tableName);
            System.out.println("Modifying provisioned throughput for " + tableName);

            try {
                table.updateTable(new ProvisionedThroughput()
                        .withReadCapacityUnits(6L).withWriteCapacityUnits(7L));

                table.waitForActive();
            } catch (Exception e) {
                System.err.println("UpdateTable request failed for " + tableName);
                System.err.println(e.getMessage());
            }
        }

        static void deleteExampleTable() {

            Table table = dynamoDB.getTable(tableName);
            try {
                System.out.println("Issuing DeleteTable request for " + tableName);
                table.delete();

                System.out.println("Waiting for " + tableName
                        + " to be deleted...this may take a while...");

                table.waitForDelete();
            } catch (Exception e) {
                System.err.println("DeleteTable request failed for " + tableName);
                System.err.println(e.getMessage());
            }
        }

    }




