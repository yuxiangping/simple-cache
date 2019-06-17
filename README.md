# simple-cache
简单的缓存系统 纯属学习。用于硬件资源较稀缺，但又存在缓存使用场景的小型系统。缓存文件持久化于服务器磁盘。

## 介绍


开始使用

1. 使用Spring启动缓存、管理生命周期
```xml
<bean id="cache" class="com.ecache.CacheManager">
    <property name="config">
        <bean class="com.ecache.Configuration">
            <property name="root" value="/usr/local/ecache/"/>
            <property name="space" value="10"/>  <!-- unit GB -->
        </bean>
    </property>
</bean>
```

2. 代码中使用
```java
class CacheTest {

  ECache<String, String> cache;

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

