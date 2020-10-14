package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.ISet;
import java.util.Random;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private int currentIndex;
    private IDictionary<T, Integer> objects;
    public static final int CAPACITY = 5;
    private Random rand;


    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        this.currentIndex = 0;
        this.pointers = new int[CAPACITY];
        this.objects = new ChainedHashDictionary<>();
        this.rand = new Random();
    }

    @Override
    public void makeSet(T item) {
        if (this.objects.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        this.checkCapacity();
        this.objects.put(item, this.currentIndex);
        this.pointers[this.currentIndex] = -1;      
        this.currentIndex++;
    }

    @Override
    public int findSet(T item) {
        if (!this.objects.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int index = this.objects.get(item);
        int nextIndex = this.pointers[index];
        ISet<Integer> traversedNodes = new ChainedHashSet<>();
        while (nextIndex >= 0) {
            traversedNodes.add(index);
            index = nextIndex;
            nextIndex = this.pointers[index];
        }
        for (int traversed : traversedNodes) {
            this.pointers[traversed] = index;
        }
        return index;
    }

    @Override
    public void union(T item1, T item2) {
        int index1 = this.findSet(item1);
        int index2 = this.findSet(item2);
        if (index1 == index2) {
            throw new IllegalArgumentException();
        }
        int rank1 = this.pointers[index1];
        int rank2 = this.pointers[index2];
        int root = -1;
        if (rank1 == rank2) {
            root = rand.nextInt(2);
        }
        if (root == 0 || rank1 < rank2) { // since all neg, if rank 1 < rank 2 -> rank 1 > rank 2
            this.pointers[index2] = index1; // point 2 to 1
            if (root != -1) {
                this.pointers[index1]--; // decrease rank by 1
            }
        } else {
            this.pointers[index1] = index2;
            if (root != -1) {
                this.pointers[index2]--;
            }
        }
        
    }
    
    private void checkCapacity() {
        if (this.currentIndex == this.pointers.length) {
            int[] newPointer = new int[(this.pointers.length * 2)];
            for (int i = 0; i < this.pointers.length; i++) {
                newPointer[i] = this.pointers[i];
            }
            this.pointers = newPointer;
        }
    }
}