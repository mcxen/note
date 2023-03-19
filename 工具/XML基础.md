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

```
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

**<teaching-plan xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="plan.xsd">**

## java操作xml

### dom

Dom4j是一个易用的，开源的库，用于解析XML。它应用于Java平台，具有性能优异、功能强大和及其易用的特点。

DOM (Document Object Model) 定义了访问和操作 XML文档的标准方法，DOM 把 XML 文档作为树结构来查看，能够通过 DOM 树来读写所有元素。

![image-20230319224625569](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230319224625569.png)

### Dom4j
