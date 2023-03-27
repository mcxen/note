import java.util.List;

public interface UserDao {
    /**
     * 查询所有
     * @return UserList
     */
    public List<User> getUsers();
}