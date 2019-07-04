# simple-cache
用于硬件资源较稀缺，但又存在缓存使用场景的小型系统。缓存文件持久化于服务器磁盘。

## 介绍


### 快速开始
1. 依赖引入

Maven
```
<dependency>
    <groupId>com.github.yuxiangping</groupId>
    <artifactId>simple-cache</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```

Gradle
```
compile group: 'com.github.yuxiangping', name: 'simple-cache', version: '1.0.0.RELEASE'
```

2. 使用Spring启动缓存、管理生命周期

```xml
<bean id="cache" class="org.yy.simplecache.CacheManager">
    <property name="config">
        <bean class="org.yy.simplecache.Configuration">
            <property name="root" value="/usr/local/simplecache/"/>
            <property name="space" value="10"/>  <!-- unit GB -->
        </bean>
    </property>
</bean>
```

3. 代码中使用

```java
class CacheTest {

  @Resource
  SimpleCache<String, String> cache;

  public boolean put(String key, String value) {
    return cache.set(key, value);
  }

  public boolean put(String key, String value, long time) {
    return cache.set(key, value, time);
  }

  public String get(String key) {
    return cache.get(key);
  }

  public boolean remove(String key) {
    return cache.remove(key);
  }

  public boolean clear() {
    return cache.clear();
  }

}
```

