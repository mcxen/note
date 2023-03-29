package POJO;

import java.util.Date;

public class Blog {
    private int id;
    private String title;
    private String author;
    private Date createTime;
    private int views;

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", createTime=" + createTime +
                ", views=" + views +
                '}';
    }
}