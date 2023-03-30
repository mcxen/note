import POJO.User;

import java.util.List;

public interface UserMapper {
//    public List<User> selectAll();
    public List<User> selectAllMap();
    public List<User> selectPage();

}
