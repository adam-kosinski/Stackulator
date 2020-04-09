import java.util.Arrays;
import java.util.Iterator;

public class AStack<T> implements IStack<T>
{
	private Object[] entries;
	private int top;
	public AStack()
	{
		entries = new Object[10];
		top = 0;
	}
	public void push(T newItem)
	{
		if(top == entries.length)
		{
			superSizeMe();
		}
		entries[top] = newItem;
		top++;
	}
	@SuppressWarnings("unchecked")
	public T pop()
	{
		if(isEmpty())
		{
			throw new EmptyStackException();
		}
		T out = (T) entries[top-1];
		entries[top-1] = null;
		top--;
		return out;
	}
	@SuppressWarnings("unchecked")
	public T peek()
	{
		if(top == 0)
		{
			throw new EmptyStackException();
		}
		return (T) entries[top - 1];
	}
	public void clear()
	{
		entries = new Object[10];
		top = 0;
	}
	public int size()
	{
		return top;
	}
	private static int newSize(int n)
	{
		return n<1000? 2*n : 3*n/2;
	}
	private void superSizeMe()
	{
		int bigger = newSize(entries.length);
		entries = Arrays.copyOf(entries, bigger);
	}
	public boolean isEmpty()
	{
		return top == 0;
	}
	@Override
	public String toString()
	{
		if(isEmpty())
		{
			return "[]";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i = top-1; i > 0; i--)
		{
			sb.append(String.format("%s, ", entries[i]));
		}
		sb.append(entries[0]);
		sb.append("]");
		return sb.toString();
	}
	public Iterator<T> iterator()
	{
		return new AStackIterator<T>();
	}
	
	private class AStackIterator<T> implements Iterator<T>
	{
		int start = top - 1;
		
		@SuppressWarnings("unchecked")
		public T next()
		{
			T out = (T) entries[start];
			start--;
			return out;
		}
		public boolean hasNext()
		{
			return start >= 0;
		}
	}
	
	
	public static void main(String[] args)
	{
		AStack<String> s = new AStack<>();
		s.push("a");
		s.push("b");
		s.push("c");
		System.out.println(s);
	}
}


