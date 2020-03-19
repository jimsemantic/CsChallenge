// Index.java
// JG Miller (JGM), Portland, OR, jimsemantic@gmail.com
// 3/15/2020


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

class Index<K, V> {
    String indexFileName;
    HashMap<K, V> hashMap;

    Index() {}

    Index(String name) throws IOException {
        indexFileName = name;
        hashMap = new HashMap();
    }

    void loadIndex(String name) throws IOException, ClassNotFoundException {
        indexFileName = name;
        deserialize();
    }

    void serialize() throws IOException {
        FileOutputStream fos = new FileOutputStream(indexFileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(hashMap);
        oos.close();
        fos.close();
        hashMap.clear();
        System.out.printf("Index saved to %s\n", indexFileName);
    }

    void deserialize() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(indexFileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        hashMap = (HashMap) ois.readObject();
        ois.close();
        fis.close();
    }

    V lookup(K key) {
        return hashMap.get(key);
    }

    void storeUnique(K key, V value) {
        hashMap.putIfAbsent(key, value);
    }

    <W> void storeMultivalue(K key, W value) {
        ArrayList<W> currValueArray = (ArrayList<W>) hashMap.get(key);
        if (currValueArray == null) {
            ArrayList<W> newValueArray = new ArrayList<W>();
            newValueArray.add(value);
            hashMap.put(key, (V) newValueArray);
        } else {
            currValueArray.add(value);
            hashMap.put(key, (V) currValueArray);
        }
    }

    void printIndex() throws IOException, ClassNotFoundException {
        deserialize();
        System.out.println(hashMap);
    }
}
