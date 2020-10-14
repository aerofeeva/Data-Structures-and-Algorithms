package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int heapSize;

    // Feel free to add more fields and constants.
    public static final int CAPACITY = 10;

    public ArrayHeap() {
        this.heap = makeArrayOfT(CAPACITY);
        this.heapSize = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        if (isEmpty()) {
            throw new EmptyContainerException();
        }
        T min = this.heap[0];
        this.heap[0] = this.heap[this.heapSize - 1];
        this.heapSize--;
        percolateDown(0);
        return min;        
    }

    @Override
    public T peekMin() {
        if (isEmpty()) {
            throw new EmptyContainerException();
        }
        return this.heap[0];
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        checkCapacity(this.heapSize);
        this.heap[this.heapSize] = item;
        percolateUp(this.heapSize);
        this.heapSize++;
    }

    @Override
    public int size() {
        return this.heapSize;
    }
    
    private void percolateDown(int index) {
        int smallestChildIndex = getSmallestChildIndex(index);
        while (index < heapSize && this.heap[index].compareTo(this.heap[smallestChildIndex]) > 0) {
            T temp = this.heap[index];
            this.heap[index] = this.heap[smallestChildIndex];
            this.heap[smallestChildIndex] = temp;
            index = smallestChildIndex;
            if (NUM_CHILDREN * index + 1 < heapSize) {
                smallestChildIndex = getSmallestChildIndex(index);
            } else {
                break;
            }
        }
    }
    
    private int getSmallestChildIndex(int index) {
        int smallestChild = NUM_CHILDREN * index + 1;
        int cap = NUM_CHILDREN;
        if (this.heapSize - smallestChild < NUM_CHILDREN) {
            cap = this.heapSize - smallestChild;
            //System.out.println(this.size + "   " + index + " resetting");
        }
        for (int i = 2; i <= cap; i++) {
            if (this.heap[NUM_CHILDREN * index + i].compareTo(this.heap[smallestChild]) < 0) {
                smallestChild = NUM_CHILDREN * index + i;
            }
        }    
        return smallestChild;     
    }
    
    private void percolateUp(int index) {
        int parentIndex = getParentIndex(index);
        while (index > 0 && this.heap[parentIndex].compareTo(this.heap[index]) > 0) {
            T temp = this.heap[parentIndex];
            this.heap[parentIndex] = this.heap[index];
            this.heap[index] = temp;
            index = parentIndex;
            parentIndex = getParentIndex(index);
        }
    }
    
    private int getParentIndex(int index) {
        return (index - 1) / NUM_CHILDREN;
    }
    
    private void checkCapacity(int neededCapacity) {
        if (neededCapacity == this.heap.length) {
            T[] newArray = makeArrayOfT(2 * this.heapSize);
            for (int i = 0; i < this.heapSize; i++) {
                newArray[i] = this.heap[i];
            }
            this.heap = newArray;
        }
    }
    
    public String toString() {
        String output = "";
        for (int i = 0; i < this.heapSize; i++) {
            output += (heap[i] + "  ,   hi dere");
        }
        return output;
    }
    
    public void remove(T item) {
        int index = 0;
        T currentItem = this.heap[index];
        while (!currentItem.equals(item)) {
            index++;
            currentItem = this.heap[index];
        }
        this.percolateUp(index);
    }
    /**
    public void decreasePriority(T item) {
        int index = 0;
        T currentItem = this.heap[index];
        while (!currentItem.equals(item)) {
            index++;
            currentItem = this.heap[index];
        }
        this.percolateUp(index);
    }
    */
}
