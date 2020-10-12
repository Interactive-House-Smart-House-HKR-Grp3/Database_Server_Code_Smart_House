public class FireStoreQueries {

    //========== CREATE ============

    void createCollection(String collectionName){
        String seed = "1@@1@@" + collectionName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    void createDocument(String collectionName, String documentName){
        String seed = "1@@2@@" + collectionName + "@@" + documentName;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    void createElement(String collectionName, String documentName, String key, String value){
        String seed = "1@@3@@" + collectionName + "@@" + documentName + "@@" + key + "@@" + value;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

    //========== READ ============

    String readCollection(String collectionName){
        String seed = "2@@1@@" + collectionName;
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        echoClient.terminate();
        return word;
    }

    String readDocument(String collectionName, String documentName){
        String seed = "2@@2@@" + collectionName + "@@" + documentName;
        String word = "";
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, true);
        word = echoClient.returnEcho();
        echoClient.terminate();
        return word;
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
        String seed = "2@@3@@" + collectionName + "@@" + documentName + "@@" + key;
        EchoClient echoClient = new EchoClient();
        echoClient.start(seed, false);
        echoClient.terminate();
    }

}
