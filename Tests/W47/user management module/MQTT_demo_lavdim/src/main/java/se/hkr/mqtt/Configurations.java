package se.hkr.mqtt;

public class Configurations {
    //Server
    static String ipAddress = "localhost"; //The IP address of the Server which the Client wishes to connect
    static int destinationPort = 2345; //The port of the Server which the Client wishes to connect
    //Broker (for smart house: tcp://smart-mqtthive.duckdns.org)
    //Public broker for testing: tcp://broker.hivemq.com
  //  static String mqttHost="tcp://broker.hivemq.com";
    static String mqttHost = "tcp://smart-mqtthive.duckdns.org";
    static  int mqttPort = 1883;

    //Dataholder. While have tha class here why not using it as Dataholder for global variables.

    static boolean writing = false;
}
