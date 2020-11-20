package se.hkr.mqtt;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

//Helper class for message handling
class MqttPostPropertyMessageListener implements IMqttMessageListener {
    @Override
    public void messageArrived(String var1, MqttMessage var2) throws Exception {


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
                User user1 = new User(accauntname, password, email, name);
                DataHolder.users.add(user1);
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






    /*    FireStoreQueries fireStoreQueries = new FireStoreQueries();
        String fireStoreFriednlyPath = var1.replace("/", "sslashs");
        String theNameOfTheArchiveCollection = "databasesslashsrchivesslashs" + fireStoreFriednlyPath;
        String MQTTFriendlyPath = fireStoreFriednlyPath.replace("sslashs", "/");
        System.out.println("reply topic  : " + MQTTFriendlyPath);
        System.out.println("reply payload: " + var2.toString());

        //Getting the timestamp
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateOnly.format(cal.getTime());
        System.out.println(date);
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
        fireStoreQueries.createDocument(theNameOfTheArchiveCollection,var2WithDashes);
        fireStoreQueries.createElement(theNameOfTheArchiveCollection,var2WithDashes,
                "timestamp", dateAndTime);


        //Last known value
        String collectionLKV = "databasesslashs";
        String documentLKV = "lastKnownValuessslashs";
        String keyLKV = "defaultKey";
        String valueLKV = "defailtValue2";
        keyLKV = fireStoreFriednlyPath;
        valueLKV = var2.toString();
        Thread.sleep(3000);
        fireStoreQueries.createDocument(collectionLKV, documentLKV);
        fireStoreQueries.createElement(collectionLKV, documentLKV, keyLKV, valueLKV);

        //Last known timestamp
        String collectionLKT = "databasesslashs";
        String documentLKT = "lastKnownTimeStampsslashs";
        String keyLKT = "defaultKey";
        String valueLKT = "defailtValue2";
        keyLKT = fireStoreFriednlyPath;
        valueLKT = dateAndTime;
        Thread.sleep(3000);
        fireStoreQueries.createDocument(collectionLKT, documentLKT);
        fireStoreQueries.createElement(collectionLKT, documentLKT, keyLKT, valueLKT);
        */
}
        }


public class MQTT_demo {
    static String responseToValidationOfTheUser ="";
    static ArrayList<String> subscriptions = new ArrayList<String>();

    static MqttClient sampleClient;
    static Random rand;
    public static void main(String[] args) {
        rand = new Random();
        subscriptions.add("web/request/user");
        subscriptions.add("web/request/register_user");



      //  String username ="ImeriLavdimm";
      //  String password ="1234567";
      //  String name="Lavdim";
      //  String email="lavdimimeri@gmail.com";
      //  User user = new User(username,password,email,name);
      //  DataHolder.users.add(user);

        //creating a test user to always have one
        //Access the methods for making register and login on DataHolder
        //   User user = new User("admin", "12345", "test@test.test", "John Doe");
      //  DataHolder.users.add(user);
     //   User user2 = DataHolder.findUserByUsernam("admin");
     //   System.out.println("Returning the information of one user, (the admin):");
     //   System.out.println("Username: " + user2.userName);
     //   System.out.println("Password: " + user2.passWord);
     //   System.out.println("Email: " + user2.email);
     //   System.out.println("Name: " + user2.name);




        //Setting up the connection
        //No the deatils are set through the configurations class
        //String port = "1883";
        String broker = Configurations.mqttHost + ":" + Configurations.mqttPort;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            //Creating the client - ADDED rand.nextInt just to make sure ID is unique
            sampleClient = new MqttClient(broker, "ServerDatabase_"+rand.nextInt(), persistence);

            //Creating the options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(180);
            sampleClient.connect(connOpts);
            System.out.println("broker: " + broker + " Connected");

            //Subscribe to topic

            for (String topicReplay:subscriptions){
                sampleClient.subscribe(topicReplay,new MqttPostPropertyMessageListener());
                System.out.println("subscribed to: " + topicReplay);
                //Thread.sleep(2000); //you do not need this
            }
         //  String topicReply1 = "web/request/user";
        //  String topicReply = "web/request/create_new_user";
          //  sampleClient.subscribe(topicReply, new MqttPostPropertyMessageListener());
          //  System.out.println("subscribed to: " + topicReply);
          //  sampleClient.subscribe(topicReply1,new MqttPostPropertyMessageListener());
          //  System.out.println("subscribed to: " + topicReply1);
           // while (true) { // while loop just to test server statistics (constant publishing)
                //It makes the indoor light blink every 4 seconds.
              //  String topic = "smart_house/gui/indoor_light";
              //  String content = "false";
              //  MqttMessage message = new MqttMessage(content.getBytes());
             //   message.setQos(0);
             //   sampleClient.publish(topic, message);
             //   System.out.println("publish: " + content);
             //   Thread.sleep(2000);


       //      Gson gson = new Gson();

       //     String [] userArray =new String[]{"Lavdimm","123456","Lavdim","lavdimImeri@hotmail.com"};
            //    User user = new User(userArray[0],userArray[1],userArray[2],userArray[3]);
       //     String topic = "web/request/create_new_user";
       //     sampleClient.publish(topic,new MqttMessage(gson.toJson(userArray).getBytes()));
       //     System.out.println("publish: " + userArray);
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
         //   System.exit(0);
        } catch (MqttException e) {
            System.out.println("reason: " + e.getReasonCode());
            System.out.println("msg: " + e.getMessage());
            System.out.println("loc: " + e.getLocalizedMessage());
            System.out.println("cause: " + e.getCause());
            System.out.println("excep: " + e);
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