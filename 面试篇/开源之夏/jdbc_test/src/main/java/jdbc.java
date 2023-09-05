import java.sql.SQLException;

public class Jdbc {
    static final String JDBCDriver = "org.postgresql.Driver";
    static final String Url = "jdbc:postgresql://弹性公网IP/数据库名?";
    static final String username = "root";
    static final String password = "Zz457178918";

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            //          知道我连接的数据库是 mysql
            Class.forName(JDBCDriver);
            System.out.println("连接数据库");            //连接数据库
            conn = DriverManager.getConnection(Url, username, password);
            System.out.println("创建数据库");            //获取执行的SQL的对象
            stmt = conn.createStatement();
            List<String> sql_create = new ArrayList<String>();
            sql_create.add("CREATE TABLE department" +
                    "(dept_name varchar(20)," +
                    "building varchar (15)," +
                    "budget numeric (12,2)," +
                    "primary key (dept_name))");            //执行DML语句，返回受影响的记录条数
            for (int i = 0; i < sql_create.size(); i++) {
                stmt.executeUpdate(sql_create.get(i));
            }
            System.out.println("成功");
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            
        }
    }


}
