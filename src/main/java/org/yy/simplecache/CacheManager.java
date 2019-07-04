package org.yy.simplecache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.yy.simplecache.persistent.PersistentFactory;
import org.yy.simplecache.persistent.PersistentFactory.Event;

/**
 * Manager for cache. Load disk cached file to memory when manager inited.
 * @author yy
 */
public class CacheManager<K, V extends Serializable> implements SimpleCache<K, V>, InitializingBean, DisposableBean {

  private Configuration config;
  private PersistentFactory factory;
  private ConcurrentMap<K, Element<K, V>> caches = new ConcurrentHashMap<K, Element<K, V>>(Integer.MAX_VALUE >> 15);

  @Override
  public void afterPropertiesSet() throws Exception {
    if (config == null) {
      throw new RuntimeException("Cache manager 'config' cannot be null.");
    }
    Assert.notNull("Persisten 'root' path cannot be empty.", config.getRoot());

    factory = new PersistentFactory(config);
    factory.reload(caches);
  }

  @Override
  public boolean set(K k, V v) {
    if (k == null || v == null) {
      return false;
    }

    Element<K, V> e = new Element<K, V>(k, v);
    caches.put(k, e);
    factory.addEvent(Event.PUT, e);
    return true;
  }

  @Override
  public boolean set(K k, V v, long expireTime) {
    if (k == null || v == null) {
      return false;
    }

    Element<K, V> e = new Element<K, V>(k, v, expireTime);
    caches.put(k, e);
    factory.addEvent(Event.PUT, e);
    return true;
  }

  @Override
  public V get(K k) {
    Element<K, V> e = caches.get(k);
    if (e == null) {
      return null;
    }

    if (e.isExpire()) {
      remove(k);
      return null;
    }
    return e.getValue();
  }

  @Override
  public boolean remove(K k) {
    caches.remove(k);
    factory.addEvent(Event.REMOVE, new Element<K, V>(k, null));
    return true;
  }

  @Override
  public boolean removeAll(List<K> keys) {
    if (CollectionUtils.isEmpty(keys)) {
      return false;
    }
    for (K k : keys) {
      caches.remove(k);
      factory.addEvent(Event.REMOVE, new Element<K, V>(k, null));
    }
    return true;
  }

  @Override
  public boolean clear() {
    caches.clear();
    factory.addEvent(Event.CLEAR, null);
    return true;
  }

  @Override
  public List<K> keys() {
    return new ArrayList<K>(caches.keySet());
  }

  @Override
  public void destroy() throws Exception {
    factory.destory();
  }

  public void setConfig(Configuration config) {
    this.config = config;
  }

}
