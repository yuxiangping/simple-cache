package com.ecache;

public class Configuration {

  /**
   * 缓存文件根目录
   */
  private String rootPath;
  /**
   * 存储空间大小 (单位GB)
   */
  private int space;

  public String getRootPath() {
    return rootPath;
  }

  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  public int getSpace() {
    return space;
  }

  public void setSpace(int space) {
    this.space = space;
  }

}
