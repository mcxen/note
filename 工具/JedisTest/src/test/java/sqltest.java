import POJO.Blog;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

public class sqltest {
    @Test
    public void test() throws IOException {
        //Reader家在xml核心配置
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
//        初始化SqlSessionFactory解析xml文件。
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();//SqlSession是jDBC的扩展类
        Connection connection = sqlSession.getConnection();
        System.out.println(connection);
    }

}
