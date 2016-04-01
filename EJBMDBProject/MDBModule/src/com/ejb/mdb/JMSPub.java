package com.ejb.mdb;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by gao on 16-3-28.
 */
public class JMSPub {
    private static final String DEFAULT_USERNAME = "yuqin";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "/jms/topic/HelloWorldMDBTopic";
    private static final Logger log = Logger.getLogger(JMSPub.class.getName());

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Topic topic = null;
        Context context = null;
        MessageProducer producer = null;
        BufferedReader msgStream = null;

        try {
            log.info("����JNDI���ʻ�����ϢҲ��������Ӧ�÷���������������Ϣ!");
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
            context = new InitialContext(env);
            log.info("��ʼ��������,'JNDI��������','�����ṩ��URL','Ӧ���û����˻�','����'���.");

            log.info("��ȡ���ӹ���!");
            connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
            log.info("����JMS���ӡ��Ự������!");
            connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = (Topic) context.lookup(DEFAULT_DESTINATION);
            producer = session.createProducer(topic);

            msgStream = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            boolean quitNow = false;
            do {
                System.out.print("����Ҫ���͵���Ϣ��(����0�˳�)");
                line = msgStream.readLine();
                if (line != null && line.trim().length() != 0) {
                    TextMessage textMessage = session.createTextMessage();
                    textMessage.setText(line);
                    producer.send(textMessage);
                    quitNow = line.equalsIgnoreCase("0");
                }
            } while (!quitNow);

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
