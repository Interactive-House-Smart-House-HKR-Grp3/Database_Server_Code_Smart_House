package se.hkr.mqtt;

import java.util.ArrayList;
import java.util.HashMap;

public class House {
    int identifier = 0;
    HashMap<String, String> lastKnownValues = new HashMap();
    HashMap<String, String>lastKnownTimeStamps = new HashMap();
    HashMap<String, Integer>dailyCount = new HashMap();
    HashMap<String, Integer>dailyAverage = new HashMap();
    HashMap<String, Integer>dailySum = new HashMap();
    HashMap<String, Integer>weeklyCount = new HashMap();
    HashMap<String, Integer>weeklyAverage = new HashMap();
    HashMap<String, Integer>weeklySum = new HashMap();
    HashMap<String, Integer>monthlyCount = new HashMap();
    HashMap<String, Integer>monthlyAverage = new HashMap();
    HashMap<String, Integer>monthlySum = new HashMap();

    HashMap<String, ArrayList<String>> logs = new HashMap();
    ArrayList<String> mainLogs = new ArrayList<String>();

    House(int identifier){
        this.identifier = identifier;
    }
}
