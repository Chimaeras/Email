package hundsun.email.Entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Joker
 */
@Data
public class Student implements Serializable {
    private String name;
    private Integer age;
    private Integer score;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", score=" + score +
                '}';
    }

}
