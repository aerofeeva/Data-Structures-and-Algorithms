package datastructures.sorting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import misc.BaseTest;
import misc.exceptions.EmptyContainerException;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }
    
    @Test(timeout=SECOND)
    public void insertManyAndPeekMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        heap.insert(5);
        heap.insert(10);
        heap.insert(-2);
        for (int i = 0; i < 100; i++) {
            assertEquals(-2, heap.peekMin());
        }
        assertTrue(!heap.isEmpty());
    }
    
    @Test(timeout=SECOND)
    public void insertManyAndRemoveMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        int[] insert = {10, 50, 7, 70, -2, 11, 15, 3, -5, 11};
        for (int i = 0; i < insert.length; i++) {
            heap.insert(insert[i]);
        }
        int[] insertOrdered = {-5, -2, 3, 7, 10, 11, 11, 15, 50, 70};
        for (int i = 0; i < insert.length; i++) {
            assertEquals(insertOrdered[i], heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void insertForAscendingTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 300; i++) {
            heap.insert(i);
        }
        
        for (int i = 0; i < 300; i++) {         
            assertEquals(i, heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void insertForDescendingTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 200; i >= 0; i--) {
            heap.insert(i);
        }
        for (int i = 0; i <= 200; i++) {
            assertEquals(i, heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void insertSinglePeekMinAndRemoveMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i <= 200; i++) {
            heap.insert(-2);
        }
        assertEquals(201, heap.size());
        for (int i = 0; i <= 200; i++) {
            assertEquals(-2, heap.peekMin());
        }
        assertEquals(201, heap.size());
        for (int i = 0; i <= 200; i++) {
            assertEquals(-2, heap.removeMin());
        }
        assertEquals(true, heap.isEmpty());
    }
    
    @Test(timeout=SECOND)
    public void insertSinglePeekMinAndRemoveMinAlternating() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i <= 200; i++) {
            heap.insert(-2);
        }
        for (int i = 0; i <= 200; i++) {
            assertEquals(-2, heap.peekMin());
            assertEquals(-2, heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void testGetErrorHandling() {
        IPriorityQueue<Integer> heap = this.makeInstance();

        try {
            heap.removeMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // This is ok: do nothing
        }
        
        try {
            heap.peekMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // This is ok: do nothing
        }
        
        try {
            heap.insert(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // This is ok: do nothing
        }
    }
}
