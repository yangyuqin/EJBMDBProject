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
public class JMSProducer {
    private static final Logger log = Logger.getLogger(JMSProducer.class.getName());
    private static final String DEFAULT_MESSAGE = "����JMS��Ϣ.....";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "/jms/queue/HelloWorldMDBQueue";
    private static final String DEFAULT_MESSAGE_COUNT = "10";
    private static final String DEFAULT_USERNAME = "yuqin";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    public static void main(String[] args) throws Exception {
        Context context=null;
        Connection connection=null;
        try {
            // ���������ĵ�JNDI����
            log.info("����JNDI���ʻ�����ϢҲ��������Ӧ�÷���������������Ϣ!");
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);// ��KEY��ֵΪ��ʼ��Context�Ĺ�����,JNDI����������
            env.put(Context.PROVIDER_URL, PROVIDER_URL);// ��KEY��ֵΪContext�����ṩ�ߵ�URL.���������ṩ�ߵ�URL
            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);//Ӧ���û��ĵ�¼��,����.
            // ��ȡ��InitialContext����.
            context = new InitialContext(env);
            log.info("��ʼ��������,'JNDI��������','�����ṩ��URL','Ӧ���û����˻�','����'���.");
            log.info("��ȡ���ӹ���!");
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
            log.info("��ȡĿ�ĵ�!");
            Destination destination = (Destination) context.lookup(DEFAULT_DESTINATION);
            // ����JMS���ӡ��Ự�������ߺ�������
            connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            connection.start();
            int count = Integer.parseInt(DEFAULT_MESSAGE_COUNT);
            // �����ض���Ŀ����Ϣ
            TextMessage message = null;
            for (int i = 0; i < count; i++) {
                message = session.createTextMessage(DEFAULT_MESSAGE);
                producer.send(message);
                log.info("message:"+message);
                log.info("message:"+DEFAULT_MESSAGE);
            }
            // �ȴ�30���˳�
            CountDownLatch latch = new CountDownLatch(1);
            latch.await(30, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }
            // �ر����Ӹ���Ự,�����̺�������
            if (connection != null) {
                connection.close();
            }
        }
    }
}
