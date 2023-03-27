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
            List<Map> maps = sqlSession.selectList("User.selectAllMap");
            for (Map map : maps) {
                System.out.println("map = " + map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
