---
typora-copy-images-to: img
---



# XML介绍

XML (Extensible Markup Language)，中文为可扩展标记语言，标准通用标记语言的子集，是一种用于标记电子文件使其具有结构性的标记语言。

XML可以用来标记数据、定义数据类型，可以允许用户对自己标记语言进行定义，是对人和机器都比较友好的数据承载方式。它提供统一的方法来描述和交换独立于应用程序或供应商的结构化数据，非常适合万维网传输， 是Internet环境中跨平台、依赖于内容的技术，也是当今处理分布式结构信息的有效工具。



## XML概述

XML是一种可扩展标记语言，也可以认为是一种数据交换格式。

- 可扩展： 语法格式约束不是很严格，用户可扩展性、自定义特性更强。
- 标记语言： 语法主要由标签组成。
- 数据交换格式：可以用作为客户端、服务端数据传输的数据格式。

### **XML与HTML的比较**

- XML与HTML非常相似，都是编写标签

- XML没有预定义标签，HTML存在大量预定义标签

- XML重在保存与传输数据，HTML用于显示信息

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/60ea8580000128d412800720.jpg" alt="img" style="zoom:50%;" />

**XML的用途**

Java程序的配置描述文件。

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230319222140938.png" alt="image-20230319222140938" style="zoom:50%;" />



用于保存程序的产生的数据。

网络间的数据传输。

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230319222306031.png" alt="image-20230319222306031" style="zoom:50%;" />

## XML语法

文件以 .xml 结尾。

### XML文档声明

xml文档声明在文件首行编写以下内容：

```xml
<?xml version="1.0" ?>
```

文档声明的属性：

- ==version==：版本号 固定值 1.0；
- ==encoding==: 指定文档的编码，一般使用UTF-8编码。默认值为 iso-8859-1；  
- standalone：指定文档是否独立  yes 或 no(不做了解)

那么我们的xml文件声明（第一行）一般都可以这样写：

```
<?xml version="1.0" encoding="UTF-8"?>
version 代表版本号1.0/1.1
encoding UTF-8设置字符集，用于支持中文       
```



### 标签（元素）

#### 一、XML标签书写规则

合法的标签名
适当的注释与缩进
合理使用属性
特殊字符与CDATA标签
有序的子元素

#### 二、合法的标签名

标签名要有意义
建议使用英文，小写字母，单词之间使用"-"分割
建议多级标签之间不要存在重名的情况

![image-20230319223055811](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230319223055811.png)

#### 三、合理使用属性

标签属性用于描述标签不可或缺的信息
对标签分组或者为标签设置Id时常用属性表示

#### 四、处理特殊字符

使用实体引用（特殊符号小的情况）

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230319223204608.png" alt="image-20230319223204608" style="zoom:33%;" />

使用CDATA标签（大量特殊符号)

CDATA指的是不应由XML解析器进行解析的文本数据
格式：`<![CDATA[..........]]>`

在XML多层嵌套的子元素中，标签前后顺序应保持一致      

#### 举例：

```xml
<employee>
        <name>张三</name>
        <age>28</age>
        <slary>5000</slary>
        <department>
            <dname>会计部</dname>
            <address>XX大厦-B103</address>
        </department>
    </employee>
    <employee>
        <name>李四</name>
        <age>25</age>
        <slary>3500</slary>
        <department>
            <dname>工程部</dname>
            <address>XX大厦-B104</address>
        </department>
    </employee>       
```



## XML语义约束DTD

1、XML文档结构正确，但可能不是有效的。例如：员工档案XML中绝不允许出现“植物品种”标签。

XML语义约束就是用于规定XML文档中允许出现哪些元素。

2、XML语义约束有两种定义方式：DTD与XML Schema    *    DTD（Document Type Definition，文档类型定义）是一种简单易用的语义约束方式。

    *    DTD文件的扩张名为.dtd。

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/60535aff000166da12800720.jpg" alt="img" style="zoom: 50%;" />

- 利用DTD中的<!ELEMENT>标签，我们可以定义XML文档中允许出现的节点及数量，以hr.xml为例：

  <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/60535bf60001ba5b12800720-20230319223542224.jpg" alt="img" style="zoom: 50%;" />

  -  DTD定义节点数量：如某个子节点需要多次重复出现，则需要在子节点后增加相应的描述符。

  <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/60535c77000152ca12800720.jpg" alt="img" style="zoom:50%;" />

  

  在XML中使用<!DOCTYPE> 标签来引用DTD文件。

  <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/60535cef0001e63612800720.jpg" alt="img" style="zoom:50%;" />

  在 DTD 中，属性通过 ATTLIST 声明来进行声明。

  ------

  ## 声明属性

  属性声明使用下列语法：

  <!ATTLIST element-name attribute-name attribute-type attribute-value>

  DTD 实例:

  <!ATTLIST payment type CDATA "check">

  XML 实例:

  <payment type="check" />

  以下是 **属性类型**的选项：

  | 类型               | 描述                          |
  | :----------------- | :---------------------------- |
  | CDATA              | 值为字符数据 (character data) |
  | (*en1*\|*en2*\|..) | 此值是枚举列表中的一个值      |
  | ID                 | 值为唯一的 id                 |
  | IDREF              | 值为另外一个元素的 id         |
  | IDREFS             | 值为其他 id 的列表            |
  | NMTOKEN            | 值为合法的 XML 名称           |
  | NMTOKENS           | 值为合法的 XML 名称的列表     |
  | ENTITY             | 值是一个实体                  |
  | ENTITIES           | 值是一个实体列表              |
  | NOTATION           | 此值是符号的名称              |
  | xml:               | 值是一个预定义的 XML 值       |

  

  ```DTD:
  <!ELEMENT square EMPTY>
  <!ATTLIST square width CDATA "0">
  合法的 XML:
  <square width="100" />
  ```

  

  

  一、创建DTD文件

  注意点：节点后一定要空格，否则约束不生效。如下：employee后紧跟一个空格，否则该约束不会生效。

  <!ELEMENT employee (name,age,salary,department)>

  

  hr.dtd

  ```dtd
  <?xml version="1.0" encoding="UTF-8"?>
  <!ELEMENT hr (employee+)>
  <!ELEMENT employee (name,age,salary,department)>
  <!ATTLIST employee no CDATA "">
  <!ELEMENT name (#PCDATA)>
  <!ELEMENT age (#PCDATA)>
  <!ELEMENT salary (#PCDATA)>
  <!ELEMENT deparment (dname,address)>
  <!ELEMENT dname (#PCDATA)>
  <!ELEMENT address (#PCDATA)>
  ```

  hr.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE hr SYSTEM "hr.dtd">
  <!-- 人力资源管理系统 -->
  <hr>
  	<employee no="3309">
  		<name>张三</name>
  		<age>31</age>
  		<salary>4000</salary>
  		<department>
  			<dname>会计部</dname>
  			<address>xx大厦-B103</address>
  		</department>
  	</employee>
  	
  	<employee no="3310">
  		<name>李四</name>
  		<age>23</age>
  		<salary>3000</salary>
  		<department>
  			<dname>工程部</dname>
  			<address>xx大厦-B104</address>
  		</department>
  	</employee>
  </hr>
  ```

## XML Schema

- XML Schema比DTD更为复杂，提供了更多功能。
- XML Schema提供了数据类型、格式限定、数据范围等特性。
- XML Schema是W3C标准。

XML Schema样例:

- 根目录的书写:**<element name="hr>** name的值即自己写的标签的名字。
- <complexType></complexType>：标签的含义是复杂节点，包含子节点时必须使用这个标签。
- <element name="employee" minOccurs="1" maxOccurs="9999"></element>，**minOccurs规定了employee标签使用的最小次数，maxOccurs规定了使用的最大次数。**
- **<sequence></sequence>标签是当子节点必须按照顺序写时使用，类似于DTD中的<!ELEMENT employee （name,age ,salary,department）>**
- 数据范围的用法:

```xml
<element name="age">
	<simpleType>
	    <restriction base="integer">
	        <minInclusive value="18"></minInclusive>
	        <maxInclusive value="60"></maxInclusive>
	    </restriction>
	</simpleType>
</element>
```

**<simpleType></simpleType>标签是对简单类型的约束。**

**<restriction base="integer"></restriction>是对整数类型进行限制。**

**<minInclusive value="18></minInclusive>是限定了age的最小值，maxInclusive是限定了age的最大值。**

- 将XML Schema文件与XML文件关联:在根节点处**：**

```xml
<teaching-plan xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="plan.xsd">
```



## java操作xml

### dom

Dom4j是一个易用的，开源的库，用于解析XML。它应用于Java平台，具有性能优异、功能强大和及其易用的特点。

DOM (Document Object Model) 定义了访问和操作 XML文档的标准方法，DOM 把 XML 文档作为树结构来查看，能够通过 DOM 树来读写所有元素。

![image-20230319224625569](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230319224625569.png)

### Dom4j

#### 读取xml文件

1.导入dom4j.jar包

2.

```java
@Test
public void readXml(){
    String path= "src/hr.xml";
    //SAXReader类是读取XML文件的核心类，用于将XML解析后以“树”的形式保存在内存中。
    SAXReader reader = new SAXReader();
    try {
        Document document = reader.read(path);
        //获取XML文档的根节点，即hr标签
        //所有的都是elemnt来包装
        Element root = document.getRootElement();
        //elements方法用于获取指定的标签集合
        List<Element> employees = root.elements("employee");
        for (Element employee : employees) {
             //element方法用于获取唯一的子节点对象
                Element name = employee.element("name");
                System.out.println(name.getName()+" : "+name.getText());
                System.out.println(employee.element("age").getText());
						//获取employee的属性值，也即no属性
                Attribute no = employee.attribute("no");
                System.out.println(no.getName()+" : "+no.getText());
        }
    } catch (DocumentException e) {
        e.printStackTrace();
    }
}
```



![image-20230320160841675](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230320160841675.png)



#### 更新xml

```java
String file = "E:/JAVAworkplace/xml/src/hr.xml";
SAXReader reader = new SAXReader();
try {
	Document docu = reader.read(file);
	Element root = docu.getRootElement();

	// 先通过addElement()方法增加对应的Element节点。
	Element employee = root.addElement("employee");
	// addAttribute()方法是用来给新增的employee增加属性值的
	// 第一个参数为属性名，第二个参数为值。
	employee.addAttribute("no", "3311");
  //这里的属性设置不能在其他的element也这样搞两个参数
	// 为employee节点增加name节点，并通过setText()对name进行赋值。
	Element name = employee.addElement("name");
	name.setText("李铁柱");
	//为employee节点增加age节点并赋值
	employee.addElement("age").setText("45");
	// 为employee节点增加salary节点并赋值的连写方式。
	employee.addElement("salary").setText("3600");
	// employee有一个复杂节点department，同样的方法进行设置。
	Element department = employee.addElement("department");
	department.addElement("dname").setText("人事部");
	department.addElement("address").setText("xx大厦-B105");
	// 然后通过文件方法，将你写好的employee节点写入到document对象中，并更新xml文件
	// 后面的"UTF-8参数很重要，是为了保证你写的中文能正常输入到XML文档中。
	Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
	docu.write(writer);
	writer.close();

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
```

### Xpath路径表达式

```xml
<?xml version="1.0" encoding="UTF-8"?>
 
<bookstore>
 
<book>
  <title lang="eng">Harry Potter</title>
  <price>29.99</price>
</book>
 
<book>
  <title lang="eng">Learning XML</title>
  <price>39.95</price>
</book>
 
</bookstore>
```



#### 选取节点

XPath 使用路径表达式在 XML 文档中选取节点。节点是通过沿着路径或者 step 来选取的。 下面列出了最有用的路径表达式：

| 表达式   | 描述                                                         |
| :------- | :----------------------------------------------------------- |
| nodename | 选取此节点的所有子节点。                                     |
| /        | 从根节点选取（取子节点）。                                   |
| //       | 从匹配选择的当前节点选择文档中的节点，而不考虑它们的位置（取子孙节点）。 |
| .        | 选取当前节点。                                               |
| ..       | 选取当前节点的父节点。                                       |
| @        | 选取属性。                                                   |

在下面的表格中，我们已列出了一些路径表达式以及表达式的结果：

| 路径表达式      | 结果                                                         |
| :-------------- | :----------------------------------------------------------- |
| bookstore       | 选取 bookstore 元素的所有子节点。                            |
| /bookstore      | 选取根元素 bookstore。注释：假如路径起始于正斜杠( / )，则此路径始终代表到某元素的绝对路径！ |
| bookstore/book  | 选取属于 bookstore 的子元素的所有 book 元素。                |
| //book          | **选取所有 book 子元素，而不管它们在文档中的位置。**         |
| bookstore//book | 选择属于 bookstore 元素的后代的所有 book 元素，而不管它们位于 bookstore 之下的什么位置。 |
| //@lang         | 选取名为 lang 的所有属性。                                   |





#### Xpath java代码举例

```java
public static void xpath(String xpathE){
        String file = "src/hr.xml";
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            //此时为node而不是element
            //不仅是element还可以是属性attribute。ele和att的父类是node
            List<Node> nodes = document.selectNodes(xpathE);
            for (Node node : nodes) {
                Element emp = (Element) node;
//                elementText直接获取Text
//                attributeValue直接获取no
                System.out.println(emp.attributeValue("no"));
                System.out.println(emp.elementText("name"));
                System.out.println(emp.elementText("age"));
                System.out.println(emp.elementText("salary"));
                System.out.println("");
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        testDom4j.xpath("//employee");
			testDom4j.xpath("/hr /employee")；
			testDom4j. xpath("//employee")；
			testDom4j. xpath(" //employee[salary<4000]");
    }
```

```sh
3309
张三
31
4000

3310
李四
23
3000

212

22
null

212
yes
22
null


Process finished with exit code 0

```



------

#### 谓语（Predicates）

谓语用来查找某个特定的节点或者包含某个指定的值的节点。

**谓语被嵌在方括号[]中。**

在下面的表格中，我们列出了带有谓语的一些路径表达式，以及表达式的结果：

| 路径表达式                          | 结果                                                         |
| :---------------------------------- | :----------------------------------------------------------- |
| /bookstore/book[1]                  | 选取属于 bookstore 子元素的第一个 book 元素。                |
| /bookstore/book[last()]             | 选取属于 bookstore 子元素的最后一个 book 元素。              |
| /bookstore/book[last()-1]           | 选取属于 bookstore 子元素的倒数第二个 book 元素。            |
| /bookstore/book[position()<3]       | 选取最前面的两个属于 bookstore 元素的子元素的 book 元素。    |
| //title[@lang]                      | 选取所有拥有名为 lang 的属性的 title 元素。                  |
| //title[@lang='eng']                | 选取所有 title 元素，且这些元素拥有值为 eng 的 lang 属性。   |
| /bookstore/book[price>35.00]        | 选取 bookstore 元素的所有 book 元素，且其中的 price 元素的值须大于 35.00。 |
| /bookstore/book[price>35.00]//title | 选取 bookstore 元素中的 book 元素的所有 title 元素，且其中的 price 元素的值须大于 35.00。 |



------

#### 选取未知节点

XPath 通配符可用来选取未知的 XML 元素。

| 通配符 | 描述                 |
| :----- | :------------------- |
| *      | 匹配任何元素节点。   |
| @*     | 匹配任何属性节点。   |
| node() | 匹配任何类型的节点。 |

在下面的表格中，我们列出了一些路径表达式，以及这些表达式的结果：

| 路径表达式   | 结果                              |
| :----------- | :-------------------------------- |
| /bookstore/* | 选取 bookstore 元素的所有子元素。 |
| //*          | 选取文档中的所有元素。            |
| //title[@*]  | 选取所有带有属性的 title 元素。   |

```java
testor.xpath("//employee [salary<4000]")；
testor.xpath("//employee [L]name=’李铁柱']"）；
testor. xpath("//employee [@no=3304]")；
testor. xpath("/ /employee[1]")；//编号最小的人
testor.xpath("/ /employee[last()")；//最后一个人
```



还可以组合表达式

testor. xpath("/ /employee[3]| //employee[8]")；



------

#### 选取若干路径

通过在路径表达式中使用"|"运算符，您可以选取若干个路径。

在下面的表格中，我们列出了一些路径表达式，以及这些表达式的结果：

| 路径表达式                       | 结果                                                         |
| :------------------------------- | :----------------------------------------------------------- |
| //book/title \| //book/price     | 选取 book 元素的所有 title 和 price 元素。                   |
| //title \| //price               | 选取文档中的所有 title 和 price 元素。                       |
| /bookstore/book/title \| //price | 选取属于 bookstore 元素的 book 元素的所有 title 元素，以及文档中所有的 price 元素。 |

### 安装jaxen



#### 下载jar包

![image-20230320185414172](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230320185414172.png)



#### 导入jar包的方法

1. Click **File** from the toolbar
2. Select **Project Structure** option (CTRL + SHIFT + ALT + S on Windows/Linux, ⌘ + ; on Mac OS X)

![dialogue in Intellij 20.3](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/ZlENo.png)

1. Select **Modules** at the left panel
2. Select **Dependencies** tab
3. Select **+** icon
4. Select **1 JARs or directories** option

###  在dom4j中如何使用xPath技术

1）导入xPath支持jar包 。  jaxen-1.1-beta-6.jar

2）使用xpath方法

List<Node>  selectNodes("xpath表达式");  查询多个节点对象

Node    selectSingleNode("xpath表达式");  查询一个节点对象
