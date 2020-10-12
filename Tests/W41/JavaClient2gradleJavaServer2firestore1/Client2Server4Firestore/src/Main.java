import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true){
            System.out.println("Give your next query");
            String word = in.nextLine();
            EchoClient echoClient = new EchoClient();
            echoClient.start(word);
        }
    }
}
