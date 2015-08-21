# ecache
简单的缓存系统 纯属学习

## 介绍
ECache -> easy cache 简单粗暴的命名

之前自己在做一个项目，服务器需要接入缓存。由于是云主机(屌丝 你懂的)，那点内存根本不够再安装个memcache或者redis之类的缓存服务。当时就用了个最搓的办法，直接MAP放内存了。弊端每次重启缓存就没了。 后面有点空闲时间就想写个简单点的缓存（中间也有看下ehcache，代码挺复杂的，可以学习学习），能持久化就行。然后就写了这个。 还能用 ^.^

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

