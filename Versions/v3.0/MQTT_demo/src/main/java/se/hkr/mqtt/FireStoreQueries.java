package se.hkr.mqtt;

import java.util.HashMap;
import java.util.Map;

public class FireStoreQueries {

    //========== CREATE ============
    //ALL CREATE methods are working as they should.

    void createCollection(String collectionName){
        String seed = "1@@1@@" + collectionName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //It creates a Collection that includes a document inside.
    //It does pretty much the same thing a createDocument
    void createCollectionWithDocument(String collectionName, String documentName){
        String seed = "1@@1@@" + collectionName + "@@" + documentName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //It creates a Collection that includes a document inside.
    //It does pretty much the same thing a createElement
    void createCollectionWithDocumentAndElement(String collectionName, String documentName, String key, String value){
        String seed = "1@@1@@" + collectionName + "@@" + documentName + "@@" + key + "@@" + value;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //It creates a document inside a Collection. If the collection does not exists, it creates one.
    void createDocument(String collectionName, String documentName){
        String seed = "1@@2@@" + collectionName + "@@" + documentName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //It creates a document inside a Collection. If the collection does not exist, it creates one and adds an element.
    //It does the same thing with createElement
    void createDocumentWithElement(String collectionName, String documentName, String key, String value){
        String seed = "1@@2@@" + collectionName + "@@" + documentName + "@@" + key + "@@" + value;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //it creates an element inside a document. if document does not exist, it creates one.
    void createElement(String collectionName, String documentName, String key, String value){
        String seed = "1@@3@@" + collectionName + "@@" + documentName + "@@" + key + "@@" + value;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //It creates or updates a document from a HashMap. A truly poweful tool and the way to do things
    void createDocumentfromAMap(String collectionName, String documentName, HashMap<String,String> map){
        String preseed = mapToSeed(map);
        String seed = "1@@4@@" + collectionName + "@@" + documentName + "@@" + preseed;
        //System.out.println("the seed is " + seed);
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //It creates or updates a document from a HashMap. A truly poweful tool and the way to do things
    void createDocumentfromAMap2(String collectionName, String documentName, HashMap<String,Integer> map){
        String preseed = mapToSeed2(map);
        String seed = "1@@4@@" + collectionName + "@@" + documentName + "@@" + preseed;
        //System.out.println("the seed is " + seed);
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //It creates or updates a document from a HashMap. A truly poweful tool and the way to do things
    void createDocumentfromAMap3(String collectionName, String documentName, HashMap<String,Double> map){
        String preseed = mapToSeed3(map);
        String seed = "1@@4@@" + collectionName + "@@" + documentName + "@@" + preseed;
        //System.out.println("the seed is " + seed);
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //========== READ ============

    String[] readCollection(String collectionName){
        String seed = "2@@1@@" + collectionName;
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        String[] arr = seedToArray(word);
        echoClient.terminate();
        return arr;
    }

    //It reads all the content of a document and return a HashMap with the key/value pairs of the document
    HashMap<String, String> readDocument(String collectionName, String documentName){
        HashMap<String, String> map =  new HashMap();
        String seed = "2@@2@@" + collectionName + "@@" + documentName;
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        if(!word.equalsIgnoreCase("-1")){
            map = seedToMap(word);
        }
        echoClient.terminate();
        return map;
    }

    //It reads all the content of a document and return a HashMap with the key/value pairs of the document
    HashMap<String, Integer> readDocument2(String collectionName, String documentName){
        HashMap<String, Integer> map =  new HashMap();
        String seed = "2@@2@@" + collectionName + "@@" + documentName;
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        if(!word.equalsIgnoreCase("-1")){
            map = seedToMap2(word);
        }

        echoClient.terminate();
        return map;
    }

    //It reads all the content of a document and return a HashMap with the key/value pairs of the document
    HashMap<String, Double> readDocument3(String collectionName, String documentName){
        HashMap<String, Double> map =  new HashMap();
        String seed = "2@@2@@" + collectionName + "@@" + documentName;
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        if(!word.equalsIgnoreCase("-1")){
            map = seedToMap3(word);
        }

        echoClient.terminate();
        return map;
    }

    String readElement(String collectionName, String documentName, String key){
        String seed = "2@@3@@" + collectionName + "@@" + documentName + "@@" + key;
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        echoClient.terminate();
        return word;
    }

    HashMap<String, String> readOneElementFromEachDocument(String collectionName, String key){
        String seed = "2@@7@@" + collectionName + "@@" + "notAValue" + "@@" + key;
        HashMap<String, String> map =  new HashMap();
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        echoClient.terminate();
        map = seedToMap(word);
        return map;
    }

    //========== EXISTS ============

    boolean existsCollection(String collectionName){
        String seed = "2@@4@@" + collectionName;
        String word = "";
        boolean answer = false;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        if(word.equalsIgnoreCase("false")){
            answer = false;
        } else {
            answer = true;
        }
        echoClient.terminate();
        return answer;
    }

    boolean existsDocument(String collectionName, String documentName){
        String seed = "2@@5@@" + collectionName + "@@" + documentName;
        String word = "";
        boolean answer = false;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        if(word.equalsIgnoreCase("false")){
            answer = false;
        } else {
            answer = true;
        }
        echoClient.terminate();
        return answer;
    }

    //legacy code. it will not be used
    boolean existsKey2(String collectionName, String documentName, String key){
        String seed = "2@@6@@" + collectionName + "@@" + documentName + "@@" + key;
        String word = "";
        boolean answer = false;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        if(word.equalsIgnoreCase("false")){
            answer = false;
        } else {
            answer = true;
        }
        echoClient.terminate();
        return answer;
    }

    //it finds if a key exists.
    //The new way to do things. It is the most high-tech piece of code of the client and server instance
    //but still under pressure, (rapid reads and writes in same collection), can fail due to slow refresh rate from
    //firebase. Use it in single operation and not in group exists() requests.
    //try to avoid using this method as much as possible.
    boolean existsKey(String collectionName, String documentName, String key){
        HashMap<String, String> map =  new HashMap();
        HashMap<String, String> map2 =  new HashMap();
        HashMap<String, String> map3 =  new HashMap();
        map = readDocument(collectionName,documentName);
        if (existsDocument(collectionName,documentName)) {
            if(map.isEmpty()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                map2 = readDocument(collectionName,documentName);
                if(map2.isEmpty()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map3 = readDocument(collectionName,documentName);
                    if(map3.isEmpty()){
                        return false;
                    }
                }
            }
            if(map.containsKey(key)){
                return true;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                map2 = readDocument(collectionName,documentName);
                if(map2.containsKey(key)){
                    return true;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map3 = readDocument(collectionName,documentName);
                    if(map3.containsKey(key)){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } //if existDocument ends here
        return false;
    }

    //It checks if a specific value exists. It is the most high-tech piece of code of the client and server instance
    //but still, under pressure, (rapid reads and writes in same collection), can fail due to slow refresh rate from
    //firebase. Use it in single operation and not in group exists() requests.
    //try to avoid using this method as much as possible.
    boolean existsValue(String collectionName, String documentName, String value){
        HashMap<String, String> map =  new HashMap();
        HashMap<String, String> map2 =  new HashMap();
        HashMap<String, String> map3 =  new HashMap();
        map = readDocument(collectionName,documentName);
        if (existsDocument(collectionName,documentName)) {
            if(map.isEmpty()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                map2 = readDocument(collectionName,documentName);
                if(map2.isEmpty()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map3 = readDocument(collectionName,documentName);
                    if(map3.isEmpty()){
                        return false;
                    }
                }
            }
            if(map.containsValue(value)){
                return true;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                map2 = readDocument(collectionName,documentName);
                if(map2.containsValue(value)){
                    return true;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map3 = readDocument(collectionName,documentName);
                    if(map3.containsValue(value)){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } //if existDocument ends here
        return false;
    }

    //========== UPDATE ============

    void updateCollection(String collectionName){
        String seed = "3@@1@@" + collectionName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    void updateDocument(String collectionName, String documentName){
        String seed = "3@@2@@" + collectionName + "@@" + documentName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    void updateElement(String collectionName, String documentName, String key, String value){
        String seed = "3@@3@@" + collectionName + "@@" + documentName + "@@" + key + "@@" + value;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //========== DELETE ============

    void deleteCollection(String collectionName){
        String seed = "4@@1@@" + collectionName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    void deleteDocument(String collectionName, String documentName){
        String seed = "4@@2@@" + collectionName + "@@" + documentName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    void deleteElement(String collectionName, String documentName, String key){
        String seed = "4@@3@@" + collectionName + "@@" + documentName + "@@" + key;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }


    //============== Helping Methods =============================

    HashMap<String, String> seedToMap(String seed){
        HashMap<String, String> map =  new HashMap();
        String[] temp2 = new String[2];
        String[] temp = seed.split("@@");
        String key = "No key";
        String value = "No value";
        for (int x=0; x < temp.length; x++){
            String miniseed = temp[x];
            if(!miniseed.equalsIgnoreCase("")){
                temp2 = miniseed.split("mpt@mpt");
                key = temp2[0].trim();
                if(temp2.length > 1){
                    value = temp2[1].trim();
                }
                map.put(key, value);
            }
        }
        return map;
    }


    HashMap<String, Integer> seedToMap2(String seed){
        HashMap<String, Integer> map =  new HashMap();
        String[] temp2 = new String[2];
        String[] temp = seed.split("@@");
        String key = "no key";
        String value = "0";
        for (int x=0; x < temp.length; x++){
            String miniseed = temp[x];
            if(!miniseed.equalsIgnoreCase("")){
                temp2 = miniseed.split("mpt@mpt");
                key = temp2[0].trim();
                if(temp2.length > 1){
                    value = temp2[1].trim();
                }
                if(DataHolder.isNumeric(value)){
                    map.put(key, Integer.valueOf(value));
                }
            }
        }
        return map;
    }

    HashMap<String, Double> seedToMap3(String seed){
        HashMap<String, Double> map =  new HashMap();
        String[] temp2 = new String[2];
        String[] temp = seed.split("@@");
        String key = "no key";
        String value = "0";
        for (int x=0; x < temp.length; x++){
            String miniseed = temp[x];
            if(!miniseed.equalsIgnoreCase("")){
                temp2 = miniseed.split("mpt@mpt");
                key = temp2[0].trim();
                if(temp2.length > 1){
                    value = temp2[1].trim();
                }
                if(DataHolder.isNumeric(value)){
                    map.put(key, Double.valueOf(value));
                }
            }
        }
        return map;
    }


    String[] seedToArray(String seed){
        String[] arr = seed.split("@@");
        return arr;
    }

    String mapToSeed(HashMap<String, String> map){
        String word = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            word = word + "@new@entry@" + entry.getKey() + "@key@value@" + entry.getValue();
        }
        //System.out.println("the pressed is " + word);
        return word;
    }

    String mapToSeed2(HashMap<String, Integer> map){
        String word = "";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            word = word + "@new@entry@" + entry.getKey() + "@key@value@" + entry.getValue();
        }
        //System.out.println("the pressed is " + word);
        return word;
    }

    String mapToSeed3(HashMap<String, Double> map){
        String word = "";
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            word = word + "@new@entry@" + entry.getKey() + "@key@value@" + entry.getValue();
        }
        //System.out.println("the pressed is " + word);
        return word;
    }

    void initializeWriting(){
        while(Configurations.writing){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Configurations.writing = true;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
