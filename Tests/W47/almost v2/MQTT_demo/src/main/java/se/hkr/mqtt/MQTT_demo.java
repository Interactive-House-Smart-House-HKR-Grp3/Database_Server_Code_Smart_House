package se.hkr.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Random;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

//Helper class for message handling
class MqttPostPropertyMessageListener implements IMqttMessageListener {

    @Override
    public void messageArrived(String var1, MqttMessage var2) throws Exception {
        FireStoreQueries fireStoreQueries = new FireStoreQueries();
        int identifier = 0;
        String flag = DataHolder.flags.get(var1);
        String fireStoreFriednlyPath = var1.replace("/", "sslashs");
        String theNameOfTheArchiveCollection = "databasesslashs" + identifier + "sslashsrchivesslashs" + fireStoreFriednlyPath;
        String MQTTFriendlyPath = fireStoreFriednlyPath.replace("sslashs", "/");
        System.out.println("reply topic  : " + MQTTFriendlyPath);
        System.out.println("reply payload: " + var2.toString());

        //Getting the timestamp. In order to server statistics in a proper way the system needs to
        //to be aware of the month the week and the day.
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateOnly.format(cal.getTime());
        SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm:ss");
        String time = timeOnly.format(cal.getTime());
        System.out.println(time);
        String dateAndTime = date + "-" +  time;
        String dateAndTimeFirestoreFriendly = dateAndTime.replace("/", "");

        //Archive
        //for the archive it has to replace the spaces with dashes because it makes it more readable
        String var2WithTimeStamp = dateAndTimeFirestoreFriendly + var2.toString();
        String var2WithDashes = var2WithTimeStamp.replace(" ", "-");
        System.out.println("Collection: " + theNameOfTheArchiveCollection);
        System.out.println("document: " + var2WithDashes);

        //if the flag is 1, it stores data and counts stats
        if(flag.equalsIgnoreCase("1")){
            //run the rest of the code
            DataHolder.houses.get(identifier).mainLogs.add( DataHolder.dailyLogsCount + "sseparatorr" + dateAndTime + " The topic:" + var1 +
                    " had the payload:" + var2.toString());
            DataHolder.dailyLogsCount++;
            if(DataHolder.houses.get(identifier).logs.containsKey(var1)){
                DataHolder.houses.get(identifier).logs.get(var1).add(dateAndTime + " The topic:" + var1 +
                        " had the payload:" + var2.toString());
            } else {
                DataHolder.houses.get(identifier).logs.put(var1, new ArrayList<String>());
            }


            //fireStoreQueries.createDocument(theNameOfTheArchiveCollection,var2WithDashes);
//        fireStoreQueries.createElement(theNameOfTheArchiveCollection,var2WithDashes,
//                "timestamp", dateAndTime);

            //Last known value
            String collectionLKV = "databasesslashs" + identifier + "sslashs";
            String documentLKV = "lastKnownValuessslashs";
            String keyLKV = "defaultKey";
            String valueLKV = "defailtValue2";
            keyLKV = fireStoreFriednlyPath;
            valueLKV = var2.toString();
//        Thread.sleep(3000);
//        fireStoreQueries.createDocument(collectionLKV, documentLKV);
//        fireStoreQueries.createElement(collectionLKV, documentLKV, keyLKV, valueLKV);
            DataHolder.houses.get(identifier).lastKnownValues.put(keyLKV,valueLKV);


            //Last known timestamp
            String collectionLKT = "databasesslashs" + identifier + "sslashs";
            String documentLKT = "lastKnownTimeStampsslashs";
            String keyLKT = "defaultKey";
            String valueLKT = "defailtValue2";
            keyLKT = fireStoreFriednlyPath;
            valueLKT = dateAndTime;
//        Thread.sleep(3000);
//        fireStoreQueries.createDocument(collectionLKT, documentLKT);
//        fireStoreQueries.createElement(collectionLKT, documentLKT, keyLKT, valueLKT);
            DataHolder.houses.get(identifier).lastKnownTimeStamps.put(keyLKT,valueLKT);


            //Statistics
            //Daily count
            if(DataHolder.houses.get(identifier).dailyCount.containsKey(fireStoreFriednlyPath)){
                int count = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, count+1);
            } else {
                DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, 0);
            }

            //Weekly count
            if(DataHolder.houses.get(identifier).weeklyCount.containsKey(fireStoreFriednlyPath)){
                int count = DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, count+1);
            } else {
                DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, 0);
            }

            //Monthly count
            if(DataHolder.houses.get(identifier).monthlyCount.containsKey(fireStoreFriednlyPath)){
                int count = DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, count+1);
            } else {
                DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, 0);
            }


            if(DataHolder.isNumeric(var2.toString())){
                //SUM
                //Daily sum
                if(DataHolder.houses.get(identifier).dailySum.containsKey(fireStoreFriednlyPath)){
                    int current = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    int newValue = Integer.parseInt(var2.toString());
                    DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, current + newValue);
                } else {
                    DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, 0);
                }


                //Weekly sum
                if(DataHolder.houses.get(identifier).weeklySum.containsKey(fireStoreFriednlyPath)){
                    int current = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath);
                    int newValue = Integer.parseInt(var2.toString());
                    DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, current+newValue);
                } else {
                    DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, 0);
                }


                //Monthly sum
                if(DataHolder.houses.get(identifier).monthlySum.containsKey(fireStoreFriednlyPath)){
                    int current = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath);
                    int newValue = Integer.parseInt(var2.toString());
                    DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, current+newValue);
                } else {
                    DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, 0);
                }


                //AVG (AVERAGE)
                //Daily average
                if(DataHolder.houses.get(identifier).dailyAverage.containsKey(fireStoreFriednlyPath)){
                    int average = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath)/
                            DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, average);
                } else {
                    DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, 0);
                }

                //Weekly average
                if(DataHolder.houses.get(identifier).weeklyAverage.containsKey(fireStoreFriednlyPath)){
                    int average = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath)/
                            DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, average);
                } else {
                    DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, 0);
                }

                //Monthly average
                if(DataHolder.houses.get(identifier).monthlyAverage.containsKey(fireStoreFriednlyPath)){
                    int average = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath)/
                            DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, average);
                } else {
                    DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, 0);
                }


            }


        } //end of if (flag.equalsIgnoreCase("1"))

        if(flag.equalsIgnoreCase("2")){
            //run some continues stream statistics
            String keyLKVtmp = fireStoreFriednlyPath;
            String valueLKVtmp = var2.toString();
            if(!valueLKVtmp.equalsIgnoreCase(DataHolder.houses.get(identifier).lastKnownValues.get(keyLKVtmp))){
                //Run the code and update the statistics.
                //run the rest of the code
                DataHolder.houses.get(identifier).mainLogs.add( DataHolder.dailyLogsCount + "sseparatorr" + dateAndTime + " The topic:" + var1 +
                        " had the payload:" + var2.toString());
                DataHolder.dailyLogsCount++;

//                //this is for specialized logs. Not yet that.
//                if(DataHolder.houses.get(identifier).logs.containsKey(var1)){
//                    DataHolder.houses.get(identifier).logs.get(var1).add(dateAndTime + " The topic:" + var1 +
//                            " had the payload:" + var2.toString());
//                } else {
//                    DataHolder.houses.get(identifier).logs.put(var1, new ArrayList<String>());
//                }



                //Last known value
                String collectionLKV = "databasesslashs" + identifier + "sslashs";
                String documentLKV = "lastKnownValuessslashs";
                String keyLKV = "defaultKey";
                String valueLKV = "defailtValue2";
                keyLKV = fireStoreFriednlyPath;
                valueLKV = var2.toString();
//        Thread.sleep(3000);
//        fireStoreQueries.createDocument(collectionLKV, documentLKV);
//        fireStoreQueries.createElement(collectionLKV, documentLKV, keyLKV, valueLKV);
                DataHolder.houses.get(identifier).lastKnownValues.put(keyLKV,valueLKV);


                //Last known timestamp
                String collectionLKT = "databasesslashs" + identifier + "sslashs";
                String documentLKT = "lastKnownTimeStampsslashs";
                String keyLKT = "defaultKey";
                String valueLKT = "defailtValue2";
                keyLKT = fireStoreFriednlyPath;
                valueLKT = dateAndTime;
//        Thread.sleep(3000);
//        fireStoreQueries.createDocument(collectionLKT, documentLKT);
//        fireStoreQueries.createElement(collectionLKT, documentLKT, keyLKT, valueLKT);
                DataHolder.houses.get(identifier).lastKnownTimeStamps.put(keyLKT,valueLKT);


                //Statistics
                //Daily count
                if(DataHolder.houses.get(identifier).dailyCount.containsKey(fireStoreFriednlyPath)){
                    int count = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, count+1);
                } else {
                    DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, 0);
                }

                //Weekly count
                if(DataHolder.houses.get(identifier).weeklyCount.containsKey(fireStoreFriednlyPath)){
                    int count = DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, count+1);
                } else {
                    DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, 0);
                }

                //Monthly count
                if(DataHolder.houses.get(identifier).monthlyCount.containsKey(fireStoreFriednlyPath)){
                    int count = DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, count+1);
                } else {
                    DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, 0);
                }


                if(DataHolder.isNumeric(var2.toString())){
                    //SUM
                    //Daily sum
                    if(DataHolder.houses.get(identifier).dailySum.containsKey(fireStoreFriednlyPath)){
                        int current = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        int newValue = Integer.parseInt(var2.toString());
                        DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, current + newValue);
                    } else {
                        DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, 0);
                    }


                    //Weekly sum
                    if(DataHolder.houses.get(identifier).weeklySum.containsKey(fireStoreFriednlyPath)){
                        int current = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath);
                        int newValue = Integer.parseInt(var2.toString());
                        DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, current+newValue);
                    } else {
                        DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, 0);
                    }


                    //Monthly sum
                    if(DataHolder.houses.get(identifier).monthlySum.containsKey(fireStoreFriednlyPath)){
                        int current = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath);
                        int newValue = Integer.parseInt(var2.toString());
                        DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, current+newValue);
                    } else {
                        DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, 0);
                    }


                    //AVG (AVERAGE)
                    //Daily average
                    if(DataHolder.houses.get(identifier).dailyAverage.containsKey(fireStoreFriednlyPath)){
                        int average = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath)/
                                DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, average);
                    } else {
                        DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, 0);
                    }

                    //Weekly average
                    if(DataHolder.houses.get(identifier).weeklyAverage.containsKey(fireStoreFriednlyPath)){
                        int average = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath)/
                                DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, average);
                    } else {
                        DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, 0);
                    }

                    //Monthly average
                    if(DataHolder.houses.get(identifier).monthlyAverage.containsKey(fireStoreFriednlyPath)){
                        int average = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath)/
                                DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, average);
                    } else {
                        DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, 0);
                    }


                }
            } else {
                System.out.println(keyLKVtmp + " Was not updated");
                System.out.println("Its value was: " + valueLKVtmp);
            }
        } //end of if (flag.equalsIgnoreCase("2"))

        if(flag.equalsIgnoreCase("3")){
            //Login module starts here
            String isTheUserValid = "null";
            Gson gson = new Gson();

            if ("web/request/register_user".equals(var1)) {
                System.out.println("reply topic  : " + var1);
                String[] userRegistration = gson.fromJson(var2.toString(), String[].class);
                //    System.out.println("reply payload: " + var2.toString());

                String accauntname = userRegistration[ 0 ];
                String password = userRegistration[ 1 ];
                String name = userRegistration[ 2 ];
                String email = userRegistration[ 3 ];

                //I am checking here if this user exists before saving the new user to the database so that the new user can not have the same userName as another User
                if (DataHolder.userExists(accauntname)){
                    isTheUserValid ="false";
                    MQTT_demo.publish("web/statistics/registration",isTheUserValid);
                }else if (!DataHolder.userExists(accauntname)) {
                    //   System.out.println("Username: " + accauntname + " password: " + password+" name: " + name+" email: "+email);
//                    User user1 = new User(accauntname, password, email, name);
//                    DataHolder.users.add(user1);
                    DataHolder.register(accauntname, password, email, name);
                    //Diplaying all users
                    System.out.println("==========================");
                    System.out.println("Displaying all registered users");
                    for (User user : DataHolder.users) {
                        System.out.println("==========================");
                        System.out.println("Username: " + user.userName);
                        System.out.println("Password: " + user.passWord);
                        System.out.println("Email: " + user.email);
                        System.out.println("Name: " + user.name);
                        System.out.println("==========================");

                        if (DataHolder.userExists(accauntname)) {
                            isTheUserValid = "true";
                            MQTT_demo.publish("web/statistics/registration", isTheUserValid);

                        } else
                            isTheUserValid = "false";
                        MQTT_demo.publish("web/statistics/registration", isTheUserValid);


                    }
                }
            }

            else if(var1.equalsIgnoreCase("web/request/user")) {
                String[] userValidation = gson.fromJson(var2.toString(), String[].class);
                String accauntname = userValidation[ 0 ];
                String password = userValidation[ 1 ];
                boolean checkForValidationOfTheUser = DataHolder.login(accauntname, password);
                if (checkForValidationOfTheUser) {
                    User user = DataHolder.findUserByUsernam(accauntname);
                    String accountName = user.getUserName();
                    String userPassword = user.getPassWord();
                    String nameOfTheUser = user.getName();
                    String emailOfTheUser = user.getEmail();
                    MQTT_demo.responseToValidationOfTheUser = "This is a valid User";

                    String[] publishArray = new String[ 4 ];
                    publishArray[ 0 ] = accountName;
                    publishArray[ 1 ] = userPassword;
                    publishArray[ 2 ] = nameOfTheUser;
                    publishArray[ 3 ] = emailOfTheUser;
                    MQTT_demo.publishArrayOfStringsAsJSON("web/statistics/user", publishArray);

                } else {
                    MQTT_demo.responseToValidationOfTheUser = "This is not a valid User";
                    String[] publishArray = new String[ 4 ];
                    publishArray[ 0 ] = null;
                    publishArray[ 1 ] = null;
                    publishArray[ 2 ] = null;
                    publishArray[ 3 ] = null;
                    MQTT_demo.publishArrayOfStringsAsJSON("web/statistics/user", publishArray);


                }

            } //Login module ends here
        }

    } //end of messageArrived()
}

public class MQTT_demo {

    static String responseToValidationOfTheUser ="";
    static ArrayList<String> subscriptions = new ArrayList<String>();

    static MqttClient sampleClient;
    static Random rand;

    public static void main(String[] args) throws InterruptedException {

        //Getting the timestamp
        Calendar cal = Calendar.getInstance();
        SecureRandom random = new SecureRandom();
        int randomseed = random.nextInt();
        String mqttUsername = Configurations.mqttUsername + randomseed;
        SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateOnly.format(cal.getTime());
        String[] timeStamp = date.split("/");
        DataHolder.month = timeStamp[0];
        DataHolder.week = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
        DataHolder.day = date;
        System.out.println("Month: " + DataHolder.month);
        System.out.println("Week: " + DataHolder.week);
        System.out.println("Date: " + DataHolder.day);

        //creating a house.
        House house = new House(0);
        DataHolder.houses.add(0, house);

        //loading the data
        DataHolder.loadHashMapsFromFireStore(0);
        DataHolder.displayTheContents(0);


        //adding subscriptions by the "subscriptions.txt" file
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "subscriptions.txt"));
            String line = reader.readLine();
            while (line != null) {
                //The subscriptions.txt must always have flag
                //The flags for now are:
                //0 for now storing the data
                //1 for storing the data, (logs and stats are includes)
                //Special flags will come soon because of the registration and login.

                String[] seed = line.split(" ");
                String flag = seed[0];
                String var1 = seed[1];
                DataHolder.flags.put(var1, flag);
                //System.out.println(line);
                subscriptions.add(var1);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        //Setting up the connection
        String port = String.valueOf(Configurations.mqttPort);
        String broker = Configurations.mqttHost + ":" + port;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            //Creating the client. the clientId can be configured on Configurations and a random number is added.
            final MqttClient sampleClient = new MqttClient(broker, mqttUsername, persistence);

            //Creating the options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(180);
            sampleClient.connect(connOpts);
            System.out.println("broker: " + broker + " Connected");


            //Doing the actual subscriptions
            for (String topicReply : subscriptions){
                sampleClient.subscribe(topicReply, new MqttPostPropertyMessageListener());
                System.out.println("subscribed to: " + topicReply);
                Thread.sleep(2000);
            }

            //updating the Firestore
            Thread t = new Thread(){
                public void run(){
                    //Thread Implmentation code here
                    while(true){
                        try {
                            //1 is the identifier. here, and autmatic system will be implemented
                            DataHolder.hashmapsToFirestore(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

            };

            t.start();

            //pulishing stuff.
            Thread displayer = new Thread(){
                public void run(){
                    //Thread Implmentation code here
                    while(true){
                        //1 is the identifier. here, and autmatic system will be implemented
                        try {
                            DataHolder.publisher(sampleClient,0);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };

            displayer.start();


            //loading users every 30 seconds.
            Thread loader = new Thread(){
                public void run(){
                    //Thread Implmentation code here
                    while(true){
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        DB db = new DB();
                        try {
                            db.loadUsers();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };

            loader.start();


            int counting = 0;
            while (true) { // while loop just to test server statistics (constant publishing)
                //Publish to topic

                //topic test/2
                String topic = "db/test/2";
                String content = "msg2! #" + counting;
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(0);
                sampleClient.publish(topic, message);
                System.out.println("publish: " + content);

                Thread.sleep(3000);
                //topic test/3
                topic = "db/test/3";
                content = String.valueOf(3);
                message = new MqttMessage(content.getBytes());
                message.setQos(0);
                sampleClient.publish(topic, message);
                System.out.println("publish: " + content);

                Thread.sleep(3000);
                //topic test/4
                topic = "db/test/4";
                content = String.valueOf(4 * counting);
                message = new MqttMessage(content.getBytes());
                message.setQos(0);
                sampleClient.publish(topic, message);
                System.out.println("publish: " + content);

                counting++;
                Thread.sleep(3000 * subscriptions.size());
            }

        } catch (MqttException e) {
            System.out.println("reason: " + e.getReasonCode());
            System.out.println("msg: " + e.getMessage());
            System.out.println("loc: " + e.getLocalizedMessage());
            System.out.println("cause: " + e.getCause());
            System.out.println("excep: " + e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Start of Login static methods

    static void publish(String topicName,String messageToBePublished){
        if(!sampleClient.isConnected()){
            mqttConnect();
        }
        String topic = topicName;
        MqttMessage message = new MqttMessage(messageToBePublished.getBytes());
        try {
            sampleClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("publish: " + messageToBePublished);
    }


    static void mqttConnect() {

        String broker = Configurations.mqttHost + ":" + Configurations.mqttPort;
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            //Creating the client
            sampleClient = new MqttClient(broker, "ServerDatabase_"+rand.nextInt(), persistence);

            //Creating the options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(180);
            sampleClient.connect(connOpts);
            System.out.println("broker: " + broker + " Connected");


            //Subscribe to topic
            //   String topicReply = "web/request/create_new_user";
            //   sampleClient.subscribe(topicReply, new MqttPostPropertyMessageListener());
            //   System.out.println("subscribed to: " + topicReply);

            // while (true) { // while loop just to test server statistics (constant publishing)
            //It makes the indoor light blink every 4 seconds.
            //  String topic = "smart_house/gui/indoor_light";
            //  String content = "false";
            //  MqttMessage message = new MqttMessage(content.getBytes());
            //   message.setQos(0);
            //   sampleClient.publish(topic, message);
            //   System.out.println("publish: " + content);
            //   Thread.sleep(2000);


            //  Gson gson = new Gson();

            //  String [] userArray =new String[]{"Lavdimm","123456","Lavdim","lavdimImeri@hotmail.com"};
            //    User user = new User(userArray[0],userArray[1],userArray[2],userArray[3]);

            //  String topic = "web/statistics/user";



            //  String topic2 = "smart_house/cmd/indoor_light";
            //   String content2 = "false";
            //   MqttMessage message2 = new MqttMessage(content2.getBytes());
            //   message2.setQos(0);
            //   sampleClient.publish(topic2, message2);
            //   System.out.println("publish: " + content2);
            // Thread.sleep(2000);
            // }

            //Disconnecting
            //   sampleClient.disconnect();
            //   System.out.println("Disconnected");
            //     System.exit(0);
        } catch (MqttException e) {
            System.out.println("reason: " + e.getReasonCode());
            System.out.println("msg: " + e.getMessage());
            System.out.println("loc: " + e.getLocalizedMessage());
            System.out.println("cause: " + e.getCause());
            System.out.println("excep: " + e);
            e.printStackTrace();
        }

    }


    // Same here, I commented the lines, you were connecting again to the broker and you already are connected at line 194
    static void publishArrayOfStringsAsJSON(String topicName,String[] userArray) {

//            String broker = Configurations.mqttHost + ":" + Configurations.mqttPort;
//            MemoryPersistence persistence = new MemoryPersistence();

        if(!sampleClient.isConnected()){
            mqttConnect();
        }

        try {
//                //Creating the client
//                MqttClient sampleClient = new MqttClient(broker, "ServerDatabase", persistence);
//
//                //Creating the options
//                MqttConnectOptions connOpts = new MqttConnectOptions();
//                connOpts.setCleanSession(true);
//                connOpts.setKeepAliveInterval(180);
//                sampleClient.connect(connOpts);
//                System.out.println("broker: " + broker + " Connected");

            //Subscribe to topic
            //   String topicReply = "web/request/create_new_user";
            //   sampleClient.subscribe(topicReply, new MqttPostPropertyMessageListener());
            //   System.out.println("subscribed to: " + topicReply);

            // while (true) { // while loop just to test server statistics (constant publishing)
            //It makes the indoor light blink every 4 seconds.
            //  String topic = "smart_house/gui/indoor_light";
            //  String content = "false";
            //  MqttMessage message = new MqttMessage(content.getBytes());
            //   message.setQos(0);
            //   sampleClient.publish(topic, message);
            //   System.out.println("publish: " + content);
            //   Thread.sleep(2000);


            Gson gson = new Gson();

            //  String [] userArray =new String[]{"Lavdimm","123456","Lavdim","lavdimImeri@hotmail.com"};
            //    User user = new User(userArray[0],userArray[1],userArray[2],userArray[3]);

            //  String topic = "web/statistics/user";
            String topic = topicName;
            //  MqttMessage message = new MqttMessage(responseToValidationOfTheUser.getBytes());
            //  MqttMessage message = new MqttMessage(messageToBePublished.getBytes());
            sampleClient.publish(topic, new MqttMessage(gson.toJson(userArray).getBytes()));
            System.out.println("publish: " + responseToValidationOfTheUser);


            //  String topic2 = "smart_house/cmd/indoor_light";
            //   String content2 = "false";
            //   MqttMessage message2 = new MqttMessage(content2.getBytes());
            //   message2.setQos(0);
            //   sampleClient.publish(topic2, message2);
            //   System.out.println("publish: " + content2);
            // Thread.sleep(2000);
            // }

            //Disconnecting
            //      sampleClient.disconnect();
            //      System.out.println("Disconnected");
            //   System.exit(0);
        } catch (MqttException e) {
            System.out.println("reason: " + e.getReasonCode());
            System.out.println("msg: " + e.getMessage());
            System.out.println("loc: " + e.getLocalizedMessage());
            System.out.println("cause: " + e.getCause());
            System.out.println("excep: " + e);
            e.printStackTrace();
        }


    } //end of login static methods
}