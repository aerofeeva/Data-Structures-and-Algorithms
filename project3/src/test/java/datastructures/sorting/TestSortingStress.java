package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

import static org.junit.Assert.assertTrue;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSortingStress extends BaseTest {
    
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=10*SECOND)
    public void testKGreaterMany() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        IList<Integer> top = Searcher.topKSort(20000, list);
        assertEquals(10000, top.size());
        for (int i = 0; i < 10000; i++) {
            assertEquals(i, top.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testHundredThousandUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 500000; i++) {
            list.add(i);
        }
        IList<Integer> top = Searcher.topKSort(20000, list);
        assertEquals(20000, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals((500000-20000 + i), top.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void insertHundredThousandSinglePeekMinAndRemoveMinAlternating() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i <= 200000; i++) {
            heap.insert(-2);
        }
        assertEquals(200001, heap.size());
        for (int i = 0; i <= 200000; i++) {
            assertEquals(-2, heap.peekMin());
            assertEquals(-2, heap.removeMin());
        }
        assertTrue(heap.isEmpty());
    }
    
    @Test(timeout=10*SECOND)
    public void insertHundredThousandSinglePeekMinAndRemoveMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i <= 200000; i++) {
            heap.insert(-2);
        }
        assertEquals(200001, heap.size());
        for (int i = 0; i <= 200000; i++) {
            assertEquals(-2, heap.peekMin());
        }
        assertEquals(200001, heap.size());
        for (int i = 0; i <= 200000; i++) {
            assertEquals(-2, heap.removeMin());
        }
        assertTrue(heap.isEmpty());
    }
    
    @Test(timeout=SECOND)
    public void insertManyAscendingTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 300000; i++) {
            heap.insert(i);
        }

        for (int i = 0; i < 300000; i++) {          
            assertEquals(i, heap.removeMin());
        }
        assertTrue(heap.isEmpty());
    }
    
    @Test(timeout=SECOND)
    public void insertManyDescendingTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 200000; i >= 0; i--) {
            heap.insert(i);
        }
        for (int i = 0; i <= 200000; i++) {
            assertEquals(i, heap.removeMin());
        }
        assertTrue(heap.isEmpty());
    } 
}
