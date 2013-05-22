package net.sf.stackwrap4j.datastructures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.StackObjBase;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.PageQuery;

/**
 * An AutoFetch list is a sophisticated wrapper around a PagedQuery.
 * It automates the task of fetching multiple pages of a Query.
 * 
 * @author Justin Nelson, Bill Cruise
 *
 */
public abstract class AutoFetchList<E extends StackObjBase> implements List<E> {

    private static final long serialVersionUID = 6305205644027871658L;

    private boolean hasMorePages = true;

    protected int total = -1;

    protected StackWrapper sw;
    protected PageQuery query;
    protected ArrayList<E> data;

    /**
     * Creates a new AutoFetchList using the given StackWrapper to make calls.
     * 
     * @param sw an instance of StackWrapper to query
     * @param query a PageQuery to use as the query parameters
     */
    public AutoFetchList(StackWrapper sw, PageQuery query) {
        if (query.getPage() != 1)
            throw new IllegalStateException(
                    "In order to use an AutoFetch list, you must start at the first page.");
        data = new ArrayList<E>();
        this.query = query;
        this.sw = sw;
        updateData();
    }

    private boolean updateData() {
        if (!hasMorePages)
            return false;
        MetadataList<E> tempList = null;
        try {
            tempList = fetchMoreData();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParameterNotSetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hasMorePages = tempList.getPage() <= tempList.getPageSize();
        total = tempList.getTotal();
        data.addAll(tempList);
        return hasMorePages;
    }

    private void fetchAllData() {
        while (updateData())
            ;
    }

    /**
     * Instructs the List to fetch another page of results from the API<br />
     * Implementing classes should decide which API method they will use to fetch another page of data.
     * @return a List containing the next page of data from the query
     * @throws IOException
     * @throws JSONException
     * @throws ParameterNotSetException
     */
    public abstract MetadataList<E> fetchMoreData() throws IOException,
            JSONException, ParameterNotSetException;

    public boolean contains(Object arg0) {
        return indexOf(arg0) != -1;
    }

    public boolean containsAll(Collection<?> arg0) {
        for (Object o : arg0) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    public E get(int arg0) {
        if (arg0 >= total)
            throw new IndexOutOfBoundsException();
        while (data.size() <= arg0)
            updateData();
        return data.get(arg0);
    }

    public int indexOf(Object arg0) {
        do {
            int idx = data.indexOf(arg0);
            if (idx != -1)
                return idx;
        } while (updateData());
        return -1;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int lastIndexOf(Object arg0) {
        fetchAllData();
        return data.lastIndexOf(arg0);
    }

    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    public ListIterator<E> listIterator(int arg0) {
        // TODO bad way to implement
        fetchAllData();
        return data.listIterator(arg0);
    }

    public int size() {
        return total;
    }

    public List<E> subList(int arg0, int arg1) {
        // TODO bad way to implement
        fetchAllData();
        return data.subList(arg0, arg1);
    }

    public Object[] toArray() {
        fetchAllData();
        return data.toArray();
    }

    public <T> T[] toArray(T[] arg0) {
        fetchAllData();
        return data.toArray(arg0);
    }

    public Iterator<E> iterator() {
        return listIterator();
    }

    // ////// Unsupported methods below

    public boolean add(E arg0) {
        throw new UnsupportedOperationException(
                "Cannot add to this type of list");
    }

    public void add(int arg0, E arg1) {
        throw new UnsupportedOperationException(
                "Cannot add to this type of list");
    }

    public boolean addAll(Collection<? extends E> arg0) {
        throw new UnsupportedOperationException(
                "Cannot add to this type of list");
    }

    public boolean addAll(int arg0, Collection<? extends E> arg1) {
        throw new UnsupportedOperationException(
                "Cannot add to this type of list");
    }

    public void clear() {
        throw new UnsupportedOperationException(
                "Cannot remove from this type of list");
    }

    public boolean remove(Object arg0) {
        throw new UnsupportedOperationException(
                "Cannot remove from this type of list");
    }

    public E remove(int arg0) {
        throw new UnsupportedOperationException(
                "Cannot remove from this type of list");
    }

    public boolean removeAll(Collection<?> arg0) {
        throw new UnsupportedOperationException(
                "Cannot remove from this type of list");
    }

    public boolean retainAll(Collection<?> arg0) {
        throw new UnsupportedOperationException(
                "Cannot remove from this type of list");
    }

    public E set(int arg0, E arg1) {
        throw new UnsupportedOperationException(
                "Cannot modify this type of list");
    }

}
