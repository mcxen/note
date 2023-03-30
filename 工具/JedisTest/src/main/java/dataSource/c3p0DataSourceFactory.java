package dataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

public class c3p0DataSourceFactory extends UnpooledDataSourceFactory {
    public c3p0DataSourceFactory() {
        //this的这个是父类提供的，Combo是c3p0，就是有c3p0，其他的数据源也是继承这个Unpoll这个数据源
        this.dataSource = new ComboPooledDataSource();
    }
}
