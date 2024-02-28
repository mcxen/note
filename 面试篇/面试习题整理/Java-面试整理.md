# Java

## 基础

### 1. int和Integer的区别

**数据类型不同**：int 是基础数据类型，而 Integer 是包装数据类型；

**默认值不同**：int 的默认值是 0，而 Integer 的默认值是 null；

**内存中存储的方式不同**：int 在内存中直接存储的是数据值，而 Integer 实际存储的是对象引用，当 new 一个 Integer 时实际上是生成一个指针指向此对象；

**实例化方式不同**：Integer 必须实例化才可以使用，而 int 不需要；

**变量的比较方式不同**：int 可以使用 == 来对比两个变量是否相等，而 Integer 一定要使用 equals 来比较两个变量是否相等；

**泛型使用不同**：Integer 能用于泛型定义，而 int 类型却不行。

### 2. int 和 Integer 的典型使用场景如下：

- **Integer 典型使用场景**：在 Spring Boot 接收参数的时候，通常会使用 Integer 而非 int，因为 Integer 的默认值是 null，而 int 的默认值是 0。如果接收参数使用 int 的话，那么前端如果忘记传递此参数，程序就会报错（提示 500 内部错误）。因为前端不传参是 null，null 不能被强转为 0，所以使用 int 就会报错。但如果使用的是 Integer 类型，则没有这个问题，程序也不会报错，所以 Spring Boot 中 Controller 接收参数时，通常会使用 Integer。
- **int 典型使用场景**：int 常用于定义类的属性类型，因为属性类型，不会 int 不会被赋值为 null（编译器会报错），所以这种场景下，使用占用资源更少的 int 类型，程序的执行效率会更高。

