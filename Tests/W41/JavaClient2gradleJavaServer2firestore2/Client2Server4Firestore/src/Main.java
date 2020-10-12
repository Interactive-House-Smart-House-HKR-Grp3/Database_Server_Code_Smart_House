import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
//        while (true){
//            System.out.println("Give your next query");
//            String word = in.nextLine();
//            EchoClient echoClient = new EchoClient();
//            echoClient.start(word, false);
//        }

        //Test script. Everything seems to work fine.
        FireStoreQueries fire = new FireStoreQueries();
        fire.createCollection("testCollection1");
        String word = fire.readCollection("testCollection1");
        System.out.println(word);
        fire.createDocument("testCollection1", "testDocument1");
        word = fire.readDocument("testCollection1", "testDocument1");
        System.out.println(word);
        fire.createDocument("testCollection1", "testDocument2");
        word = fire.readCollection("testCollection1");
        System.out.println(word);
        fire.deleteCollection("testCollection1");


    }
}
