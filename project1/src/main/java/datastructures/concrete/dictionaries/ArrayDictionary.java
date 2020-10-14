package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
* See IDictionary for more details on what this class should do
*/
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;
    private int size;
    
    // You're encouraged to add extra fields (and helper methods) though!
    
    public static final int CAPACITY = 5;
    
    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(CAPACITY);
        this.size = 0;
    }
    
    /**
    * This method will return a new, empty array of the given size
    * that can contain Pair<K, V> objects.
    *
    * Note that each element in the array will initially be null.
    */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }
    
    @Override
    public V get(K key) {
        int index = checkKey(key);
        if (index >= 0) {
            return this.pairs[index].value;
        }
        throw new NoSuchKeyException();
    }
    
    @Override
    public void put(K key, V value) {
        int index = checkKey(key);
        if (index >= 0) {
            this.pairs[index].value = value;
        } else {
            checkCapacity(size);
            Pair<K, V> newPair = new Pair<K, V>(key, value);
            this.pairs[size] = newPair;
            this.size++;
        }
    }
    
    @Override
    public V remove(K key) {
        int index = checkKey(key);
        if (index >= 0) {
            Pair<K, V> removedPair = this.pairs[index];
            for (int i = index; i < this.size - 1; i++) {
                this.pairs[i] = this.pairs[i + 1]; 
            }
            this.size--;
            return removedPair.value;
        } else {
            throw new NoSuchKeyException();
        }
    }
    
    @Override
    public boolean containsKey(K key) {
        return checkKey(key) >= 0;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    private int checkKey(K key) {
        for (int i = 0; i < this.size; i++) {
            if (this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                return i;
            }
        }
        return -1;
    }
    
    private void checkCapacity(int neededCapacity) {
        if (neededCapacity == this.pairs.length) {
            Pair<K, V>[] newArray = makeArrayOfPairs(2 * this.size);
            for (int i = 0; i < this.size; i++) {
                newArray[i] = this.pairs[i];
            }
            this.pairs = newArray;
        }
    }
    
    private static class Pair<K, V> {
        public K key;
        public V value;
        
        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}