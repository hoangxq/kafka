package server4;

import model.Data;
import utils.KafkaUtils;
import utils.constants.KafkaConstants;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Server4 {

    public static void main(String[] args) throws InterruptedException {

        while (true) {
            String message = KafkaUtils.consumeMessage(KafkaConstants.INSERT_DATA_TOPIC, KafkaConstants.GROUP_ID, KafkaConstants.BOOTSTRAP_SERVERS);
            insertData(message);
            Thread.sleep(2000);
        }

    }

    public static void insertData(String message) {
        EntityManagerFactory emf = null;
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            emf = Persistence.createEntityManagerFactory("jpa");
            entityManager = emf.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Data data = new Data(message);

            entityManager.persist(data);
            transaction.commit();

        } catch (Exception e) {
            assert transaction != null;
            transaction.rollback();
        } finally {
            assert entityManager != null;
            entityManager.close();
            emf.close();
        }
    }
}
