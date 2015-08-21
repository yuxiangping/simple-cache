package com.ecache;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Easy cache interface
 * @author: xiangping_yu
 * @data : 2015-6-9
 * @since : 1.5
 */
public interface ECache<K ,V extends Serializable> {

  Logger logger = LoggerFactory.getLogger(ECache.class);
  
  boolean set(K key, V value);
  
  boolean set(K key, V value, long expireTime);
  
  V get(K key);
  
  boolean remove(K key);
  
  boolean removeAll(List<K> keys);
  
  boolean clear();
  
  List<K> keys();
  
}
