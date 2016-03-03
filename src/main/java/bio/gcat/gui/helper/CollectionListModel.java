/*
 * Copyright [2016] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.gui.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractListModel;

public class CollectionListModel<E> extends AbstractListModel<E> implements List<E> {
	private static final long serialVersionUID = 1l;
	
	List<E> list = new ArrayList<>();

	@Override public int size() { return list.size(); }
	@Override public boolean isEmpty() { return list.isEmpty(); }
	@Override public boolean contains(Object object) { return list.contains(object); }
	@Override public Iterator<E> iterator() { return list.iterator(); }
	@Override public Object[] toArray() { return list.toArray(); }
	@Override public <T> T[] toArray(T[] array) { return list.toArray(array); }
	@Override public synchronized boolean add(E item) {
		int index = list.size();
		list.add(item);
		change(index);
		return true;
	}
	@Override public synchronized boolean remove(Object object) {
		int index = list.indexOf(object);
		if(index!=-1) {
			list.remove(object);
			change(index);
			return true;
		} else return false;
	}
	@Override public boolean containsAll(Collection<?> collection) { return list.containsAll(collection); }
	@Override public synchronized boolean addAll(Collection<? extends E> collection) {
		int index = list.size();
		if(list.addAll(collection)) {
			change(index,list.size()-1);
			return true;
		} else return false;
	}
	@Override public synchronized boolean addAll(int index,Collection<? extends E> collection) {
		int size = list.size();
		if(list.addAll(index,collection)) {
			change(index,index+(list.size()-size));
			return true;
		} else return false;
	}
	@Override public synchronized boolean removeAll(Collection<?> collection) {
		int size = list.size();
		if(list.removeAll(collection)) {
			change(0,size-1);
			return true;
		} else return false;
	}
	@Override public synchronized boolean retainAll(Collection<?> collection) {
		int size = list.size();
		if(list.retainAll(collection)) {
			change(0,size-1);
			return true;
		} else return false;
	}
	@Override public synchronized void clear() {
		int size = list.size();
		list.clear();
		change(0,size-1);
	}
	@Override public E get(int index) { return get(index); }
	@Override public synchronized E set(int index,E item) {
		E old = list.set(index,item);
		change(index);
		return old;
	}
	@Override public synchronized void add(int index,E item) {
		list.add(index,item);
		change(index+1);
	}
	@Override public synchronized E remove(int index) {
		E old = list.remove(index);
		change(index);
		return old;
	}
	@Override public int indexOf(Object object) { return list.indexOf(object); }
	@Override public int lastIndexOf(Object object) { return list.lastIndexOf(object); }
	@Override public ListIterator<E> listIterator() { return list.listIterator(); }
	@Override public ListIterator<E> listIterator(int index) { return list.listIterator(index); }
	@Override public List<E> subList(int fromIndex,int toIndex) { return list.subList(fromIndex,toIndex); }
	
	public void change(int index) { fireContentsChanged(this,index,index); }
	public void change(int fromIndex,int toIndex) { fireContentsChanged(this,fromIndex,toIndex); }
	public void change(Object object) { int index = list.indexOf(object); if(index!=-1) fireContentsChanged(this,index,index); }
	
	@Override public E getElementAt(int index) { return list.get(index); }
	@Override public int getSize() { return list.size(); }
}