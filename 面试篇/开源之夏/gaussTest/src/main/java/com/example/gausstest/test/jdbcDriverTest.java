package com.example.gausstest.test;
import java.sql.*;

public class jdbcDriverTest {
    public static void main(String[] args) {
        // JDBC连接信息
        String url = "jdbc:postgresql://192.168.161.18:5432/opengauss";
        String username = "opengauss";
        String password = "gauss@123";

        // 注册OpenGauss JDBC驱动程序
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // 连接成功，创建PreparedStatement对象以执行SQL语句
//            String sql = "select a.datname,b.rolname,string_agg(a.pri_t,',') from (select datname,(aclexplode(COALESCE(datacl, acldefault('d'::\"char\",datdba)))).grantee as grantee,(aclexplode(COALESCE(datacl, acldefault('d'::\"char\", datdba)))).privilege_type as pri_t from pg_database where datname not like 'template%') a,pg_roles b where (a.grantee=b.oid or a.grantee=0) and b.rolname='opengauss' group by a.datname,b.rolname;";
            String sql = "SELECT rolname,rolsuper,rolcreaterole,rolsystemadmin,rolauditadmin FROM pg_roles WHERE rolcreaterole = 'true';";
            PreparedStatement statement = connection.prepareStatement(sql);

            // 执行查询
            ResultSet resultSet = statement.executeQuery();

            // 处理查询结果
            while (resultSet.next()) {
                // 从结果集中获取数据
                String rolname = resultSet.getString("rolname");
                String rolsuper = resultSet.getString("rolsuper");
                String rolcreaterole = resultSet.getString("rolcreaterole");
                String rolsystemadmin = resultSet.getString("rolsystemadmin");
                String rolauditadmin = resultSet.getString("rolauditadmin");

                // 处理数据...
                System.out.println("rolname = " + rolname);
                System.out.println("rolsuper = " + rolsuper);
                System.out.println("rolcreaterole = " + rolcreaterole);
                System.out.println("rolsystemadmin = " + rolsystemadmin);
                System.out.println("rolauditadmin = " + rolauditadmin);
            }

            // 关闭结果集和PreparedStatement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
