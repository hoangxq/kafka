package server2;

import utils.KafkaUtils;
import utils.constants.KafkaConstants;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Server2 {

    public static void main(String[] args) throws Exception {

        KafkaUtils.produceMessage(KafkaConstants.GET_HTML_TOPIC, KafkaConstants.BOOTSTRAP_SERVERS, readFile("src/main/java/server2/index.html"));

    }

    public static String readFile(String filename) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}
