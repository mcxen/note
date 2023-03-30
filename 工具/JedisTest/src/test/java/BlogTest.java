import POJO.Blog;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
}
