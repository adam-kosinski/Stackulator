public interface IStack<T> extends Iterable<T>
{
	public void push(T newItem);
	public T pop();
	public T peek();
	public void clear();
	public int size();
	public boolean isEmpty();
}