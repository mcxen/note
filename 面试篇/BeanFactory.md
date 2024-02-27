## 前言



本系列章节自顶向下的了解一下 `BeanFactory` 相关代码，对代码层级的设计、整个 `BeanFactory` 的概念 以及 一些核心实现的逻辑的进行深入一点的了解



从顶层接口 `BeanFactory` 入手



## 版本



**Spring 5.3.x**



## BeanFactory



```
public interface BeanFactory {

	/**
	 * FactoryBean 的前缀，beanName 前加这个获取的就是
	 * 		FactoryBean 本身
	 */
	String FACTORY_BEAN_PREFIX = "&";

	/**
	 * 获取 beanName 对应的 bean 实例（可能涉及 bean 的创建）
	 * 1）会对别名进行转换
	 * 2）当前 BeanFactory 中找不到回去父级找
	 */
	Object getBean(String name) throws BeansException;
	/**
	 * 获取指定 name type 的 bean 实例，找不到或者类型转换失败会抛异常
	 * 1）会对别名进行转换
	 * 2）当前 BeanFactory 中找不到回去父级找
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
	/**
	 * 这里允许指定创建 bean 用到的参数，比如这个 bean 的属性是基于
	 * 		构造方法 | 工厂方法 赋值的，当然这些参数可以在 BeanDefinition
	 * 		中进行覆盖
	 */
	Object getBean(String name, Object... args) throws BeansException;
	/**
	 * 基于类型获取 bean，有多个的话会抛异常
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * 返回对应的 ObjectProvider，这是一种可以延迟获取 bean 实例的模式
	 * 		比如基于 ObjectProvider#getObject 方法获取实例
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * 是否包含指定 name 的 bean definition 或 单例
	 * 此处返回 true 并不意味着 getBean 能返回对应实例
	 * 1）支持别名解析
	 * 2）支持层级查找
	 */
	boolean containsBean(String name);

	/**
	 * name 对应的 bean 是否单例（singleton）
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
	/**
	 * name 对应的 bean 是否原型（prototype）
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 指定 name 的 bean 是否匹配对应类型
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 获取指定 name 的 bean 实例的类型，如果目标 bean 实例是一个 FactoryBean
	 * 		则返回的使其创建实例的类型，即 FactoryBean#getObjectType
	 * 这会造成 FactoryBean 的提前实例化
	 * 1）支持别名解析
	 * 2）支持层级查找
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;
	/**
	 * 在 getType(String name) 的基础上由参数 allowFactoryBeanInit
	 * 		决定是否允许 FactoryBean 的提前初始化
	 */
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

	/**
	 * 返回指定 name 的所有别名，这意味这基于这些 name 获取的 bean 实例都是同一个
	 * 如果这个地方传入的是一个别名，那么会返回 bean 原始名称和别名组成的数组
	 * 支持层级查找
	 */
	String[] getAliases(String name);

}
```



- 定义获取 `FactoryBean` 本身的前缀 `&`
- 定义一组 `getBean` 方法获取 `bean` 实例，`getBean` 的过程可能伴随着 `bean` 实例的创建（其中基于 `构造方法` `工厂方法` 的属性注入可以通过传入 `arg[]` 参数实现）
- `getBeanProvider` 方法是个单独的机制，它只返回一个 `ObjectProvider`，具体的 `bean` 实例获取可以依赖 `ObjectProvider#getObject` `ObjectProvider#getIfAvailable` 等方法，类似一种 `延迟加载` 的模式
- 其他方法可见注释



### HierarchicalBeanFactory



```
public interface HierarchicalBeanFactory extends BeanFactory {

	// 获取父级 BeanFactory
	@Nullable
	BeanFactory getParentBeanFactory();

	/**
	 * 跟 BeanFactory#containsBean 类似，该方法只检索
	 * 		当前 BeanFactory，而不会层级检索
	 */
	boolean containsLocalBean(String name);

}
```



`BeanFactory` 的 `子接口`：



- 提供了层级关系维护，`getParentBeanFactory` 用于获取 `父BeanFactory`
- `containsLocalBean` 不同于 `BeanFactory#containsBean`，只会在当前层级中判断是否存在指定 `name` 的 `bean实例`



### ListableBeanFactory



```
public interface ListableBeanFactory extends BeanFactory {
	
	/**
	 * 当前层级是否包含指定 name 的 BeanDefinition
	 * 注意是 BeanDefinition，所以是不考虑手动注册的 singleton 的
	 * 		比如 registerSingleton 方法注册的
	 */
	boolean containsBeanDefinition(String beanName);

	// 返回当前层级所有 BeanDefinition 个数
	int getBeanDefinitionCount();

	// 返回当前层级所有 BeanDefinition 名称数组
	String[] getBeanDefinitionNames();

	// getBeanProvider 方法的拓展
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit);
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit);

	/**
	 * 获取指定类型（子类）的 bean names
	 * 对于 FactoryBean 会先尝试匹配 getObject 实例的类型，匹配不上才
	 * 		匹配对应 FactoryBean
	 * 不会考虑层级，但是此处也会把 singleton 考虑在内	
	 * 其中：
	 * boolean includeNonSingletons 参数决定是否考虑 singleton 以外的 bean
	 * boolean allowEagerInit 是否初始化对应 FactoryBean，如果 false 则类型
	 * 		直接匹配对应 FactoryBean
	 */
	String[] getBeanNamesForType(ResolvableType type);
	String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit);
	String[] getBeanNamesForType(@Nullable Class<?> type);
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * 基本同上，返回的 Map 结构
	 * k：beanName
	 * v：beanInstance
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	// 返回标注了指定 注解 的 beanName 数组
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	// 返回标注了指定 注解 的 bean实例集合：name -> bean实例
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * 依次从 指定bean 其父类 接口 和 工厂方法 上获取指定注解
	 * 同样也涉及 FactoryBean 初始化的问题，由参数 allowFactoryBeanInit 决定 
	 */
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(
			String beanName, Class<A> annotationType, boolean allowFactoryBeanInit)
			throws NoSuchBeanDefinitionException;

}
```



`BeanFactory` 的 `子接口`：



- 拓展了 `枚举` 功能，提供了多个枚举方法
- 这些方法都仅限于当前层级的 `枚举`，如果需要考虑父级，可以借助 `BeanFactoryUtils`
- `containsBeanDefinition` `getBeanDefinitionCount` `getBeanDefinitionNames` 这几个方法是完全基于当前 `BeanFactory` 中的 `BeanDefinition` 的，换句话说，对于我们通过 `registerSingleton` 等方法注册的 `bean` 实例，它们是不感知的
- `getBeanNamesForType` `getBeansOfType` 方法是会感知自行注册 `bean` 实例的，当然大多数场景的 `bean` 实例都是基于 `BeanDefinition` 扫描的
- 具体的方法内容可参考注释



### AutowireCapableBeanFactory



```
public interface AutowireCapableBeanFactory extends BeanFactory {
	
	/**
	 * 表示不进行自动注入，默认情况下都是该状态，真正的注入
	 * 		一般都是基于 @Autowired 等注解驱动
	 */
	int AUTOWIRE_NO = 0;

	/**
	 * 基于属性 name 的自动注入模式，一般不用
	 */
	int AUTOWIRE_BY_NAME = 1;

	/**
	 * 基于属性 type 的自动注入模式，@Bean 方法就是这种模式
	 */
	int AUTOWIRE_BY_TYPE = 2;

	/**
	 * 基于 构造方法 的自动注入，一般不用
	 */
	int AUTOWIRE_CONSTRUCTOR = 3;

	/**
	 * 自动检测自动注入的方式
	 * @Deprecated
	 */
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;

	// 通过此后缀获取原始实例（比如 代理实例的 target）
	String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

	// 创建、填充 bean实例 相关方法

	/**
	 * 创建指定类型的 bean实例：包括 实例创建、带有注解属性的注入、
	 * 		各种初始化回调，还有 BeanPostProcessors 的执行
	 */
	<T> T createBean(Class<T> beanClass) throws BeansException;

	/**
	 * 自动填充指定 bean实例属性，本质上是 after-instantiation 和
	 * 		property post-processing 回调的执行
	 */
	void autowireBean(Object existingBean) throws BeansException;

	/**
	 * 配置给定 bean实例，本质上是 属性填充 以及 初始化回调 的执行
	 */
	Object configureBean(Object existingBean, String beanName) throws BeansException;

	// 细粒度控制bean生命周期的相关方法
	
	/**
	 * 基于 autowireMode 创建 bean实例
	 */
	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * 基于 autowireMode 创建 bean 实例并自动填充
	 */
	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * 基于 autowireMode 自动填充已经存在的 bean 实例
	 * 已经存在的实例不支持 AUTOWIRE_CONSTRUCTOR 
	 */
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	/**
	 * 基于 BeanDefinition 的属性填充已存在的 bean 实例
	 */
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	/**
	 * 初始化给定 bean，比如 初始化回调 的执行等
	 */
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	// 执行给定 bean实例 的所有 BeanPostProcessor#postProcessBeforeInitialization
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	// 执行给定 bean实例 的所有 BeanPostProcessor#postProcessAfterInitialization
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	// 销毁指定 bean实例，执行所有 DisposableBean#destory
	void destroyBean(Object existingBean);

	// 针对注入元素的实例解析

	// 基于 name type 的依赖解析，可以理解为针对属性获取待注入的 bean实例
	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;
	Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;
}
```



`BeanFactory` 的 `子接口`：



- 拓展了 `自动注入` 等相关能力
- 它的能力被单独拎出来，因此允许非 `Spring` 组件的实例也借助它来实现 `自动装配`
- 它的功能没有暴露给 `ApplicationContext`，但是在 `ApplicationContext` 上下文中，可以通过 `ApplicationContext#getAutowireCapableBeanFactor` 获取该实例，当然也可以直接获取 `BeanFactory` 进行类型转换
- 它定义了不同的粒度生命周期的相关方法，基于主要的几个里程碑比如 实例的创建 属性的填充 初始化回调 来划分，一般 `Spring` 内部自然是对 `bean` 实例执行全过程的生命周期



#### ConfigurableBeanFactory



```
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry
```



`HierarchicalBeanFactory` 的子接口，拓展了配置能力，这是 `Spring` 常用的类设计模式，这一层抽象了所有 `BeanFactory` 类相关组件的维护，比如：



- `BeanExpressionResolver`：`SpEL` 解析器
- `ConversionService`：转换服务
- `TypeConverter`：类型转换的顶层接口，相当于整合了 `ConversionService` 和 `PropertyEditorRegistry`
- `BeanPostProcessor`：支持添加 `BeanPostProcessor` 对 `bean实例` 进行后处理
- 等等



另外还提供了诸如 `getMergedBeanDefinition` `isFactoryBean` 等方法



##### ConfigurableListableBeanFactory



```
public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory
```



集大成者，整合了所有接口的能力，拓展了核心方法 `preInstantiateSingletons`：单例的创建



## 总结



- `BeanFactory`，顶层接口，提供了最基础的方法譬如 `getBean` `containsBean`
- `HierarchicalBeanFactory`，子接口，拓展了 `层级关系`
- `ListableBeanFactory`，子接口，拓展了 `枚举能力`
- `AutowireCapableBeanFactory`，子接口，拓展了 `自动装配` 能力
- `ConfigurableBeanFactory`，子接口，拓展了相关组件 `可配置化` 的能力
- `ConfigurableListableBeanFactory`，集大成者，直接实现类 `DefaultListableBeanFactory`