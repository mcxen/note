package com.mcx.gaussprivilege.test;
import java.sql.*;

public class jdbcDriverTest {
    public static void main(String[] args) {
        // JDBC连接信息
        String url = "jdbc:postgresql://192.168.1.49:123/opengauss";//远程ip连接没有用。
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
//            String sql = "SELECT rolname,rolsuper,rolcreaterole,rolsystemadmin,rolauditadmin FROM pg_roles WHERE rolcreaterole = 'true';";
            String sql ="SELECT * FROM (SELECT datname, grantee, privilege_type AS pri_t FROM (SELECT datname, (aclexplode(COALESCE(datacl, acldefault('d'::\"char\", datdba)))).grantee AS grantee, (aclexplode(COALESCE(datacl, acldefault('d'::\"char\", datdba)))).privilege_type AS privilege_type FROM pg_database WHERE datname NOT LIKE 'template%') subquery) a JOIN pg_roles b ON a.grantee = b.oid OR a.grantee = 0 WHERE b.rolname NOT LIKE 'gs%' GROUP BY a.datname, b.rolname;";
            PreparedStatement statement = connection.prepareStatement(sql);
            /*

             */
            // 执行查询
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String user = resultSet.getString("rol_name");
                String table_name = resultSet.getString("table_name");
                String privileges = resultSet.getString("privileges");

                System.out.println("rol_name: " + user);
                System.out.println("Table Name: " + table_name);
                System.out.println("Privileges: " + privileges);
                System.out.println("-----------------------------");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
