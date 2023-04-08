import org.junit.Test;
import redis.clients.jedis.Jedis;

public class jedistest {
    @Test
    public void ttt(){
        Jedis jedis = new Jedis("47.113.146.223");
        jedis.select(2);
        String name = jedis.getSet("name","chinae");
        System.out.println("name = " + jedis.getSet("name","chinae"));
        jedis.set("sn","1992-2992");
        System.out.println(jedis.get("sn"));
        System.out.println(jedis.keys("*"));
        jedis.close();
    }
}
