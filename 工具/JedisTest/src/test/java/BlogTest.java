import DAO.blogDAO;
import POJO.Blog;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.*;

public class BlogTest {
    @Test
    public void testBatch(){
//        批处理
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
        ArrayList<Blog> blogs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Blog blog = new Blog();
            blog.setId(i+19);
            blog.setTitle("测试"+i);
            blog.setAuthor("测试"+i);
            blog.setViews(i+19);
            blog.setCreateTime(new Date());
            blogs.add(blog);
        }
        sqlSession.insert("BlogMapper.batchInsert",blogs);

        sqlSession.commit();
        HashMap<String, Object> map = new HashMap<>();
//        map.put("title","yes");
        List<Blog> blogss = sqlSession.selectList("BlogMapper.findBlogsByBlog",map);
        for (Blog blog : blogss) {
            System.out.println("blog = " + blog);
        }
    }
    @Test
    public void testAnno(){
//        测试注解开发
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
//        这里的insert select之类的都是基于xml方式的调用

        blogDAO blogdao = sqlSession.getMapper(blogDAO.class);
        List<Blog> blogs = blogdao.selectAllUnder(5);
        for (Blog blog : blogs) {
            System.out.println("blog = " + blog);
        }

        Blog blog = new Blog();
        blog.setId(93);
        blog.setTitle("测试");
        blog.setAuthor("测试");
        blog.setViews(19);
        blog.setCreateTime(new Date());
        int insertResult = blogdao.insertBlog(blog);
        sqlSession.commit();
        System.out.println("insertResult = " + insertResult);
    }
}
