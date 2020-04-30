package icraft.easyhc;

import java.util.HashMap;

public class HashMapKeyFinder<K, V> {
    private HashMap<K, V> hashMap;


    public HashMapKeyFinder(HashMap<K, V> hashMap) {
        this.hashMap = hashMap;
    }

    K get(V value) throws Exception {
        for(K key : hashMap.keySet()){
            if(hashMap.get(key).equals(value)) return key;
        }
        throw new Exception("HashMap does not contain key with that value!");
    }
}
