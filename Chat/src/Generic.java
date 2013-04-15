
public class Generic<T> 
{
	private T m_t;
	
	public Generic()
	{
		
	}
	
	public T type()
	{
		return m_t;
	}
	
	public T type(T t)
	{
		m_t = t;
		return t;
	}
}
