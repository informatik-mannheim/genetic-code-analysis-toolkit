/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
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
package de.hsma.gentool.gui.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

public abstract class ListTableModel<E> extends AbstractTableModel implements List<E> {
	private static final long serialVersionUID = 0x4c0def0e;

  protected EventListenerList listenerList = new EventListenerList();

	private List<E> list = new ArrayList<E>();
	
	@Override public int getRowCount() { return size(); }
	@Override public Object getValueAt(int rowIndex,int columnIndex) {
		return getValueAt(get(rowIndex),columnIndex);
	}
	public abstract Object getValueAt(E element,int columnIndex);	
	
  public void addListDataListener(ListDataListener listener) { listenerList.add(ListDataListener.class, listener); }
  public void removeListDataListener(ListDataListener listener) { listenerList.remove(ListDataListener.class, listener); }
  public ListDataListener[] getListDataListeners() { return listenerList.getListeners(ListDataListener.class); }
  public <T extends EventListener> T[] getListeners(Class<T> listenerType) { return listenerList.getListeners(listenerType); }
  
  protected void fireListContentsChanged(Object source, int fromIndex, int toIndex) {
  	ListDataEvent event = null;
  	Object[] listeners = listenerList.getListenerList();
  	for(int listener=listeners.length-2;listener>=0;listener-=2)
  		if(listeners[listener]== ListDataListener.class) {
  			if(event == null)
  				event = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, fromIndex, toIndex);
  			((ListDataListener)listeners[listener+1]).contentsChanged(event);
  		}
  	/* list event -> table event */
  	fireTableRowsUpdated(fromIndex,toIndex);
  }
  protected void fireListIntervalAdded(Object source, int fromIndex, int toIndex) {
  	ListDataEvent event = null;
  	Object[] listeners = listenerList.getListenerList();
  	for(int listener=listeners.length-2;listener>=0;listener-=2)
  		if(listeners[listener]== ListDataListener.class) {
  			if(event == null)
  				event = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, fromIndex, toIndex);
  			((ListDataListener)listeners[listener+1]).intervalAdded(event);
  		}
  	/* list event -> table event */
  	fireTableRowsInserted(fromIndex,toIndex);
  }
  protected void fireListIntervalRemoved(Object source, int fromIndex, int toIndex) {
  	ListDataEvent event = null;
  	Object[] listeners = listenerList.getListenerList();
  	for(int listener=listeners.length-2;listener>=0;listener-=2)
  		if(listeners[listener]== ListDataListener.class) {
  			if(event == null)
  				event = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, fromIndex, toIndex);
  			((ListDataListener)listeners[listener+1]).intervalRemoved(event);
  		}
  	/* list event -> table event */
  	fireTableRowsDeleted(fromIndex,toIndex);
  }
	
	@Override public int size() { return list.size(); }
	@Override public boolean isEmpty() { return list.isEmpty();	}
	@Override public boolean contains(Object object) { return list.contains(object); }
	@Override public Iterator<E> iterator() { return list.iterator(); }
	@Override public Object[] toArray() { return list.toArray(); }
	@Override public <T> T[] toArray(T[] array) { return list.toArray(array); }
	@Override public boolean add(E element) {
		int index = size(); list.add(element);
		fireListIntervalAdded(this,index,index);
		return true;
	}
	@Override public boolean remove(Object object) {
		int index = indexOf(object);
    boolean remove = list.remove(object);
    if(index>=0) fireListIntervalRemoved(this,index,index);
    return remove;
	}
	@Override public boolean containsAll(Collection<?> collection) { return list.containsAll(collection); }
	@Override public boolean addAll(Collection<? extends E> collection) {
		int indexFrom = size(),indexTo;
		boolean add = list.addAll(collection);
		if((indexTo=size()-1)>=indexFrom)
			fireListIntervalAdded(this,indexFrom,indexTo);
		return add;
	}
	@Override public boolean addAll(int index,Collection<? extends E> collection) {
		int sizeOld = size(), sizeNew;
		boolean add = list.addAll(index,collection);
		if((sizeNew=size())!=sizeOld)
			fireListIntervalAdded(this,index,index+(sizeNew-sizeOld));
		return add;
	}
	@Override public boolean removeAll(Collection<?> collection) {
		boolean remove = list.removeAll(collection);
		if(remove) fireListContentsChanged(this,0,size()-1);
		return remove; 
	}
	@Override public boolean retainAll(Collection<?> collection) {
		boolean retain = list.retainAll(collection);
		if(retain) fireListContentsChanged(this,0,size()-1);
		return retain;
	}
	@Override public void clear() {
    int index = size()-1; list.clear();
    if(index>=0) fireListIntervalRemoved(this,0,index);
	}
	@Override public E get(int index) { return list.get(index); }
	@Override public E set(int index,E element) {
		E set = list.set(index,element);
		fireListContentsChanged(this,index,index);
		return set;
	}
	@Override public void add(int index,E element) {
		list.add(index,element);
		fireListIntervalAdded(this,index,index);
	}
	@Override public E remove(int index) {
    E remove = list.remove(index);
    fireListIntervalRemoved(this,index,index);
    return remove;
	}
	@Override public int indexOf(Object object) { return list.indexOf(object); }
	@Override public int lastIndexOf(Object object) { return list.lastIndexOf(object); }
	@Override public ListIterator<E> listIterator() { return list.listIterator(); }
	@Override public ListIterator<E> listIterator(int index) { return list.listIterator(index); }
	@Override public List<E> subList(int fromIndex,int toIndex) { return list.subList(fromIndex,toIndex); }
}
