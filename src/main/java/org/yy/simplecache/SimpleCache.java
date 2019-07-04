package org.yy.simplecache;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple cache interface
 * @author yy
 */
public interface SimpleCache<K ,V extends Serializable> {

  Logger logger = LoggerFactory.getLogger(SimpleCache.class);
  
  boolean set(K key, V value);
  
  boolean set(K key, V value, long expireTime);
  
  V get(K key);
  
  boolean remove(K key);
  
  boolean removeAll(List<K> keys);
  
  boolean clear();
  
  List<K> keys();
  
}
