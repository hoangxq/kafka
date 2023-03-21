package server3;

import model.Data;
import utils.KafkaUtils;
import utils.constants.KafkaConstants;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Server3 {

    public static void main(String[] args) throws Exception {

        while (true) {
            String message = readFile("src/main/java/server3/data/head.txt") + getData() +
                    readFile("src/main/java/server3/data/foot.txt");
            KafkaUtils.produceMessage(KafkaConstants.GET_DATA_TOPIC, KafkaConstants.BOOTSTRAP_SERVERS, message);
            Thread.sleep(2000);
        }

    }

    public static String readFile(String filename) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static String getData (){
        EntityManagerFactory emf = null;
        EntityManager entityManager = null;
        StringBuilder res = new StringBuilder();
        try {
            emf = Persistence.createEntityManagerFactory("jpa");
            entityManager = emf.createEntityManager();

            Query q = entityManager.createQuery("select d from Data d");
            List<Data> resultList = q.getResultList();
            System.out.println("num of data:" + resultList.size());

            for (Data next : resultList) {
                System.out.println("next data: " + next.getName());
                res.append("<li>").append(next.getName()).append("</li>");
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            entityManager.close();
            emf.close();
        }
        return res.toString();
    }
}
