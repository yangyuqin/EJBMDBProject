package com.ejb.mdb;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gao on 16-3-28.
 */
public class JMSConsumer {
    private static final Logger log = Logger.getLogger(JMSConsumer.class.getName());
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "/jms/queue/HelloWorldMDBQueue";
    private static final String DEFAULT_USERNAME = "yuqin";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    private static final int WAIT_COUNT = 5;

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        Destination destination = null;
        TextMessage message = null;
        Context context = null;
        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
            context = new InitialContext(env);
            connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
            destination = (Destination) context.lookup(DEFAULT_DESTINATION);
            connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(destination);
            connection.start();
            // �ȴ�30���˳�
            CountDownLatch latch = new CountDownLatch(1);
            log.info("��ʼ��JBOSS�˽�����Ϣ-----");
            int i = 0;
            for (; i < WAIT_COUNT; i++) {
                if (message != null) {
                    log.info("���յ�����Ϣ������:" + message.getText());
                    i = 0;
                }
                log.info("��ʼ��JBOSS�˽�����Ϣ-----");
                message = (TextMessage) consumer.receive(5000);
                latch.await(1, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
