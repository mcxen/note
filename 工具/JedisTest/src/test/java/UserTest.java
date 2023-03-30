import POJO.User;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class UserTest {
    @Test
//    测试分页查询
    public void testPageHelper(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println("connection = " + connection);
            sqlSession.commit();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            PageHelper.startPage(1,2);
//            范型指名为User对象
            Page<User> page = (Page) mapper.selectPage();
            System.out.println(page.getPages());
            List<User> users = page.getResult();
//            getResult是获取当前页面的结果
            for (User user : users) {
                System.out.println("user = " + user);
            }
        } catch (Exception e) {
            if (sqlSession!=null)
                sqlSession.rollback();
            throw new RuntimeException(e);
        }
    }
}
