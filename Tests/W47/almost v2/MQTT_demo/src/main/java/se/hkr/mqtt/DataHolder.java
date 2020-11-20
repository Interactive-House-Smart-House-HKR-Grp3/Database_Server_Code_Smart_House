package se.hkr.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataHolder {
    static boolean dbActivated = true;
    static boolean isConnected = false;
    static String activeUser = "deafault";
    static ArrayList<User> users = new ArrayList<User>();
    static String month = "";
    static String week = "";
    static String day = "";
    static int dailyLogsCount = 0;
    static HashMap<String, String>flags = new HashMap();
    static ArrayList<House> houses = new ArrayList<House>(); //this is the placeholder for the several Houses



    static void hashmapsToFirestore(int identifier) throws InterruptedException {
        String friendlydate = day.replace("/", "");
        FireStoreQueries fire = new FireStoreQueries();

        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier + "sslashs",
                "lastKnownValuessslashs",houses.get(identifier).lastKnownValues);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier + "sslashs",
                "lastKnownTimeStampsslashs",houses.get(identifier).lastKnownTimeStamps);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashscountsslashs",
                "dailyCountsslashs" + friendlydate + "sslashs",houses.get(identifier).dailyCount);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashscountsslashs",
                "weeklyCountsslashs" + week + "sslashs",houses.get(identifier).weeklyCount);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier+ "sslashsstatssslashscountsslashs",
                "monthlyCountsslashs" + month + "sslashs",houses.get(identifier).monthlyCount);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashssumsslashs",
                "dailySumsslashs" + friendlydate + "sslashs",houses.get(identifier).dailySum);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier +"sslashsstatssslashssumsslashs",
                "weeklySumsslashs" + week + "sslashs",houses.get(identifier).weeklySum);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashssumsslashs",
                "monthlySumsslashs" + month + "sslashs",houses.get(identifier).monthlySum);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashsaveragesslashs",
                "dailyAveragesslashs" + friendlydate + "sslashs",houses.get(identifier).dailyAverage);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashsaveragesslashs",
                "weeklyAveragesslashs" + week + "sslashs",houses.get(identifier).weeklyAverage);
        Thread.sleep(2000);

        //added to loader
        fire.createDocumentfromAMap2("databasesslashs" + identifier + "sslashsstatssslashsaveragesslashs",
                "monthlyAveragesslashs" + month + "sslashs",houses.get(identifier).monthlyAverage);
        Thread.sleep(2000);

        //Hopefully saves the daily logs
        //added to loader
        fire.createDocumentfromAMap("databasesslashs" + identifier + "sslashslogssslashsdailylogssslashs",
                "mainsslashs" + friendlydate,
                arrayListToHashMap(houses.get(identifier).mainLogs));
        Thread.sleep(2000);
    }


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
        houses.get(identifier).lastKnownValues = fire.readDocument("databasesslashs" + identifier
                        + "sslashs",
                "lastKnownValuessslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Last known Timestamps is loading");
        houses.get(identifier).lastKnownTimeStamps = fire.readDocument("databasesslashs" + identifier + "sslashs",
                "lastKnownTimeStampsslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Daily Count is loading");
        houses.get(identifier).dailyCount = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs",
                "dailyCountsslashs" + friendlydate.toString() + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Weekly Count is loading");
        houses.get(identifier).weeklyCount = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs",
                "weeklyCountsslashs" + week + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Monthly Count is loading");
        houses.get(identifier).monthlyCount = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashscountsslashs",
                "monthlyCountsslashs" + month + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Daily Sum is loading");
        houses.get(identifier).dailySum = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashssumsslashs",
                "dailySumsslashs" + friendlydate + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Weekly Sum is loading");
        houses.get(identifier).weeklySum = fire.readDocument2("databasesslashs" + identifier
                        +"sslashsstatssslashssumsslashs",
                "weeklySumsslashs" + week + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Monthly Sum is loading");
        houses.get(identifier).monthlySum = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashssumsslashs",
                "monthlySumsslashs" + month + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Daily Average is loading");
        houses.get(identifier).dailyAverage = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs",
                "dailyAveragesslashs" + friendlydate + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Weekly Average is loading");
        houses.get(identifier).weeklyAverage = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs",
                "weeklyAveragesslashs" + week + "sslashs");
        Thread.sleep(2000);

        //added to the publisher
        System.out.println("Monthly Average is loading");
        houses.get(identifier).monthlyAverage = fire.readDocument2("databasesslashs" + identifier
                        + "sslashsstatssslashsaveragesslashs",
                "monthlyAveragesslashs" + month + "sslashs");
        Thread.sleep(2000);

        System.out.println("Main Logs are loading");
        houses.get(identifier).mainLogs = hashMapToArrayList(fire.readDocument("databasesslashs" + identifier + "sslashslogssslashsdailylogssslashs",
                "mainsslashs" + friendlydate));
        Thread.sleep(2000);
    }

    //It is for publishing data to topics. I don't know why I called it displayer
    static void publisher(MqttClient sampleClient, int identifier) throws MqttException {

        String friendlydate = day.replace("/", "");
        MqttMessage message;
        String topic = "db/test/5";
        String content = "Hello I am the displayer and that is my identifier: "  + identifier;
//        MqttMessage message = new MqttMessage(content.getBytes());
//        message.setQos(0);
//        sampleClient.publish(topic, message);

        //publishing lastKnownValues on "database/" + identifier + "/lastKnownValues/topic_of_the_subscription"
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownValues.entrySet()) {
            System.out.println("lastKnownValues iterates");
            topic = "database/" + identifier
                    + "/lastKnownValues/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue();
            System.out.println("Monitored topic: " + topic);
            System.out.println("Monitored content: " + content);
            if(!entry.getKey().replace("sslashs", "/").equalsIgnoreCase("-1")){
                message = new MqttMessage(content.getBytes());
                message.setQos(0);
                sampleClient.publish(topic, message);
            }
            try {
                Thread.sleep(300);
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

        //publishing lastKnownTimeStamps on "database/" + identifier + "/lastKnownTimeStamps/topic_of_the_subscription"
        for (Map.Entry<String, String> entry : houses.get(identifier).lastKnownTimeStamps.entrySet()) {
            topic = "database/" + identifier
                    + "/lastKnownTimeStamps/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue();
            if(!entry.getKey().replace("sslashs", "/").equalsIgnoreCase("-1")){
                message = new MqttMessage(content.getBytes());
                message.setQos(0);
                sampleClient.publish(topic, message);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        for (Map.Entry<String, Integer> entry : houses.get(identifier).dailyCount.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/count/" +
                    "dailyCount/" + friendlydate + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //publishing weeklyCount on "database/" + identifier + "/stats/count/",
        // "weeklyCount/" + week + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).weeklyCount.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/count/" +
                    "weeklyCount/" + week + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //publishing monthlyCount on "database/" + identifier + "/stats/count/",
        // "monthlyCount/" + week + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).monthlyCount.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/count/" +
                    "monthlyCount/" + month + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //================= SUM =============================

        //publishing dailySum on "database/" + identifier + "/stats/sum/" +
        // "dailySum/" + friendlydate.toString() + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).dailySum.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/sum/" +
                    "dailySum/" + friendlydate + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //publishing weeklySum on "database/" + identifier + "/stats/sum/" +
        // "weeklySum/" + week + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).weeklySum.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/sum/" +
                    "weeklySum/" + week + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //publishing monthlySum on "database/" + identifier + "/stats/sum/" +
        // "monthlySum/" + month + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).monthlySum.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/sum/" +
                    "monthlySum/" + month + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //================= AVERAGE =============================

        //publishing dailyAverage on "database/" + identifier + "/stats/average/" +
        // "dailyAverage/" + friendlydate.toString() + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).dailyAverage.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/average/" +
                    "dailyAverage/" + friendlydate + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //publishing weeklyAverage on "database/" + identifier + "/stats/average/" +
        // "weeklyAverage/" + week + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).weeklyAverage.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/average/" +
                    "weeklyAverage/" + week + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //publishing monthlyAverage on "database/" + identifier + "/stats/average/" +
        // "monthlyAverage/" + month + "/topic_of_the_subscription"
        for (Map.Entry<String, Integer> entry : houses.get(identifier).monthlyAverage.entrySet()) {
            topic = "database/" + identifier
                    + "/stats/average/" +
                    "monthlyAverage/" + month + "/" + entry.getKey().replace("sslashs", "/");
            content = entry.getValue().toString();
            message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish(topic, message);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        //"database/" + identifier + "/logs/dailylogs/",
//        //                "main/" + friendlydate + "/"
//        String msg = "";
//        for (String text : houses.get(identifier).mainLogs){
//            if(text != null){
//                if(!text.equalsIgnoreCase("null")){
//                    msg = msg + text + "\n";
//                }
//            }
//        }
//        topic = "database/" + identifier + "/logs/dailylogs/" + "main";
//        message = new MqttMessage(msg.getBytes());
//        message.setQos(0);
//        sampleClient.publish(topic, message);

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
        displayDatafromAMapInteger(identifier, houses.get(identifier).dailySum, "Daily Sum");
        displayDatafromAMapInteger(identifier, houses.get(identifier).weeklySum, "Weekly Sum");
        displayDatafromAMapInteger(identifier, houses.get(identifier).monthlySum, "Monthly Sum");
        displayDatafromAMapInteger(identifier, houses.get(identifier).dailyAverage, "Daily Average");
        displayDatafromAMapInteger(identifier, houses.get(identifier).weeklyAverage, "Weekly Average");
        displayDatafromAMapInteger(identifier, houses.get(identifier).monthlyAverage, "Monthly Average");
        displayAnArrayList(houses.get(identifier).mainLogs, "Main Logs", 0);
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
                    //arrayList.add(log);
                    arr[index] = log;
                }
            }
        }

        //System.out.println(arr[x]);
        arrayList.addAll(Arrays.asList(arr));

        return arrayList;
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

}
