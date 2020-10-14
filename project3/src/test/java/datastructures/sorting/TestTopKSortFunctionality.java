package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Searcher;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestTopKSortFunctionality extends BaseTest {
    
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testComplicatedUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        int[] insert = {10, 50, 7, 70, -2, 11, 15, 3, -5, 11};
        for (int i = 0; i < insert.length; i++) {
            list.add(insert[i]);
        }
        int[] insertOrdered = {-5, -2, 3, 7, 10, 11, 11, 15, 50, 70};
        IList<Integer> top = Searcher.topKSort(3, list);
        assertEquals(3, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(insertOrdered[7 + i], top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testSingleElementSort() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        IList<Integer> top = Searcher.topKSort(1, list);
        assertEquals(1, top.size());
        assertEquals(1, top.get(0));
    }
    
    @Test(timeout=SECOND)
    public void testHundredUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 500; i++) {
            list.add(i);
        }
        IList<Integer> top = Searcher.topKSort(150, list);
        assertEquals(150, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(350 + i, top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testNegativeKThrowsException() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        try {
            Searcher.topKSort(-1, list);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // This is ok: do nothing
        }
    }
    
    @Test(timeout=SECOND)
    public void testKGreaterThanSize() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        IList<Integer> top = Searcher.topKSort(50, list);
        assertEquals(20, top.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testKZero() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        IList<Integer> top = Searcher.topKSort(0, list);
        assertEquals(0, top.size());
    }
}

