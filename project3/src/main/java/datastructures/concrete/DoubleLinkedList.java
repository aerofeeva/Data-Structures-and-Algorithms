package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
* Note: For more info on the expected behavior of your methods, see
* the source code for IList.
*/
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;
    
    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }
    
    @Override
    public void add(T item) {
        Node<T> newItem = new Node<T>(item);
        if (this.size == 0) {
            this.front = newItem;
            this.back = newItem;
        } else {
            this.back.next = newItem;
            newItem.prev = this.back;
            this.back = newItem;
        }
        this.size++;
    }
    
    @Override
    public T remove() {
        if (this.size == 0) {
            throw new EmptyContainerException();
        } else {
            Node<T> temp = this.front;
            if (this.size == 1) {
                this.front = null;
                this.back = null;
            } else {
                temp = this.back;
                this.back = this.back.prev;
                this.back.next = null;
            }
            this.size--;
            return temp.data;
        }
        
    }
    
    @Override
    public T get(int index) {
        if (index < this.size && index >= 0) {
            return this.getNode(index).data;
        }
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public void set(int index, T item) {
        if (index < this.size && index >= 0) {
            Node<T> temp = this.getNode(index);
            Node<T> newData = new Node<T>(item);
            if (size == 1) {
                this.front = newData;
                this.back = newData;
            } else if (index == 0) {
                this.front = newData;
                newData.next = temp.next;
                newData.next.prev = newData;
            } else if (index == this.size - 1) {
                temp.prev.next = newData;
                newData.prev = temp.prev;
                this.back = newData;
            } else {
                Node<T> temp2 = temp.prev;
                temp.next.prev = newData;
                newData.next = temp.next;
                temp2.next = newData;
                newData.prev = temp2;
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public void insert(int index, T item) {
        if (index <= this.size && index >= 0) {
            Node<T> newData = new Node<T>(item);
            if (size == 0 || index == size) {
                this.add(item);
            } else if (index == 0) {
                newData.next = this.front;
                this.front = newData;
                newData.next.prev = newData; 
                this.size++;
            } else {
                Node<T> temp = this.getNode(index);
                Node<T> temp2 = temp.prev; 
                temp2.next = newData;
                temp.prev = newData;
                newData.next = temp;
                newData.prev = temp2;
                this.size++;
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public T delete(int index) {
        if (size == 0) {
            throw new IndexOutOfBoundsException();
        } else if (index < this.size && index >= 0) {
            Node<T> temp = this.getNode(index);
            if (index == 0) {
                if (size != 1) { 
                    this.front = this.front.next;
                    this.front.prev = null;
                } else {
                    this.back = null;
                    this.front = null;
                }
            } else if (index == size - 1) {
                this.back = this.back.prev;
                this.back.next = null;
            } else {
                Node<T> temp2 = temp.prev;
                temp2.next = temp.next;
                temp.next.prev = temp2;
            }
            this.size--;
            return temp.data;
        }
        throw new IndexOutOfBoundsException();
    }
    
    
    @Override
    public int indexOf(T item) {
        return checkIndex(item);
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean contains(T other) {
        return (checkIndex(other) != -1);
    }
    
    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }
    
    private Node<T> getNode(int index) {
        Node<T> temp = this.front;
        if (index < this.size / 2) {
            for (int i = 0; i < index; i++) {
                temp = temp.next;
            }
        } else {
            temp = this.back;
            for (int i = 0; i < this.size - index - 1; i++) {
                temp = temp.prev;
            }
        }
        return temp;
    }
    
    private int checkIndex(T item) {
        boolean flag = false;
        int index = 0;
        Node<T> temp = this.front;
        while (index < this.size && !flag) {
            if (temp.data == item || temp.data.equals(item)) {
                flag = true;
            } else {
                index++;
                temp = temp.next;
            }
            
        }
        if (flag) {
            return index;
        } else {
            return -1;
        }
    }
    
    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;
        
        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
        
        public Node(E data) {
            this(null, data, null);
        }
        
        // Feel free to add additional constructors or methods to this class.
    }
    
    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;
        
        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }
        
        /**
        * Returns 'true' if the iterator still has elements to look at
        * returns 'false' otherwise.
        */
        public boolean hasNext() {
            return current != null;
        }
        
        /**
        * Returns the next item in the iteration and internally updates the
        * iterator to advance one element forward.
        *
        * @throws NoSuchElementException if we have reached the end of the iteration and
        *         there are no more elements to look at.
        */
        public T next() {
            if (this.hasNext()) {
                T data = this.current.data;
                this.current = this.current.next;
                return data;
            }
            throw new NoSuchElementException();
        }
    }
}
