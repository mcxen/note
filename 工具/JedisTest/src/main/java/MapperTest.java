import DTO.UserDTO;
import POJO.Blog;
import POJO.User;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapperTest {
    @Test
    public void testByone() {
        // 获取SqlSession
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
//            User就是xml的名字，
            List<User> user = sqlSession.selectList("User.selectAll");

            Map<String, Object> map = new HashMap<>();
            map.put("id",5);
            map.put("username","haha");
            map.put("password","23333");

            sqlSession.insert("User.insert",map);
//            sqlSession.commit();
            User user2 = sqlSession.selectOne("User.selectByid",5);
            //上面是带参数查询

//            for (User user1 : user) {
//                System.out.println("user1 = " + user1);
//            }
            System.out.println(user2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
//
    }
    @Test
    public void testMap(){
        try {
            SqlSession sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println("connection = " + connection);
            List<User> maps = sqlSession.selectList("User.seMap");
//            for (User user : maps) {
//                System.out.println("user = " + user);
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMapperImple(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println("connection = " + connection);
//            User yes = new User(43, "232", "323232");
//            int insert = sqlSession.insert("UserMapper.insert", yes);

//            System.out.println(yes.getId());
            //结果为0

            //insert返回类型，代表插入的总数
            sqlSession.commit();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = mapper.selectAllMap();
//            List<User> users = sqlSession.selectList("UserMapper.selectAllMap");
            for (User user : users) {
                System.out.println("user = " + user);
            }
        } catch (Exception e) {
            if (sqlSession!=null)
                sqlSession.rollback();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDynamicSql(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title","yes");
        List<Blog> blogs = sqlSession.selectList("BlogMapper.findBlogsByBlog",map);
        for (Blog blog : blogs) {
            System.out.println("blog = " + blog);
        }
    }
}
