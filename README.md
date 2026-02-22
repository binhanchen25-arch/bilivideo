# 第1章 搭建环境

<mark>本笔记标题不代表对应课程标题</mark>

- 项目架构分类

  - 业务(功能)架构
    - 顶层：用户服务，如注册登录、大会员权限、查找感兴趣视频等
    - 中间层： 在线视频流播放+实时弹幕
    - 底层：管理后台，如视频上传、数据统计、系统消息推送等
  - 技术架构
    - 技术选型：SpringBoot2.X + Mybatis + MySQL + Maven
    - 开发模式：项目采用经典MVC，模式控制层（controller层）、服务层（Service）、数据层（Dao）
  - 部署架构
    - 前端：服务转发 + 负载均衡
    - 后端： 业务处理 + 功能实现
    - 工具：缓存、队列

- 创建多模块、多环境项目

  - 多模块：创建MVC模式多模块项目，控制层（controller）、服务层（Service）、数据层（Dao）
  - 多环境：添加不同环境的properties配置文件
  - 在IDEA中配置JDK和Maven

- 搭建数据库与持久层

  - 持久层框架：Mybatis，特点是支持XML形式管理、支持动态SQL

  - 在项目中添加Mybatis框架依赖、在配置文件中配置Mybatis

    ``` xml
    <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>2.2.2</version>
            </dependency>
    ```

    ``` properties
    #Mybatis
    mybatis.mapper-locations=classpath:mapper/*.xml
    ```

  - <mark>mapper文件夹都放到了dao模块，配置文件是在service模块中，service模块依赖dao层，所以应该是上层能扫描到下层的classpath</mark>

  - @Mapper: 注解，Mybatis提供，作用是项目在启动的时候将Dao层的数据封装成一个实体类

  - `` <mapper namespace="类路径"></mapper>进行相关的关联 ``

  - ``` properties
    
    ```

  # 别名

    mybatis.type-aliases-package=com.bilibili.dao

  ```
  
  ```

- 实现热部署

  - 打开Idea设置，搜索compile，勾选Compile independent modules in parallel

  - Ctr + Alt + Shift + /,选择registry，勾选compiler.document.save.enabled和compiler.automake.allow.when.app.running

  - 在项目启动项里边，即Edit Configurations...配置启动的配置，在``On ‘update’ action`` 和 ``On frame deactivation`` 选择成 Update classes and resources

  - 引入依赖

    ``` xml
       <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <version>2.7.9</version>
            </dependency>
    ```

- 添加配置项

  ``` properties
  spring.devtools.restart.enabled=true
  ```

# 第2章 从用户功能体验后端精典开发模式

## 2-1 用户开发模块概要-RESTful风格接口设计

- RESTful风格接口设计：RESTful架构、HTTP方法语义、HTTP方法幂等性、RESTful接口设计原则
- 用户模块开发概要：通用功能和通用配置、用户相关功能
- REST（Representation State Transfer）表述性状态转移，REST指的是一组架构约束条件和原则
- RESTful表述的是资源的状态性转移，在web中资源就是URI（Uniform Resource Identifier）
- 如果一个架构符合REST的约束条件和原则，我们就称他为RESTful架构，HTTP是目前与REST相关的唯一实例
- RESTful架构应该遵循统一的接口原则，应该使用标准的HTTP方法如GET和POST，并且遵循这些方法的语义
  - GET：获取指定的资源
  - DELETE： 删除指定的资源
  - POST：发送数据给服务器，依据HTTP 1.1 规范中的描述，结合实际项目开发经验，POST经常为了统一的方法来涵盖一下功能：
    - 在公告板，新闻组，邮件列表或类似的文章组中发布消息
    - 通过注册新增用户
    - 向数据处理程序提供一批数据，例如提交一个表单
  - PUT：使用请求中的负载(即数据)创建或者替换目标资源。PUT和POST的区别在于PUT是幂等的，而POST不是。<mark>幂等的含义可以理解为调用一次与连续调用多次是等价的（没有副作用或副作用不变）</mark>mark
  - GET、DELETE、PUT具备幂等性，POST不具备幂等性
  - POST和PUT的区别
    - 比较容易混淆的是HTTP POST 和 PUT
    - POST和PUT的区别容易被简单地误认为“POST”表示创建资源，“PUT”表示更新资源
    - 实际上，两者均可用于创建资源，更为卑职的差别是在幂等性方面
- RESTful接口的命名规则：
  - 原则1：HTTP方法后跟的URL必须是名词且统一成名词负数形式
  - 原则2： URL中不采用大小写混合的驼峰命名，尽量采用小写单词，如需链接多个单词，则采用“-”链接
  - 示例： /users、/users-fan；反例：/getUsers、/getUersFans
- RESTful接口URL分级原则
  - 原则1：一级用来定为资源分类，如/users即表示需要定位到用户相关资源
  - 原则2： 二级仍用来定位具体某个资源，如/users/20即表示id为20的用户，再如/users/20/fans/1即表示id为20的用户的id为1的粉丝
- <mark>tips: 原则是为了开发更规范，但不应该成为束缚我们的枷锁</mark>

<font color="red">GET和POST方法可以使用相同的路径（相同资源名称URI）</font>

``` java
// 可以同时使用
@GetMapping("/users")
...
@PostMapping("/users")
```

``` java 
public String postData(@RequestBody Map<String, Object> data){
    Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
    Array.sort(idArray);
    int nextId = idArray[idArray.length-1] + 1;
    dataMap.put(nextId, data);
    return "post seccess";
}
```

@RequestBody 注解：SpringBoot会将数据data进行自动的封装，封装成以JSON的格式传输进来 （将前端传的值进行封装，封装成一个JSON类型）

 ``` java
// PUT 方法
@PutMapping("/put")
public String putData(@RequestBody Map<Stirng, Object> data){
    Integer id = Integer.valuseOf(String.valueOf(data.get("id")));
    Map<String, Object> containedData = data.get(id);
    if(containedGata == null){
        Intager[] idArray = data.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);
        int nextId = idArray[idArray.length - 1] + 1;
        dataMap.put(nextId, data);
    }else{
        dataMap.put(id,data);
    }
}
 ```

## 2-2通用功能与配置

- 通用功功能：加解密工具（AES、RSA、MD5）

- 通用配置：JSON信息转换配置、全局异常处理配置
- @Configuration 声明是一个配置类
  - @Bean注解。可以向SpringBoot上下文中注入一个类
  - @Primary注解：相当于声明优先级比较高，当存在多个注入类时，优先选择
- @Order 注解：@Order(Ordered.HIGHEST_PRECEDENCE),表明执行顺序，最高
- 实现java.io.[Serializable](https://so.csdn.net/so/search?q=Serializable&spm=1001.2101.3001.7020)这个接口是**为序列化，serialVersionUID 用来<font color="red">表明实现序列化类的不同版本间的兼容性（即在版本升级时反序列化仍保持对象的唯一性)</font>。如果你修改了此类, 要修改此值。否则以前用老版本的类序列化的类恢复时会出错。

## 2-3 用户注册与登录



## 2-4 基于JWT的用户token验证

- 生成的用户令牌，保存在本地的LocalStorage里边

- JWT （JSON Web Token）：是一个规范，用于在空间受限环境下安全传递“声明”

- JWT由三部分组成：

  -  第一部分是头部（header）：声明的类型、加密算法（通常是SHA256）
  -  第二部分是载荷（payload）：存放有效信息、一般包含签发者、所面向的用户、接受方、过期时间、签发时间以及唯一身份标识
  -  第三部分是签名（signature）：只要由头部、载荷以及秘钥组合加密而成

- ``` java
  ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
  // 为了获取Token，Token传给前端之后，保存在LocalStorage里边，下次（后边）前端在请求接口的时候，从LocalStorage里边拿到Token，一般放在请求头里，所有的接口统一放到请求头里边，这样就不区分接口具体要传哪些参数。
  String token = requestAttributes.getRequest().getHeader("token");
  Long userId = TokenUtil.verifyToken(token);
          
  ```

### 2-4-1 多模块查找依赖找不到

<font color="red" size=10>tip:</font>

<font color="green" size=5>1、提示找不到Service层的Bean（即无法自动注入bean）,解决方案：</font>

``` java
@SpringBootApplication(scanBasePackages = {"com.imooc.bilibili.api", "com.bilibili.service"})
```

<font color="green" size=5>2、提示找不到Dao层的Bean（即无法自动注入@Mapper注解标示的类）,解决方案：</font>

``` java
@MapperScan(basePackages = "com.bilibili.dao")
```

<font color="purple" size=5>以上均是在启动类添加</font>

- 测试接口时，token要放到header里边，不是Params里

<font color="green" size=5>3、XML文件提示数据库的表和字段找不到，解决方案：在Database中配置数据库链接</font>

- Java中的四种内部类：静态内部类、成员内部类、局部内部类、匿名内部类

  参考：https://blog.csdn.net/xiaowanzi_zj/article/details/121893431

- redis中的setnx，如果set not exist ，返回1，否则返回0，linux创建多级目录门mkdir -p，递归-r，git查看不同 git -diff

## 2-5 用户关注与动态更新

- Service层不直接引用其他的Dao（Mapper），而是引入其他类的Service（只关注被引用的Service提供的功能），<mark>应该是Service与Service进行交互，引用的Service里边在调用对应的Dao去交互</mark>。这样再Dao中做一些修改的话，对其他的Service是无关的。在设计模式上来讲，这是松耦合的设计模式

#### 坑位

<mark>坑位：</mark>动态SQL中使用参数作为变量，<font color="red">需要@Param注解，即使只有一个参数</font>

- 错误示例

```java
// 接口方法
List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);
```

``` xml
 <select id="getUserInfoByUserIds" resultType="com.bilibili.domain.UserInfo">
        select
            *
        from
            t_user_info
        where
            1=1
            <if test="userIdList != null and userIdList.size > 0">
                and userId in
                <foreach collection="userIdList" item="userId" index="index" open="(" close=")" separator=",">
                    #{userId}
                </foreach>
            </if>
    </select>
```

<mark>在xml文件中提示userIdList找不到</mark>，原因是使用了idea自带的jdk，没有修改为jdk8，jdk8编译时采取了**强制保持方法参数变量名**（使用jdk8，去掉注解还是报错，具体待究）

- 正确示例

  ``` java
  List<UserInfo> getUserInfoByUserIds(@Param("userIdList") Set<Long> userIdList);
  ```

  <mark>添加注解后就不再提示找不到了</mark>参考文档：https://blog.csdn.net/ningmeng666_c/article/details/115440720

#### xml中的SQL使用了$，需要@Param

虽然$存在SQL注入安全问题，但是有时候确实要使用，比如说传入列名或者表名时，就需要加上@Param注解，例如：
接口方法：

```java
List<User> getUsersOrderByParam(@Param("order_by") String order_by);

12
```

xml文件：

```xml
<select id="getUsersOrderByParam" resultMap="BaseResultMap" parameterType="String">
        SELECT * FROM T_USER
        <if test="order_by != null and order_by != ''">
            ORDER BY ${order_by} DESC
        </if>
</select>
```

- 注释写在接口方法上

### 2-6 添加或获取用户关注分组

- @RequestParam注解也表示必填的选项

  <font color="gree" size=5>JSONObject类使用技巧</font>

  JSONObject 为阿里的fastJson包里边的一个类，它<mark>实现</mark>了一个Map类，可以当成一个Map类，里边内置了一些好用的方法，所以不适用Map类，使用JSONObject类

  ``` java
  public PageResult<UserInofo> pageListInfo(JSONObject para){
      // 可以直接获取Integer等类型，如果是Map，只能是String或Object类型，获得Map里的值之后还要进行转换，使用JSONObject已经实现可以直接转换
      // 
      Integer no = param.getInteger("no");
      Integer size = params.getInteger("size");
      params.put("start", (no-1)*size);
      params.put("limit", size);
      Integer total = userDao.pageCountUserInfo(param);
  }
  ```

  

  ```java
  // UserDao 类
  //从上一层自动生成方法的实现如下
  Integer pageCountUserInfos(JSONObject params);
  //使用上边的JSONObject在Mybatis标签里或相关的xml语句时，传入参数类型时要写很长，不方便。在这里可以手动改成Map类型.因为JSONObject实现了Map类，可以直接转换，这样就可以在参数类型里写成java.util.Map类型
  Integer pageCountUserInfos(Map<String, Object> params)
  ```

#### 使用Mybatis进行模糊查询的五种方式


1. 使用 ${...} 代替 #{...}

   ``` java
   // 使用${}不能防止sql注入，不推荐使用
   select * from table where column like '%${value}%'
   ```

2. 使用sql拼接字符串

   ```java
   select * from table where column like concat('%', #{value}, '%')
   ```

3. 把'%#{value}%'改为"%"#{value}"%"

   ```java
   select * from table where column like "%"#{value}"%"
   ```

4. 使用标签

   ```java
   <select id="aaa" resultType="com.example.entity.ABCEntity"
     parameterType="com.example.entity.ABCEntity">
     <bind name = "pattern1" value = "'%' + _parameter.name + '%'" />
     SELECT * FROM table
     <where>
      <if test="column != null and name != ''">
       name like #{pattern1}
      </if>
     </where>
    </select>
   ```

   5、代码中拼接后注入

   ```java
   select * from table where column like #{sqltext}
   ```

## 2-7订阅发布模式

| 条目   | 订阅发布模式                                                 | 观察者模式                                         |
| ------ | ------------------------------------------------------------ | -------------------------------------------------- |
| 角色   | 发布者（Producer）、订阅者（Consumer）、代理人（Broker）     | 观察者（Observe）、主体（Subject）                 |
| 耦合性 | 发布者和订阅者者之间完全解耦，他们彼此不知道对方，完全通过代理人来执行事项 | 观察者和主题之间是松耦合的关系，他们之间没有代理人 |
| 总结   | 订阅发布模式≠观察者模式                                      |                                                    |

## 2-8生产者与消费者

- RocketMQ使用知识，详情查看 **wiki笔记 10-11章节内容**

- @Value注解，是Springboot提供引入变量的一种方式

  ```java
  // 自定义的属性定义在application-test.properties配置文件中
  // 可以从配置中获取属性进行初始化
  @Value("${rocketmq.name.server.address}")
  private String nameServerAddr;
  ```

- RedisTemplate 提供一些方法，有时候可以自己也可以封装，如果有时候觉得使用起来比较麻烦，会封装一些RedisUtil工具类，本教程使用原生的

- DefaultMQProducer 是RocketMQ自带的实体类

- @Autowired注解报错，提示找不到类，可以替换成@Recorce注解

## 2-9 用户权限控制

- 控制用户对系统资源（URI）的操作

  - 前端：对页面或页面元素的权限控制
    - 访问权限：那些页面可以访问、那些页面元素课件等等
    - 操作权限：如页面按钮是否可以点击，是否可以增删改查等
  - 后端：对接口及数据的权限控制
    - 接口是否可以调用
    - 接口具体字段范围等

- RBAC权限控制模型（Role-Based Access Control）:基于角色的权限控制

  - RBAC模型的层级：RABC0、RABC1、RABC2、RABC3

  - 关键字：<mark>用户、角色、资源、权限、操作</mark>

    - 用户：注册用户
    - 角色：Lv0-Lv6会员
    - 权限：视频投稿、发布动态、各种弹幕功能
    - 资源：页面、页面元素
    - 操作：点击、跳转、增删改查等

  - 数据库表设计：角色表、用户角色表、元素操作权限表、角色元素操作权限关联表、页面菜单权限表、角色页面菜单权限关联表

    ![image-20230324155333946](https://gitee.com/huxiaoyisheng/pic-bed/raw/master/img/image-20230324155333946.png)

- t_auth_element_operation表里边的elementCode字段（根元素对应的唯一编码，）在写的时候要<mark>保持前后端一致</mark>,比如数据里值为postVideoButton,前端也要使用postVideoButon组件（或者说是按钮）来跟数据库的记录保持对应，这样前端才能通过唯一编码确定对应的是哪一个按钮，拿到按钮之后在根据拿到的数据进行判断这个按钮是否可以被点击或者备操作。



- <font color="gree" size=3>@Param（mybatis提供的）</font>，因为参数是列表类型，并且参数名不是list所以前边添加@Param注解

  ```java
  // 在 Dao / Mapper 层
  List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(@Param("roleIdSet" Map<Long> roleIdSet));
  ```

  <font color="red">如果使用List ，Set这种集合式的形式进行传参的话，***一定*** 要用</font><font color="ruby">@Param注解</font>来命名一下相关的名称，为了方便在<foreach>循环标签里使用

## 2-10 SpringAOP 切面编程

-  概念：是一种约定流程的编程。

-  关键字：<mark>约定</mark>(AOP的核心)，把固定的流程抽出来做成一种约定流程，插入个性化的工具

-  典型案例：数据库事务包括打开数据库，设置属性，执行sql语句，没有异常则提交事务，有一场则回滚事务，最后关闭数据库连接。

-  术语：
   - 连接点（join point）：对应的被拦截对象
   - 切点（point cut）:用过正则或指示器的规则来适配连接点
   - 切面（aspect）:可以定义切点、各类通知和引入的内容
   - 通知（advice)：分为前置通知（before）、后置通知（after）、时候返回通知（afterReturning）、异常通知（afterThrowing）
   - 织入（weaving）:为原有服务（service）对象生成代理对象，然后将与切点匹配的连接点拦截，并将各类通知加入约定流程
   - 目标对象（target）：被代理对象

## 2-11 接口控制权限

- 引入AOP依赖
- 添加Annotation注解类，使用@interface关键字
  - @Retention(RetentionPolicy.RUNTIME) 执行策略
  - @Target({ElementType.METHOD}) 执行目标
  - @Document
  - @Component 需要进行注入，是为了让SpringBoot能扫描到，常用于工具类、配置项上。
- 添加切面类ApiLimitedRoleAspect，使用@Aspect注解进行标示，同时还用到的注解@Order(1)优先级、@Component
- 使用@Pointcut("@annotation(com.imooc.bilibili.domain.annotation.ApiLimitedRole)"(上边添加的注解类地址)
- @Before("check() && @annotation(apiLimitedRole)")  这个apiLimitedRole是自定义的，随便命名
- SetA.retainAll(SetB),<mark>retainAll()方法</mark>表示连个集合取交集。
- **思路**：添加一个注解类，里边存放一个字符串数组（参数），创建一个切面类，里边写上控制方法，在调用Api（Controller）上添加自定义的注解，放入相关参数

## 2-22 数据权限控制

- 两种实现方式
  1. 在具体业务代码里加入实际的控制逻辑（基于SpringAOP切面编程实现），可以实现重复性功能实践
  2. 

- Object[] orgs = joinPoint.getArgs();获得切到的方法的参数

``` java
Object[] args = jointPoint.getArgs();
for(Object arg : args){
    if(arg instanceof UserMoment){
        UserMoment usermoment = (UserMoment)arg;
        String type = userMoment.getType();
        if(roleCodeSet.contains(AuthRoleConstant.ROLE_LV0 && !"0".equals(type))){
            throw new ConditionException("参数异常");
        }
    }
}
```

## 2-23 添加用户默认角色

- <mark>在使用依赖的时候，要注意引用的顺序，避免出现循环引用</mark>。比如：UserService引用UserAuthService,UserAuthService引用UserRoleService，UserRoleService引用UserService，就会造成循环引用。

# 第3章 打造高性能的视频与弹幕系统

## 3-1FastDFS文件服务器

- 引入依赖

- 加入配置

  ``` properties
  #fastdfs,是一个tracher列表，可为多个，用逗号分隔开
  fdfs.tracker-list=39.107.54.108:22222,39.107.54.108:22222
  ```

- MultipartFile：SpringBoot封装的一个实体类，使用过程中和普通文件没有太大区别，两者是可以进行转换的，底层原理是用二进制流进行转换

- ``` java
  // 文件上传之后返回一个路径, StorePath是FastFileStorageClient封装好的一个类，存储的是文件上传后的路径所有信息
  StorePath storPath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
  return storPath.getPath();// getFullPath()获取全路径
  ```

- ``` java
  // FastDfs提供的，支持断点上传的工具类，里边提供的appendFile()方法，分片上传，会在断了之后再次上传，有可能重复，使用modifyFile只会在断了的位置修复继续上传，不会造成重复上传
  @Autowired
  private AppendFileStorageClient appendFileStorageClient;
  ```

- 安装FsatDFS按照网址（https://github.com/happyfish100/fastdfs/wiki）的方式，不要按照给的文档，并且记得开放22122（或者直接关闭防火墙）这个端口号<mark>测试上传的时候，提示拒绝链接，是因为没有启动软件,按照网址下方的启动进行设置即可</mark>

- 本地FastDFS地址http://192.168.248.139/8888(后边跟上传返回的id，直接访问22122端口，会提示禁止下载)

- 配置nginx之后，记得要重启

- 前后端分离这种情况，一般都是前端对大文件进行分片（前端拿到文件之后，进行分片，保存在本地内存中，再调用后端及的接口进行一片片的上传，为了理解流程，本教程后端写了一下流程），上传一个分片之后，会分会一个路径，后边根据该路径进行上传

- 对文件进行MD5加密，来获取一个字符串，为后边的秒传做准备

- 分片上传，第一个是上传（返回一个存储路径），后边的是修改上传文件内容，从而达到上传文件的目的

- Long类型转换为String类型，使用String.valueOf(uploedeSize),<mark>不要使用其他方法，推荐使用该方法，该方法的好处是避免出现空指针异常</mark>

- increment方法自增1

  ```java
  redisTemplate.opsValue().increment(uploadedNoKey);
  ```

- redisTemplate提供一个删除列表里边的所有值的key都删除

  ```java
  redisTemplate.delete(keyList)// 批量删除redis里边的值
  ```

## 3-2 文件分片

- File.createTempFile() 创建临时文件

  ``` java
  // 文件类型转换 MultipartFile -> File
      public File multipartFileToFile(MultipartFile multipartFile) throws Exception {
          String originalFileName = multipartFile.getOriginalFilename();
          String[] fileName = originalFileName.split("\\.");
          File file = File.createTempFile(fileName[0], "." + fileName[1]);
          multipartFile.transferTo(file);
          return file;
      }
  ```

- 一般分片大小为2-5M

- RandomAccessFile类，java提供的文件工具类，支持随机访问（即可以去跳转到任意位置）

  ```java 
  // r 代表赋予读的权限
  RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
  // 跳转到置顶i的位置
  randomAccessFile.seek(i);
  ...
  // 最后记得要关闭
  randomAccessFile.close();
  ```

## 3-3 测试断点续传

- 测试时发现，上传到FastDFS的文件大小为原来的2倍，是因为上传的时候把整个文件当成分片文件进行的上传，所以上传了几次就变大为原来的多少倍

## 3-4 秒传文件

- 文件MD5加密一般是由前端来做
- ByteArrayOutputStream比特数组输出流

``` java
// 获取文件MD5加密后的字符串
public static String getFileMD5(MultipartFIle file) throws Exception{
    InputStream fis = file.getInputStream();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int byteReade;
    while((byteReade = fis.read(buffer)) > 0){
        baos.write(buffer, 0, byteRead);
    }
    fis.close();
    return DigestUtils.md5Hex(baos.toByteArray())
}

```



## 3-5 视频在线播放

- 不要把资源的绝对路径暴露出去，通过后端接口进行一次封装

- 在进行传输的时候，可以对流进行AES加密，前端在进行解密（解密通过代码混淆的方式），这样被别人获取到流也无法打开

- 因为是通过流的方式进行传输，接口不需要返回类型（void）,会通过Http响应的输出流里边

- 获取文件信息

  ``` java
  FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, url);
  ```

- 请求头信息

  ``` java
  // 获得的是一个枚举类型
  Enumeration<String> headerNames = request.getHeaderNames();
  Map<String, Object> headers = new HashMap<>();
  while(headerNames.hasMoreElements()){
      String header = headerName.nextElement();
      headers.put(header, request.getHeader(header));
  }
  ```

  

- 对range进行切片之后，第一个数据是为控的数据，所以起始位置begain = Long.parseLong(range[1]);而不是range[0];

## 3-6 视频点赞

- 对参数的合法性进行校验，否则很容易被攻击者攻击的
- @RequestParam 注解，表示对传入的参数不能为空，或者null
- @RequestBody注解，适合多个传参的注解，统一封装成一个实体类
- 如果一个方法内有两次对数据库进行操作（如有两个数据库的添加），为了数据库的一致性，要加<mark>@Transactional</mark>>注解

## 坑位

<font color="gree" size=3>当在controller层传入的参数是一个列表（集合），哪怕只有一个参数，也要添加@Param("参数名")，否则会提示找不到。</font>参考链接：https://blog.csdn.net/Lao3shen/article/details/125222585

## 3-7 弹幕系统设计

- WebSocket简介： 

  - WebSocket协议是基于TCP的一种新的网络协议。他实现了浏览器与服务器的全双工（Full-Duplex）通信；
  - 全双工通信：客户端可以主动发送信息给服务端，服务端也可以主动发送信息给客户端；
  - WebSocket协议的优点：报文体积小、支持长链接

- 弹幕系统架构设计

  ![image-20230331123429287](https://gitee.com/huxiaoyisheng/pic-bed/raw/master/img/image-20230331123429287.png)

  - 可以在MQ中保存数据，减小并发的压力

## 3-8 SpringBoot整合WebSocket

- 整合及使用

  1. 引入依赖

  2. 配置WebSocketConfig

     ``` java
     @Configuration
     public class WebSocketConfig {
     
         @Bean
         public ServerEndpointExporter serverEndpointExporter(){
             // 作用：主要是用来发现WebSocket服务的
             return new ServerEndpointExporter();
         }
     }
     ```

  3. 创建WebSocket服务类WebSocketService.java

     ``` java
     @Component
     // 被下边的注解标注，就说明是一个和WebSocket相关的服务类了,里边的路径值随便定义，访问的时候是用自定义的路径即可
     @ServerEndpoint("/imserver")
     public class WebSocketService{
         
         private final Logger = LoggerFactory.getLogger(this.class);
         
         // AtomicInteger java提供的一个原子操作（为了保证线程安全）的相关类
         private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
         
         // ConcurrentHashMap 是一个线程安全的Map
         // 每个客户端链接都会生成一个WebSocketService，所以WebSocketService是一个多例模式，多例模式下是用@Autowire注入的话会产生问题
         private static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();
         
         // Session 是服务端和客户端的一个会话,WebSocket里边包含一个Session
         private Session session;
         
         private String sessionId;
         
         // WebSocket 提供的一个注解，当调用成功时，就调用@OnOpen注解表示的方法
         @OnOpen
         public void openConnection(Session session){
             this.sessionId = session.getId();
             this.session = session;
             if(WEBSOCKET_MAP.containsKey(sessionId)){
                 WEBSOCKET_MAP.remove(sessionId);
                 // this 代表当前的WebSocketService
                 WEBSOCKET_MAP.put(sessionId, this);
             }else{
                WEBSOCKET_MAP.put(sessionId, this);
                 ONLINE_COUNT.getAndIncrement;
             }
             // ONLINE_COUNT.get() 会返回一个原始的int类型
             logger.info("用户连接成功" + sessionId + ",当前在线人数为：" + ONLINE_COUNT.get());
             try{
                 this.sendMessage("0")
             }catch(Exception e){
                 logger.error("连接异常！");
             }
     }
         
         @OnClose
         public void closeConnection(){
             if(WEBSOCKET_MAP.containKey(sessionId)){
                WEBSOCKET_MAP.remove(sessionId);
                ONLINE_COUNT.getAndDecrement(); 
             }
             logger.info("用户退出：" + sessionId + "，当前在线人数为：" + ONLINE_COUNT.get());
         }
         
         @OnMessage
         public void onMessage(String message){
             
         }
         
         @OnError
         public void onError(Throwable error){
             
         }
         
         public void sendMassege(String message) throws IOException{
            // 调用会话现成的getBasicRemote()方法获得Basic实体类，在调用发送信息的方法
             this.session.getBasicRemote().sentText(message);
         }
     ```

     

## 3-9 多例模式下的Bean注入

- 因为WebSocket是多例模式，所以在WebSocketService类里注入RedisTemplat时，系统会认为已经注入一个RedisTemplat了，就不再注入，会导致注入的RedisTemplate为空（NULL）；

  ``` java
  @Autowire
  private RedisTemplat redisTemplat;
  ...
  // redisTemplat 会为空
  redisTemplat.opsForValue().get("kkk");
  ```

  

- 修改方法为(WebSocketService.java类)

  ``` java
  privat static ApplicationContext APPLICATION_CONTEXT;
  public static void setApplicationContext(ApplicationContext applicationContext){
      WebSocketService.APPLICATION_CONTEXT = applicationContext;
  }
  ...
  RedisTemplate<String, String> redisTemplate = (RedisTemplate)WebSocketService.APPLICATION_CONTEXT.getBean("redisTemplate");
  // redisTemplate 就不再为空了
  redisTemplate.opaForValue().get("kkk");
  ```

  在启动类中添加：

  ``` java 
  // 把app放入到WebSocketService的方法中，使得相当于有了一个全局上下文变量
  WebSocketService.setApplicationContext(app);
  ```

  

## 3-10 弹幕系统实现

- <font color="gree" size=3>tips:</font><font color = "green">在XML文件中，由于><(大于、小于符号)容易与标签符号混淆，所以需要使用特殊符号进行表示，如：``` <![CDATA[>=]]>``` 表示大于等于符号</font> ，用于区别标签的内容，加入一些声明避免一些额外会发生的东西。

  ``` xml
  <if test="createTime != null and createTime != '' ">
      createTime <![CDATA[>=]]> #{createTime}
  </if>
  ```

  

- ``` java
  // 把String类型的massage（json格式的信息）转换成Object类型
  Danmu danmu = JSONObject.parseObject(massage, Danmu.class)
  ```

- @PathParam 注解有WebSocket提供，为了获取链接后边拼的参数的

  ``` java
  @ServerEndpoint("/imserver/{token}")
  ...
  @OnOpen
  public void openConnection(Session session, @PathParam("token") String token){
      try{
          userId = TokenUtil.verifyToken(token);
      }catch(Exception ignored){}
  }
  ```

- ``` java
  // JSONObject.toJSONString(list)将list转换成json格式
  redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
  
  // 将json格式的String转换成相应的类
  String value = redisTemplate.opsForValue().get(key);
  list = JSONArray.parseArray(value, Danmu.class);
  ```

## 3-11 弹幕性能优化



## 3-12 弹幕异步存储

- 需要在执行异步操作的方法（Service层）上添加@Async注解，再在启动类上添加 @EnableAsync注解，即可实现异步
- 添加事物@Transactional 并且在启动类上添加 @EnableTransactionManagement 注解





## 3-13 在线人数统计

- 定时任务使用注解@Scheduled注解在定时执行的方法上，并且再在启动类上添加 @EnableScheduling注解，即可实现定时任务