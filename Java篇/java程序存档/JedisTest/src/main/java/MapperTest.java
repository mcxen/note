import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class MapperTest {
    @Test
    public void test() {
        // 获取SqlSession
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
//            User就是xml的名字，
            List<User> user = sqlSession.selectList("User.selectAll");
            for (User user1 : user) {
                System.out.println("user1 = " + user1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
//
    }
}
