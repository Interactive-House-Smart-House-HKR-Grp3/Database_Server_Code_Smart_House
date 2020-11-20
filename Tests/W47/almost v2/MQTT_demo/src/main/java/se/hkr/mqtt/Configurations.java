package se.hkr.mqtt;

public class Configurations {
    //Server
    static String ipAddress = "localhost"; //The IP address of the Server which the Client wishes to connect
    static int destinationPort = 2345; //The port of the Server which the Client wishes to connect
    //Broker (for smart house: tcp://smart-mqtthive.duckdns.org)
    //Broker for when the group 3 have closed the server "tcp://broker.hivemq.com"
    static String mqttHost = "tcp://broker.hivemq.com";
    static  int mqttPort = 1883;
    static String mqttUsername = "serverDatabase"; //the system will add a random number at the end of it.

    //Dataholder. While have tha class here why not using it as Dataholder for global variables.
    //Please don't touch anything bellow this comment.
    static boolean writing = false;
}
