# SLog

Log日志框架，支持输出到控制台，和文件等支持自定义扩展
![](./img/1513934900618.jpg)

### 功能说明
1. 输出Log到控制台
2. 输出Log到文件
3. Release版本控制台静默
4. 支持自定义Tag
5. 支持输出线程信息
6. 支持输出调用堆栈信息
7. 支持Log定位
8. 支持输出格式自定义
9. 支持多线程环境
10. 支持打印扩展等

### 使用说明

1. 添加依赖

```java
implementation 'io.github.heartinfei:slogger:1.0.1'
```

2. 初始化
```java
public class SApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        S.init(this);
        if (BuildConfig.DEBUG) {
            S.addPlant(new DebugPlan());    //输出到控制台
        } else {
            S.addPlant(new ReleasePlan(path)); //输出到文件
        }
    }
}

```
3. 使用 
```
//普通输出
S.i("Test");

//Error输出
S.e(...);

//自定义Tag
S.log("MyTag","Message .....");

```

4. 自定义`Configuration`

```java
Configuration config = new Configuration.Builder(this)
                .trackInfoDeep(Integer.MAX_VALUE)
                .tag("S_LOG") //default is "SApplication"
                .isPrintLineNo(true)    //defaut true
                .isPrintTag(true)       //defaut true
                .isPrintTrackInfo(true) //defaut true
                .isShowThreadInfo(true) //defaut true
                .build();
S.addConfig(c);

```

5. 扩展Log输出方式
通过继承`BasePlan`可创建自定义`Plan`，可参考`ReleasePlan`的实现

6. 其他请参考[Demo](https://github.com/heartinfei/SLog)