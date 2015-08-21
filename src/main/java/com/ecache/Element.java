package com.ecache;

import java.io.Serializable;

public class Element<K, V> implements Serializable {

  private static final long serialVersionUID = 1918373817726811488L;
  
  private K key;
  private V value;
  private long expireTime;
  private long createTime;

  public Element(K key, V value) {
    this.key = key;
    this.value = value;
    this.createTime = System.currentTimeMillis();
  }
  
  public Element(K key, V value, long expireTime) {
    this.key = key;
    this.value = value;
    this.expireTime = expireTime;
    this.createTime = System.currentTimeMillis();
  }

  public boolean isExpire() {
    if(expireTime == 0) {
      return false;
    }
    return (System.currentTimeMillis() - createTime) >= expireTime;
  }
  
  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public long getExpireTime() {
    return expireTime;
  }

  public long getCreateTime() {
    return createTime;
  }
  
}
