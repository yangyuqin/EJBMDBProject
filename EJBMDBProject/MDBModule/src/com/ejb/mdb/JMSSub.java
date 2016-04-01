package com.ejb.mdb;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gao on 16-3-28.
 */
public class JMSSub {

    private static final String DEFAULT_USERNAME = "yuqin";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final Logger log = Logger.getLogger(JMSSub.class.getName());


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Topic topic = null;
        Context context = null;
        MessageConsumer consumer = null;

        try {
            log.info("����JNDI���ʻ�����ϢҲ��������Ӧ�÷���������������Ϣ!");
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
            context = new InitialContext(env);
            log.info("��ʼ��������,'JNDI��������','�����ṩ��URL','Ӧ���û����˻�','����'���.");

            log.info("����JMS���ӡ��Ự������!");
            connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
            connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("HelloWorldMDBTopic");

            consumer = session.createConsumer(topic);
            consumer.setMessageListener(new javax.jms.MessageListener() {
                public void onMessage(Message message) {
                    try {
                        TextMessage tm = (TextMessage) message;
                        System.out.println("���յ�����Ϣ����: " + tm.getText().toString());
                        System.out.println("JMSĿ�ĵ�: " + tm.getJMSDestination());
                        System.out.println("JMS�ظ�: " + tm.getJMSReplyTo());
                        System.out.println("JMS��ϢID��: " + tm.getJMSMessageID());
                        System.out.println("�Ƿ����½���: " + tm.getJMSRedelivered());
                    } catch (JMSException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            connection.start();

            //�ȴ�30���˳�
            CountDownLatch latch = new CountDownLatch(1);
            latch.await(100, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }
            // �ر����Ӹ���Ự,�����ߺͶ�����
            if (connection != null) {
                connection.close();
            }
        }
    }
}
