package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
//import datastructures.concrete.dictionaries.ArrayDictionary.Pair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
//import misc.exceptions.NotYetImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    
    // You're encouraged to add extra fields (and helper methods) though!
    private int size;
    public static final int CAPACITY = 5;
    
    
    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(CAPACITY);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int arraySize) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[arraySize];
    }

    @Override
    public V get(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }
        int code = this.getCode(key);
        return this.chains[code].get(key);       
    }
    
    @Override
    public void put(K key, V value) {
        checkSize();
        int code = this.getCode(key);
        if (this.chains[code] == null) {
            this.chains[code] = new ArrayDictionary<K, V>();
        } 
        if (!this.containsKey(key)) {
            this.size++;
        }
        this.chains[code].put(key, value);
    }

    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }
        int code = this.getCode(key);
        this.size--;
        return this.chains[code].remove(key);
    }

    @Override
    public boolean containsKey(K key) { 
        int code = this.getCode(key);
        if (this.chains[code] == null) {
            return false;
        }
        
        return this.chains[code].containsKey(key);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains, this.size);
    }
    
    private void checkSize() { // checks if load factor is greater than 1
        if (1.0 * this.size / this.chains.length > 1) {
            IDictionary<K, V>[] newChain = makeArrayOfChains(2 * this.size);
            Iterator<KVPair<K, V>> iter = this.iterator();
            while (iter.hasNext()) {
                KVPair<K, V> pair = iter.next();
                K key = pair.getKey();
                V val = pair.getValue();
                int code = Math.abs(key.hashCode() % newChain.length);
                if (key == null) {
                    code = 0;
                }
                if (newChain[code] == null) {
                    newChain[code] = new ArrayDictionary<K, V>();
                }
                newChain[code].put(key, val);
            }
            this.chains = newChain;
        }
    }
    
    private int getCode(K key) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode() % this.chains.length);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Think about what exactly your *invariants* are. Once you've
     *    decided, write them down in a comment somewhere to help you
     *    remember.
     *
     * 3. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 4. Think about what exactly your *invariants* are. As a 
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int chainIndex;
        private boolean bucketFlag;
        private int size;
        private int count;
        private Iterator<KVPair<K, V>> iter;
        

        public ChainedIterator(IDictionary<K, V>[] chains, int inputSize) {
            this.chains = chains;
            this.chainIndex = 0;
            this.size = inputSize;
            this.bucketFlag = false;
            this.count = 0;
        }
        

        @Override
        public boolean hasNext() { 
            return this.count < this.size;      
        }
            
        @Override
        public KVPair<K, V> next() {
            if (this.hasNext()) {
                if (!this.bucketFlag && this.chains[chainIndex] != null) {
                    this.iter = this.chains[chainIndex].iterator();
                    this.bucketFlag = true;
                    count++;
                    KVPair<K, V> pair = this.iter.next();
                    if (!iter.hasNext()) {
                        this.bucketFlag = false;
                        chainIndex++;
                    }
                    return pair;
                } else if (this.chains[chainIndex] != null) {
                    KVPair<K, V> pair = iter.next();
                    if (!iter.hasNext()) {
                        this.bucketFlag = false;
                        chainIndex++;
                    }
                    count++;
                    return pair;            
                } else {
                    chainIndex++;
                    return this.next();
                }

            } 
            System.out.println(count + "   "+ size + "    " + chainIndex + "   " + chains.length);
            throw new NoSuchElementException();
        }

    }
    
}