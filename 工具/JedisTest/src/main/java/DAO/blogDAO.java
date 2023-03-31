package DAO;

import POJO.Blog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

public interface blogDAO {
    @Select("select * from blog")
    public List<Blog> selectAll();

    @Select("select * from blog where id < #{max}")
    public List<Blog> selectAllUnder(@Param("max") int max);

    @Insert("INSERT INTO blog(id,title,author,create_time,views) values(#{id},#{title},#{author},#{createTime},#{views})")
    @SelectKey(statement = "select last_insert_id()", before = false, keyProperty = "id",resultType = Integer.class)
    public int insertBlog(Blog blog);
}
