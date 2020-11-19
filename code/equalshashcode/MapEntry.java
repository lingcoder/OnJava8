// equalshashcode/MapEntry.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A simple Map.Entry for sample Map implementations
import java.util.*;

public class MapEntry<K, V> implements Map.Entry<K, V> {
  private K key;
  private V value;
  public MapEntry(K key, V value) {
    this.key = key;
    this.value = value;
  }
  @Override
  public K getKey() { return key; }
  @Override
  public V getValue() { return value; }
  @Override
  public V setValue(V v) {
    V result = value;
    value = v;
    return result;
  }
  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object rval) {
    return rval instanceof MapEntry &&
      Objects.equals(key,
        ((MapEntry<K, V>)rval).getKey()) &&
      Objects.equals(value,
        ((MapEntry<K, V>)rval).getValue());
  }
  @Override
  public String toString() {
    return key + "=" + value;
  }
}
