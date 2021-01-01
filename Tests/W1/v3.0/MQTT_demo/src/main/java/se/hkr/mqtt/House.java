package se.hkr.mqtt;

import java.util.ArrayList;
import java.util.HashMap;

public class House {
    int identifier = 0;

    //Last Known Stuff
    HashMap<String, String> lastKnownValues = new HashMap();
    HashMap<String, String>lastKnownTimeStamps = new HashMap();

    //Human Readable Last Known Stuff
    HashMap<String, String> prelastKnownValues = new HashMap();
    HashMap<String, String> prelastKnownTimeStamps = new HashMap();

    //Daily stats
    HashMap<String, Integer>dailyCount = new HashMap();
    HashMap<String, Double>dailyAverage = new HashMap();
    HashMap<String, Double>dailySum = new HashMap();

    //Human Readable Daily Stats
    HashMap<String, Integer>predailyCount = new HashMap();
    HashMap<String, Double>predailyAverage = new HashMap();
    HashMap<String, Double>predailySum = new HashMap();

    //Daily stats Day by Day
    HashMap<String, Integer[]>dailyCountdbd = new HashMap();
    HashMap<String, Double[]>dailyAveragedbd = new HashMap();
    HashMap<String, Double[]>dailySumdbd = new HashMap();

    //Daily stats Hour by hour
    HashMap<String, Integer[]>dailyCounthbh = new HashMap();
    HashMap<String, Double[]>dailyAveragehbh = new HashMap();
    HashMap<String, Double[]>dailySumhbh = new HashMap();

    //Monthly stats Hour by hour
    HashMap<String, Integer[]>dailyCountmhbh = new HashMap();
    HashMap<String, Double[]>dailyAveragemhbh = new HashMap();
    HashMap<String, Double[]>dailySummhbh = new HashMap();

    //Weekly Stats
    HashMap<String, Integer>weeklyCount = new HashMap();
    HashMap<String, Double>weeklyAverage = new HashMap();
    HashMap<String, Double>weeklySum = new HashMap();

    //Human Readable Weekly Stats
    HashMap<String, Integer>preweeklyCount = new HashMap();
    HashMap<String, Double>preweeklyAverage = new HashMap();
    HashMap<String, Double>preweeklySum = new HashMap();

    //Monthly Stats
    HashMap<String, Integer>monthlyCount = new HashMap();
    HashMap<String, Double>monthlyAverage = new HashMap();
    HashMap<String, Double>monthlySum = new HashMap();

    //Human Readable Monthly Stats
    HashMap<String, Integer>premonthlyCount = new HashMap();
    HashMap<String, Double>premonthlyAverage = new HashMap();
    HashMap<String, Double>premonthlySum = new HashMap();

    //Daily Logs in Data Structures. (31 days of the month).
    //All of them are human readable.
    //as an arraylist
    HashMap<String, ArrayList<String>>dailyLogsArrayList = new HashMap();
    //as an array
    HashMap<String, String[]>dailyLogsArray = new HashMap();
    //as a String concatination. Very ineffective
    HashMap<String, String>dailyLogsArrayConCat = new HashMap(); //String concatination.

    //[Sector 2]Daily Logs in Data Structures. (31 days of the month).
    //[Sector 2]All of them are human readable.
    //[Sector 2]as an arraylist
    HashMap<String, ArrayList<String>>dailyLogsArrayList2 = new HashMap();
    //Sector2. as an array
    HashMap<String, String[]>dailyLogsArray2 = new HashMap();
    //Sector2. as a String concatination. Very ineffective
    HashMap<String, String>dailyLogsArrayConCat2 = new HashMap(); //String concatination.

    //Subscription specific Logs. Raw
    HashMap<String, ArrayList<String>> logs = new HashMap(); //of the day
    HashMap<String, ArrayList<String>> wlogs = new HashMap(); //weekly
    HashMap<String, ArrayList<String>> mlogs = new HashMap(); //monthly

    //Subscription specific Logs. Raw and prepared.(Prepared are human readable)
    HashMap<String, ArrayList<String>> prepLogs = new HashMap(); //of the day
    HashMap<String, ArrayList<String>> prepwlogs = new HashMap(); //weekly
    HashMap<String, ArrayList<String>> prepmlogs = new HashMap(); //monthly

    //Subscription specific Logs 2. Raw. 2 belongs to the things that Alex asked.
    HashMap<String, ArrayList<String>> logs2 = new HashMap(); //of the day
    HashMap<String, ArrayList<String>> wlogs2 = new HashMap(); //weekly
    HashMap<String, ArrayList<String>> mlogs2 = new HashMap(); //monthly

    //Subscription specific Logs 2. Raw and prepared.(Prepared are human readable)
    HashMap<String, HashMap<String, String>> prepLogs2 = new HashMap(); //of the day
    HashMap<String, HashMap<String, String>> prepwlogs2 = new HashMap(); //weekly
    HashMap<String, HashMap<String, String>> prepmlogs2 = new HashMap(); //monthly

    //Main logs. Raw
    ArrayList<String> mainLogs = new ArrayList<String>();
    ArrayList<String> mainLogsWeek = new ArrayList<String>();
    ArrayList<String> mainLogsMonth = new ArrayList<String>();

    //Main logs. Prepared, (Human Readable)
    ArrayList<String> premainLogs = new ArrayList<String>();
    ArrayList<String> premainLogsWeek = new ArrayList<String>();
    ArrayList<String> premainLogsMonth = new ArrayList<String>();

    //Main logs 2. Raw .2 belongs to the things that Alex asked.
    ArrayList<String> mainLogs2 = new ArrayList<String>();
    ArrayList<String> mainLogsWeek2 = new ArrayList<String>();
    ArrayList<String> mainLogsMonth2 = new ArrayList<String>();

    //Main logs 2. Prepared, (Human Readable)
    HashMap<String, String> premainLogs2 = new HashMap();
    HashMap<String, String> premainLogsWeek2 = new HashMap();
    HashMap<String, String> premainLogsMonth2 = new HashMap();

    House(int identifier){
        this.identifier = identifier;
    }
}
