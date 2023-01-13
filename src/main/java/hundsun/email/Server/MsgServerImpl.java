package hundsun.email.Server;

import hundsun.email.Entity.Email;
import hundsun.email.Entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Node;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Joker
 */
@Service
public class MsgServerImpl {

    // 是否需要定义序列化ID
    // private static final long serialVersionUID = -4392658638228508589L;

    @Autowired
    private Email email;

    public String receiveMsg() throws IOException {
        String fileName = "TEST_E_IntraDayNPVAlert_all.xml";
        HashMap<String, String> map = new HashMap<>();
        map.put("alert_value", "100.00");
        map.put("datetime", "2023-01-10");
        String content = readEmailTemplate(fileName, map);
        String subject = "test";
        return sendEmail(subject, content);

    }

    public String sendEmail(String emailSubject, String emailContent) {
        // 收件人电子邮箱
        String to = email.getToAddress();
        // 发件人电子邮箱
        String from = email.getFromAddress();
        // 授权码
        String password = email.getPassword();

        // 1、创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();
        // 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
        props.setProperty("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.host", "smtp.163.com");
        // 需要经过授权，也就是用户名和密码的校验，这样才能通过验证
        props.put("mail.smtp.auth", "true");

        // 2、根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        // true 在控制台（console)上看到发送邮件的过程
        session.setDebug(true);

        // 3、 创建一封复杂邮件（文本+附件）
        try {
            // 3.1. 创建邮件对象
            MimeMessage message = new MimeMessage(session);
            // 3.2. From: 发件人
            message.setFrom(new InternetAddress(from));
            // 3.3. To: 收件人（可以增加多个收件人）
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // 3.4. To: 收件人（可以增加多个抄送）
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(to));
            // 3.5. Subject: 邮件主题
            message.setSubject(emailSubject);
            // 3.6. 邮件内容
            message.setText(emailContent);

//            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
//            MimeMultipart multipart = new MimeMultipart();
//            // 设置邮件的文本内容
//            MimeBodyPart contentPart = new MimeBodyPart();
//            contentPart.setContent(emailContent, "text/html;charset=utf-8");
//            multipart.addBodyPart(contentPart);

            // 3.7. 邮件附件
//            String attPath = "D:\\data\\ftpUpload\\2020\\12\\10\\盖章指令文件.zip";
//            MimeBodyPart attachment = new MimeBodyPart();
//            DataHandler dh = new DataHandler(new FileDataSource(attPath)); // 读取本地文件
//            attachment.setDataHandler(dh); // 将附件数据添加到“节点”
//            attachment.setFileName(MimeUtility.encodeText(dh.getName())); // 设置附件的文件名（需要编码）
//            multipart.addBodyPart(attachment);
//            multipart.setSubType("mixed"); // 混合关系
//            // 3.8. 设置整个邮件的关系（将最终的混合“节点”作为邮件的内容添加到邮件对象）
//            message.setContent(multipart);

            // 3.9. 设置发件时间
            message.setSentDate(new Date());
            // 3.10. 保存上面的所有设置
            message.saveChanges(); // 保存变化
            // 四、 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport("smtp");

            // 五、 使用 邮箱账号 和 授权码 连接邮件服务器
            transport.connect("smtp.163.com", from, password);

            // 六、 发送邮件,
            transport.sendMessage(message, message.getAllRecipients());

            // 七、 关闭连接
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    /**
     * 读取信件模板
     *
     * @param fileName 文件名称
     * @param map      传入参数
     * @return 填入param后的Email内容
     */
    public String readEmailTemplate(String fileName, HashMap<String, String> map) {
        try {
            // 创建解析器工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            // 创建Document对象
            String baseDir = email.getTemplateFileAddress();
            Document doc = db.parse(baseDir + fileName);
            // 获取节点名称
            NodeList nodeList = doc.getElementsByTagName("NotificationTemplate");
            // 获取节点属性和字段
            String encoding = "";
            String enabled = "";
            String subject = "";
            String content = "";
            String others = "";

            // 获取当前节点
            org.w3c.dom.Node email = nodeList.item(0);
            // 获取子节点
            NodeList childNodes = email.getChildNodes();
            for (int k = 0; k < childNodes.getLength(); k++) {
                // 判断是否为节点
                if (childNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println(childNodes.item(k).getNodeName());
                    // 获取不同节点属性
                    switch (childNodes.item(k).getNodeName()) {
                        case "encoding":
                            encoding = childNodes.item(k).getTextContent();
                            break;
                        case "enabled":
                            enabled = childNodes.item(k).getTextContent();
                            break;
                        case "subject":
                            subject = childNodes.item(k).getTextContent();
                            break;
                        case "content":
                            content = childNodes.item(k).getTextContent();
                            break;
                        default:
                            others = childNodes.item(k).getTextContent();
                            break;
                    }
                }
            }
            // 填入param
            content = insertParam(content, map);
            System.out.println(content);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 填入param
     *
     * @param s   Email填充前内容
     * @param map 待填充参数
     * @return Email填充param后的内容
     */
    public String insertParam(String s, HashMap<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            s = s.replaceAll("\\{" + entry.getKey() + "}", entry.getValue());
        }
        return s;
    }

    /**
     * 序列化
     */
    public static void serialize() throws IOException {
        Student student = new Student();
        student.setName("linko");
        student.setAge(18);
        student.setScore(1000);

        ObjectOutputStream outputStream =
                new ObjectOutputStream(Files.newOutputStream(new File("student.txt").toPath()));
        outputStream.writeObject(student);
        outputStream.close();

        System.out.println("序列化成功！已经生成student.txt文件");
        System.out.println("==============================================");
    }

    /**
     * 反序列化
     */
    public static void deserialize() throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream =
                new ObjectInputStream(Files.newInputStream(new File("student.txt").toPath()));
        Student student = (Student) inputStream.readObject();
        inputStream.close();

        System.out.println("反序列化结果为：");
        System.out.println(student);
    }

}
