package electionMVC.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Set <T> implements Collection <T>, Serializable{
	private static final long serialVersionUID = 1L;
	private List <T> list = new ArrayList<T>();
	
	
	public <T> T get(int index){
		return (T) list.get(index);
	}
	
	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator <T> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <S> S[] toArray(S[] a) {
		return list.toArray(a);
	}
	
	@Override
	public boolean add(T citizen) { 
		if (list.contains(citizen)) {
			return false;
		}
		return list.add(citizen);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean addOneAtLeast = false;
		for (T t : c) {
			if (add(t)) {
				addOneAtLeast = true;
			}
		}
		return addOneAtLeast;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public void clear() {
		list.clear();
	}
}
