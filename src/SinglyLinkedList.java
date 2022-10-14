
/**
 *
 * @author Rich
 * @version 03/16/2017
 * This class consist of method for creations of nodes, and methods on how to manipulate nodes
 * data structure.
 */
public class SinglyLinkedList<E> {
    private static class Node<E>{
        private E element;
        private Node<E> next;
        
        public Node(E e, Node<E> n)
        {
            element = e;
            next = n;
        }
        /**
         * 
         * @return  the element in the node.
         */
        public E getElement()
        {
            return element;
        }
        /**
         * 
         * @return the pointer to the next node of the list.
         */
        public Node<E> getNext()
        {
            return next;
        }
        // setters
        /**
         * 
         * @param newNext set the pointer to point to the next item
         */
        public void setNext(Node<E> newNext)
        {
            next = newNext;
        }
            
    }
    //  continuing of the StinglyLinkedList
    
    private Node<E> head = null;    // point to 1st node of the list
    private Node<E>  tail = null;   // pointer to the last node of the list
    private int size = 0;
    
    
    public SinglyLinkedList(){}   // construcst an initially empty list. execute the previous 3 line above;
    
    /**
     * 
     * @return  how many nodes in the list
     */
    public int size()
    {
        return size;
    }
   /**
    * 
    * @return true if list is empty.
    */
    public boolean isEmpty()
    {
        return size==0;
    }
    /**
     * 
     * @return  the first element in the first node
     */
    public E first()
    {
        if (isEmpty())
            return null;
        return head.getElement();
    }
    /**
     * 
     * @return the last element in the list
     */
    public E last()
    {
        if(isEmpty()) return null;
        return tail.getElement();
    }
    /**
     * 
     * @param e generic element to be place as first element
     */
    public void addFirst(E e)
    {
        head = new Node(e,head);
        if (size == 0)
            tail = head;
        size++;
    }
    /**
     * 
     * @param e generic type element to set as last
     */
    public void addLast(E e)
    {
        Node<E> newest = new Node(e, null);
        if(isEmpty())
        {
            head = newest;
        }
        else
            tail.setNext(newest);
        
        tail = newest;
        size++;  
    }
    /**
     * 
     * @return the element in the 1st node removed
     */
    public E removeFirst()
    {
        if (isEmpty())
            return null;
        E answer = head.getElement();
        head = head.getNext();
        size--;
        if(size == 0)
            tail = null;
        return answer;
    }
    
    // my removeLast
    /**
     * 
     * @return  the removed item
     */
    public E removeLast()
    {
       if(isEmpty()) return null;
       
       E answer = tail.getElement();
       
       Node<E> current = head, previous = head;
       
       while(current.getNext() != null)
       {
           previous = current; // the one before to last node/tail.
           current = current.getNext();  
       }
       // after exiting while loop current holds the memRef of tail, it is pointing to tail. current =tail
       previous.setNext(null); // break the chain btw the one- before last and last node.
       tail = previous;
       
       return answer;
    }
    /**
     * 
     * @return String format of the object
     */
    @Override
    public String toString()
    {
        String listElements = "";
      
       Node<E> current = head;
       while(current != null)
       {
           listElements += current.getElement() +"<--";
           current = current.getNext(); // update current to point to next node in the listt.   
       }
       return listElements;
    }
   /**
    * 
    * @param o object ref
    * @return true if two linked list are equal
    */ 
   public boolean equals(Object o)
   {
       if(!(o instanceof SinglyLinkedList))
           return false;
       SinglyLinkedList l = (SinglyLinkedList) o;
       if (size != l.size())
           return false;
       Node<E> sourceCurrentNodePtr = head; // current node pointer/Refvar for the "blueprint" list.
       Node<E> targetCurrentNodePtr = l.head; // identifiers that points to current head of the list we're testing for equality.
       while(sourceCurrentNodePtr != null)
       {
          if(!sourceCurrentNodePtr.getElement().equals(targetCurrentNodePtr.getElement()))
              return false;
          sourceCurrentNodePtr = sourceCurrentNodePtr.getNext(); // updtate memory pointer.; advancing current to next Node.
          targetCurrentNodePtr = targetCurrentNodePtr.getNext(); 
       }
       return true;
   }
   
}