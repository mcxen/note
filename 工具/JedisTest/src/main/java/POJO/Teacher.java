package POJO;

import java.util.List;

public class Teacher {
    private int id;
    private String name;

    private List<Student> students;

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", students=" + students +
                '}';
    }
}