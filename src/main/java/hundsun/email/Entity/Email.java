package hundsun.email.Entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author Joker
 */
@Data
@Component
@ConfigurationProperties("email")
public class Email {

    /**
     * 数据脱敏：被transient修饰的字段不会被序列化
     */
    // transient String password;

    private HashMap<String, String> msg;
    /**
     * 授权码
     */
    transient String password;

    /**
     * 发送邮箱地址
     */
    private String fromAddress;

    /**
     * 收件邮箱地址
     */
    private String toAddress;

    /**
     * 邮件模板地址
     */
    private String templateFileAddress;
}
