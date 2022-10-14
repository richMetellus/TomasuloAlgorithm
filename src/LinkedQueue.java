/**
 *
 * @version 10/20/2017
 * @author Rich
 * @param <E>  Generic Type that will be of type Instruction for this project.
 */
public class LinkedQueue<E> implements Queue<E> {
    
    // create an emtpy queue/list
    private SinglyLinkedList<E> list = new SinglyLinkedList<>();
    
    public LinkedQueue(){}          // new queue relies on the initially empty list
    @Override
    public int size(){return list.size();}
    @Override
    public boolean isEmpty(){return list.isEmpty();}
    @Override
    public void enqueue(E element){list.addLast(element);}
    @Override
    public E first(){return list.first();}
    @Override
    public E dequeue(){return list.removeFirst();}
    
    @Override
    public String toString()
    {
        return list.toString();
    }
}
