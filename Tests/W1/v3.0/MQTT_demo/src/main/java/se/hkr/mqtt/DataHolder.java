package se.hkr.mqtt;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataHolder {
    static MqttClient sampleclient;
    static boolean dbActivated = true;
    static boolean isConnected = false;
    static String activeUser = "deafault";
    static ArrayList<User> users = new ArrayList<User>();
    static String year = "2020"; //I will need to fix the year functionality
    static String month = "";
    static String week = "";
    static String day = "";
    static String dayStamp = ""; //the day is used as a tag. for a day by day use
    static String hour = ""; //the hour is used as tag for hour by hour use.
    static int datenum = 0;
    static int dailyLogsCount = 0;
    static int weeklyLogsCount = 0;
    static int monthlyLogsCount = 0;
    static int dailyLogsCount2 = 0; //for sector 2
    static int weeklyLogsCount2 = 0; //for sector 2
    static int monthlyLogsCount2 = 0; //for sector 2
    static HashMap<String, String>flags = new HashMap();
    static ArrayList<House> houses = new ArrayList<House>(); //this is the placeholder for the several Houses
    static int snapshotnum = 0;

    //Critical area for publishing the data
    static boolean publishing = true;


    static void hashmapsToFirestore(int identifier) throws InterruptedException {
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();
        snapshotnum++;
        System.out.println("");
        System.out.println("===============================");
        System.out.println("Saving the Snapshot #" + snapshotnum + " to the server");
        System.out.println("===============================");

        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier + "sslashs",
                "lastKnownValues" + month + year, houses.get(identifier).lastKnownValues);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier + "sslashs",
                "lastKnownTimeStamps" + month + year,houses.get(identifier).lastKnownTimeStamps);
        Thread.sleep(2000);

        //added to loader. duration optimized
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashscountsslashs",
                "dailyCountsslashs" + friendlydate + "sslashs",houses.get(identifier).dailyCount);
        Thread.sleep(2000);

        //added to loader. duration optimized
        fire.createDocumentfromAMap2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs" + year + "sslashs",
                "weeklyCountsslashs" + week + "sslashs",houses.get(identifier).weeklyCount);
        Thread.sleep(2000);

        //added to loader. duration optimized
        fire.createDocumentfromAMap2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs" + year + "sslashs",
                "monthlyCountsslashs" + month + "sslashs",houses.get(identifier).monthlyCount);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap3("databasesslashs" + identifier + "sslashsstatssslashssumsslashs",
                "dailySumsslashs" + friendlydate + "sslashs",houses.get(identifier).dailySum);
        Thread.sleep(2000);

        //added to loader. duration optimized
        fire.createDocumentfromAMap3("databasesslashs" + identifier
                        +"sslashsstatssslashssumsslashs" + year + "sslashs",
                "weeklySumsslashs" + week + "sslashs",houses.get(identifier).weeklySum);
        Thread.sleep(2000);

        //added to loader. duration optimized
        fire.createDocumentfromAMap3("databasesslashs" + identifier
                        + "sslashsstatssslashssumsslashs" + year + "sslashs",
                "monthlySumsslashs" + month + "sslashs",houses.get(identifier).monthlySum);
        Thread.sleep(2000);

        //added to loader. duration optimized.
        fire.createDocumentfromAMap3("databasesslashs" + identifier + "sslashsstatssslashsaveragesslashs",
                "dailyAveragesslashs" + friendlydate + "sslashs",houses.get(identifier).dailyAverage);
        Thread.sleep(2000);

        //added to loader. Duration optimized
        fire.createDocumentfromAMap3("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs" + year + "sslashs",
                "weeklyAveragesslashs" + week + "sslashs",houses.get(identifier).weeklyAverage);
        Thread.sleep(2000);

        //added to loader. Duration Optimized
        fire.createDocumentfromAMap3("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs" + year + "sslashs",
                "monthlyAveragesslashs" + month + "sslashs",houses.get(identifier).monthlyAverage);
        Thread.sleep(2000);

        //daily main logs
        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier + "sslashslogssslashsdailylogssslashs",
                "mainsslashs" + friendlydate,
                arrayListToHashMap(houses.get(identifier).mainLogs));
        Thread.sleep(2000);

        //weekly main logs
        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier
                        + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainWeeksslashs" + week,
                arrayListToHashMap(houses.get(identifier).mainLogsWeek));
        Thread.sleep(2000);

        //monthly main logs
        //added to loader
        fire.createDocumentfromAMap("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainMonthsslashs" + month,
                arrayListToHashMap(houses.get(identifier).mainLogsMonth));
        Thread.sleep(2000);

        //daily subscription specific log. Hopefully it works.
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs.entrySet()) {
            String topic =  entry.getKey();
            ArrayList<String> arrayList = entry.getValue();
            fire.createDocumentfromAMap("databasesslashs" + identifier
                            + "sslashslogssslashsdailylogssslashs" + dayStamp + "sslashs",
                    "mainLogssslashs" + topic,
                    arrayListToHashMap(arrayList));
            Thread.sleep(2000);
        }

        //Weekly subscription specific log. Hopefully it works.
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).wlogs.entrySet()) {
            String topic =  entry.getKey();
            ArrayList<String> arrayList = entry.getValue();
            fire.createDocumentfromAMap("databasesslashs" + identifier
                            + "sslashslogssslashsweeklylogssslashs" + week + year + "sslashs",
                    "mainLogssslashs" + topic,
                    arrayListToHashMap(arrayList));
            Thread.sleep(2000);
        }

        //Monthly subscription specific log. Hopefully it works.
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).wlogs.entrySet()) {
            String topic =  entry.getKey();
            ArrayList<String> arrayList = entry.getValue();
            fire.createDocumentfromAMap("databasesslashs" + identifier
                            + "sslashslogssslashsmonthlylogssslashs" + month + year + "sslashs",
                    "mainLogssslashs" + topic,
                    arrayListToHashMap(arrayList));
            Thread.sleep(2000);
        }


        //============================ SECTOR 2 STARTS HERE =======================================

        //daily main logs 2
        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier + "sslashslogssslashsdailylogssslashs",
                "main2sslashs" + friendlydate,
                arrayListToHashMap(houses.get(identifier).mainLogs2));
        Thread.sleep(2000);

        //weekly main logs 2
        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier
                        + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainWeek2sslashs" + week,
                arrayListToHashMap(houses.get(identifier).mainLogsWeek2));
        Thread.sleep(2000);

        //monthly main logs 2
        //added to loader
        fire.createDocumentfromAMap("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainMonth2sslashs" + month,
                arrayListToHashMap(houses.get(identifier).mainLogsMonth2));
        Thread.sleep(2000);

        //daily subscription specific log 2. Hopefully it works.
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs2.entrySet()) {
            String topic =  entry.getKey();
            ArrayList<String> arrayList = entry.getValue();
            fire.createDocumentfromAMap("databasesslashs" + identifier
                            + "sslashslogssslashsdailylogssslashs" + dayStamp + "sslashs",
                    "mainLogs2sslashs" + topic,
                    arrayListToHashMap(arrayList));
            Thread.sleep(2000);
        }

        //Weekly subscription specific log 2. Hopefully it works.
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).wlogs2.entrySet()) {
            String topic =  entry.getKey();
            ArrayList<String> arrayList = entry.getValue();
            fire.createDocumentfromAMap("databasesslashs" + identifier
                            + "sslashslogssslashsweeklylogssslashs" + week + year + "sslashs",
                    "mainLogs2sslashs" + topic,
                    arrayListToHashMap(arrayList));
            Thread.sleep(2000);
        }

        //Monthly subscription specific log 2. Hopefully it works.
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).wlogs2.entrySet()) {
            String topic =  entry.getKey();
            ArrayList<String> arrayList = entry.getValue();
            fire.createDocumentfromAMap("databasesslashs" + identifier
                            + "sslashslogssslashsmonthlylogssslashs" + month + year + "sslashs",
                    "mainLogs2sslashs" + topic,
                    arrayListToHashMap(arrayList));
            Thread.sleep(2000);
        }

        //============================ SECTOR 2 ENDS HERE =======================================

        //daily Count stats in a day by day basis
        for (Map.Entry<String, Integer[]> entry : houses.get(identifier).dailyCountdbd.entrySet()) {
            String topic =  entry.getKey();
            Integer[] arr = entry.getValue();
            HashMap map = arrayIntegerToHashMap(arr);
            //displayDatafromAMapString(0, map, "Hashmap of Count");
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("countdbd" + month + year,firestorepath, map);
            Thread.sleep(2000);
        }

        //daily Count stats in a hour by hour basis
        for (Map.Entry<String, Integer[]> entry : houses.get(identifier).dailyCounthbh.entrySet()) {
            String topic =  entry.getKey();
            Integer[] arr = entry.getValue();
            HashMap map = arrayIntegerToHashMap(arr);
            //displayDatafromAMapString(0, map, "Hashmap of Count");
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("counthbh" + dayStamp,firestorepath, map);
            Thread.sleep(2000);
        }

        //Monthly Count stats in a hour by hour basis
        for (Map.Entry<String, Integer[]> entry : houses.get(identifier).dailyCountmhbh.entrySet()) {
            String topic =  entry.getKey();
            Integer[] arr = entry.getValue();
            HashMap map = arrayIntegerToHashMap(arr);
            //displayDatafromAMapString(0, map, "Hashmap of Count");
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("countmhbh" + month + year,firestorepath, map);
            Thread.sleep(2000);
        }

        //daily Sum stats in a day by day basis
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailySumdbd.entrySet()) {
            String topic =  entry.getKey();
            Double[] arr = entry.getValue();
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("sumdbd" + month + year,
                    firestorepath,
                    arrayDoubleToHashMap(arr));
            Thread.sleep(2000);
        }

        //daily Sum stats in a hour by hour basis
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailySumhbh.entrySet()) {
            String topic =  entry.getKey();
            Double[] arr = entry.getValue();
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("sumhbh" + dayStamp,
                    firestorepath,
                    arrayDoubleToHashMap(arr));
            Thread.sleep(2000);
        }

        //Monthly Sum stats in a hour by hour basis
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailySummhbh.entrySet()) {
            String topic =  entry.getKey();
            Double[] arr = entry.getValue();
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("summhbh" + month + year,
                    firestorepath,
                    arrayDoubleToHashMap(arr));
            Thread.sleep(2000);
        }

        //daily Average stats in a day by day basis
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailyAveragedbd.entrySet()) {
            String topic =  entry.getKey();
            Double[] arr = entry.getValue();
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("averagedbd" + month + year,
                    firestorepath,
                    arrayDoubleToHashMap(arr));
            Thread.sleep(2000);
        }

        //daily Average stats in a hour by hour basis
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailyAveragehbh.entrySet()) {
            String topic =  entry.getKey();
            Double[] arr = entry.getValue();
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("averagehbh" + dayStamp,
                    firestorepath,
                    arrayDoubleToHashMap(arr));
            Thread.sleep(2000);
        }

        //Monthly Average stats in a hour by hour basis
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailyAveragemhbh.entrySet()) {
            String topic =  entry.getKey();
            Double[] arr = entry.getValue();
            String firestorepath = topic.replace("/", "sslashs");
            fire.createDocumentfromAMap("averagemhbh" + month + year,
                    firestorepath,
                    arrayDoubleToHashMap(arr));
            Thread.sleep(2000);
        }





        System.out.println("");
        System.out.println("============================");
        System.out.println("Snapshot #" + snapshotnum + " saved to the server");
        System.out.println("============================");
    }


    //This is the loader
    static void loadHashMapsFromFireStore(int identifier) throws InterruptedException {
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();

        //loading the users
        System.out.println("Users are loading");
        DB db = new DB();
        try {
            db.loadUsers();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Last known Values is loading");
        houses.get(identifier).lastKnownValues = fire.readDocument("databasesslashs"
                        + identifier
                        + "sslashs",
                "lastKnownValues" + month + year);
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Last known Timestamps is loading");
        houses.get(identifier).lastKnownTimeStamps = fire.readDocument("databasesslashs"
                        + identifier + "sslashs",
                "lastKnownTimeStamps" + month + year);
        Thread.sleep(2000);

        //added to the publisher. Duration is optimized.
        System.out.println("Daily Count is loading");
        houses.get(identifier).dailyCount = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs",
                "dailyCountsslashs" + friendlydate.toString() + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. duration is optimized
        System.out.println("Weekly Count is loading");
        houses.get(identifier).weeklyCount = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs" + year + "sslashs",
                "weeklyCountsslashs" + week + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. Duration optimized
        System.out.println("Monthly Count is loading");
        houses.get(identifier).monthlyCount = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs" + year + "sslashs",
                "monthlyCountsslashs" + month + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. duration optimized
        System.out.println("Daily Sum is loading");
        houses.get(identifier).dailySum = fire.readDocument3("databasesslashs" + identifier
                        + "sslashsstatssslashssumsslashs",
                "dailySumsslashs" + friendlydate + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. Duration optimized
        System.out.println("Weekly Sum is loading");
        houses.get(identifier).weeklySum = fire.readDocument3("databasesslashs" + identifier
                        +"sslashsstatssslashssumsslashs" + year + "sslashs",
                "weeklySumsslashs" + week + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. Duration Optimized
        System.out.println("Monthly Sum is loading");
        houses.get(identifier).monthlySum = fire.readDocument3("databasesslashs" + identifier
                        + "sslashsstatssslashssumsslashs"  + year + "sslashs",
                "monthlySumsslashs" + month + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. duration optimized
        System.out.println("Daily Average is loading");
        houses.get(identifier).dailyAverage = fire.readDocument3("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs",
                "dailyAveragesslashs" + friendlydate + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. duration optimized
        System.out.println("Weekly Average is loading");
        houses.get(identifier).weeklyAverage = fire.readDocument3("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs" + year + "sslashs",
                "weeklyAveragesslashs" + week + "sslashs");
        Thread.sleep(2000);

        //added to the publisher. duration optimized
        System.out.println("Monthly Average is loading");
        houses.get(identifier).monthlyAverage = fire.readDocument3("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs" + year + "sslashs",
                "monthlyAveragesslashs" + month + "sslashs");
        Thread.sleep(2000);

        //daily main logs
        System.out.println("Daily Main Logs are loading");
//        houses.get(identifier).mainLogs = hashMapToArrayList(
//                fire.readDocument("databasesslashs" + identifier + "sslashslogssslashsdailylogssslashs",
//                "mainsslashs" + friendlydate));
        hashMapToArrayList2(houses.get(identifier).mainLogs, fire.readDocument("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs",
                "mainsslashs" + friendlydate));
        Thread.sleep(2000);

        //weekly main logs
        System.out.println("Weekly Main Logs are loading");
        hashMapToArrayList2(houses.get(identifier).mainLogsWeek, fire.readDocument("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainWeeksslashs" + week));
        Thread.sleep(2000);

        //Monthly main logs
        System.out.println("Monthly Main Logs are loading");
        hashMapToArrayList2(houses.get(identifier).mainLogsMonth, fire.readDocument("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainMonthsslashs" + month));
        Thread.sleep(2000);

        //needs to be on the last loads. especailly after the lastKnownValues.
        System.out.println("Subscription specific Logs are loading");
        loadSpecificLogs(identifier);
        Thread.sleep(2000);

        //needs to be on the last loads. especailly after the lastKnownValues.
        System.out.println("Weekly Subscription specific Logs are loading");
        loadSpecificLogsW(identifier);
        Thread.sleep(2000);

        //needs to be on the last loads. especailly after the lastKnownValues.
        System.out.println("Monthly Subscription specific Logs are loading");
        loadSpecificLogsM(identifier);
        Thread.sleep(2000);

        //Making the specific logs human readable.
        System.out.println("Subscription specific Logs are prepared");
        prepareSpecificLogs(identifier);
        Thread.sleep(2000);

        //Making the weekly specific logs human readable.
        System.out.println("Weekly Subscription specific Logs are prepared");
        prepareSpecificLogsW(identifier);
        Thread.sleep(2000);

        //Making the monthly specific logs human readable.
        System.out.println("Monthly Subscription specific Logs are prepared");
        prepareSpecificLogsM(identifier);
        Thread.sleep(2000);

        //Making the main logs human readable
        System.out.println("Main Logs are prepared");
        prepareMainLogs(identifier);
        Thread.sleep(2000);

        //loading the daily logs. After implementation this method needs to be last in this method.
        System.out.println("Loading the day by day logs");
        loadDailyLogs(identifier, friendlydate);
        Thread.sleep(2000);

        //============================ SECTOR 2 STARTS HERE =======================================

        //daily main logs 2
        System.out.println("Daily Main Logs 2 are loading");
//        houses.get(identifier).mainLogs = hashMapToArrayList(
//                fire.readDocument("databasesslashs" + identifier + "sslashslogssslashsdailylogssslashs",
//                "mainsslashs" + friendlydate));
        hashMapToArrayList2(houses.get(identifier).mainLogs2, fire.readDocument("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs",
                "main2sslashs" + friendlydate));
        Thread.sleep(2000);

        //weekly main logs 2
        System.out.println("Weekly Main Logs 2 are loading");
        hashMapToArrayList2(houses.get(identifier).mainLogsWeek2, fire.readDocument("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainWeek2sslashs" + week));
        Thread.sleep(2000);

        //Monthly main logs 2
        System.out.println("Monthly Main Logs 2 are loading");
        hashMapToArrayList2(houses.get(identifier).mainLogsMonth2, fire.readDocument("databasesslashs"
                        + identifier + "sslashslogssslashsdailylogssslashs" + year + "sslashs",
                "mainMonth2sslashs" + month));
        Thread.sleep(2000);

        //needs to be on the last loads. especailly after the lastKnownValues.
        System.out.println("Subscription specific Logs 2 are loading");
        loadSpecificLogs2(identifier);
        Thread.sleep(2000);

        //needs to be on the last loads. especailly after the lastKnownValues.
        System.out.println("Weekly Subscription specific Logs 2 are loading");
        loadSpecificLogsW2(identifier);
        Thread.sleep(2000);

        //needs to be on the last loads. especailly after the lastKnownValues.
        System.out.println("Monthly Subscription specific Logs 2 are loading");
        loadSpecificLogsM2(identifier);
        Thread.sleep(2000);

        //Making the specific logs human readable.
        System.out.println("Subscription specific Logs 2 are prepared");
        prepareSpecificLogs2(identifier);
        Thread.sleep(2000);

        //Making the weekly specific logs 2 human readable.
        System.out.println("Weekly Subscription specific Logs 2 are prepared");
        prepareSpecificLogsW2(identifier);
        Thread.sleep(2000);

        //Making the monthly specific logs 2 human readable.
        System.out.println("Monthly Subscription specific Logs 2 are prepared");
        prepareSpecificLogsM2(identifier);
        Thread.sleep(2000);

        //Making the main logs human readable
        System.out.println("Main Logs 2 are prepared");
        prepareMainLogs2(identifier);
        Thread.sleep(2000);

        //loading the daily logs. After implementation this method needs to be last in this method.
        System.out.println("Loading the day by day logs 2");
        loadDailyLogs2(identifier, friendlydate);
        Thread.sleep(2000);

        //============================ SECTOR 2 ENDS HERE =======================================

        //preparing the stats and last known thing to make them human readable
        System.out.println("Making the stats human readable");
        prepareAllStats(identifier);
        Thread.sleep(2000);

        //Day by day statistics

        //Loading the daily Counts
        System.out.println("Loading the day by day Count Statistics");
        loadDailyCount(identifier);
        Thread.sleep(2000);

        //Loading the daily Sum
        System.out.println("Loading the day by day Sum Statistics");
        loadDailySum(identifier);
        Thread.sleep(2000);

        //Loading the daily Average
        System.out.println("Loading the day by day Average Statistics");
        loadDailyAverage(identifier);
        Thread.sleep(2000);

        //Hour by Hour Statistics

        //Loading the hourly Counts
        System.out.println("Loading the hour by hour Count Statistics");
        loadHourlyCount(identifier);
        Thread.sleep(2000);

        //Loading the hourly Sum
        System.out.println("Loading the hour by hour Sum Statistics");
        loadHourlySum(identifier);
        Thread.sleep(2000);

        //Loading the hourly Average
        System.out.println("Loading the hour by hour Average Statistics");
        loadHourlyAverage(identifier);
        Thread.sleep(2000);

        //Monthly Hour by Hour Statistics

        //Loading the monthly hourly Counts
        System.out.println("Loading the hour by hour Count Statistics");
        loadMHourlyCount(identifier);
        Thread.sleep(2000);

        //Loading the hourly Sum
        System.out.println("Loading the hour by hour Sum Statistics");
        loadMHourlySum(identifier);
        Thread.sleep(2000);

        //Loading the hourly Average
        System.out.println("Loading the hour by hour Average Statistics");
        loadMHourlyAverage(identifier);
        Thread.sleep(2000);
    }

    //loads the logs
    static void loadSpecificLogs(int identifier){
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();
        HashMap<String, ArrayList<String>> map = new HashMap();
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            System.out.println("Loading the logs for " + topic);
            ArrayList<String> arrayList = new ArrayList<String>();
            hashMapToArrayList2(arrayList,
                    fire.readDocument("databasesslashs" + identifier
                                    + "sslashslogssslashsdailylogssslashs" + dayStamp + "sslashs",
                            "mainLogssslashs" + topic));
            houses.get(identifier).logs.put(topic,arrayList);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


//        //Only for testing. Displaying the subscription specific logs.
//        System.out.println("Displaying specific logs. Only for testing");
//        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs.entrySet()) {
//            String topic =  entry.getKey();
//            ArrayList<String> arrayList = entry.getValue();
//            displayAnArrayList(arrayList, topic, identifier);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    //loads the weekly specific logs
    static void loadSpecificLogsW(int identifier){
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();
        HashMap<String, ArrayList<String>> map = new HashMap();
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            System.out.println("Loading the weekly logs for " + topic);
            ArrayList<String> arrayList = new ArrayList<String>();
            hashMapToArrayList2(arrayList,
                    fire.readDocument("databasesslashs" + identifier
                                    + "sslashslogssslashsweeklylogssslashs" + week + year + "sslashs",
                            "mainLogssslashs" + topic));
            houses.get(identifier).wlogs.put(topic,arrayList);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



//        //Only for testing. Displaying the subscription specific logs.
//        System.out.println("Displaying specific logs. Only for testing");
//        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs.entrySet()) {
//            String topic =  entry.getKey();
//            ArrayList<String> arrayList = entry.getValue();
//            displayAnArrayList(arrayList, topic, identifier);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }


    //loads the weekly specific logs
    static void loadSpecificLogsM(int identifier){
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();
        HashMap<String, ArrayList<String>> map = new HashMap();
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            System.out.println("Loading the monthly logs for " + topic);
            ArrayList<String> arrayList = new ArrayList<String>();
            hashMapToArrayList2(arrayList,
                    fire.readDocument("databasesslashs" + identifier
                                    + "sslashslogssslashsmonthlylogssslashs" + month + year + "sslashs",
                            "mainLogssslashs" + topic));
            houses.get(identifier).mlogs.put(topic,arrayList);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



//        //Only for testing. Displaying the subscription specific logs.
//        System.out.println("Displaying specific logs. Only for testing");
//        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs.entrySet()) {
//            String topic =  entry.getKey();
//            ArrayList<String> arrayList = entry.getValue();
//            displayAnArrayList(arrayList, topic, identifier);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }



    //preparing human readable subscription specific logs
    static void prepareSpecificLogs(int identifier) {
        //String friendlydate = day.replace("/", "");
        HashMap<String, ArrayList<String>> map = new HashMap();
        //Map<String, ArrayList<String>> mapTmp = houses.get(identifier).prepLogs;
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Preparing the logs for " + topicHumanReadable);
            ArrayList<String> arrayList = houses.get(identifier).logs.get(topic);
            ArrayList<String> arrayList2 = new ArrayList<String>();
            for(String log: arrayList){
                String[] tmp = log.split("sseparatorr");
                String newLog = tmp[1];
                arrayList2.add(newLog);
            }

            houses.get(identifier).prepLogs.put(topicHumanReadable, arrayList2);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Only for testing. Displaying the subscription specific logs.
        System.out.println("Displaying PREPARED specific logs. Only for testing");
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).prepLogs.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            ArrayList<String> arrayList = entry.getValue();
            displayAnArrayList(arrayList, topicHumanReadable, identifier);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //preparing weekly human readable subscription specific logs
    static void prepareSpecificLogsW(int identifier) {
        //String friendlydate = day.replace("/", "");
        HashMap<String, ArrayList<String>> map = new HashMap();
        //Map<String, ArrayList<String>> mapTmp = houses.get(identifier).prepLogs;
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Preparing the weekly logs for " + topicHumanReadable);
            ArrayList<String> arrayList = houses.get(identifier).wlogs.get(topic);
            ArrayList<String> arrayList2 = new ArrayList<String>();
            for(String log: arrayList){
                String[] tmp = log.split("sseparatorr");
                String newLog = tmp[1];
                arrayList2.add(newLog);
            }

            houses.get(identifier).prepwlogs.put(topicHumanReadable, arrayList2);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Only for testing. Displaying the subscription specific logs.
        System.out.println("Displaying weekly PREPARED specific logs. Only for testing");
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).prepwlogs.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            ArrayList<String> arrayList = entry.getValue();
            displayAnArrayList(arrayList, topicHumanReadable, identifier);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //preparing monthly human readable subscription specific logs
    static void prepareSpecificLogsM(int identifier) {
        //String friendlydate = day.replace("/", "");
        HashMap<String, ArrayList<String>> map = new HashMap();
        //Map<String, ArrayList<String>> mapTmp = houses.get(identifier).prepLogs;
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Preparing the monthly logs for " + topicHumanReadable);
            ArrayList<String> arrayList = houses.get(identifier).mlogs.get(topic);
            ArrayList<String> arrayList2 = new ArrayList<String>();
            for(String log: arrayList){
                String[] tmp = log.split("sseparatorr");
                String newLog = tmp[1];
                arrayList2.add(newLog);
            }

            houses.get(identifier).prepmlogs.put(topicHumanReadable, arrayList2);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Only for testing. Displaying the subscription specific logs.
        System.out.println("Displaying monthly PREPARED specific logs. Only for testing");
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).prepmlogs.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            ArrayList<String> arrayList = entry.getValue();
            displayAnArrayList(arrayList, topicHumanReadable, identifier);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void prepareMainLogs(int identifier) {

        //Preparing Daily Main Logs
        System.out.println("preparing Daily Main Logs");
        for(String log : houses.get(identifier).mainLogs){
            String[] msg = log.split("sseparatorr");
            if(msg.length>1){
                houses.get(identifier).premainLogs.add(msg[1]);
            }
        }

        //Preparing Weekly Main Logs
        System.out.println("preparing Weekly Main Logs");
        for(String log : houses.get(identifier).mainLogsWeek){
            String[] msg = log.split("sseparatorr");
            if(msg.length>1){
                houses.get(identifier).premainLogsWeek.add(msg[1]);
            }
        }

        //Preparing Monthly Main Logs
        System.out.println("preparing Monthly Main Logs");
        for(String log : houses.get(identifier).mainLogsMonth){
            String[] msg = log.split("sseparatorr");
            if(msg.length>1){
                houses.get(identifier).premainLogsMonth.add(msg[1]);
            }
        }

        //Display the preparaded Arrays. For test only
        System.out.println("Displaying the prepared Main Logs. For testing purposes");
        displayAnArrayList(houses.get(identifier).premainLogs, "Prepared Daily Main Logs", identifier);
        displayAnArrayList(houses.get(identifier).premainLogsWeek, "Prepared Weekly Main Logs", identifier);
        displayAnArrayList(houses.get(identifier).premainLogsMonth, "Prepared Monthly Main Logs", identifier);
    }


    //============================ SECTOR 2 STARTS HERE =======================================

    //loads the logs
    static void loadSpecificLogs2(int identifier){
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();
        HashMap<String, ArrayList<String>> map = new HashMap();
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            System.out.println("Loading the logs 2 for " + topic);
            ArrayList<String> arrayList = new ArrayList<String>();
            hashMapToArrayList2(arrayList,
                    fire.readDocument("databasesslashs" + identifier
                                    + "sslashslogssslashsdailylogssslashs" + dayStamp + "sslashs",
                            "mainLogs2sslashs" + topic));
            houses.get(identifier).logs2.put(topic,arrayList);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


//        //Only for testing. Displaying the subscription specific logs.
//        System.out.println("Displaying specific logs. Only for testing");
//        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs.entrySet()) {
//            String topic =  entry.getKey();
//            ArrayList<String> arrayList = entry.getValue();
//            displayAnArrayList(arrayList, topic, identifier);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    //loads the weekly specific logs 2
    static void loadSpecificLogsW2(int identifier){
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();
        HashMap<String, ArrayList<String>> map = new HashMap();
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            System.out.println("Loading the weekly logs 2 for " + topic);
            ArrayList<String> arrayList = new ArrayList<String>();
            hashMapToArrayList2(arrayList,
                    fire.readDocument("databasesslashs" + identifier
                                    + "sslashslogssslashsweeklylogssslashs" + week + year + "sslashs",
                            "mainLogs2sslashs" + topic));
            houses.get(identifier).wlogs2.put(topic,arrayList);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



//        //Only for testing. Displaying the subscription specific logs.
//        System.out.println("Displaying specific logs. Only for testing");
//        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs.entrySet()) {
//            String topic =  entry.getKey();
//            ArrayList<String> arrayList = entry.getValue();
//            displayAnArrayList(arrayList, topic, identifier);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }


    //loads the weekly specific logs 2
    static void loadSpecificLogsM2(int identifier){
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();
        HashMap<String, ArrayList<String>> map = new HashMap();
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            System.out.println("Loading the monthly logs 2 for " + topic);
            ArrayList<String> arrayList = new ArrayList<String>();
            hashMapToArrayList2(arrayList,
                    fire.readDocument("databasesslashs" + identifier
                                    + "sslashslogssslashsmonthlylogssslashs" + month + year + "sslashs",
                            "mainLogs2sslashs" + topic));
            houses.get(identifier).mlogs2.put(topic,arrayList);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



//        //Only for testing. Displaying the subscription specific logs.
//        System.out.println("Displaying specific logs. Only for testing");
//        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).logs.entrySet()) {
//            String topic =  entry.getKey();
//            ArrayList<String> arrayList = entry.getValue();
//            displayAnArrayList(arrayList, topic, identifier);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }



    //preparing human readable subscription specific logs 2
    static void prepareSpecificLogs2(int identifier) {
        //String friendlydate = day.replace("/", "");
        HashMap<String, ArrayList<String>> map = new HashMap();
        //Map<String, ArrayList<String>> mapTmp = houses.get(identifier).prepLogs;
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Preparing the logs 2 for " + topicHumanReadable);
            ArrayList<String> arrayList = houses.get(identifier).logs2.get(topic);
            //ArrayList<String> arrayList2 = new ArrayList<String>();
            HashMap<String, String> map2 = new HashMap();
            for(String log: arrayList){
                String[] tmp = log.split("sseparatorr");
                String newLog = tmp[1];
                String[] tmp2 = newLog.split("ssectorr2");
                map2.put(tmp2[0],tmp2[1]);
            }

            houses.get(identifier).prepLogs2.put(topicHumanReadable, map2);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Only for testing. Displaying the subscription specific logs.
        System.out.println("Displaying PREPARED specific logs. Only for testing");
        for (Map.Entry<String, HashMap<String, String>> entry : houses.get(identifier).prepLogs2.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            HashMap<String, String> map2 = entry.getValue();
            displayDatafromAMapString(identifier, map2, topicHumanReadable);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //preparing human readable subscription specific weekly logs 2
    static void prepareSpecificLogsW2(int identifier) {
        //String friendlydate = day.replace("/", "");
        HashMap<String, ArrayList<String>> map = new HashMap();
        //Map<String, ArrayList<String>> mapTmp = houses.get(identifier).prepLogs;
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Preparing the Weekly logs 2 for " + topicHumanReadable);
            ArrayList<String> arrayList = houses.get(identifier).wlogs2.get(topic);
            //ArrayList<String> arrayList2 = new ArrayList<String>();
            HashMap<String, String> map2 = new HashMap();
            for(String log: arrayList){
                String[] tmp = log.split("sseparatorr");
                String newLog = tmp[1];
                String[] tmp2 = newLog.split("ssectorr2");
                map2.put(tmp2[0],tmp2[1]);
            }

            houses.get(identifier).prepwlogs2.put(topicHumanReadable, map2);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Only for testing. Displaying the subscription specific logs.
        System.out.println("Displaying PREPARED specific weekly logs. Only for testing");
        for (Map.Entry<String, HashMap<String, String>> entry : houses.get(identifier).prepwlogs2.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            HashMap<String, String> map2 = entry.getValue();
            displayDatafromAMapString(identifier, map2, topicHumanReadable);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //preparing human readable subscription specific monthly logs 2
    static void prepareSpecificLogsM2(int identifier) {
        //String friendlydate = day.replace("/", "");
        HashMap<String, ArrayList<String>> map = new HashMap();
        //Map<String, ArrayList<String>> mapTmp = houses.get(identifier).prepLogs;
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Preparing the monthly logs 2 for " + topicHumanReadable);
            ArrayList<String> arrayList = houses.get(identifier).mlogs2.get(topic);
            //ArrayList<String> arrayList2 = new ArrayList<String>();
            HashMap<String, String> map2 = new HashMap();
            for(String log: arrayList){
                String[] tmp = log.split("sseparatorr");
                String newLog = tmp[1];
                String[] tmp2 = newLog.split("ssectorr2");
                map2.put(tmp2[0],tmp2[1]);
            }

            houses.get(identifier).prepmlogs2.put(topicHumanReadable, map2);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Only for testing. Displaying the subscription specific logs.
        System.out.println("Displaying PREPARED monthly specific logs. Only for testing");
        for (Map.Entry<String, HashMap<String, String>> entry : houses.get(identifier).prepmlogs2.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            HashMap<String, String> map2 = entry.getValue();
            displayDatafromAMapString(identifier, map2, topicHumanReadable);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void prepareMainLogs2(int identifier) {

        //Preparing Daily Main Logs 2
        System.out.println("preparing Daily Main Logs");
        for(String log : houses.get(identifier).mainLogs2){
            String[] msg = log.split("sseparatorr");
            String[] msg2 = msg[1].split("ssectorr2");

            if(msg.length>1){
                houses.get(identifier).premainLogs2.put(msg2[0], msg2[1]);
            }
        }

        //Preparing Weekly Main Logs 2
        System.out.println("preparing Weekly Main Logs 2");
        for(String log : houses.get(identifier).mainLogsWeek2){
            String[] msg = log.split("sseparatorr");
            String[] msg2 = msg[1].split("ssectorr2");
            if(msg.length>1){
                houses.get(identifier).premainLogsWeek2.put(msg2[0], msg2[1]);
            }
        }

        //Preparing Monthly Main Logs 2
        System.out.println("preparing Monthly Main Logs 2");
        for(String log : houses.get(identifier).mainLogsMonth2){
            String[] msg = log.split("sseparatorr");
            String[] msg2 = msg[1].split("ssectorr2");
            if(msg.length>1){
                houses.get(identifier).premainLogsMonth2.put(msg2[0], msg2[1]);
            }
        }

        //Display the preparaded Arrays. For test only
        System.out.println("Displaying the prepared Main Logs 2. For testing purposes");
        displayDatafromAMapString(identifier, houses.get(identifier).premainLogs2, "Prepared Daily Main Logs");
        displayDatafromAMapString(identifier, houses.get(identifier).premainLogsWeek2, "Prepared Weekly Main Logs");
        displayDatafromAMapString(identifier, houses.get(identifier).premainLogsMonth2, "Prepared Monthly Main Logs");
    }

    static void loadDailyLogs2(int identifier, String friendlydate){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the daily logs 2. The identifier is: " + identifier);
        System.out.println("The friendly date is: " + friendlydate);
        String dateSignature;
        for (int x = 1; x <32; x++){
            System.out.println("Loading the logs of day: " + x);
            if(x<10){
                dateSignature = month + "0" +  x + year;
                System.out.println("The timestamp of that day is: " + dateSignature);
            } else {
                dateSignature = month + x + year;
                System.out.println("The timestamp of that day is: " + dateSignature);
            }
            houses.get(identifier).dailyLogsArrayList2.put(dateSignature, new ArrayList<String>());

            //the problem with the sseparatorr is not here.
            hashMapToArrayList3(houses.get(identifier).dailyLogsArrayList2.get(dateSignature),
                    fire.readDocument("databasesslashs" + identifier
                            + "sslashslogssslashsdailylogssslashs", "main2sslashs"
                            + dateSignature));

            //tranfering the data from the aarylist to the String array.
            String[] arr = houses.get(identifier).dailyLogsArrayList2.get(dateSignature).toArray(new String[0]);
            houses.get(identifier).dailyLogsArray2.put(dateSignature, arr);

            //concatinating String...
            //it may crash the system. Need to consider the possibility of turning it on and off by the configurations
            String msg = "";
            for(String log : arr){
                //the problem is in the arraylist. this patch here solves the issue
                //but it will produce a bug when I will solve the issue on the aaraylist
                //so I will deactivate the patch here.
                //String[] tmp = log.split("sseparatorr");
                //String newLog = tmp[1];
                //msg = msg + newLog + "\n";
                msg = msg + log + "\n";
            }
            houses.get(identifier).dailyLogsArrayConCat2.put(dateSignature, msg);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //Displaying the daily logs of the month. for now only for testing. for monthly stats I need to find other way.
        System.out.println("Displaying the daily logs 2 of the month inside the arrayList 2, not in order");
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).dailyLogsArrayList2.entrySet()) {
            String day =  entry.getKey();
            ArrayList<String> logs = entry.getValue();
            for(String log : logs){
                System.out.println("[" + day + "] " + log);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displaying the daily logs of the month. for now only for testing. for monthly stats I need to find other way.
        System.out.println("Displaying the daily logs of the month inside the array 2, not in order");
        for (Map.Entry<String, String[]> entry : houses.get(identifier).dailyLogsArray2.entrySet()) {
            String day =  entry.getKey();
            String[] logs = entry.getValue();
            for(String log : logs){
                System.out.println("[" + day + "] " + log);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displaying the daily logs of the month. as concatination. really bad approach.
        System.out.println("Displaying the daily logs 2 of the month as a concatination, not in order");
        for (Map.Entry<String, String> entry : houses.get(identifier).dailyLogsArrayConCat2.entrySet()) {
            String day =  entry.getKey();
            String logs = entry.getValue();
            System.out.println("[" + day + "] \n" + logs);
//            for(String log : logs){
//                System.out.println("[" + day + "] " + log);
//            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    //The algorithm that decides the variable 2
    static String sector2algorithm(String var2){
        String variable2 = "0";
        if(var2.equalsIgnoreCase("true")){
            variable2 = "1";
        }

        if(var2.equalsIgnoreCase("false")){
            variable2 = "2";
        }
        if(var2.equalsIgnoreCase("on")){
            variable2 = "1";
        }

        if(var2.equalsIgnoreCase("off")){
            variable2 = "2";
        }

        if(var2.equalsIgnoreCase("armed")){
            variable2 = "1";
        }

        if(var2.equalsIgnoreCase("open")){
            variable2 = "1";
        }

        if(var2.equalsIgnoreCase("closed")){
            variable2 = "1";
        }

        if(var2.equalsIgnoreCase("triggered")){
            variable2 = "3";
        }

        if(isNumeric(var2)){
            variable2 = var2;
        }

        return variable2;
    }

    //============================ SECTOR 2 ENDSS HERE =======================================


    static  void prepareAllStats(int identifier){
        preparingStats(identifier, houses.get(identifier).lastKnownValues,
                houses.get(identifier).prelastKnownValues, "Last Known Values");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStats(identifier, houses.get(identifier).lastKnownTimeStamps,
                houses.get(identifier).prelastKnownTimeStamps, "Last Known Timestamps");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsInteger(identifier, houses.get(identifier).dailyCount,
                houses.get(identifier).predailyCount, "Daily Count");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsInteger(identifier, houses.get(identifier).weeklyCount,
                houses.get(identifier).preweeklyCount, "Weekly Count");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsInteger(identifier, houses.get(identifier).monthlyCount,
                houses.get(identifier).premonthlyCount, "Monthly Count");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsDouble(identifier, houses.get(identifier).dailySum,
                houses.get(identifier).predailySum, "Daily Sum");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsDouble(identifier, houses.get(identifier).weeklySum,
                houses.get(identifier).preweeklySum, "Weekly Sum");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsDouble(identifier, houses.get(identifier).monthlySum,
                houses.get(identifier).premonthlySum, "Monthly Sum");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsDouble(identifier, houses.get(identifier).dailyAverage,
                houses.get(identifier).predailyAverage, "Daily Average");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsDouble(identifier, houses.get(identifier).weeklyAverage,
                houses.get(identifier).preweeklyAverage, "Weekly Average");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preparingStatsDouble(identifier, houses.get(identifier).monthlyAverage,
                houses.get(identifier).premonthlyAverage, "Monthly Average");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    static void preparingStats(int identifier, Map<String, String> unprepared,
                               Map<String, String> prepared, String title){
        System.out.println("Preparing the " + title);
        for (Map.Entry<String, String> entry : unprepared.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            //System.out.println("Preparing the " + title + " for " + topicHumanReadable);
            prepared.put(topicHumanReadable, entry.getValue());

        }

        for (Map.Entry<String, String> entry : prepared.entrySet()) {
            String topic =  entry.getKey();
            System.out.println(topic + ":" + entry.getValue());
        }
    }

    static void preparingStatsInteger(int identifier, Map<String, Integer> unprepared,
                               Map<String, Integer> prepared, String title){
        System.out.println("Preparing the " + title);
        for (Map.Entry<String, Integer> entry : unprepared.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            prepared.put(topicHumanReadable, entry.getValue());

        }

        for (Map.Entry<String, Integer> entry : prepared.entrySet()) {
            String topic =  entry.getKey();
            System.out.println(topic + ":" + entry.getValue());
        }
    }

    static void preparingStatsDouble(int identifier, Map<String, Double> unprepared,
                                      Map<String, Double> prepared, String title){
        System.out.println("Preparing the " + title);
        for (Map.Entry<String, Double> entry : unprepared.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            prepared.put(topicHumanReadable, entry.getValue());

        }

        for (Map.Entry<String, Double> entry : prepared.entrySet()) {
            String topic =  entry.getKey();
            System.out.println(topic + ":" + entry.getValue());
        }
    }

    static void loadDailyLogs(int identifier, String friendlydate){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the daily logs. The identifier is: " + identifier);
        System.out.println("The friendly date is: " + friendlydate);
        String dateSignature;
        for (int x = 1; x <32; x++){
            System.out.println("Loading the logs of day: " + x);
            if(x<10){
                dateSignature = month + "0" +  x + year;
                System.out.println("The timestamp of that day is: " + dateSignature);
            } else {
                dateSignature = month + x + year;
                System.out.println("The timestamp of that day is: " + dateSignature);
            }
            houses.get(identifier).dailyLogsArrayList.put(dateSignature, new ArrayList<String>());

            //the problem with the sseparatorr is not here.
            hashMapToArrayList3(houses.get(identifier).dailyLogsArrayList.get(dateSignature),
                    fire.readDocument("databasesslashs" + identifier
                                    + "sslashslogssslashsdailylogssslashs", "mainsslashs"
                            + dateSignature));

            //tranfering the data from the aarylist to the String array.
            String[] arr = houses.get(identifier).dailyLogsArrayList.get(dateSignature).toArray(new String[0]);
            houses.get(identifier).dailyLogsArray.put(dateSignature, arr);

            //concatinating String...
            //it may crash the system. Need to consider the possibility of turning it on and off by the configurations
            String msg = "";
            for(String log : arr){
                //the problem is in the arraylist. this patch here solves the issue
                //but it will produce a bug when I will solve the issue on the aaraylist
                //so I will deactivate the patch here.
                //String[] tmp = log.split("sseparatorr");
                //String newLog = tmp[1];
                //msg = msg + newLog + "\n";
                msg = msg + log + "\n";
            }
            houses.get(identifier).dailyLogsArrayConCat.put(dateSignature, msg);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //Displaying the daily logs of the month. for now only for testing. for monthly stats I need to find other way.
        System.out.println("Displaying the daily logs of the month inside the arrayList, not in order");
        for (Map.Entry<String, ArrayList<String>> entry : houses.get(identifier).dailyLogsArrayList.entrySet()) {
            String day =  entry.getKey();
            ArrayList<String> logs = entry.getValue();
            for(String log : logs){
                System.out.println("[" + day + "] " + log);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displaying the daily logs of the month. for now only for testing. for monthly stats I need to find other way.
        System.out.println("Displaying the daily logs of the month inside the array, not in order");
        for (Map.Entry<String, String[]> entry : houses.get(identifier).dailyLogsArray.entrySet()) {
            String day =  entry.getKey();
            String[] logs = entry.getValue();
            for(String log : logs){
                System.out.println("[" + day + "] " + log);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displaying the daily logs of the month. as concatination. really bad approach.
        System.out.println("Displaying the daily logs of the month as a concatination, not in order");
        for (Map.Entry<String, String> entry : houses.get(identifier).dailyLogsArrayConCat.entrySet()) {
            String day =  entry.getKey();
            String logs = entry.getValue();
            System.out.println("[" + day + "] \n" + logs);
//            for(String log : logs){
//                System.out.println("[" + day + "] " + log);
//            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    static void loadDailyCount(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the daily Count Stats. The identifier is: " + identifier);
        //System.out.println("The friendly date is: " + friendlydate);
        String dateSignature;


        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving daily Sum stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("countdbd" + month + year, topic);
            System.out.println("The size of the hashmap with the count stats is " + map.size());
            Integer[] arr = new Integer[32];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                //String value = map.get(x);
                //System.out.println("The value of " + x + " is " + value);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Integer.valueOf(msg);
                    } else {
                        arr[x] = 0;
                    }
                    if(msg != null) {
                        if (isNumeric(msg)) {
                            if (!msg.equalsIgnoreCase("0")) {
                                System.out.println("The value " + msg + " Was retrieved from day " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailyCountdbd.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of dailyCountdbd. Only for testing purposes
        System.out.println("Displayign the contents of dailyCountdbd. Only for testing purposes");
        for (Map.Entry<String, Integer[]> entry : houses.get(identifier).dailyCountdbd.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  daily stats for " + topicHumanReadable);
            Integer[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if (isNumeric(arr[x].toString())) {
                        if(arr[x] != 0){
                            System.out.println("Day: " + x + ", Count " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadDailyCount


    static void loadHourlyCount(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the daily hour by hour Count Stats. The identifier is: " + identifier);



        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving daily hour by hour Sum stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("counthbh" + dayStamp, topic);
            System.out.println("The size of the hashmap with the hourly count stats is " + map.size());
            Integer[] arr = new Integer[25];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Integer.valueOf(msg);
                    } else {
                        arr[x] = 0;
                    }
                    if(!msg.equalsIgnoreCase("0")){
                        if(isNumeric(msg)) {
                            if(!msg.equalsIgnoreCase("0.0")){
                                System.out.println("The value " + msg + " Was retrieved from the hour " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailyCounthbh.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of daily hour by hour Count. Only for testing purposes
        System.out.println("Displayign the contents of dailyCountdbd. Only for testing purposes");
        for (Map.Entry<String, Integer[]> entry : houses.get(identifier).dailyCounthbh.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  daily hour by hour stats for " + topicHumanReadable);
            Integer[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if (isNumeric(arr[x].toString())) {
                        if(arr[x] != 0){
                            if(arr[x] != 0.0){
                                System.out.println("Hour: " + x + ", Count " + arr[x]);
                            }
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadHourlyCount


    static void loadMHourlyCount(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the Monthly hour by hour Count Stats. The identifier is: " + identifier);



        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving daily hour by hour Sum stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("countmhbh" + month + year, topic);
            System.out.println("The size of the hashmap with the hourly count stats is " + map.size());
            Integer[] arr = new Integer[9999];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Integer.valueOf(msg);
                    } else {
                        arr[x] = 0;
                    }
                    if(!msg.equalsIgnoreCase("0")){
                        if(isNumeric(msg)) {
                            if(!msg.equalsIgnoreCase("0.0")) {
                                System.out.println("The value " + msg + " Was retrieved from the hour " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailyCountmhbh.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of Monthly hour by hour Count. Only for testing purposes
        System.out.println("Displayign the contents of monthly Count hbh. Only for testing purposes");
        for (Map.Entry<String, Integer[]> entry : houses.get(identifier).dailyCountmhbh.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  Monthly hour by hour stats for " + topicHumanReadable);
            Integer[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if (isNumeric(arr[x].toString())) {
                        if(arr[x] != 0){
                            System.out.println("Hour: " + x + ", Count " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadMHourlyCount


    static void loadDailySum(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the daily Sum Stats. The identifier is: " + identifier);


        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving daily Sum stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("sumdbd" + month + year, topic);
            System.out.println("The size of the hashmap with the Sum stats is " + map.size());
            Double[] arr = new Double[32];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                //String value = map.get(x);
                //System.out.println("The value of " + x + " is " + value);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Double.valueOf(msg);
                    } else {
                        arr[x] = 0.0;
                    }
                    if(!msg.equalsIgnoreCase("0")){
                        if(!msg.equalsIgnoreCase("0.0")){
                            if(isNumeric(msg)) {
                                System.out.println("The value " + msg + " Was retrieved from day " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailySumdbd.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of dailySumdbd. Only for testing purposes
        System.out.println("Displaying the contents of dailySumdbd. Only for testing purposes");
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailySumdbd.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  daily SUM stats for " + topicHumanReadable);
            Double[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if (isNumeric(arr[x].toString())) {
                        if(arr[x] != 0.0){
                            System.out.println("Day: " + x + ", Sum: " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadDailySum


    static void loadHourlySum(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the hourly Sum Stats. The identifier is: " + identifier);


        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving hourly Sum stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("sumhbh" + dayStamp, topic);
            System.out.println("The size of the hashmap with the hourly Sum stats is " + map.size());
            Double[] arr = new Double[25];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                //String value = map.get(x);
                //System.out.println("The value of " + x + " is " + value);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Double.valueOf(msg);
                    } else {
                        arr[x] = 0.0;
                    }
                    if(!msg.equalsIgnoreCase("0")){
                        if(!msg.equalsIgnoreCase("0.0")){
                            if(isNumeric(msg)) {
                                System.out.println("The value " + msg + " Was retrieved from hour " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailySumhbh.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of hourly Sum. Only for testing purposes
        System.out.println("Displaying the contents of hourlly Sum. Only for testing purposes");
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailySumhbh.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  hourly SUM stats for " + topicHumanReadable);
            Double[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if (isNumeric(arr[x].toString())) {
                        if(arr[x] != 0.0){
                            System.out.println("Hour: " + x + ", Sum: " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadHourySum


    static void loadMHourlySum(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the Monthly hourly Sum Stats. The identifier is: " + identifier);


        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving monthly hourly Sum stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("summhbh" + month + year, topic);
            System.out.println("The size of the hashmap with the monthly hourly Sum stats is " + map.size());
            Double[] arr = new Double[9999];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                //String value = map.get(x);
                //System.out.println("The value of " + x + " is " + value);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Double.valueOf(msg);
                    } else {
                        arr[x] = 0.0;
                    }
                    if(!msg.equalsIgnoreCase("0")){
                        if(!msg.equalsIgnoreCase("0.0")){
                            if(isNumeric(msg)) {
                                System.out.println("The value " + msg + " Was retrieved from hour " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailySummhbh.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of Monthly hourly Sum. Only for testing purposes
        System.out.println("Displaying the contents of Monthly hourly Sum. Only for testing purposes");
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailySummhbh.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  monthly hourly SUM stats for " + topicHumanReadable);
            Double[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if (isNumeric(arr[x].toString())) {
                        if(arr[x] != 0.0){
                            System.out.println("Hour: " + x + ", Sum: " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadMHourySum



    static void loadDailyAverage(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the daily Average Stats. The identifier is: " + identifier);


        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving daily Average stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("averagedbd" + month + year, topic);
            System.out.println("The size of the hashmap with the Average stats is " + map.size());
            Double[] arr = new Double[32];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                //String value = map.get(x);
                //System.out.println("The value of " + x + " is " + value);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Double.valueOf(msg);
                    } else {
                        arr[x] = 0.0;
                    }
                    if(!msg.equalsIgnoreCase("0")){
                        if(!msg.equalsIgnoreCase("0.0")){
                            if(isNumeric(msg)) {
                                System.out.println("The value " + msg + " Was retrieved from day " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailyAveragedbd.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of dailySumdbd. Only for testing purposes
        System.out.println("Displaying the contents of dailyAveragedbd. Only for testing purposes");
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailyAveragedbd.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  daily Average stats for " + topicHumanReadable);
            Double[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if (isNumeric(arr[x].toString())) {
                        if(arr[x] != 0.0){
                            System.out.println("Day: " + x + ", Average: " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadDailyAverage

    static void loadHourlyAverage(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the hourly Average Stats. The identifier is: " + identifier);


        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving hourly Average stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("averagehbh" + dayStamp, topic);
            System.out.println("The size of the hashmap with the hourly Average stats is " + map.size());
            Double[] arr = new Double[25];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                //String value = map.get(x);
                //System.out.println("The value of " + x + " is " + value);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Double.valueOf(msg);
                    } else {
                        arr[x] = 0.0;
                    }
                    if(isNumeric(msg)){
                        if(!msg.equalsIgnoreCase("0")){
                            if(!msg.equalsIgnoreCase("0.0")){
                                System.out.println("The value " + msg + " Was retrieved from hour " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailyAveragehbh.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of dailySumdbd. Only for testing purposes
        System.out.println("Displaying the contents of hourly Average. Only for testing purposes");
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailyAveragehbh.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  hourly Average stats for " + topicHumanReadable);
            Double[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if(isNumeric(arr[x].toString())){
                        if(arr[x] != 0.0){
                            System.out.println("Hour: " + x + ", Average: " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadHourlyAverage

    static void loadMHourlyAverage(int identifier){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Loading the monthly hourly Average Stats. The identifier is: " + identifier);


        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Retrieving hourly Average stats for " + topicHumanReadable);
            HashMap<String, String> map = fire.readDocument("averagemhbh" + month + year, topic);
            System.out.println("The size of the hashmap with the monthly hourly Average stats is " + map.size());
            Double[] arr = new Double[9999];
            for(int x = 1; x<arr.length; x++){
                String xs = String.valueOf(x);
                //String value = map.get(x);
                //System.out.println("The value of " + x + " is " + value);
                if(map.containsKey(xs)){
                    String msg = map.get(xs);
                    if(isNumeric(msg)){
                        arr[x] = Double.valueOf(msg);
                    } else {
                        arr[x] = 0.0;
                    }
                    if(!msg.equalsIgnoreCase("0")){
                        if(!msg.equalsIgnoreCase("0.0")){
                            if(isNumeric(msg)){
                                System.out.println("The value " + msg + " Was retrieved from hour " + xs);
                            }
                        }
                    }
                }
            }
            houses.get(identifier).dailyAveragemhbh.put(topicHumanReadable, arr);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Displayign the contents of Average. Only for testing purposes
        System.out.println("Displaying the contents of monthly hourly Average. Only for testing purposes");
        for (Map.Entry<String, Double[]> entry : houses.get(identifier).dailyAveragemhbh.entrySet()) {
            String topic =  entry.getKey();
            String topicHumanReadable = topic.replace("sslashs", "/");
            System.out.println("Displaying  monthly hourly Average stats for " + topicHumanReadable);
            Double[] arr = entry.getValue();
            for(int x = 0; x<arr.length; x++){
                if(arr[x] != null) {
                    if(isNumeric(arr[x].toString())){
                        if(arr[x] != 0.0){
                            System.out.println("Hour: " + x + ", Average: " + arr[x]);
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } //end of loadMHourlyAverage

    //It is for publishing data to topics. I don't know why I called it displayer
    static void publisher(MqttClient sampleClient, int identifier) throws MqttException {
        if(Configurations.publisherAllowed) {


            String friendlydate = day.replace("/", "");
            MqttMessage message;
            String topic = "db/test/5";
            String content = "Hello I am the displayer and that is my identifier: " + identifier;
//        MqttMessage message = new MqttMessage(content.getBytes());
//        message.setQos(0);
//        sampleClient.publish(topic, message);

            //publishing lastKnownValues on "database/" + identifier + "/lastKnownValues/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
                    //System.out.println("lastKnownValues iterates");
                    topic = "database/" + identifier
                            + "/lastKnownValues/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue();
                    //System.out.println("Monitored topic: " + topic);
                    //System.out.println("Monitored content: " + content);
                    if (!entry.getKey().replace("sslashs", "/").equalsIgnoreCase("-1")) {
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        if (publishing) {
                            sampleClient.publish(topic, message);
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //System.out.println(entry.getKey() + " = " + entry.getValue());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //publishing lastKnownTimeStamps on "database/" + identifier + "/lastKnownTimeStamps/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownTimeStamps.entrySet()) {
                    topic = "database/" + identifier
                            + "/lastKnownTimeStamps/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue();
                    if (!entry.getKey().replace("sslashs", "/").equalsIgnoreCase("-1")) {
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        if (publishing) {
                            sampleClient.publish(topic, message);
                        }
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //=============== COUNT ==============================================

            //publishing dailyCount on "database/" + identifier + "/stats/count/",
            // "dailyCount/" + friendlydate.toString() + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Integer> entry : houses.get(identifier).dailyCount.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/count/" +
                            "dailyCount/" + friendlydate + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //publishing weeklyCount on "database/" + identifier + "/stats/count/",
            // "weeklyCount/" + week + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Integer> entry : houses.get(identifier).weeklyCount.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/count/" +
                            "weeklyCount/" + week + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //publishing monthlyCount on "database/" + identifier + "/stats/count/",
            // "monthlyCount/" + week + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Integer> entry : houses.get(identifier).monthlyCount.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/count/" +
                            "monthlyCount/" + month + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            //================= SUM =============================

            //publishing dailySum on "database/" + identifier + "/stats/sum/" +
            // "dailySum/" + friendlydate.toString() + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Double> entry : houses.get(identifier).dailySum.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/sum/" +
                            "dailySum/" + friendlydate + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //publishing weeklySum on "database/" + identifier + "/stats/sum/" +
            // "weeklySum/" + week + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Double> entry : houses.get(identifier).weeklySum.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/sum/" +
                            "weeklySum/" + week + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //publishing monthlySum on "database/" + identifier + "/stats/sum/" +
            // "monthlySum/" + month + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Double> entry : houses.get(identifier).monthlySum.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/sum/" +
                            "monthlySum/" + month + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //================= AVERAGE =============================

            //publishing dailyAverage on "database/" + identifier + "/stats/average/" +
            // "dailyAverage/" + friendlydate.toString() + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Double> entry : houses.get(identifier).dailyAverage.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/average/" +
                            "dailyAverage/" + friendlydate + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //publishing weeklyAverage on "database/" + identifier + "/stats/average/" +
            // "weeklyAverage/" + week + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Double> entry : houses.get(identifier).weeklyAverage.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/average/" +
                            "weeklyAverage/" + week + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //publishing monthlyAverage on "database/" + identifier + "/stats/average/" +
            // "monthlyAverage/" + month + "/topic_of_the_subscription"
            if (publishing) {
                for (Map.Entry<String, Double> entry : houses.get(identifier).monthlyAverage.entrySet()) {
                    topic = "database/" + identifier
                            + "/stats/average/" +
                            "monthlyAverage/" + month + "/" + entry.getKey().replace("sslashs", "/");
                    content = entry.getValue().toString();
                    message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    if (publishing) {
                        sampleClient.publish(topic, message);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            //============= DATA STRUCTURES ========================================

            //publishing a day to day basis logs for a month as a HashMap of ArrayList<String>
            if (publishing) {
                publishAMap(sampleClient, "db/logs/daybyday/arraylist", houses.get(0).dailyLogsArrayList);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing a day to day basis logs for a month as a HashMap of String[] array
            if (publishing) {
                publishAMap(sampleClient, "db/logs/daybyday/array", houses.get(0).dailyLogsArray);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing a day to day basis logs for a month as a HashMap of Concatinated String messages
            if (publishing) {
                publishAMap(sampleClient, "db/logs/daybyday/concat", houses.get(0).dailyLogsArrayConCat);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing specific by subscription logs a HashMap of ArrayList<String>
            if (publishing) {
                publishAMap(sampleClient, "db/logs/specificlogs", houses.get(0).prepLogs);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing Daily Main Logs as an ArrayList
            if (publishing) {
                publishArrayList(sampleClient, "db/logs/today", houses.get(0).premainLogs);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing Weekly Main Logs as an ArrayList
            if (publishing) {
                publishArrayList(sampleClient, "db/logs/thisweek", houses.get(0).premainLogsWeek);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing Monthly Main Logs as an ArrayList
            if (publishing) {
                publishArrayList(sampleClient, "db/logs/thismonth", houses.get(0).premainLogsMonth);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared LastKnownValues as a HashMap<String, String>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/lastknownvalues",
                        houses.get(identifier).prelastKnownValues);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared LastKnownTimestamps as a HashMap<String, String>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/lastknowntimestamps",
                        houses.get(identifier).prelastKnownTimeStamps);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Daily Count Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/dailycount",
                        houses.get(identifier).predailyCount);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Weekly Count Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/weeklycount",
                        houses.get(identifier).preweeklyCount);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Monthly Count Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/monthlycount",
                        houses.get(identifier).premonthlyCount);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Daily sum Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/dailysum",
                        houses.get(identifier).predailySum);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Weekly Sum Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/weeklysum",
                        houses.get(identifier).preweeklySum);
            }

            //publishing prepared Monthly Sum Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/monthlysum",
                        houses.get(identifier).premonthlySum);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Daily Average Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/dailyaverage",
                        houses.get(identifier).predailyAverage);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Weekly Average Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/weeklyaverage",
                        houses.get(identifier).preweeklyAverage);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //publishing prepared Monthly Average Stats as a HashMap<String, Integer>
            if (publishing) {
                publishAMap(sampleClient, "db/ds/stats/monthlyaverage",
                        houses.get(identifier).premonthlyAverage);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }//end of publisherAllowed

    }//end of publisher()

    //It displays the content of a Hashmap<String, Integer> in a nice way
    static void displayDatafromAMapInteger(int identifier, HashMap<String, Integer> map, String nameOfMap){
        System.out.println("");
        System.out.println("Displaying " + nameOfMap + " of house with id:" + identifier);
        System.out.println("===================================");
        if(map.isEmpty()){
            System.out.println(nameOfMap + " is empty");
        } else {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String topic = entry.getKey().replace("sslashs", "/");
                String msg = entry.getValue().toString();
                System.out.println(topic + " === " + msg);
            }
        }
        System.out.println("===================================");
    }

    //It displays the content of a Hashmap<String, Integer> in a nice way
    static void displayDatafromAMapDouble(int identifier, HashMap<String, Double> map, String nameOfMap){
        System.out.println("");
        System.out.println("Displaying " + nameOfMap + " of house with id:" + identifier);
        System.out.println("===================================");
        if(map.isEmpty()){
            System.out.println(nameOfMap + " is empty");
        } else {
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                String topic = entry.getKey().replace("sslashs", "/");
                String msg = entry.getValue().toString();
                System.out.println(topic + " === " + msg);
            }
        }
        System.out.println("===================================");
    }

    //It displays the content of a Hashmap<String, Integer> in a nice way
    static void displayDatafromAMapString(int identifier, HashMap<String, String> map, String nameOfMap){
        System.out.println("");
        System.out.println("Displaying " + nameOfMap + " of house with id:" + identifier);
        System.out.println("===================================");
        if(map.isEmpty()){
            System.out.println(nameOfMap + " is empty");
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String topic = entry.getKey().replace("sslashs", "/");
                String msg = entry.getValue();
                System.out.println(topic + " === " + msg);
            }
        }
        System.out.println("===================================");
    }

    //It displays the content of a Hashmap<String, Integer> in a nice way
    static void displayDatafromAMapDateInteger(int identifier, Map<Date, Integer> map, String nameOfMap){
        System.out.println("");
        System.out.println("Displaying " + nameOfMap + " of house with id:" + identifier);
        System.out.println("===================================");
        if(map.isEmpty()){
            System.out.println(nameOfMap + " is empty");
        } else {
            for (HashMap.Entry<Date, Integer> entry : map.entrySet()) {
                String key = "No value found";
                key = entry.getKey().toString();
                String value = "No value found";
                value = entry.getValue().toString();
                System.out.println(key + " === " + value);
            }
        }
        System.out.println("===================================");
    }

    static void displayAnArrayList2(ArrayList<String> arrayList, String arraylistName, int identifier){
        System.out.println("");
        System.out.println("Displaying " + arraylistName + " from house with id:" + identifier);
        System.out.println("===================================");
        if(arrayList.isEmpty()){
            System.out.println(arraylistName + " is empty");
        } else {
            for(String log : arrayList){
                if(log != null){
                    if(!log.equalsIgnoreCase("null")){
                        String[] msg = log.split("sseparatorr");
                        System.out.println(msg[1]);
                    }
                }
            }
        }
        System.out.println("===================================");
    }

    static void displayAnArrayList(ArrayList<String> arrayList, String arraylistName, int identifier){
        System.out.println("");
        System.out.println("Displaying " + arraylistName + " from house with id:" + identifier);
        System.out.println("===================================");
        if(arrayList.isEmpty()){
            System.out.println(arraylistName + " is empty");
        } else {
            for(String log : arrayList){
                if(log != null){
                    if(!log.equalsIgnoreCase("null")){
                        System.out.println(log);
                    }
                }
            }
        }
        System.out.println("===================================");
    }

    static void displayTheContents(int identifier){
        displayDatafromAMapString(identifier, houses.get(identifier).lastKnownValues, "Last Known Values");
        displayDatafromAMapString(identifier, houses.get(identifier).lastKnownTimeStamps,
                "Last Known Timestamps");
        displayDatafromAMapInteger(identifier, houses.get(identifier).dailyCount, "Daily Count");
        displayDatafromAMapInteger(identifier, houses.get(identifier).weeklyCount, "Weekly Count");
        displayDatafromAMapInteger(identifier, houses.get(identifier).monthlyCount, "Monthly Count");
        displayDatafromAMapDouble(identifier, houses.get(identifier).dailySum, "Daily Sum");
        displayDatafromAMapDouble(identifier, houses.get(identifier).weeklySum, "Weekly Sum");
        displayDatafromAMapDouble(identifier, houses.get(identifier).monthlySum, "Monthly Sum");
        displayDatafromAMapDouble(identifier, houses.get(identifier).dailyAverage, "Daily Average");
        displayDatafromAMapDouble(identifier, houses.get(identifier).weeklyAverage, "Weekly Average");
        displayDatafromAMapDouble(identifier, houses.get(identifier).monthlyAverage, "Monthly Average");
        displayAnArrayList2(houses.get(identifier).mainLogs, "Daily Main Logs", 0);
        displayAnArrayList2(houses.get(identifier).mainLogsWeek, "Weekly Main Logs", 0);
        displayAnArrayList2(houses.get(identifier).mainLogsMonth, "Monthly Main Logs", 0);
    }


    static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    //this one takes the sseparatorr in to consideration
    static HashMap<String, String> arrayListToHashMap(ArrayList<String> arrayList){
        HashMap<String, String> map = new HashMap<String, String>();
        if(!arrayList.isEmpty()){
            for(String entry : arrayList){
                if(entry != null){
                    if(!entry.equalsIgnoreCase("")){
                        String[] msg = entry.split("sseparatorr");
                        if(msg.length > 1){
                            //System.out.println(msg[0] + " : " + msg[1]);
                            map.put(msg[0], msg[1]);
                        }
                    }
                }

            }
        }
        return map;
    }

    //this is to be used for arraylists that are not logs. There are not many... but still, it can be an issue.
    static ArrayList<String> hashMapToArrayList(HashMap<String, String> map){
        ArrayList<String> arrayList = new ArrayList<String>();
        dailyLogsCount = map.size();
        String[] arr = new String[map.size() + 100];
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(DataHolder.isNumeric(entry.getKey())){
                int index = Integer.parseInt(entry.getKey());
                if(index > dailyLogsCount){
                    dailyLogsCount = index;
                }
                String log = entry.getValue();
                //System.out.println("The log is: " + log);
                if(index != -1){
                    if(log != null){
                        if(!log.equalsIgnoreCase("")){
                            if(!log.equalsIgnoreCase("null")){
                                arr[index] = log;
                            }
                        }
                    }
                    //arrayList.add(log);
                }
            }
        }

        //System.out.println(arr[x]);
        //arrayList.addAll(Arrays.asList(arr));
        for(int x=0; x<arr.length; x++){
            if(arr[x] != null){
                if(!arr[x].equalsIgnoreCase("")){
                    if(!arr[x].equalsIgnoreCase("null")){
                        arrayList.add(x + "sseparatorr" + arr[x]);
                    }
                }
            }
        }

        return arrayList;
    }


    static HashMap<String, String> arrayIntegerToHashMap(Integer[] arr){
        HashMap<String, String> map = new HashMap<String, String>();
        for(int x=0; x<arr.length; x++){
            String xs = String.valueOf(x);
            String arrs = String.valueOf(arr[x]);
            map.put(xs, arrs);
        }
        //displayDatafromAMapString(0, map, "arrayIntegerToHashMap:Count");
        return map;
    }

    static HashMap<String, String> arrayDoubleToHashMap(Double[] arr){
        HashMap<String, String> map = new HashMap<String, String>();
        for(int x=0; x<arr.length; x++){
            String xs = String.valueOf(x);
            String arrs = String.valueOf(arr[x]);
            map.put(xs, arrs);
        }
        return map;
    }

    static void hashMapToArrayList2(ArrayList<String> arrayList, HashMap<String, String> map){
        dailyLogsCount = map.size();
        String[] arr = new String[map.size() + 100];
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(DataHolder.isNumeric(entry.getKey())){
                int index = Integer.parseInt(entry.getKey());
                if(index > dailyLogsCount){
                    dailyLogsCount = index;
                }
                String log = entry.getValue();
                //System.out.println("The log is: " + log);
                if(index != -1){
                    if(log != null){
                        if(!log.equalsIgnoreCase("")){
                            if(!log.equalsIgnoreCase("null")){
                                arr[index] = log;
                            }
                        }
                    }
                    //arrayList.add(log);
                }
            }
        }

        //System.out.println(arr[x]);
        //arrayList.addAll(Arrays.asList(arr));
        for(int x=0; x<arr.length; x++){
            if(arr[x] != null){
                if(!arr[x].equalsIgnoreCase("")){
                    if(!arr[x].equalsIgnoreCase("null")){
                        arrayList.add(x + "sseparatorr" + arr[x]);
                    }
                }
            }
        }
    }

    //this is a patch. Normaly we are using hashMapToArrayList2, but somewhere we messed up with
    //the daily logs and it displays the sseparatorr which normaly it should be deleted.
    //to avoid bugs the best way to solve the issue is here.
    static void hashMapToArrayList3(ArrayList<String> arrayList, HashMap<String, String> map){
        dailyLogsCount = map.size();
        String[] arr = new String[map.size() + 100];
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(DataHolder.isNumeric(entry.getKey())){
                int index = Integer.parseInt(entry.getKey());
                if(index > dailyLogsCount){
                    dailyLogsCount = index;
                }
                String log = entry.getValue();
                //System.out.println("The log is: " + log);
                if(index != -1){
                    if(log != null){
                        if(!log.equalsIgnoreCase("")){
                            if(!log.equalsIgnoreCase("null")){
                                //String[] tmp = log.split("sseparatorr");
                                //String newLog = tmp[1];
                                //System.out.println("The log of hashmapttoarralist3 is: " + log);
                                arr[index] = log;
                            }
                        }
                    }
                    //arrayList.add(log);
                }
            }
        }

        //System.out.println(arr[x]);
        //arrayList.addAll(Arrays.asList(arr));
        for(int x=0; x<arr.length; x++){
            if(arr[x] != null){
                if(!arr[x].equalsIgnoreCase("")){
                    if(!arr[x].equalsIgnoreCase("null")){
                        //solving the "sseparatorr" bug HERE
                        //arrayList.add(x + "sseparatorr" + arr[x]);
                        arrayList.add(arr[x]);
                    }
                }
            }
        }
    }

    static String[] arrayListToArray(ArrayList<String> arrayList){
        int size = arrayList.size();
        String[] array = new String[size];
        int count = 0;
        for(String entry: arrayList){
            array[count] = entry;
            count++;
        }
        return array;
    }

    //====================== User Management code ======================================

    static boolean userExists(String username){
        for(User user : users){
            if(user.userName.equalsIgnoreCase(username)){
                return true;
            }
        }
        return false;
    }


    static boolean passwordCorrect(String username, String password){
        for(User user : users){
            if(user.userName.equalsIgnoreCase(username)){
                if(user.passWord.equalsIgnoreCase(password)){
                    return true;
                }
            }
        }
        return false;
    }


    static boolean login(String username, String password){
        if(userExists(username)){
            if(passwordCorrect(username,password)){

                return true;
            }
        }
        return false;
    }



    static void register(String userName, String passWord, String email, String name){
        if(!userExists(userName)){
            User user = new User(userName, passWord, email, name);
            DataHolder.users.add(user);
            DB db = new DB();
            db.addOneUser(userName,passWord,email,name);
        }
    }

    static boolean editUser(String username,String password,String email,String name){
        User user =findUserByUsernam(username);
        if (user.userName.equalsIgnoreCase(username)){
            user.setName(name);
            user.setPassWord(password);
            user.setEmail(email);
            return true;
        }else
            return false;

    }



    static User findUserByUsernam(String username){
        for(User user : users){
            if(user.userName.equalsIgnoreCase(username)){
                return user;
            }
        }
        return null;
    }


    //==================== Publishing tools =======================

    //publishing an ArrayList
    static void publishArrayList(MqttClient sampleClient, String topic, ArrayList arrayList) throws MqttException{
        Gson gson = new Gson();
        sampleClient.publish(topic, new MqttMessage(gson.toJson(arrayList).getBytes()));
    }

    //publishing a String Array
    static void publishStringArray(MqttClient sampleClient, String topic, String[] array) throws MqttException{
        Gson gson = new Gson();
        sampleClient.publish(topic, new MqttMessage(gson.toJson(array).getBytes()));
    }

    //publishing a Map
    static void publishAMap(MqttClient sampleClient, String topic, Map map) throws MqttException{
        Gson gson = new Gson();
        sampleClient.publish(topic, new MqttMessage(gson.toJson(map).getBytes()));
    }

    //publishing a HashMap
    static void publishAHashMap(MqttClient sampleClient, String topic, HashMap map) throws MqttException{
        Gson gson = new Gson();
        sampleClient.publish(topic, new MqttMessage(gson.toJson(map).getBytes()));
    }


    // ==================== apocalypse surviving tools ==============================

    static Date stringToDate(String string){
        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
//        String date = dateOnly.format(cal.getTime());
//        SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm:ss");
//        String time = timeOnly.format(cal.getTime());
        //String dateAndTime = date + time;
        Date date3 = null;
        try {
            String string2 = string.replace("-", " ");
            date3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(string2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date3;
    }

    static Date stringToDatePatch(String string){
        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
//        String date = dateOnly.format(cal.getTime());
//        SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm:ss");
//        String time = timeOnly.format(cal.getTime());
        //String dateAndTime = date + time;
        Date date3 = null;
        try {
            String string2 = string.replace("-", " ");
            date3 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(string2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date3;
    }

    static HashMap<Date, Integer> stringStringMapToDateInteger(HashMap<String, String> map1){
        HashMap<Date, Integer> map2 = new HashMap<Date, Integer>();
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String forDate = entry.getKey();
            Integer theInteger = Integer.valueOf(entry.getValue());
            Date theDate = stringToDate(forDate);
            System.out.println("translating an entry to Date/Integer");
            System.out.println("The Date: " + theDate.toString());
            System.out.println("The Integer: " + theDate.toString());
            map2.put(theDate, theInteger);
        }
        return map2;
    }

    static HashMap<Date, Integer> stringStringMapToDateIntegerPatch(HashMap<String, String> map1){
        HashMap<Date, Integer> map2 = new HashMap<Date, Integer>();
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String forDate = entry.getKey();
            Integer theInteger = Integer.valueOf(entry.getValue());
            Date theDate = stringToDatePatch(forDate);
            System.out.println("translating an entry to Date/Integer");
            System.out.println("The Date: " + theDate.toString());
            System.out.println("The Integer: " + theDate.toString());
            map2.put(theDate, theInteger);
        }
        return map2;
    }

    static HashMap<Date, Integer> doubleArraypToDateInteger24(Double[] arr){
        HashMap<Date, Integer> map2 = new HashMap<Date, Integer>();
        for(int x= 0; x<arr.length; x++){
            String z = "00";
            if(x <10){
                z = "0" + x;
            }else if ( x == 24){
                z = "00";
            } else if ( x == 0){
                z = "00";
            } else {
                z = String.valueOf(x);
            }
            String forDate = datenum + "/" + month + "/" + year + "-" + z + ":00:00";
            System.out.println("forDate: " + forDate);
            Date theDate = stringToDate(forDate);
            if(arr[x] != null){
                Double dub = Math.ceil(arr[x]);
                Integer num = dub.intValue();
                if(isNumeric(arr[x].toString())){
                    if(!arr[x].toString().equalsIgnoreCase("0.0")){
                        if(!arr[x].toString().equalsIgnoreCase("0")){
                            System.out.println("The date (in date):" + theDate);
                            System.out.println("arr[x]:" + arr[x]);
                            System.out.println("The integer" + num);
                            map2.put(theDate, num);
                        }
                    }
                }
            }
        }
        return map2;
    }

    static HashMap<Date, Double> doubleArraypToDateDouble24(Double[] arr){
        HashMap<Date, Double> map2 = new HashMap<Date, Double>();
        for(int x= 0; x<arr.length; x++){
            String z = "00";
            if(x <10){
                z = "0" + x;
            }else if ( x == 24){
                z = "00";
            }else if ( x == 0){
                z = "00";
            } else {
                z = String.valueOf(x);
            }
            String forDate = datenum + "/" + month + "/" + year + "-" + z + ":00:00";
            System.out.println("forDate: " + forDate);
            Date theDate = stringToDate(forDate);
            if(arr[x] != null){
                if(isNumeric(arr[x].toString())){
                    if(!arr[x].toString().equalsIgnoreCase("0.0")){
                        if(!arr[x].toString().equalsIgnoreCase("0")){
                            System.out.println("The date (in date):" + theDate);
                            System.out.println("arr[x]:" + arr[x]);
                            map2.put(theDate, arr[x]);
                        }
                    }
                }
            }
        }
        return map2;
    }

    static HashMap<Date, Integer> doubleArraypToDateIntegerM(Double[] arr){
        HashMap<Date, Integer> map2 = new HashMap<Date, Integer>();
        String number = "0000";
        System.out.println("The zise of the Double[] arr: " + arr.length);
        for(int x= 0; x<arr.length; x++){
            if(x<10){
                number = "000" + x;
            }

            if(x>= 10){
                number = "00" + x;
            }

            if(x>=100){
                number = "0" + x;
            }

            if(x>=1000){
                number = String.valueOf(x);
            }

            //System.out.println("the number that comes form x is: " + number);
            String[] num = number.split("");
            String dayofthemonth = num[0] + num[1];
            //System.out.println("Day of the month: " + dayofthemonth);
            String z = num[2] + num[3];
            //System.out.println("Time(z): " + z);
            if(arr[x] != null){
                if(isNumeric(arr[x].toString())){
                    if(!arr[x].toString().equalsIgnoreCase("0.0")){
                        String forDate = dayofthemonth + "/" + month + "/" + year + "-" + z + ":00:00";
                        System.out.println("forDate:" + forDate);
                        Date theDate = stringToDate(forDate);
                        int average = arr[x].intValue();
                        map2.put(theDate, average);
                    }
                }
            }

        }
        return map2;
    }


    static HashMap<Date, Integer> doubleArraypToDateIntegerW(Double[] arr){
        HashMap<Date, Integer> map2 = new HashMap<Date, Integer>();
        String actual = month+ datenum;
        System.out.println("Actual(day): " + actual);
        int actualday = datenum * 100;
        System.out.println("Actualday: " + actualday);
        int bottom = 0;
        int top = actualday + 100;
        if(actualday > 700){
            bottom = actualday -700;
        }
        String number = "0000";
        System.out.println("The zise of the Double[] arr: " + arr.length);
        for(int x = 0; x<arr.length; x++){
                if(x<10){
                    number = "000" + x;
                }

                if(x>= 10){
                    number = "00" + x;
                }

                if(x>=100){
                    number = "0" + x;
                }

                if(x>=1000){
                    number = String.valueOf(x);
                }

                //System.out.println("the number that comes form x is: " + number);
                String[] num = number.split("");
                String dayofthemonth = num[0] + num[1];
                //System.out.println("Day of the month: " + dayofthemonth);
                String z = num[2] + num[3];
                //System.out.println("Time(z): " + z);
                if(arr[x] != null){
                    if(isNumeric(arr[x].toString())){
                        if(!arr[x].toString().equalsIgnoreCase("0.0")){
                            String forDate = dayofthemonth + "/" + month + "/" + year + "-" + z + ":00:00";
                            System.out.println("forDate:" + forDate);
                            Date theDate = stringToDate(forDate);
                            int average = arr[x].intValue();
                            if(x>=bottom){
                                map2.put(theDate, average);
                            }
                        }
                    }
                }


            }

        return map2;
    }

    static HashMap<Date, Double> doubleArraypToDateDoubleM(Double[] arr){
        HashMap<Date, Double> map2 = new HashMap<Date, Double>();
        String number = "0000";
        System.out.println("The zise of the Double[] arr: " + arr.length);
        for(int x= 0; x<arr.length; x++){
            if(x<10){
                number = "000" + x;
            }

            if(x>= 10){
                number = "00" + x;
            }

            if(x>=100){
                number = "0" + x;
            }

            if(x>=1000){
                number = String.valueOf(x);
            }

            //System.out.println("the number that comes form x is: " + number);
            String[] num = number.split("");
            String dayofthemonth = num[0] + num[1];
            //System.out.println("Day of the month: " + dayofthemonth);
            String z = num[2] + num[3];
            //System.out.println("Time(z): " + z);
            if(arr[x] != null){
                if(isNumeric(arr[x].toString())){
                    if(!arr[x].toString().equalsIgnoreCase("0.0")){
                        String forDate = dayofthemonth + "/" + month + "/" + year + "-" + z + ":00:00";
                        System.out.println("forDate:" + forDate);
                        Date theDate = stringToDate(forDate);
                        map2.put(theDate, arr[x]);
                    }
                }
            }

        }
        return map2;
    }


    static HashMap<Date, Double> doubleArraypToDateDoubleW(Double[] arr){
        HashMap<Date, Double> map2 = new HashMap<Date, Double>();
        String actual = month+ datenum;
        System.out.println("Actual(day): " + actual);
        int actualday = datenum * 100;
        System.out.println("Actualday: " + actualday);
        int bottom = 0;
        int top = actualday + 100;
        if(actualday > 700){
            bottom = actualday -700;
        }
        String number = "0000";
        System.out.println("The zise of the Double[] arr: " + arr.length);
        for(int x=0; x<arr.length; x++){
                if(x<10){
                    number = "000" + x;
                }

                if(x>= 10){
                    number = "00" + x;
                }

                if(x>=100){
                    number = "0" + x;
                }

                if(x>=1000){
                    number = String.valueOf(x);
                }

                //System.out.println("the number that comes form x is: " + number);
                String[] num = number.split("");
                String dayofthemonth = num[0] + num[1];
                //System.out.println("Day of the month: " + dayofthemonth);
                String z = num[2] + num[3];
                //System.out.println("Time(z): " + z);
                if(arr[x] != null){
                    if(isNumeric(arr[x].toString())){
                        if(!arr[x].toString().equalsIgnoreCase("0.0")){
                            String forDate = dayofthemonth + "/" + month + "/" + year + "-" + z + ":00:00";
                            System.out.println("forDate:" + forDate);
                            Date theDate = stringToDate(forDate);
                            if(x>=bottom){
                                map2.put(theDate, arr[x]);
                            }
                        }
                    }
                }

        }
        return map2;
    }



}
