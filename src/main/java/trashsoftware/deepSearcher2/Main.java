package trashsoftware.deepSearcher2;

import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.util.Log;

import java.io.PrintStream;

public class Main {
    
    static {
        System.setErr(System.out);
    }

    public static void main(String[] args) {
        try {
            Client.startClient();
        } catch (Exception e) {
            System.out.println("Caught");
            Log.severe(e);
            e.printStackTrace();
        }
    }
}
