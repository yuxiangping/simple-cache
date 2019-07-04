package org.yy.simplecache;

/**
 * Disk persistent configuration.
 * @author yy
 */
public class Configuration {

  /**
   * 缓存文件根目录
   */
  private String root;
  /**
   * 存储空间大小 (单位GB)
   */
  private int space;

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public int getSpace() {
    return space;
  }

  public void setSpace(int space) {
    this.space = space;
  }

}
