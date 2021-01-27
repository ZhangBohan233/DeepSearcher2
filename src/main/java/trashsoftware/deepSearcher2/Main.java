package trashsoftware.deepSearcher2;

import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.util.EventLogger;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            Client.startClient();
        } catch (Exception e) {
            EventLogger.log(e);
            e.printStackTrace();
        }
    }
}
