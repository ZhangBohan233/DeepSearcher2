package trashsoftware.deepSearcher2;

import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.util.EventLogger;

public class Main {

    public static void main(String[] args) {
        try {
            Client.startClient();
        } catch (Exception e) {
            System.out.println("Caught");
            EventLogger.log(e);
            e.printStackTrace();
        }
    }
}
