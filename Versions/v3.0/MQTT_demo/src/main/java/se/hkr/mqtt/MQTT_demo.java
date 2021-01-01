package se.hkr.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

//Helper class for message handling
class MqttPostPropertyMessageListener implements IMqttMessageListener {

    @Override
    public void messageArrived(String var1, MqttMessage var2) throws Exception {
        DataHolder.publishing = false; //stops any publishing to write data to the data structures.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FireStoreQueries fireStoreQueries = new FireStoreQueries();
        int identifier = 0;
        String flag = DataHolder.flags.get(var1);
        String fireStoreFriednlyPath = var1.replace("/", "sslashs");
        String theNameOfTheArchiveCollection = "databasesslashs" + identifier + "sslashsrchivesslashs" + fireStoreFriednlyPath;
        String MQTTFriendlyPath = fireStoreFriednlyPath.replace("sslashs", "/");
        System.out.println("");
        System.out.println("=======================================================================");
        System.out.println("MQTT Message Arrived from the topic "  + MQTTFriendlyPath);
        System.out.println("=======================================================================");
        //System.out.println("reply topic  : " + MQTTFriendlyPath);
        System.out.println("The subscription of the topic is on mode: " + flag);
        //System.out.println("Payload of the message: " + var2.toString());

        //Getting the timestamp. In order to server statistics in a proper way the system needs to
        //to be aware of the month the week and the day.
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateOnly.format(cal.getTime());
        SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm:ss");
        String time = timeOnly.format(cal.getTime());
        String dateAndTime = date + "-" +  time;
        System.out.println("Time of the arrival: " + dateAndTime);
        String dateAndTimeFirestoreFriendly = dateAndTime.replace("/", "");
        DataHolder.dayStamp = date.replace("/", "");
        String[] tmp = time.split(":");
        String[] tmp2 = date.split("/");
        DataHolder.datenum = Integer.parseInt(tmp2[1]);
        DataHolder.hour = tmp[0];

        //Archive
        //for the archive it has to replace the spaces with dashes because it makes it more readable
        String var2WithTimeStamp = dateAndTimeFirestoreFriendly + var2.toString();
        String var2WithDashes = var2WithTimeStamp.replace(" ", "-");
        //System.out.println("Collection: " + theNameOfTheArchiveCollection);
        //System.out.println("document: " + var2WithDashes);

        if(flag.equalsIgnoreCase("0")){
            System.out.println("Payload of the message: " + var2.toString());
        }

        //if the flag is 1, it stores data and counts stats
        if(flag.equalsIgnoreCase("1")){
            //run the rest of the code
            System.out.println("Payload of the message: " + var2.toString());

            //daily logs
            DataHolder.dailyLogsCount = DataHolder.houses.get(0).mainLogs.size();
            DataHolder.houses.get(identifier).mainLogs.add( DataHolder.dailyLogsCount + "sseparatorr" + dateAndTime
                    + " The topic:" + var1 + " had the payload:" + var2.toString());

            //prepared main logs
            DataHolder.houses.get(identifier).premainLogs.add(dateAndTime + " The topic:" + var1 +
                    " had the payload:" + var2.toString());
            //DataHolder.dailyLogsCount++;

            //weekly logs
            DataHolder.weeklyLogsCount = DataHolder.houses.get(0).mainLogsWeek.size();
            DataHolder.houses.get(identifier).mainLogsWeek.add( DataHolder.weeklyLogsCount + "sseparatorr"
                    + dateAndTime
                    + " The topic:" + var1 + " had the payload:" + var2.toString());

            //prepared main logs
            DataHolder.houses.get(identifier).premainLogsWeek.add(dateAndTime + " The topic:" + var1 +
                    " had the payload:" + var2.toString());
            //DataHolder.weeklyLogsCount++;

            //monthly logs
            DataHolder.monthlyLogsCount = DataHolder.houses.get(0).mainLogsMonth.size();
            DataHolder.houses.get(identifier).mainLogsMonth.add( DataHolder.monthlyLogsCount + "sseparatorr"
                    + dateAndTime
                    + " The topic:" + var1 + " had the payload:" + var2.toString());

            //prepared main logs
            DataHolder.houses.get(identifier).premainLogsMonth.add(dateAndTime + " The topic:" + var1 +
                    " had the payload:" + var2.toString());
            //DataHolder.monthlyLogsCount++;

            //updating the logs of today on the prepared daily logs
            //String friendlyDate = date.replace("/", "");
            DataHolder.houses.get(identifier).dailyLogsArrayList.get(DataHolder.dayStamp).add(dateAndTime + " The topic:" + var1 +
                    " had the payload:" + var2.toString());

            //Subscription specific logs
            if(DataHolder.houses.get(identifier).logs.containsKey(fireStoreFriednlyPath)){
                //adding to the logs
                int lognum = DataHolder.houses.get(identifier).logs.get(fireStoreFriednlyPath).size();
                DataHolder.houses.get(identifier).logs.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr"
                                + dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepLogs.get(var1)
                        .add(dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());
            } else {
                //creating on logs
                DataHolder.houses.get(identifier).logs.put(fireStoreFriednlyPath, new ArrayList<String>());

                //creating on preLogs
                DataHolder.houses.get(identifier).prepLogs.put(var1, new ArrayList<String>());

                //adding to the logs
                int lognum = 0;
                DataHolder.houses.get(identifier).logs.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr"
                                + dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepLogs.get(var1)
                        .add(dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());
            }


            //Weekly Subscription Specific Logs
            if(DataHolder.houses.get(identifier).wlogs.containsKey(fireStoreFriednlyPath)){
                //adding to the logs
                int lognum = DataHolder.houses.get(identifier).wlogs.get(fireStoreFriednlyPath).size();
                DataHolder.houses.get(identifier).wlogs.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr"
                                + dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepwlogs.get(var1)
                        .add(dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());
            } else {
                //creating on logs
                DataHolder.houses.get(identifier).wlogs.put(fireStoreFriednlyPath, new ArrayList<String>());

                //creating on preLogs
                DataHolder.houses.get(identifier).prepwlogs.put(var1, new ArrayList<String>());

                //adding to the logs
                int lognum = 0;
                DataHolder.houses.get(identifier).wlogs.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr"
                                + dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepwlogs.get(var1)
                        .add(dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());
            }

            //Monthly Subscription Specific Logs
            if(DataHolder.houses.get(identifier).mlogs.containsKey(fireStoreFriednlyPath)){
                //adding to the logs
                int lognum = DataHolder.houses.get(identifier).mlogs.get(fireStoreFriednlyPath).size();
                DataHolder.houses.get(identifier).mlogs.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr"
                                + dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepmlogs.get(var1)
                        .add(dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());
            } else {
                //creating on logs
                DataHolder.houses.get(identifier).mlogs.put(fireStoreFriednlyPath, new ArrayList<String>());

                //creating on preLogs
                DataHolder.houses.get(identifier).prepmlogs.put(var1, new ArrayList<String>());

                //adding to the logs
                int lognum = 0;
                DataHolder.houses.get(identifier).mlogs.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr"
                                + dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepmlogs.get(var1)
                        .add(dateAndTime + " The topic:" + var1 +
                                " had the payload:" + var2.toString());
            }


            //            //this is for TEST. is the only way to test that
//            DataHolder.displayAnArrayList(DataHolder.houses.get(identifier)
//                    .prepLogs.get(var1), "[PREPARED]" + var1, 0);




            //===================== SECTOR 2 STARTS HERE =============================================

            String variable2 = "0"; //this little code indicator that Alex asked for.
            variable2 = DataHolder.sector2algorithm(var2.toString()); //probably it will need a calibration.


            //daily logs
            DataHolder.dailyLogsCount2 = DataHolder.houses.get(0).mainLogs2.size();
            DataHolder.houses.get(identifier).mainLogs2.add(DataHolder.dailyLogsCount2 + "sseparatorr" + dateAndTime
                    + "ssectorr2" + variable2);

            //prepared main logs
            DataHolder.houses.get(identifier).premainLogs2.put(dateAndTime, variable2);

            //weekly logs
            DataHolder.weeklyLogsCount2 = DataHolder.houses.get(0).mainLogsWeek2.size();
            DataHolder.houses.get(identifier).mainLogsWeek2.add( DataHolder.weeklyLogsCount2 + "sseparatorr" + dateAndTime
                    + "ssectorr2" + variable2);

            //prepared main logs
            DataHolder.houses.get(identifier).premainLogsWeek2.put(dateAndTime, variable2);

            //monthly logs
            DataHolder.monthlyLogsCount2 = DataHolder.houses.get(0).mainLogsMonth2.size();
            DataHolder.houses.get(identifier).mainLogsMonth2.add( DataHolder.monthlyLogsCount2 + "sseparatorr" + dateAndTime
                    + "ssectorr2" + variable2);

            //prepared main logs2
            DataHolder.houses.get(identifier).premainLogsMonth2.put(dateAndTime, variable2);

            //updating the logs2 of today on the prepared daily logs
            //String friendlyDate = date.replace("/", "");
            DataHolder.houses.get(identifier).dailyLogsArrayList2.get(DataHolder.dayStamp).add(dateAndTime
                    + "ssectorr2" + variable2);

            //Subscription specific logs2
            if(DataHolder.houses.get(identifier).logs2.containsKey(fireStoreFriednlyPath)){
                //adding to the logs2
                int lognum = DataHolder.houses.get(identifier).logs2.get(fireStoreFriednlyPath).size();
                DataHolder.houses.get(identifier).logs2.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr" + dateAndTime
                                + "ssectorr2" + variable2);

                //adding to the prepLogs2
                DataHolder.houses.get(identifier).prepLogs2.get(var1)
                        .put(dateAndTime, variable2);
            } else {
                //creating on logs2
                DataHolder.houses.get(identifier).logs2.put(fireStoreFriednlyPath, new ArrayList<String>());

                //creating on preLogs
                DataHolder.houses.get(identifier).prepLogs2.put(var1, new HashMap());

                //adding to the logs
                int lognum = 0;
                DataHolder.houses.get(identifier).logs2.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr" + dateAndTime
                                + "ssectorr2" + variable2);

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepLogs2.get(var1)
                        .put(dateAndTime, variable2);
            }


            //Weekly Subscription Specific Logs (Sector 2)
            if(DataHolder.houses.get(identifier).wlogs2.containsKey(fireStoreFriednlyPath)){
                //adding to the logs
                int lognum = DataHolder.houses.get(identifier).wlogs2.get(fireStoreFriednlyPath).size();
                DataHolder.houses.get(identifier).wlogs2.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr" + dateAndTime
                                + "ssectorr2" + variable2);

                //adding to the prepLogs2
                DataHolder.houses.get(identifier).prepwlogs2.get(var1)
                        .put(dateAndTime, variable2);
            } else {
                //creating on logs2
                DataHolder.houses.get(identifier).wlogs2.put(fireStoreFriednlyPath, new ArrayList<String>());

                //creating on preLogs2
                DataHolder.houses.get(identifier).prepwlogs2.put(var1, new HashMap());

                //adding to the logs2
                int lognum = 0;
                DataHolder.houses.get(identifier).wlogs2.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr" + dateAndTime
                                + "ssectorr2" + variable2);

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepwlogs2.get(var1)
                        .put(dateAndTime, variable2);
            }

            //Monthly Subscription Specific Logs (sector 2)
            if(DataHolder.houses.get(identifier).mlogs2.containsKey(fireStoreFriednlyPath)){
                //adding to the logs 2
                int lognum = DataHolder.houses.get(identifier).mlogs2.get(fireStoreFriednlyPath).size();
                DataHolder.houses.get(identifier).mlogs2.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr" + dateAndTime
                                + "ssectorr2" + variable2);

                //adding to the prepLogs2
                DataHolder.houses.get(identifier).prepmlogs2.get(var1)
                        .put(dateAndTime, variable2);
            } else {
                //creating on logs 2
                DataHolder.houses.get(identifier).mlogs2.put(fireStoreFriednlyPath, new ArrayList<String>());

                //creating on preLogs 2
                DataHolder.houses.get(identifier).prepmlogs2.put(var1, new HashMap());

                //adding to the logs 2
                int lognum = 0;
                DataHolder.houses.get(identifier).mlogs2.get(fireStoreFriednlyPath)
                        .add(lognum + "sseparatorr" + dateAndTime
                                + "ssectorr2" + variable2);

                //adding to the prepLogs
                DataHolder.houses.get(identifier).prepmlogs2.get(var1)
                        .put(dateAndTime, variable2);
            }
            //===================== SECTOR 2 ENDS HERE =============================================



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
            DataHolder.houses.get(identifier).prelastKnownValues.put(var1,valueLKV);


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
            DataHolder.houses.get(identifier).prelastKnownTimeStamps.put(var1,valueLKT);


            //Statistics

            //================ COUNT ===================================
            //Daily count. Micropreparation is concluded
            if(DataHolder.houses.get(identifier).dailyCount.containsKey(fireStoreFriednlyPath)){
                int count = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, count+1);
                DataHolder.houses.get(identifier).predailyCount.put(var1, count+1);
            } else {
                DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, 1);
                DataHolder.houses.get(identifier).predailyCount.put(var1, 1);
            }


            //Daily Count in a Day by Day basis
            if(DataHolder.houses.get(identifier).dailyCountdbd.containsKey(var1)){
                Integer[] arr = DataHolder.houses.get(identifier).dailyCountdbd.get(var1);
                arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCountdbd.put(var1, arr);
            } else {
                Integer[] arr = new Integer[32];
                arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCountdbd.put(var1, arr);
            }

            //Daily Count in a Hour by Hour basis
            if(DataHolder.houses.get(identifier).dailyCounthbh.containsKey(var1)){
                Integer[] arr = DataHolder.houses.get(identifier).dailyCounthbh.get(var1);
                arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCounthbh.put(var1, arr);
            } else {
                Integer[] arr = new Integer[25];
                arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCounthbh.put(var1, arr);
            }

            //Daily Count in a Monthly Hour by Hour basis
            if(DataHolder.houses.get(identifier).dailyCountmhbh.containsKey(var1)){
                Integer[] arr = DataHolder.houses.get(identifier).dailyCountmhbh.get(var1);
                int adder = DataHolder.datenum * 100;
                arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCountmhbh.put(var1, arr);
            } else {
                Integer[] arr = new Integer[9999];
                int adder = DataHolder.datenum * 100;
                arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).dailyCountmhbh.put(var1, arr);
            }

            //Weekly count. Micropreparation is concluded
            if(DataHolder.houses.get(identifier).weeklyCount.containsKey(fireStoreFriednlyPath)){
                int count = DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, count+1);
                DataHolder.houses.get(identifier).preweeklyCount.put(var1, count+1);
            } else {
                DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, 1);
                DataHolder.houses.get(identifier).preweeklyCount.put(var1, 1);
            }

            //Monthly count. Micropreparation is concluded
            if(DataHolder.houses.get(identifier).monthlyCount.containsKey(fireStoreFriednlyPath)){
                int count = DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, count+1);
                DataHolder.houses.get(identifier).premonthlyCount.put(var1, count+1);
            } else {
                DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, 1);
                DataHolder.houses.get(identifier).premonthlyCount.put(var1, 1);
            }


            if(DataHolder.isNumeric(var2.toString())){

                //================ SUM ===================================
                //Daily Sum. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).dailySum.containsKey(fireStoreFriednlyPath)){
                    Double current = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    Double newValue = Double.parseDouble(var2.toString());
                    DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, current + newValue);
                    DataHolder.houses.get(identifier).predailySum.put(var1, current + newValue);
                } else {
                    DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, 0.0);
                    DataHolder.houses.get(identifier).predailySum.put(fireStoreFriednlyPath, 0.0);
                }


                //Daily Sum in a day by day basis
                if(DataHolder.houses.get(identifier).dailySumdbd.containsKey(var1)){
                    Double[] arr = DataHolder.houses.get(identifier).dailySumdbd.get(var1);
                    arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailySumdbd.put(var1, arr);
                } else {
                    Double[] arr = new Double[32];
                    arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailySumdbd.put(var1, arr);
                }

                //Daily Sum in a Hour by Hour basis
                if(DataHolder.houses.get(identifier).dailySumhbh.containsKey(var1)){
                    Double[] arr = DataHolder.houses.get(identifier).dailySumhbh.get(var1);
                    arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailySumhbh.put(var1, arr);
                } else {
                    Double[] arr = new Double[25];
                    arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailySumhbh.put(var1, arr);
                }

                //Monthly Sum in a Hour by Hour basis
                if(DataHolder.houses.get(identifier).dailySummhbh.containsKey(var1)){
                    Double[] arr = DataHolder.houses.get(identifier).dailySummhbh.get(var1);
                    int adder = DataHolder.datenum * 100;
                    arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailySummhbh.put(var1, arr);
                } else {
                    Double[] arr = new Double[9999];
                    int adder = DataHolder.datenum * 100;
                    arr[Integer.parseInt(DataHolder.hour)+adder] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailySummhbh.put(var1, arr);
                }


                //Weekly Sum. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).weeklySum.containsKey(fireStoreFriednlyPath)){
                    Double current = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath);
                    Double newValue = Double.parseDouble(var2.toString());
                    DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, current+newValue);
                    DataHolder.houses.get(identifier).preweeklySum.put(var1, current+newValue);
                } else {
                    DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, 0.0);
                    DataHolder.houses.get(identifier).preweeklySum.put(var1, 0.0);
                }


                //Monthly sum. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).monthlySum.containsKey(fireStoreFriednlyPath)){
                    Double current = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath);
                    Double newValue = Double.parseDouble(var2.toString());
                    DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, current+newValue);
                    DataHolder.houses.get(identifier).premonthlySum.put(var1, current+newValue);
                } else {
                    DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, 0.0);
                    DataHolder.houses.get(identifier).premonthlySum.put(var1, 0.0);
                }


                //================ AVG (AVERAGE) ==============================

                //Daily average. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).dailyAverage.containsKey(fireStoreFriednlyPath)){
                    Double average = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath)/
                            DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, average);
                    DataHolder.houses.get(identifier).predailyAverage.put(var1, average);
                } else {
                    DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, 0.0);
                    DataHolder.houses.get(identifier).predailyAverage.put(var1, 0.0);
                }


                //Daily Average in a day by day basis
                if(DataHolder.houses.get(identifier).dailyAveragedbd.containsKey(var1)){
                    Double[] arr = DataHolder.houses.get(identifier).dailyAveragedbd.get(var1);
                    arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAveragedbd.put(var1, arr);
                } else {
                    Double[] arr = new Double[32];
                    arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAveragedbd.put(var1, arr);
                }

                //Daily Average in a Hour by Hour basis
                if(DataHolder.houses.get(identifier).dailyAveragehbh.containsKey(var1)){
                    Double[] arr = DataHolder.houses.get(identifier).dailyAveragehbh.get(var1);
                    arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAveragehbh.put(var1, arr);
                } else {
                    Double[] arr = new Double[25];
                    arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAveragehbh.put(var1, arr);
                }

                //Monthly Average in a Hour by Hour basis
                if(DataHolder.houses.get(identifier).dailyAveragemhbh.containsKey(var1)){
                    Double[] arr = DataHolder.houses.get(identifier).dailyAveragemhbh.get(var1);
                    int adder = DataHolder.datenum * 100;
                    arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAveragemhbh.put(var1, arr);
                } else {
                    Double[] arr = new Double[9999];
                    int adder = DataHolder.datenum * 100;
                    arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyAveragemhbh.put(var1, arr);
                }

                //Weekly average. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).weeklyAverage.containsKey(fireStoreFriednlyPath)){
                    Double average = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath)/
                            DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, average);
                    DataHolder.houses.get(identifier).preweeklyAverage.put(var1, average);
                } else {
                    DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, 0.0);
                    DataHolder.houses.get(identifier).preweeklyAverage.put(var1, 0.0);
                }

                //Monthly average. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).monthlyAverage.containsKey(fireStoreFriednlyPath)){
                    Double average = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath)/
                            DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, average);
                    DataHolder.houses.get(identifier).premonthlyAverage.put(var1, average);
                } else {
                    DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, 0.0);
                    DataHolder.houses.get(identifier).premonthlyAverage.put(var1, 0.0);
                }


            } //end of isnumeric() test

//            //For testing purposes. It tests the logs.
//            DataHolder.displayAnArrayList(DataHolder.houses.get(identifier).dailyLogsArrayList.get(friendlyDate),
//                    "[PREPARED] Daily Logs of today", identifier);


        } //end of if (flag.equalsIgnoreCase("1"))

        if(flag.equalsIgnoreCase("2")){
            System.out.println("Payload of the message: " + var2.toString());
            //run some continues stream statistics
            String keyLKVtmp = fireStoreFriednlyPath;
            String valueLKVtmp = var2.toString();
            if(!valueLKVtmp.equalsIgnoreCase(DataHolder.houses.get(identifier).lastKnownValues.get(keyLKVtmp))){

                //daily logs
                DataHolder.dailyLogsCount = DataHolder.houses.get(0).mainLogs.size();
                DataHolder.houses.get(identifier).mainLogs.add( DataHolder.dailyLogsCount + "sseparatorr" + dateAndTime
                        + " The topic:" + var1 + " had the payload:" + var2.toString());

                //prepared main logs
                DataHolder.houses.get(identifier).premainLogs.add(dateAndTime + " The topic:" + var1 +
                        " had the payload:" + var2.toString());
                //DataHolder.dailyLogsCount++;

                //weekly logs
                DataHolder.weeklyLogsCount = DataHolder.houses.get(0).mainLogsWeek.size();
                DataHolder.houses.get(identifier).mainLogsWeek.add( DataHolder.weeklyLogsCount + "sseparatorr"
                        + dateAndTime
                        + " The topic:" + var1 + " had the payload:" + var2.toString());

                //prepared main logs
                DataHolder.houses.get(identifier).premainLogsWeek.add(dateAndTime + " The topic:" + var1 +
                        " had the payload:" + var2.toString());
                //DataHolder.weeklyLogsCount++;

                //monthly logs
                DataHolder.monthlyLogsCount = DataHolder.houses.get(0).mainLogsMonth.size();
                DataHolder.houses.get(identifier).mainLogsMonth.add( DataHolder.monthlyLogsCount + "sseparatorr"
                        + dateAndTime
                        + " The topic:" + var1 + " had the payload:" + var2.toString());

                //prepared main logs
                DataHolder.houses.get(identifier).premainLogsMonth.add(dateAndTime + " The topic:" + var1 +
                        " had the payload:" + var2.toString());
                //DataHolder.monthlyLogsCount++;

                //updating the logs of today on the prepared daily logs
                //String friendlyDate = date.replace("/", "");
                DataHolder.houses.get(identifier).dailyLogsArrayList.get(DataHolder.dayStamp).add(dateAndTime + " The topic:" + var1 +
                        " had the payload:" + var2.toString());

                //Subscription specific logs
                if(DataHolder.houses.get(identifier).logs.containsKey(fireStoreFriednlyPath)){
                    //adding to the logs
                    int lognum = DataHolder.houses.get(identifier).logs.get(fireStoreFriednlyPath).size();
                    DataHolder.houses.get(identifier).logs.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr"
                                    + dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepLogs.get(var1)
                            .add(dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());
                } else {
                    //creating on logs
                    DataHolder.houses.get(identifier).logs.put(fireStoreFriednlyPath, new ArrayList<String>());

                    //creating on preLogs
                    DataHolder.houses.get(identifier).prepLogs.put(var1, new ArrayList<String>());

                    //adding to the logs
                    int lognum = 0;
                    DataHolder.houses.get(identifier).logs.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr"
                                    + dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepLogs.get(var1)
                            .add(dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());
                }


                //Weekly Subscription Specific Logs
                if(DataHolder.houses.get(identifier).wlogs.containsKey(fireStoreFriednlyPath)){
                    //adding to the logs
                    int lognum = DataHolder.houses.get(identifier).wlogs.get(fireStoreFriednlyPath).size();
                    DataHolder.houses.get(identifier).wlogs.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr"
                                    + dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepwlogs.get(var1)
                            .add(dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());
                } else {
                    //creating on logs
                    DataHolder.houses.get(identifier).wlogs.put(fireStoreFriednlyPath, new ArrayList<String>());

                    //creating on preLogs
                    DataHolder.houses.get(identifier).prepwlogs.put(var1, new ArrayList<String>());

                    //adding to the logs
                    int lognum = 0;
                    DataHolder.houses.get(identifier).wlogs.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr"
                                    + dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepwlogs.get(var1)
                            .add(dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());
                }

                //Monthly Subscription Specific Logs
                if(DataHolder.houses.get(identifier).mlogs.containsKey(fireStoreFriednlyPath)){
                    //adding to the logs
                    int lognum = DataHolder.houses.get(identifier).mlogs.get(fireStoreFriednlyPath).size();
                    DataHolder.houses.get(identifier).mlogs.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr"
                                    + dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepmlogs.get(var1)
                            .add(dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());
                } else {
                    //creating on logs
                    DataHolder.houses.get(identifier).mlogs.put(fireStoreFriednlyPath, new ArrayList<String>());

                    //creating on preLogs
                    DataHolder.houses.get(identifier).prepmlogs.put(var1, new ArrayList<String>());

                    //adding to the logs
                    int lognum = 0;
                    DataHolder.houses.get(identifier).mlogs.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr"
                                    + dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepmlogs.get(var1)
                            .add(dateAndTime + " The topic:" + var1 +
                                    " had the payload:" + var2.toString());
                }


                //            //this is for TEST. is the only way to test that
//            DataHolder.displayAnArrayList(DataHolder.houses.get(identifier)
//                    .prepLogs.get(var1), "[PREPARED]" + var1, 0);




                //===================== SECTOR 2 STARTS HERE =============================================

                String variable2 = "0"; //this little code indicator that Alex asked for.
                variable2 = DataHolder.sector2algorithm(var2.toString()); //probably it will need a calibration.


                //daily logs
                DataHolder.dailyLogsCount2 = DataHolder.houses.get(0).mainLogs2.size();
                DataHolder.houses.get(identifier).mainLogs2.add(DataHolder.dailyLogsCount2 + "sseparatorr" + dateAndTime
                        + "ssectorr2" + variable2);

                //prepared main logs
                DataHolder.houses.get(identifier).premainLogs2.put(dateAndTime, variable2);

                //weekly logs
                DataHolder.weeklyLogsCount2 = DataHolder.houses.get(0).mainLogsWeek2.size();
                DataHolder.houses.get(identifier).mainLogsWeek2.add( DataHolder.weeklyLogsCount2 + "sseparatorr" + dateAndTime
                        + "ssectorr2" + variable2);

                //prepared main logs
                DataHolder.houses.get(identifier).premainLogsWeek2.put(dateAndTime, variable2);

                //monthly logs
                DataHolder.monthlyLogsCount2 = DataHolder.houses.get(0).mainLogsMonth2.size();
                DataHolder.houses.get(identifier).mainLogsMonth2.add( DataHolder.monthlyLogsCount2 + "sseparatorr" + dateAndTime
                        + "ssectorr2" + variable2);

                //prepared main logs2
                DataHolder.houses.get(identifier).premainLogsMonth2.put(dateAndTime, variable2);

                //updating the logs2 of today on the prepared daily logs
                //String friendlyDate = date.replace("/", "");
                DataHolder.houses.get(identifier).dailyLogsArrayList2.get(DataHolder.dayStamp).add(dateAndTime
                        + "ssectorr2" + variable2);

                //Subscription specific logs2
                if(DataHolder.houses.get(identifier).logs2.containsKey(fireStoreFriednlyPath)){
                    //adding to the logs2
                    int lognum = DataHolder.houses.get(identifier).logs2.get(fireStoreFriednlyPath).size();
                    DataHolder.houses.get(identifier).logs2.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr" + dateAndTime
                                    + "ssectorr2" + variable2);

                    //adding to the prepLogs2
                    DataHolder.houses.get(identifier).prepLogs2.get(var1)
                            .put(dateAndTime, variable2);
                } else {
                    //creating on logs2
                    DataHolder.houses.get(identifier).logs2.put(fireStoreFriednlyPath, new ArrayList<String>());

                    //creating on preLogs
                    DataHolder.houses.get(identifier).prepLogs2.put(var1, new HashMap());

                    //adding to the logs
                    int lognum = 0;
                    DataHolder.houses.get(identifier).logs2.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr" + dateAndTime
                                    + "ssectorr2" + variable2);

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepLogs2.get(var1)
                            .put(dateAndTime, variable2);
                }


                //Weekly Subscription Specific Logs (Sector 2)
                if(DataHolder.houses.get(identifier).wlogs2.containsKey(fireStoreFriednlyPath)){
                    //adding to the logs
                    int lognum = DataHolder.houses.get(identifier).wlogs2.get(fireStoreFriednlyPath).size();
                    DataHolder.houses.get(identifier).wlogs2.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr" + dateAndTime
                                    + "ssectorr2" + variable2);

                    //adding to the prepLogs2
                    DataHolder.houses.get(identifier).prepwlogs2.get(var1)
                            .put(dateAndTime, variable2);
                } else {
                    //creating on logs2
                    DataHolder.houses.get(identifier).wlogs2.put(fireStoreFriednlyPath, new ArrayList<String>());

                    //creating on preLogs2
                    DataHolder.houses.get(identifier).prepwlogs2.put(var1, new HashMap());

                    //adding to the logs2
                    int lognum = 0;
                    DataHolder.houses.get(identifier).wlogs2.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr" + dateAndTime
                                    + "ssectorr2" + variable2);

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepwlogs2.get(var1)
                            .put(dateAndTime, variable2);
                }

                //Monthly Subscription Specific Logs (sector 2)
                if(DataHolder.houses.get(identifier).mlogs2.containsKey(fireStoreFriednlyPath)){
                    //adding to the logs 2
                    int lognum = DataHolder.houses.get(identifier).mlogs2.get(fireStoreFriednlyPath).size();
                    DataHolder.houses.get(identifier).mlogs2.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr" + dateAndTime
                                    + "ssectorr2" + variable2);

                    //adding to the prepLogs2
                    DataHolder.houses.get(identifier).prepmlogs2.get(var1)
                            .put(dateAndTime, variable2);
                } else {
                    //creating on logs 2
                    DataHolder.houses.get(identifier).mlogs2.put(fireStoreFriednlyPath, new ArrayList<String>());

                    //creating on preLogs 2
                    DataHolder.houses.get(identifier).prepmlogs2.put(var1, new HashMap());

                    //adding to the logs 2
                    int lognum = 0;
                    DataHolder.houses.get(identifier).mlogs2.get(fireStoreFriednlyPath)
                            .add(lognum + "sseparatorr" + dateAndTime
                                    + "ssectorr2" + variable2);

                    //adding to the prepLogs
                    DataHolder.houses.get(identifier).prepmlogs2.get(var1)
                            .put(dateAndTime, variable2);
                }
                //===================== SECTOR 2 ENDS HERE =============================================



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
                DataHolder.houses.get(identifier).prelastKnownValues.put(var1,valueLKV);


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
                DataHolder.houses.get(identifier).prelastKnownTimeStamps.put(var1,valueLKT);


                //Statistics

                //================ COUNT ===================================
                //Daily count. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).dailyCount.containsKey(fireStoreFriednlyPath)){
                    int count = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, count+1);
                    DataHolder.houses.get(identifier).predailyCount.put(var1, count+1);
                } else {
                    DataHolder.houses.get(identifier).dailyCount.put(fireStoreFriednlyPath, 1);
                    DataHolder.houses.get(identifier).predailyCount.put(var1, 1);
                }


                //Daily Count in a Day by Day basis
                if(DataHolder.houses.get(identifier).dailyCountdbd.containsKey(var1)){
                    Integer[] arr = DataHolder.houses.get(identifier).dailyCountdbd.get(var1);
                    arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCountdbd.put(var1, arr);
                } else {
                    Integer[] arr = new Integer[32];
                    arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCountdbd.put(var1, arr);
                }

                //Daily Count in a Hour by Hour basis
                if(DataHolder.houses.get(identifier).dailyCounthbh.containsKey(var1)){
                    Integer[] arr = DataHolder.houses.get(identifier).dailyCounthbh.get(var1);
                    arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCounthbh.put(var1, arr);
                } else {
                    Integer[] arr = new Integer[25];
                    arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCounthbh.put(var1, arr);
                }

                //Daily Count in a Monthly Hour by Hour basis
                if(DataHolder.houses.get(identifier).dailyCountmhbh.containsKey(var1)){
                    Integer[] arr = DataHolder.houses.get(identifier).dailyCountmhbh.get(var1);
                    int adder = DataHolder.datenum * 100;
                    arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCountmhbh.put(var1, arr);
                } else {
                    Integer[] arr = new Integer[9999];
                    int adder = DataHolder.datenum * 100;
                    arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).dailyCountmhbh.put(var1, arr);
                }

                //Weekly count. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).weeklyCount.containsKey(fireStoreFriednlyPath)){
                    int count = DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, count+1);
                    DataHolder.houses.get(identifier).preweeklyCount.put(var1, count+1);
                } else {
                    DataHolder.houses.get(identifier).weeklyCount.put(fireStoreFriednlyPath, 1);
                    DataHolder.houses.get(identifier).preweeklyCount.put(var1, 1);
                }

                //Monthly count. Micropreparation is concluded
                if(DataHolder.houses.get(identifier).monthlyCount.containsKey(fireStoreFriednlyPath)){
                    int count = DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                    DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, count+1);
                    DataHolder.houses.get(identifier).premonthlyCount.put(var1, count+1);
                } else {
                    DataHolder.houses.get(identifier).monthlyCount.put(fireStoreFriednlyPath, 1);
                    DataHolder.houses.get(identifier).premonthlyCount.put(var1, 1);
                }


                if(DataHolder.isNumeric(var2.toString())){

                    //================ SUM ===================================
                    //Daily Sum. Micropreparation is concluded
                    if(DataHolder.houses.get(identifier).dailySum.containsKey(fireStoreFriednlyPath)){
                        Double current = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        Double newValue = Double.parseDouble(var2.toString());
                        DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, current + newValue);
                        DataHolder.houses.get(identifier).predailySum.put(var1, current + newValue);
                    } else {
                        DataHolder.houses.get(identifier).dailySum.put(fireStoreFriednlyPath, 0.0);
                        DataHolder.houses.get(identifier).predailySum.put(fireStoreFriednlyPath, 0.0);
                    }


                    //Daily Sum in a day by day basis
                    if(DataHolder.houses.get(identifier).dailySumdbd.containsKey(var1)){
                        Double[] arr = DataHolder.houses.get(identifier).dailySumdbd.get(var1);
                        arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailySumdbd.put(var1, arr);
                    } else {
                        Double[] arr = new Double[32];
                        arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailySumdbd.put(var1, arr);
                    }

                    //Daily Sum in a Hour by Hour basis
                    if(DataHolder.houses.get(identifier).dailySumhbh.containsKey(var1)){
                        Double[] arr = DataHolder.houses.get(identifier).dailySumhbh.get(var1);
                        arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailySumhbh.put(var1, arr);
                    } else {
                        Double[] arr = new Double[25];
                        arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailySumhbh.put(var1, arr);
                    }

                    //Monthly Sum in a Hour by Hour basis
                    if(DataHolder.houses.get(identifier).dailySummhbh.containsKey(var1)){
                        Double[] arr = DataHolder.houses.get(identifier).dailySummhbh.get(var1);
                        int adder = DataHolder.datenum * 100;
                        arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailySummhbh.put(var1, arr);
                    } else {
                        Double[] arr = new Double[9999];
                        int adder = DataHolder.datenum * 100;
                        arr[Integer.parseInt(DataHolder.hour)+adder] = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailySummhbh.put(var1, arr);
                    }


                    //Weekly Sum. Micropreparation is concluded
                    if(DataHolder.houses.get(identifier).weeklySum.containsKey(fireStoreFriednlyPath)){
                        Double current = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath);
                        Double newValue = Double.parseDouble(var2.toString());
                        DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, current+newValue);
                        DataHolder.houses.get(identifier).preweeklySum.put(var1, current+newValue);
                    } else {
                        DataHolder.houses.get(identifier).weeklySum.put(fireStoreFriednlyPath, 0.0);
                        DataHolder.houses.get(identifier).preweeklySum.put(var1, 0.0);
                    }


                    //Monthly sum. Micropreparation is concluded
                    if(DataHolder.houses.get(identifier).monthlySum.containsKey(fireStoreFriednlyPath)){
                        Double current = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath);
                        Double newValue = Double.parseDouble(var2.toString());
                        DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, current+newValue);
                        DataHolder.houses.get(identifier).premonthlySum.put(var1, current+newValue);
                    } else {
                        DataHolder.houses.get(identifier).monthlySum.put(fireStoreFriednlyPath, 0.0);
                        DataHolder.houses.get(identifier).premonthlySum.put(var1, 0.0);
                    }


                    //================ AVG (AVERAGE) ==============================

                    //Daily average. Micropreparation is concluded
                    if(DataHolder.houses.get(identifier).dailyAverage.containsKey(fireStoreFriednlyPath)){
                        Double average = DataHolder.houses.get(identifier).dailySum.get(fireStoreFriednlyPath)/
                                DataHolder.houses.get(identifier).dailyCount.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, average);
                        DataHolder.houses.get(identifier).predailyAverage.put(var1, average);
                    } else {
                        DataHolder.houses.get(identifier).dailyAverage.put(fireStoreFriednlyPath, 0.0);
                        DataHolder.houses.get(identifier).predailyAverage.put(var1, 0.0);
                    }


                    //Daily Average in a day by day basis
                    if(DataHolder.houses.get(identifier).dailyAveragedbd.containsKey(var1)){
                        Double[] arr = DataHolder.houses.get(identifier).dailyAveragedbd.get(var1);
                        arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAveragedbd.put(var1, arr);
                    } else {
                        Double[] arr = new Double[32];
                        arr[DataHolder.datenum] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAveragedbd.put(var1, arr);
                    }

                    //Daily Average in a Hour by Hour basis
                    if(DataHolder.houses.get(identifier).dailyAveragehbh.containsKey(var1)){
                        Double[] arr = DataHolder.houses.get(identifier).dailyAveragehbh.get(var1);
                        arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAveragehbh.put(var1, arr);
                    } else {
                        Double[] arr = new Double[25];
                        arr[Integer.parseInt(DataHolder.hour)] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAveragehbh.put(var1, arr);
                    }

                    //Monthly Average in a Hour by Hour basis
                    if(DataHolder.houses.get(identifier).dailyAveragemhbh.containsKey(var1)){
                        Double[] arr = DataHolder.houses.get(identifier).dailyAveragemhbh.get(var1);
                        int adder = DataHolder.datenum * 100;
                        arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAveragemhbh.put(var1, arr);
                    } else {
                        Double[] arr = new Double[9999];
                        int adder = DataHolder.datenum * 100;
                        arr[Integer.parseInt(DataHolder.hour) + adder] = DataHolder.houses.get(identifier).dailyAverage.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).dailyAveragemhbh.put(var1, arr);
                    }

                    //Weekly average. Micropreparation is concluded
                    if(DataHolder.houses.get(identifier).weeklyAverage.containsKey(fireStoreFriednlyPath)){
                        Double average = DataHolder.houses.get(identifier).weeklySum.get(fireStoreFriednlyPath)/
                                DataHolder.houses.get(identifier).weeklyCount.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, average);
                        DataHolder.houses.get(identifier).preweeklyAverage.put(var1, average);
                    } else {
                        DataHolder.houses.get(identifier).weeklyAverage.put(fireStoreFriednlyPath, 0.0);
                        DataHolder.houses.get(identifier).preweeklyAverage.put(var1, 0.0);
                    }

                    //Monthly average. Micropreparation is concluded
                    if(DataHolder.houses.get(identifier).monthlyAverage.containsKey(fireStoreFriednlyPath)){
                        Double average = DataHolder.houses.get(identifier).monthlySum.get(fireStoreFriednlyPath)/
                                DataHolder.houses.get(identifier).monthlyCount.get(fireStoreFriednlyPath);
                        DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, average);
                        DataHolder.houses.get(identifier).premonthlyAverage.put(var1, average);
                    } else {
                        DataHolder.houses.get(identifier).monthlyAverage.put(fireStoreFriednlyPath, 0.0);
                        DataHolder.houses.get(identifier).premonthlyAverage.put(var1, 0.0);
                    }


                } //end of isnumeric() test

//            //For testing purposes. It tests the logs.
//            DataHolder.displayAnArrayList(DataHolder.houses.get(identifier).dailyLogsArrayList.get(friendlyDate),
//                    "[PREPARED] Daily Logs of today", identifier);




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

                    }

                    if (DataHolder.userExists(accauntname)) {
                        isTheUserValid = "true";
                        MQTT_demo.publish("web/statistics/registration", isTheUserValid);

                    } else
                        isTheUserValid = "false";
                    MQTT_demo.publish( "web/statistics/registration", isTheUserValid);
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

        //Listening to a subscription that delivers an ArrayList. For testing purposes
        if(flag.equalsIgnoreCase("testArrayList")){
            Gson gson = new Gson();

            System.out.println("reply topic  : " + var1);
            ArrayList receivedArrayList = gson.fromJson(var2.toString(), ArrayList.class);
            //DataHolder.displayAnArrayList(receivedArrayList,"Received ArrayList", 0);
            System.out.println("An ArrayList was received with the size of " + receivedArrayList.size());
        }

        //Listening to a subscription that delivers an ArrayList. For testing purposes
        if(flag.equalsIgnoreCase("testStringArray")){
            Gson gson = new Gson();

            System.out.println("reply topic  : " + var1);
            String[] receivedArray = gson.fromJson(var2.toString(), String[].class);
            System.out.println("An String[] array was received with the size of " + receivedArray.length);
            //DataHolder.displayAnArrayList(receivedArray,"Received ArrayList", 0);
        }


        //Listening to a subscription that delivers a Map. For testing purposes
        if(flag.equalsIgnoreCase("testMap")){
            Gson gson = new Gson();

            System.out.println("reply topic  : " + var1);
            Map receivedMap = gson.fromJson(var2.toString(), Map.class);
            System.out.println("A Map was received with the size of " + receivedMap.size());
            //DataHolder.displayAnArrayList(receivedArray,"Received ArrayList", 0);
        }

        //Listening to a subscription that delivers a Map. For testing purposes
        if(flag.equalsIgnoreCase("testMapDisplayIntegerDate")){
            Gson gson = new Gson();

            System.out.println("reply topic  : " + var1);
            Map receivedMap = gson.fromJson(var2.toString(), Map.class);
            HashMap<Date, Integer> receivedMap2 = (HashMap<Date, Integer>) receivedMap;
            final Map<Date, Integer> receivedMap2t = receivedMap;
            final int identifiert = identifier;
            System.out.println("A Map was received with the size of " + receivedMap.size());
            //updating the Firestore
            Thread tt = new Thread(){
                public void run(){
                    //Thread Implmentation code here
                    if(!receivedMap2t.isEmpty()){
                        DataHolder.displayDatafromAMapDateInteger(identifiert, receivedMap2t,
                                "received Date Integer Map");
                    } else {
                        System.out.println("Received an empty Date Integer map");
                    }

                }

            };

            tt.start();
        }

        //Listening to a subscription that delivers a HashMap. For testing purposes
        if(flag.equalsIgnoreCase("testHashMap")){
            Gson gson = new Gson();

            System.out.println("reply topic  : " + var1);
            Map receivedMap = gson.fromJson(var2.toString(), HashMap.class);
            System.out.println("A HashMap was received with the size of " + receivedMap.size());
            System.out.println("A simple display " + receivedMap.toString());
            //DataHolder.displayAnArrayList(receivedArray,"Received ArrayList", 0);
        }

        //Basic request from sector 2
        if(flag.equalsIgnoreCase("s2basicrequest")){
            tmp2 = var1.split("/");
            String firstToken = tmp2[0];
            String lastToken = tmp2[tmp2.length -1];
            String topicToReplyTo = firstToken + "/statistics/" +  lastToken;
            String retrieveFrom = "smart_house/gui/" + lastToken;
            if(var2.toString().equalsIgnoreCase("2")){
                HashMap<String, String> tmpmap = new HashMap<String, String>();
                HashMap<Date, Integer> tmpmap2 = new HashMap<Date, Integer>();
                if(DataHolder.houses.get(0).prepwlogs2.containsKey(retrieveFrom)){
                    tmpmap = DataHolder.houses.get(0).prepwlogs2.get(retrieveFrom);
                    tmpmap2 = DataHolder.stringStringMapToDateIntegerPatch(tmpmap);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            } else if(var2.toString().equalsIgnoreCase("3")){
                HashMap<String, String> tmpmap = new HashMap<String, String>();
                HashMap<Date, Integer> tmpmap2 = new HashMap<Date, Integer>();
                if(DataHolder.houses.get(0).prepmlogs2.containsKey(retrieveFrom)){
                    tmpmap = DataHolder.houses.get(0).prepmlogs2.get(retrieveFrom);
                    tmpmap2 = DataHolder.stringStringMapToDateIntegerPatch(tmpmap);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            } else {
                HashMap<String, String> tmpmap = new HashMap<String, String>();
                HashMap<Date, Integer> tmpmap2 = new HashMap<Date, Integer>();
                if(DataHolder.houses.get(0).prepLogs2.containsKey(retrieveFrom)){
                    tmpmap = DataHolder.houses.get(0).prepLogs2.get(retrieveFrom);
                    tmpmap2 = DataHolder.stringStringMapToDateIntegerPatch(tmpmap);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            }
        }

        //Basic request from sector 2
        if(flag.equalsIgnoreCase("s2averageinteger")){
            tmp2 = var1.split("/");
            String firstToken = tmp2[0];
            String lastToken = tmp2[tmp2.length -1];
            String topicToReplyTo = firstToken + "/statistics/" +  lastToken;
            String retrieveFrom = "smart_house/gui/" + lastToken;
            if(var2.toString().equalsIgnoreCase("2")){
                Double[] tmparr2 = new Double[9999];
                HashMap<Date, Integer> tmpmap2 = new HashMap<Date, Integer>();
                if(DataHolder.houses.get(0).dailyAveragemhbh.containsKey(retrieveFrom)){
                    tmparr2 = DataHolder.houses.get(0).dailyAveragemhbh.get(retrieveFrom);
                    System.out.println("The size of dailyAveragemhbh: "
                            + DataHolder.houses.get(0).dailyAveragemhbh.get(retrieveFrom).length);
                    System.out.println("The size of tmparr2: " + tmparr2.length);

                    //this need to be replaced with a custom method
                    tmpmap2 = DataHolder.doubleArraypToDateIntegerW(tmparr2);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            } else if(var2.toString().equalsIgnoreCase("3")){
                Double[] tmparr3 = new Double[9999];
                HashMap<Date, Integer> tmpmap2 = new HashMap<Date, Integer>();
                if(DataHolder.houses.get(0).dailyAveragemhbh.containsKey(retrieveFrom)){
                    tmparr3 = DataHolder.houses.get(0).dailyAveragemhbh.get(retrieveFrom);
                    tmpmap2 = DataHolder.doubleArraypToDateIntegerM(tmparr3);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            } else {
                Double[] tmparr4 = new Double[25];
                HashMap<Date, Integer> tmpmap2 = new HashMap<Date, Integer>();
                if(DataHolder.houses.get(0).dailyAveragehbh.containsKey(retrieveFrom)){
                    tmparr4 = DataHolder.houses.get(0).dailyAveragehbh.get(retrieveFrom);
                    tmpmap2 = DataHolder.doubleArraypToDateInteger24(tmparr4);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            }
        }


        //average double
        if(flag.equalsIgnoreCase("s2averagedouble")){
            tmp2 = var1.split("/");
            String firstToken = tmp2[0];
            String lastToken = tmp2[tmp2.length -1];
            String topicToReplyTo = firstToken + "/statistics/" +  lastToken;
            String retrieveFrom = "smart_house/gui/" + lastToken;
            if(var2.toString().equalsIgnoreCase("2")){
                Double[] tmparr = new Double[9999];
                HashMap<Date, Double> tmpmap2 = new HashMap<Date, Double>();
                if(DataHolder.houses.get(0).dailyAveragemhbh.containsKey(retrieveFrom)){
                    tmparr = DataHolder.houses.get(0).dailyAveragemhbh.get(retrieveFrom);

                    //this need to be replaced with a custom method
                    tmpmap2 = DataHolder.doubleArraypToDateDoubleW(tmparr);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            } else if(var2.toString().equalsIgnoreCase("3")){
                Double[] tmparr = new Double[9999];
                HashMap<Date, Double> tmpmap2 = new HashMap<Date, Double>();
                if(DataHolder.houses.get(0).dailyAveragemhbh.containsKey(retrieveFrom)){
                    tmparr = DataHolder.houses.get(0).dailyAveragemhbh.get(retrieveFrom);
                    tmpmap2 = DataHolder.doubleArraypToDateDoubleM(tmparr);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            } else {
                Double[] tmparr = new Double[25];
                HashMap<Date, Double> tmpmap2 = new HashMap<Date, Double>();
                if(DataHolder.houses.get(0).dailyAveragehbh.containsKey(retrieveFrom)){
                    tmparr = DataHolder.houses.get(0).dailyAveragehbh.get(retrieveFrom);
                    tmpmap2 = DataHolder.doubleArraypToDateDouble24(tmparr);
                }
                DataHolder.publishAMap(DataHolder.sampleclient, topicToReplyTo,
                        tmpmap2);
            }
        }

        //DataHolder.displayAnArrayList(DataHolder.houses.get(0).mainLogs, "Main logs for testing", 0);
        System.out.println("=======================================================================");
        System.out.println("");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataHolder.publishing = true; //the data is accessed and now it can publish again.
    } //end of messageArrived()
}

public class MQTT_demo {

    static String responseToValidationOfTheUser ="";
    static ArrayList<String> subscriptions = new ArrayList<String>();

    static MqttClient sampleClient;
    static MqttClient sampleClient2;
    static Random rand;

    public static void main(String[] args) throws InterruptedException {

        //Getting the timestamp
        Calendar cal = Calendar.getInstance();
        SecureRandom random = new SecureRandom();
        Gson gson = new Gson();


        int randomseed = random.nextInt();
        String mqttUsername = Configurations.mqttUsername + randomseed;
        String mqttUsername2 = Configurations.mqttUsername + randomseed + 1;
        SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateOnly.format(cal.getTime());
        String[] timeStamp = date.split("/");
        DataHolder.month = timeStamp[0];
        DataHolder.week = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
        DataHolder.day = date;
        DataHolder.dayStamp = date.replace("/", "");
        SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm:ss");
        String time = timeOnly.format(cal.getTime());
        String[] tmp = time.split(":");
        DataHolder.hour = tmp[0];
        System.out.println("Month: " + DataHolder.month);
        System.out.println("Week: " + DataHolder.week);
        System.out.println("Date: " + DataHolder.day);
        System.out.println("Daystamp: " + DataHolder.dayStamp);
        System.out.println("Hour: " + DataHolder.hour);
        System.out.println("Time of starting: " + time);
        String dateAndTime = date + "-" +  time;
        System.out.println("Time of the arrival: " + dateAndTime);
        System.out.println("Making the time of the arrival a date");
        Date testDate = DataHolder.stringToDate(dateAndTime);
        System.out.println(testDate.toString());
        String dateAndTimeFirestoreFriendly = dateAndTime.replace("/", "");
        System.out.println("Date and time on firestorm mode: " + dateAndTimeFirestoreFriendly);
        String dateFirestoreFriendly = date.replace("/", "");
        System.out.println("Date on firestorm mode: " + dateFirestoreFriendly);

        //creating a house.
        House house = new House(0);
        DataHolder.houses.add(0, house);

        //loading the data
        DataHolder.loadHashMapsFromFireStore(0);
        DataHolder.displayTheContents(0);
        DataHolder.displayAnArrayList(DataHolder.houses.get(0).mainLogs, "Main logs for testing", 0);


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
            DataHolder.sampleclient = sampleClient;
            //final MqttClient sampleClient2 = new MqttClient(broker, mqttUsername2, persistence);

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


            //loading users every 60 seconds.
            Thread loader = new Thread(){
                public void run(){
                    //Thread Implmentation code here
                    while(true){
                        try {
                            Thread.sleep(60000);
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
                    //publishing stuff for testing
                    if(Configurations.testPublisher){

                    //topic test/2
                    String topic = "db/test/2";
                    String content = "msg2! #" + counting;
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(0);
                    sampleClient.publish(topic, message);
                    System.out.println("publish: " + content);


                    Thread.sleep(3000);
                    //topic test/3
                    if (DataHolder.publishing) {
                        topic = "smart_house/gui/electricity_consumptiontest";
                        content = String.valueOf(counting);
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        sampleClient.publish(topic, message);
                        System.out.println("publish: " + content);
                    }

                    Thread.sleep(6000);
                    //web/request/electricity_consumption
                    if (DataHolder.publishing) {
                        topic = "web/request/electricity_consumptiontest";
                        content = String.valueOf(1);
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        sampleClient.publish(topic, message);
                        System.out.println("publish: " + content);
                    }

                    Thread.sleep(6000);
                    //web/request/electricity_consumption
                    if (DataHolder.publishing) {
                        topic = "web/request/electricity_consumptiontest";
                        content = String.valueOf(2);
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        sampleClient.publish(topic, message);
                        System.out.println("publish: " + content);
                    }

                    Thread.sleep(6000);
                    //web/request/electricity_consumption
                    if (DataHolder.publishing) {
                        topic = "web/request/electricity_consumptiontest";
                        content = String.valueOf(3);
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        sampleClient.publish(topic, message);
                        System.out.println("publish: " + content);
                    }

                    Thread.sleep(3000);
                    //topic test/3
                    if (DataHolder.publishing) {
                        topic = "db/test/3";
                        content = String.valueOf(3);
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        sampleClient.publish(topic, message);
                        System.out.println("publish: " + content);
                    }

                    Thread.sleep(3000);
                    //topic test/4
                    if (DataHolder.publishing) {
                        topic = "db/test/4";
                        content = String.valueOf(4 * counting);
                        message = new MqttMessage(content.getBytes());
                        message.setQos(0);
                        sampleClient.publish(topic, message);
                        System.out.println("publish: " + content);
                    }

//                    Thread.sleep(3000);
//                    //Register
//                    if (DataHolder.publishing) {
//                        System.out.println("Trying to do a register");
//                        topic = "web/request/register_user";
//                        //content = String.valueOf(4 * counting);
//                        //message = new MqttMessage(content.getBytes());
//                        //message.setQos(0);
//                        //sampleClient.publish(topic, message);
//                        String userName = "someUser" + counting;
//                        String passWord = "12345";
//                        String email = "justAnEmail";
//                        String name = "John Doe";
//                        String[] userArrayRegister = new String[]{userName, passWord, email, name};
//                        sampleClient.publish(topic, new MqttMessage(gson.toJson(userArrayRegister)
//                                .getBytes()));
//                        System.out.println("publish: " + userArrayRegister.toString());
//                    }

                    Thread.sleep(3000);
                    //Login
                    if (DataHolder.publishing) {
                        System.out.println("Trying to do a login");
                        topic = "web/request/user";
                        //content = String.valueOf(4 * counting);
                        //message = new MqttMessage(content.getBytes());
                        String[] userArray = new String[]{"testuser1", "12345"};
                        sampleClient.publish(topic, new MqttMessage(gson.toJson(userArray).getBytes()));
                        //message.setQos(0);
                        //sampleClient.publish(topic, message);
                        System.out.println("publish: Login: " + userArray[0]);
                        Thread.sleep(3000);
                    }


//                    //Publishing an ArrayList as a test
//                    System.out.println("publishing the ArrayList with the main logs");
//                    DataHolder.publishArrayList(sampleClient,"testArrayList",
//                            DataHolder.houses.get(0).mainLogsMonth);
//                    Thread.sleep(3000 * subscriptions.size());
//
//                    //Publishing a String[] Array as a test
//                    System.out.println("publishing a String Array with 3 String values");
//                    String[] arr = {"value1", "value2", "value3"};
//                    DataHolder.publishStringArray(sampleClient, "testStringArray", arr);
//                    Thread.sleep(3000 * subscriptions.size());
//
//                    //Publishing a Map as a test
//                    System.out.println("publishing a String Array with 3 String values");
//                    Map map = DataHolder.houses.get(0).logs;
//                    DataHolder.publishAMap(sampleClient, "testMap", map);
//                    Thread.sleep(3000 * subscriptions.size());
//
//                    //Publishing a HashMap as a test
//                    System.out.println("publishing a String Array with 3 String values");
//                    HashMap map2 = DataHolder.houses.get(0).logs;
//                    DataHolder.publishAHashMap(sampleClient, "testHashMap", map2);

                    counting++;
                    //Thread.sleep(3000 * subscriptions.size());
                }
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

    static void publish(String topicName,String messageToBePublished) throws MqttException {
//        if(!sampleClient.isConnected()){
//            mqttConnect();
//        }
        //MqttClient sampleClient;
        SecureRandom random = new SecureRandom();
        int randomseed = random.nextInt();
        String mqttUsername = Configurations.mqttUsername + randomseed;

//Setting up the connection
        String port = String.valueOf(Configurations.mqttPort);
        String broker = Configurations.mqttHost + ":" + port;
        MemoryPersistence persistence = new MemoryPersistence();
            //Creating the client. the clientId can be configured on Configurations and a random number is added.
            final MqttClient sampleClient = new MqttClient(broker, mqttUsername, persistence);

            //Creating the options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(180);
            sampleClient.connect(connOpts);

            String topic = topicName;
            MqttMessage message = new MqttMessage(messageToBePublished.getBytes());
            try {
                sampleClient.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            System.out.println("publish: " + messageToBePublished);
    }


//    static void mqttConnect() {
//
//        String broker = Configurations.mqttHost + ":" + Configurations.mqttPort;
//        MemoryPersistence persistence = new MemoryPersistence();
//
//        try {
//            //Creating the client
//            sampleClient = new MqttClient(broker, "ServerDatabase_"+rand.nextInt(), persistence);
//
//            //Creating the options
//            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setCleanSession(true);
//            connOpts.setKeepAliveInterval(180);
//            sampleClient.connect(connOpts);
//            System.out.println("broker: " + broker + " Connected");
//
//
//            //Subscribe to topic
//            //   String topicReply = "web/request/create_new_user";
//            //   sampleClient.subscribe(topicReply, new MqttPostPropertyMessageListener());
//            //   System.out.println("subscribed to: " + topicReply);
//
//            // while (true) { // while loop just to test server statistics (constant publishing)
//            //It makes the indoor light blink every 4 seconds.
//            //  String topic = "smart_house/gui/indoor_light";
//            //  String content = "false";
//            //  MqttMessage message = new MqttMessage(content.getBytes());
//            //   message.setQos(0);
//            //   sampleClient.publish(topic, message);
//            //   System.out.println("publish: " + content);
//            //   Thread.sleep(2000);
//
//
//            //  Gson gson = new Gson();
//
//            //  String [] userArray =new String[]{"Lavdimm","123456","Lavdim","lavdimImeri@hotmail.com"};
//            //    User user = new User(userArray[0],userArray[1],userArray[2],userArray[3]);
//
//            //  String topic = "web/statistics/user";
//
//
//
//            //  String topic2 = "smart_house/cmd/indoor_light";
//            //   String content2 = "false";
//            //   MqttMessage message2 = new MqttMessage(content2.getBytes());
//            //   message2.setQos(0);
//            //   sampleClient.publish(topic2, message2);
//            //   System.out.println("publish: " + content2);
//            // Thread.sleep(2000);
//            // }
//
//            //Disconnecting
//            //   sampleClient.disconnect();
//            //   System.out.println("Disconnected");
//            //     System.exit(0);
//        } catch (MqttException e) {
//            System.out.println("reason: " + e.getReasonCode());
//            System.out.println("msg: " + e.getMessage());
//            System.out.println("loc: " + e.getLocalizedMessage());
//            System.out.println("cause: " + e.getCause());
//            System.out.println("excep: " + e);
//            e.printStackTrace();
//        }
//
//    }


    // Same here, I commented the lines, you were connecting again to the broker and you already are connected at line 194
    static void publishArrayOfStringsAsJSON(String topicName,String[] userArray) {

//            String broker = Configurations.mqttHost + ":" + Configurations.mqttPort;
//            MemoryPersistence persistence = new MemoryPersistence();


//        if(!sampleClient.isConnected()){
//            mqttConnect();
//        }

        try {

            SecureRandom random = new SecureRandom();
            int randomseed = random.nextInt();
            String mqttUsername = Configurations.mqttUsername + randomseed;

//Setting up the connection
            String port = String.valueOf(Configurations.mqttPort);
            String broker = Configurations.mqttHost + ":" + port;
            MemoryPersistence persistence = new MemoryPersistence();
            //Creating the client. the clientId can be configured on Configurations and a random number is added.
            final MqttClient sampleClient = new MqttClient(broker, mqttUsername, persistence);

            //Creating the options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(180);
            sampleClient.connect(connOpts);

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