# 序言

> 银鞍照白马，飒沓如流星。





# Fastjson基本概念&组件

Fastjson是Alibaba开发的Java语言编写的高性能JSON库，用于将数据在JSON和Java Object之间互相转换，提供两个主要接口*JSON.toJSONString*和*JSON.parseObject/JSON.parse*来分别实现序列化和反序列化操作。

[项目地址](https://github.com/alibaba/fastjson)

组件api使用方法也很简洁

```
//序列化
String text = JSON.toJSONString(obj); 
//反序列化
JSON.parse(); //解析为JSONObject类型或者JSONArray类型
JSON.parseObject("{...}"); //JSON文本解析成JSONObject类型
JSON.parseObject("{...}", VO.class); //JSON文本解析成VO.class类
```

# 使用Fastjson进行（反）序列化

以下使用测试均是基于1.2.24版本的fastjson jar包

建议从[maven仓库](https://mvnrepository.com/artifact/com.alibaba/fastjson/1.2.24)中找到所有版本jar包,方便漏洞复现。

```xml
    <dependencies>
        <!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.24</version>
        </dependency>
    </dependencies>
```



先构建需要序列化的Student类：

```

public class Student {
    private String name;
    private int age;

    public Student() {
        System.out.println("构造函数");
    }

    public String getName() {
        System.out.println("getName");
        return name;
    }

    public void setName(String name) {
        System.out.println("setName");
        this.name = name;
    }

    public int getAge() {
        System.out.println("getAge");
        return age;
    }

    public void setAge(int age) {
        System.out.println("getAge");
        this.age = age;
    }
}//执行那个函数就会打印出来
```

再使用Fastjson组件：

```
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FJSerializeTest {
    public static void main(String[] args) {
        Student student = new Student();
        student.setName("0range");
        student.setAge(18);

        //序列化
        String serializedStr = JSON.toJSONString(student);
        System.out.println("serializedStr="+serializedStr);

        System.out.println("----------");

        //通过parse方法进行反序列化，返回的是一个JSONObject对象
        Object obj1 = JSON.parse(serializedStr);
        System.out.println("parse反序列化对象名称:"+obj1.getClass().getName());
        System.out.println("parse反序列化："+obj1);

        System.out.println("----------");

        //通过parseObject,不指定类，返回的是一个JSONObject
        Object obj2 = JSON.parseObject(serializedStr);
        System.out.println("parseObject反序列化对象名称:"+obj2.getClass().getName());
        System.out.println("parseObject反序列化:"+obj2);

        System.out.println("----------");

        //通过parseObject,指定类后返回的是一个相应的类对象
        Object obj3 = JSON.parseObject(serializedStr,Student.class);
        System.out.println("parseObject反序列化对象名称:"+obj3.getClass().getName());
        System.out.println("parseObject反序列化:"+obj3);
    }
}
```

以上使用了一种序列化，三种反序列化
结果如下：

```
构造函数
setName
setAge
getAge
getName
serializedStr={"age":18,"name":"0range"}
----------
parse反序列化对象名称:com.alibaba.fastjson.JSONObject
parse反序列化：{"name":"0range","age":18}
----------
parseObject反序列化对象名称:com.alibaba.fastjson.JSONObject
parseObject反序列化:{"name":"0range","age":18}
----------
构造函数
setAge
setName
parseObject反序列化对象名称:FastjsonTmpl.Student
parseObject反序列化:FastjsonTmpl.Student@78e03bb5
```

**这里面parseObject()其实就是parse()的一个封装**，对于parse的结果进行一次结果判定然后cast为JSONObject类型。

源码如下：

```
public static JSONObject parseObject(String text) {
        Object obj = parse(text);
        return obj instanceof JSONObject ? (JSONObject)obj : (JSONObject)toJSON(obj);
}
```

# Fastjson基础架构图

[![image-20220331093719887](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20220331093719887.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20220331093719887.png)

[image-20220331093719887](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20220331093719887.png)



JSON：门面类，提供入口

DefaultJSONParser：主类

ParserConfig：配置相关类

JSONLexerBase：字符分析类

JavaBeanDeserializer：JavaBean反序列化类

# 序列化配置：SerializerFeature.WriteClassName

SerializerFeature.WriteClassName，是JSON.toJSONString()中的一个设置属性值，设置之后在序列化的时候会多写入一个@type，即写上被序列化的类名，type可以指定反序列化的类，并且调用其getter/setter/is方法。

Fastjson接受的JSON可以通过@type字段来指定该JSON应当还原成何种类型的对象，在反序列化的时候方便操作。

Demo：

```
public class FJTest {
    public static void main(String[] args){
        Student student = new Student();
        student.setName("0range");
        student.setAge(18);
        String jsonstring = JSON.toJSONString(student, SerializerFeature.WriteClassName);
        System.out.println(jsonstring);
    }
}
```

输出如下：

```
//设置了SerializerFeature.WriteClassName
构造函数
setName
setAge
getAge
getName
{"@type":"FastjsonTmpl.Student","age":18,"name":"0range"}

// 未设置SerializerFeature.WriteClassName
构造函数
setName
setAge
getAge
getName
{"age":18,"name":"0range"}
```

执行反序列化：

```
public class FJTest2 {
    public static void main(String[] args){
        String jsonstring ="{\"@type\":\"FastjsonTmpl.Student\",\"age\":18,\"name\":\"0range\"}";
        Student obj = JSON.parseObject(jsonstring, Student.class, Feature.SupportNonPublicField);
        System.out.println(obj);
        System.out.println(obj.getClass().getName());
    }
}
```

结果如下：

```
构造函数
setAge
setName
FastjsonTmpl.Student@4bf558aa
FastjsonTmpl.Student
```

# 反序列化配置：Feature.SupportNonPublicField

如果需要还原出private属性的话，还需要在反序列化组件JSON.parseObject/JSON.parse中加上Feature.SupportNonPublicField参数。

这里改下Student类，将私有属性age的setAge()函数注释掉（一般没人会给私有属性加setter方法，加了就没必要声明为private了）：

```
public class Student2 {
    private String name;
    private int age;

    public Student() {
        System.out.println("构造函数");
    }

    public String getName() {
        System.out.println("getName");
        return name;
    }

    public void setName(String name) {
        System.out.println("setName");
        this.name = name;
    }

    public int getAge() {
        System.out.println("getAge");
        return age;
    }

//    public void setAge(int age) {
//        System.out.println("setAge");
//        this.age = age;
//    }
}
```

现在这两个都是private属性了，看看接下来会是什么结果。

修改FJTest2.java，去掉Feature.SupportNonPublicField，添加输出两个属性getter方法的返回值：

```
public class FJTest2 {
    public static void main(String[] args){
        String jsonstring ="{\"@type\":\"FastjsonTmpl.Student2\",\"age\":18,\"name\":\"0range\"}";
        Student2 obj = JSON.parseObject(jsonstring, Student2.class);
        System.out.println(obj);
        System.out.println(obj.getClass().getName());
        System.out.println(obj.getName() + " " + obj.getAge());
    }
}
```

结果如下：

```
构造函数
setName
FastjsonTmpl.Student2@4bf558aa
FastjsonTmpl.Student2
getName
getAge
0range 0
```

可以看到，由于没有setage方法，年龄结果是0。

再接着添加Feature.SupportNonPublicField：

```
Student2 obj = JSON.parseObject(jsonstring, Student2.class, Feature.SupportNonPublicField);
```

结果：

```
构造函数
setName
Student@2c59109c
Student
getName
getAge
0range 18
```

这里写一下理解：

JSON对象其实就是一个String字符串，那么在反序列化的时候，其实更像是用类去套出对象的信息，这里我认为比较像socket套接字的感觉，对于传进来的网络流，其实就是用socket库按照字段长度去“迎接”。

第一个**没有**设置Feature.SupportNonPublicField的情况下，其实也是正常去套，但是由于没有setage方法去“迎接”，那么默认就会将private塑形age初始化为0。

但是第二种情况下设置了Feature.SupportNonPublicField，那么其实就是更加暴力一点，我认为他会不管你有没有迎接我的函数，我直接把age=18写到字节码，摁到对应的对象属性中，private也拦不住我。

也就是说，若想让传给JSON.parseObject()进行反序列化的JSON内容指向的对象类中的私有变量成功还原出来，则需要在调用JSON.parseObject()时加上Feature.SupportNonPublicField这个属性设置才行。

# setter/getter 的触发条件

## 小结

- 使用JSON.parse(jsonstr)和JSON.parseObject(jsonstr, xxx.class)两种方式返回的结果相同:

  **无参构造函数+JSON字符串中指定属性的setter()+特殊的getter()**

根据前面的结果，有如下结论：

- 当反序列化为`JSON.parseObject(*,*.class)`形式即指定class时，调用反序列化得到的类的无参构造函数、JSON里面的指定属性的setter方法、**特殊**的getter()；

- 当反序列化为`JSON.parseObject(*)`形式即未指定class时，会调用反序列化得到的类的无参构造函数、JSON里面的指定属性的setter方法，**所有属性**的getter方法，无视访问修饰符；

- 原因：

  `JSON.parseObject(*,*.class)`形式得到的都是特定的要求类；

  当反序列化为`JSON.parseObject(*)`形式得到的都是JSONObject类对象，会额外调用`JSON.toJSON()`方法，会无差别调用所有getter。

**下面直接引用结论，Fastjson会对满足下列要求的setter/getter方法进行调用：**

满足条件的setter：

- 函数名长度大于4
- 以set开头
- 非静态函数
- 返回类型为void或当前类
- 参数个数为1个

**特殊**的getter：

- 函数名长度大于等于4
- 非静态函数
- 以get开头且第4个字母为大写
- 无参数
- **返回值类型继承自Collection || Map || AtomicBoolean || AtomicInteger || AtomicLong**
- **对应的属性，没有setter才会调用getter**

## demo

这里我继续进行测试，跟进去看看getter和setter的区别；

首先上代码：

```
public class tianrongxinTest {
    public String t1;
    private int t2;
    private Boolean t3;
    private Properties t4;
    private Properties t5;

    public tianrongxinTest(){
        System.out.println("tianrongxinTest() is called!");
    }

    public String getT1() {
        System.out.println("getT1() is called!");
        return t1;
    }

    public void setT1(String t1) {
        System.out.println("setT1() is called!");
        this.t1 = t1;
    }

    public int getT2() {
        System.out.println("getT2() is called!");
        return t2;
    }

    public void setT2(int t2) {
        System.out.println("setT2() is called!");
        this.t2 = t2;
    }

    public Boolean getT3() {
        System.out.println("getT3() is called!");
        return t3;
    }

    public void setT3(Boolean t3) {
        System.out.println("setT3() is called!");
        this.t3 = t3;
    }

    public Properties getT4() {
        System.out.println("getT4() is called!");
        return t4;
    }

    public void setT4(Properties t4) {
        System.out.println("setT4() is called!");
        this.t4 = t4;
    }

    public Properties getT5() {
        System.out.println("getT5() is called!");
        return t5;
    }

    public void setT5(Properties t5) {
        System.out.println("setT5() is called!");
        this.t5 = t5;
    }

    @Override
    public String toString() {
        return "tianrongxinTest{" +
                "t1='" + t1 + '\'' +
                ", t2=" + t2 +
                ", t3=" + t3 +
                ", t4=" + t4 +
                ", t5=" + t5 +
                '}';
    }

    public static void main(String[] args) {
        String jsonstr = "{\"@type\":\"FKtemp.tianrongxinTest\",\"t1\":\"t1\",\"t2\":2,\"t3\":1,\"t4\":{},\"t5\":{}}";
        Object obj = JSON.parse(jsonstr);
        //Object obj =JSON.parseObject(jsonstr, tianrongxinTest.class);
        //Object obj =JSON.parseObject(jsonstr);
        String result = obj.toString();
        System.out.println(result);
    }
}
```

这里参考了天融信的博客，进行了本地测试。

```
Object obj = JSON.parse(jsonstr);
Object obj = JSON.parseObject(jsonstr, tianrongxinTest.class);
Object obj = JSON.parseObject(jsonstr);
```

### 经验1

关于1和2来说，二者后面的调用链是完全一样的。二者不同点在于调用JavaBeanInfo.build()方法时传入clazz参数的来源不同；

JSON.parseObject(jsonstr,tianrongxinTest.class)在调用JavaBeanInfo.build() 方法时传入的clazz参数源于parseObject方法中第二个参数中指定的“tianrongxinTest.class”。

[![image-20200716205735592](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200716205735592.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716205735592.png)

[image-20200716205735592](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716205735592.png)



JSON.parse(jsonstr);这种方式调用JavaBeanInfo.build() 方法时传入的clazz参数获取于json字符串中@type字段的值，具体来源在

parseObject:367, DefaultJSONParser (com.alibaba.fastjson.parser)这里：

[![img](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/640.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/640.png)

[img](https://fynch3r.github.io/images/Fastjson学习笔记01/640.png)



因此，只要Json字符串的@type字段值与JSON.parseObject(jsonstr, FastJsonTest.class);中第二个参数中类名一致，这两种方式执行的过程与结果是完全一致的。二者唯一的区别就是获取clazz参数的途径不同。

[![img](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/640-20200716205831059.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716205831059.png)

[img](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716205831059.png)



### 经验2

关于getter和setter的触发来说，1.2.24是这样的；

这个问题要从**JavaBeanInfo.build()** 方法中获取答案：

程序会使用JavaBeanInfo.build() 方法对传入的JSON字符串进行解析。在JavaBeanInfo.build() 方法中，程序将会创建一个fieldList数组来存放后续将要处理的目标类的 setter 方法及某些特定条件的 getter 方法。通过上文的结果可见，**目标类中所有的setter方法都可以被调用**，程序从clazz（目标类对象）中通过getMethods获取本类以及父类或者父接口中所有的公共方法，接着进行循环判断这些方法是否可以加入fieldList中以便后续处理。

[![img](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/640-20200716205938671.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716205938671.png)

[img](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716205938671.png)



[![image-20200716210011363](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200716210011363.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716210011363.png)

[image-20200716210011363](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716210011363.png)



理解：

对于所有的setter方法，Fastjson都直接给装填到fieldList中，再之后遍历getter时候，如果发现有对应的setter，那么就不执行getter方法了。

举几个例子：

[![image-20200716210355394](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200716210355394.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716210355394.png)

[image-20200716210355394](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716210355394.png)



[![image-20200716210543645](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200716210543645.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716210543645.png)

[image-20200716210543645](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716210543645.png)



经验3:

这里执行第三个：`Object obj =JSON.parseObject(jsonstr);`

执行结果：

[![image-20200716211056146](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200716211056146.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716211056146.png)

[image-20200716211056146](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200716211056146.png)



并且，返回值变成JSON对象了！

通过上文两种方式从执行流程几乎一样，结果也完全相同；然而使用JSON.parseObject(jsonstr)这种方式，执行的结果与返回值却与前两者不同：JSON.parseObject(jsonstr)返回值为JSONObject类对象，且将FastJsonTest类中的所有getter与setter都被调用。

通过阅读源码可以发现JSON.parseObject(String text)实现如下：

[![img](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/640-20200716211916696.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211916696.png)

[img](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211916696.png)



parseObject(String text)其实就是先执行了parse(),随后将返回的Java对象通过JSON.toJSON()转为 JSONObject对象。

JSON.toJSON()方法会将目标类中所有getter方法记录下来，见下图：

[![img](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/640-20200716211931362.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211931362.png)

[img](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211931362.png)



随后通过反射依次调用目标类中所有的getter方法：

[![img](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/640-20200716211941499.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211941499.png)

[img](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211941499.png)



完整的调用链如下：

[![img](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/640-20200716211950951.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211950951.png)

[img](https://fynch3r.github.io/images/Fastjson学习笔记01/640-20200716211950951.png)



总结：

上文例子中，JSON.parse(jsonstr)与JSON.parseObject(jsonstr, FastJsonTest.class)可以认为是完全一样的，而parseObject(String text)是在二者的基础上又执行了一次JSON.toJSON()；

[![image-20220331155550573](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20220331155550573.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20220331155550573.png)

[image-20220331155550573](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20220331155550573.png)



# parse与parseObject区别

前面的demo都是用parseObject()演示的，还没说到parse()。两者主要的区别就是带`@type`的时候，parseObject()返回的是JSONObject而parse()返回的是实际类型的对象.

当在没有对应类的定义的情况下，一般情况下都会使用JSON.parseObject()来获取数据。

> FastJson中的 parse() 和 parseObject()方法都可以用来将JSON字符串反序列化成Java对象，parseObject() 本质上也是调用 parse() 进行反序列化的。但是 parseObject() 会额外的将Java对象转为 JSONObject对象，即 JSON.toJSON()。所以进行反序列化时的细节区别在于，parse() 会识别并调用目标类的 setter 方法及某些特定条件的 getter 方法，而 parseObject() 由于多执行了 JSON.toJSON(obj)，所以在处理过程中会调用反序列化目标类的所有 setter 和 getter 方法。

看一下parseObject(String text)源码：

```
public static JSONObject parseObject(String text) {
        Object obj = parse(text);
        return obj instanceof JSONObject ? (JSONObject)obj : (JSONObject)toJSON(obj);
    }
```

parseObject({..})其实就是parse({..})的一个封装，对于parse的结果进行一次结果判定然后转化为JSONObject类型。

也就是说，我们用parse()反序列化会直接得到特定的类，而无需像parseObject()一样返回的是JSONObject类型的对象、还可能需要去设置第二个参数指定返回特定的类。

结论：

- parse(“”) 会调用全部 setter 方法及某些特定条件的 getter 方法
- parseObject(“”, xxx.class) 会识别并调用目标类的全部 setter 方法及某些特定条件的 getter 方法
- parseObject(“”) 会调用全部setter/getter

这里我再举一个例子，先建立一个序列化实验用的Person类：

```
public class Person {
    //属性
    public String name;
    private String full_name;
    private int age;
    private Boolean sex;
    private Properties prop;
    //构造函数
    public Person(){
        System.out.println("Person构造函数");
    }
    //set
    public void setAge(int age){
        System.out.println("setAge()");
        this.age = age;
    }
    //get 返回Boolean
    public Boolean getSex(){
        System.out.println("getSex()");
        return this.sex;
    }
    //get 返回ProPerties
    public Properties getProp(){
        System.out.println("getProp()");
        return this.prop;
    }
    //在输出时会自动调用的对象ToString函数
    @Override
    public String toString() {
        String s = "[Person Object] name=" + this.name + " full_name=" + this.full_name  + ", age=" + this.age + ", prop=" + this.prop + ", sex=" + this.sex;
        return s;
    }
}
```

再写一个反序列化类来调用他：

```
public class type {

    public static void main(String[] args) {
        String eneity3 = "{\"@type\":\"fastjson2change.Person\", \"name\":\"lala\", \"full_name\":\"lalalolo\", \"age\": 13, \"prop\": {\"123\":123}, \"sex\": 1}";
        //反序列化
        Object obj = JSON.parseObject(eneity3,Person.class);
        //输出会调用obj对象的tooString函数
        System.out.println(obj);
    }
}
```

结果如下：

```
Person构造函数
setAge()
getProp()
[Person Object] name=lala full_name=null, age=13, prop=null, sex=null

public name 反序列化成功
private full_name 反序列化失败
private age setAge函数被调用
private sex getsex函数没有被调用
private prop getprop函数被成功调用
```

可以得知：

- public修饰符的属性会进行反序列化赋值，private修饰符的属性不会直接进行反序列化赋值，而是会调用setxxx(xxx为属性名)的函数进行赋值。
- getxxx(xxx为属性名)的函数会根据函数返回值的不同，而选择被调用或不被调用

这里调试一下，跟进去看看，可以看到下图位置处针对我们的@type进行定向解析：

[![image-20200624114114569](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624114114569.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624114114569.png)

[image-20200624114114569](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624114114569.png)



决定这个set/get函数是否将被调用的代码最终在`com.alibaba.fastjson.util.JavaBeanInfo#build`函数处

[![image-20200624120125884](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624120125884.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624120125884.png)

[image-20200624120125884](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624120125884.png)



继续跟，继续跟。。。

[![image-20200624120447372](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624120447372.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624120447372.png)

[image-20200624120447372](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624120447372.png)



[![image-20200624121013685](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624121013685.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624121013685.png)

[image-20200624121013685](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624121013685.png)



接下来是一堆判断条件：

[![image-20200624121144284](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624121144284.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624121144284.png)

[image-20200624121144284](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624121144284.png)



在进入build函数后会遍历一遍传入class的所有方法，去寻找满足set开头的特定类型方法；**再遍历一遍所有方法去寻找get开头的特定类型的方法**。

**set开头的方法要求如下：**

- 方法名长度大于4且以set开头，且第四个字母要是大写
- 非静态方法
- 返回类型为void或当前类
- 参数个数为1个

寻找到符合要求的set开头的方法后会根据一定规则提取方法名后的变量名（会过滤_，就是set_name这样的方法名中的下划线会被略过，得到name）。再去跟这个类的属性去比对有没有这个名称的属性。

如果没有这个属性并且这个set方法的输入是一个布尔型（是boolean类型，不是Boolean类型，这两个是不一样的），会重新给属性名前面加上**is**，再取头两个字符，第一个字符为大写（即isNa），去寻找这个属性名。

[![image-20200624121614209](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624121614209.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624121614209.png)

[image-20200624121614209](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624121614209.png)



这里的is就是有的网上有的文章中说反序列化会自动调用get、set、is方法的由来。个人觉得这种说法应该是错误的。

真实情况应该是确认存在符合setXxx方法后，会与这个方法绑定一个xxx属性，如果xxx属性不存在则会绑定isXx属性（这里is后第一个字符需要大写，才会被绑定）。并没有调用is开头的方法

自己从源码中分析或者尝试在类中添加isXx方法都是不会被调用的，这里只是为了指出其他文章中的一个错误。这个与调用的set方法绑定的属性，再之后并没有发现对于调用过程有什么影响。

**所以只要目标类中有满足条件的set方法，并且对应的属性名存在字符串中，这个set方法就可以被调用。**

**get开头的方法要求如下：**

- 方法名长度大于等于4
- 非静态方法
- 以get开头且第4个字母为大写
- 无传入参数
- 返回值类型继承自Collection/Map/AtomicBoolean/AtomicInteger AtomicLong

所以我们上面例子中的getsex方法没有被调用是因为返回类型不符合，而getprop方法被成功调用是因为Properties 继承 Hashtable，而Hashtable实现了Map接口，返回类型符合条件。

再顺便看一下最后触发方法调用的地方com.alibaba.fastjson.parser.deserializer.FieldDeserializer#setValue，（在被调用的方法中下断点即可）

[![image-20200624122556545](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624122556545.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624122556545.png)

[image-20200624122556545](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624122556545.png)



那么至此我们可以知道

- @type可以指定反序列化成服务器上的任意类
- 然后服务端会解析这个类，提取出这个类中符合要求的setter方法与getter方法（如setxxx）
- 如果传入json字符串的键值中存在这个值（如xxx)，就会去调用执行对应的setter、getter方法（即setxxx方法、getxxx方法）

看上去应该是挺正常的使用逻辑，反序列化需要调用对应参数的setter、getter方法来恢复数据。

但是在可以调用任意类的情况下，如果setter、getter方法中存在可以利用的情况，就会导致任意命令执行。

对应反序列化攻击利用三要素来说，以上我们就是找到了source点，下面来探讨反序列化利用链。

# Fastjson反序列化漏洞原理

## 漏洞原理

由前面知道，Fastjson是自己实现的一套序列化和反序列化机制，不是用的Java原生的序列化和反序列化机制。无论是哪个版本，Fastjson反序列化漏洞的原理都是一样的，只不过不同版本是针对不同的黑名单或者利用不同利用链来进行绕过利用而已。

通过Fastjson反序列化漏洞，攻击者可以传入一个恶意构造的JSON内容，程序对其进行反序列化后得到恶意类并执行了恶意类中的恶意函数，进而导致代码执行。

**那么如何才能够反序列化出恶意类呢？**

由前面demo知道，Fastjson使用parseObject()/parse()进行反序列化的时候可以指定类型。如果指定的类型太大，包含太多子类，就有利用空间了。例如，如果指定类型为Object或JSONObject，则可以反序列化出来任意类。例如代码写`Object o = JSON.parseObject(poc,Object.class)`就可以反序列化出Object类或其任意子类，而Object又是任意类的父类，所以就可以反序列化出所有类。

**接着，如何才能触发反序列化得到的恶意类中的恶意函数呢？**

由前面知道，在某些情况下进行反序列化时会将反序列化得到的类的构造函数、getter方法、setter方法执行一遍，如果这三种方法中存在危险操作，则可能导致反序列化漏洞的存在。换句话说，就是攻击者传入要进行反序列化的类中的构造函数、getter方法、setter方法中要存在漏洞才能触发。

到DefaultJSONParser.parseObject(Map object, Object fieldName)中看下，JSON中以@type形式传入的类的时候，调用deserializer.deserialize()处理该类，并去调用这个类的setter和getter方法：

```
@SuppressWarnings({ "unchecked", "rawtypes" })
public final Object parseObject(final Map object, Object fieldName) {
    ...
    // JSON.DEFAULT_TYPE_KEY即@type
    if (key == JSON.DEFAULT_TYPE_KEY && !lexer.isEnabled(Feature.DisableSpecialKeyDetect)) {
		...
        ObjectDeserializer deserializer = config.getDeserializer(clazz);
        return deserializer.deserialze(this, clazz, fieldName);
```

**小结一下**

若反序列化指定类型的类如`Student obj = JSON.parseObject(text, Student.class);`，该类本身的构造函数、setter方法、getter方法存在危险操作，则存在Fastjson反序列化漏洞；

若反序列化未指定类型的类如`Object obj = JSON.parseObject(text, Object.class);`，该若该类的子类的构造方法、setter方法、getter方法存在危险操作，则存在Fastjson反序列化漏洞；

## PoC写法

一般的，Fastjson反序列化漏洞的PoC写法如下，@type指定了反序列化得到的类：

```
{
"@type":"xxx.xxx.xxx",
"xxx":"xxx",
...
}
```

关键是要找出一个特殊的在目标环境中已存在的类，满足如下两个条件：

1. 该类的构造函数、setter方法、getter方法中的某一个存在危险操作，比如造成命令执行；
2. 可以控制该漏洞函数的变量（一般就是该类的属性）；

## 漏洞demo

由前面比较的案例知道，当反序列化指定的类型是Object.class，即代码为`Object obj = JSON.parseObject(jsonstring, Object.class, Feature.SupportNonPublicField);`时，反序列化过程中会调用类的构造函数、所有属性的setter方法、私有属性的getter方法，因此我们这里直接做最简单的修改，将Student类中会被调用的getter方法添加漏洞代码，这里修改getProperties()作为演示：

```
public class StudentDemo {
    private String name;
    private int age;
    private String address;
    private Properties properties;

    public StudentDemo() {
        System.out.println("无参构造函数");
    }

    public String getName() {
        System.out.println("getName");
        return name;
    }

    public void setName(String name) {
        System.out.println("setName");
        this.name = name;
    }

    public int getAge() {
        System.out.println("getAge");
        return age;
    }

    public String getAddress() {
        System.out.println("getAddress");
        return address;
    }

    public Properties getProperties() throws Exception {
        System.out.println("getProperties");
        Runtime.getRuntime().exec("calc");
        return properties;
    }
}
```

触发代码：

```
String jsonstring ="{\"@type\":\"fastjson2change.StudentDemo\",\"age\":18,\"name\":\"0range\",\"address\":\"China\",\"properties\":{}}";
        Object obj = JSON.parseObject(jsonstring, Object.class, Feature.SupportNonPublicField);
        System.out.println(obj);
        System.out.println(obj.getClass().getName());
```

结果成功触发：

[![image-20200624163813380](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624163813380.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624163813380.png)

[image-20200624163813380](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624163813380.png)



很明显，前面的Demo中反序列化的类是一个Object类，该类是任意类的父类，其子类Student存在Fastjson反序列化漏洞，当@type指向Student类是反序列化就会触发漏洞。

对于另一种反序列化指定类的情景，是该指定类本身就存在漏洞，比如我们将上述Demo中反序列化那行代码改成直接反序列化得到Student类而非Object类，这样就是另一个触发也是最直接的触发场景：

```
StudentDemo obj = JSON.parseObject(jsonstring, StudentDemo.class, Feature.SupportNonPublicField);
```

触发场景；

[![image-20200624164056028](https://fynch3r.github.io/images/Fastjson%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B001/image-20200624164056028.png)](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624164056028.png)

[image-20200624164056028](https://fynch3r.github.io/images/Fastjson学习笔记01/image-20200624164056028.png)



这一篇先写到这里，下一篇讲讲Fastjson的漏洞时间线。