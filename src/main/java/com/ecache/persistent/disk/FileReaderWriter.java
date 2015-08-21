package com.ecache.persistent.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.ecache.ECache;
import com.ecache.Element;

@SuppressWarnings("unchecked")
public class FileReaderWriter {

  private static final String INDEX_ = "ecache-disk.index";
  private static final String DATA_ = ".data";

  private static final int DEL = 0x7F;
  private static final char ESCAPE = '%';
  private static final Set<Character> ILLEGALS = new HashSet<Character>();
  static {
    ILLEGALS.add('/');
    ILLEGALS.add('\\');
    ILLEGALS.add('<');
    ILLEGALS.add('>');
    ILLEGALS.add(':');
    ILLEGALS.add('"');
    ILLEGALS.add('|');
    ILLEGALS.add('?');
    ILLEGALS.add('*');
    ILLEGALS.add('.');
  }

  private final String root;
  private final int space;

  private Map<Integer, String> indexMap;
  private File indexFile;

  public FileReaderWriter(String rootPath, int space) {
    this.root = rootPath;
    this.space = space;
    loadIndex();
  }

  public <T> List<T> readAll() {
    List<T> list = new ArrayList<T>(indexMap.size());
    for (Map.Entry<Integer, String> entry : indexMap.entrySet()) {
      T t = (T) loadData(entry.getValue());
      if(t == null) {
        remove(entry.getKey());
      } else {
        list.add(t);
      }
    }
    return list;
  }

  public <K, V> boolean add(K k, Element<K, V> e) {
    int hash = hash(k.hashCode());
    String history = indexMap.get(hash);
    if(history != null) {
      new File(history).delete();
    }
    
    String name = safeName(UUID.randomUUID().toString());
    String path = new StringBuilder().append(root).append(File.separator).append(name).append(DATA_).toString();
    File data = new File(path);
    
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(data)));
      oos.writeObject(e);
    } catch (FileNotFoundException ex) {
      ECache.logger.error("Write data file error. File not found. File path:"+path, ex);
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      ECache.logger.error("Write data file io error. File path:"+path, ex);
      throw new RuntimeException(ex);
    }  finally {
      try {
        if (oos != null) {
          oos.close();
        }
      } catch (IOException ioe) {
        ECache.logger.error("Close data file error.", ioe);
      }
    }
    indexMap.put(hash, path);
    return true;
  }

  public <T, K> T read(K k) {
    int hash = hash(k.hashCode());
    String path = indexMap.get(hash);
    if (path == null) {
      return null;
    }
    T t = (T) loadData(path);
    if(t == null) {
      remove(k);
    }
    return t;
  }

  public <K> boolean remove(K k) {
    int hash = hash(k.hashCode());
    return remove(hash);
  }

  public boolean clear() {
    File cacheRoot = new File(root);
    if (deleteFile(cacheRoot) && indexFile.delete()) {
      indexMap.clear();
      return true;
    }
    return false;
  }

  public void flush() {
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(indexFile)));
      oos.writeObject(indexMap);
    } catch (FileNotFoundException ex) {
      ECache.logger.error("Flush index error. File not found.", ex);
      throw new RuntimeException(ex);
    } catch (IOException ioe) {
      ECache.logger.error("Flush index io error.", ioe);
      throw new RuntimeException(ioe);
    } finally {
      try {
        if (oos != null) {
          oos.close();
        }
      } catch (IOException ioe) {
        ECache.logger.error("Close index file error.", ioe);
      }
    }
  }

  private boolean remove(int hash) {
    String path = indexMap.get(hash);
    if (path == null) {
      return false;
    }
    indexMap.remove(hash);
    File file = new File(path);
    if (!file.delete()) {
      return false;
    }
    return true;
  }
  
  private Object loadData(String filePath) {
    File dataFile = new File(filePath);
    if (!dataFile.exists()) {
      return null;
    }

    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(dataFile)));
      return ois.readObject();
    } catch (FileNotFoundException ex) {
      ECache.logger.error("Data file not found. File path:" + filePath, ex);
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      ECache.logger.error("Read data file error. File path:" + filePath, ex);
      throw new RuntimeException(ex);
    } catch (ClassNotFoundException ex) {
      ECache.logger.error("Read data file error. File path:" + filePath, ex);
      throw new RuntimeException(ex);
    } finally {
      try {
        if (ois != null) {
          ois.close();
        }
      } catch (IOException ioe) {
        ECache.logger.error("Close data file error. File path:" + filePath, ioe);
      }
    }
  }

  private void loadIndex() {
    ObjectInputStream ois = null;
    try {
      indexFile = new File(root + File.separator + INDEX_);
      if (!indexFile.exists()) {
        initIndexFile();
      }
      ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(indexFile)));
      indexMap = (Map<Integer, String>) ois.readObject();
    } catch (FileNotFoundException ex) {
      ECache.logger.error("Index file not found.", ex);
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      ECache.logger.error("Read index file error.", ex);
      throw new RuntimeException(ex);
    } catch (ClassNotFoundException ex) {
      ECache.logger.error("Read index file error.", ex);
      throw new RuntimeException(ex);
    } finally {
      try {
        if (ois != null) {
          ois.close();
        }
      } catch (IOException ioe) {
        ECache.logger.error("Close index file error.", ioe);
      }
    }
  }

  private void initIndexFile() {
    try {
      indexFile.createNewFile();
      indexMap = new LinkedHashMap<Integer, String>();
      flush();
    } catch (IOException ex) {
      ECache.logger.error("Init index file io error.", ex);
      throw new RuntimeException(ex);
    }
  }
  
  boolean isFull() {
    if(space <= 0) {
      return false;
    }
    
    File rootFile = new File(root);
    File[] list = rootFile.listFiles();
    double _space = 0;
    for(File f : list) {
      _space += f.length();
    }
    return _space >= space;
  }
  
  private int hash(int h) {
    h += (h << 15) ^ 0xffffcd7d;
    h ^= (h >>> 10);
    h += (h << 3);
    h ^= (h >>> 6);
    h += (h << 2) + (h << 14);
    return h ^ (h >>> 16);
  }

  private boolean deleteFile(File file) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (File f : files) {
        deleteFile(f);
      }
    }
    return file.delete();
  }

  private String safeName(String name) {
    int len = name.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char c = name.charAt(i);
      if (c <= ' ' || c >= DEL || (c >= 'A' && c <= 'Z') || ILLEGALS.contains(c) || c == ESCAPE) {
        sb.append(ESCAPE);
        sb.append(String.format("%04x", (int) c));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
