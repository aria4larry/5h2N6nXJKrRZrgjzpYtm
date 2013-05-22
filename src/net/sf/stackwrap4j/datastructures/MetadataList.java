package net.sf.stackwrap4j.datastructures;

import java.util.ArrayList;
import java.util.List;

import net.sf.stackwrap4j.entities.StackObjBase;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;

/**
 * A MetadataList contains information about the data associated with the query that created it.
 * 
 * It encapsulates data about the total number of results, the page this list is from, and the page size of the list
 * @author Justin Nelson
 * @author Bill Cruise
 *
 * @param <E>
 */
public class MetadataList<E extends StackObjBase> extends ArrayList<E> {

    private static final long serialVersionUID = 968623794644895438L;

    private int total, page, pageSize;

    /**
     * Creates a metadata list out of the JSON returned by a query
     * @param json
     * @param objects
     * @throws JSONException
     */
    public MetadataList(String json, List<E> objects) throws JSONException {
        parseValues(json);
        addAll(objects);
    }

    /**
     * Gets the total number of items that can be fetched using the query
     * @return the total number of results possible
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the current page of the query
     * @return the current page
     */
    public int getPage() {
        return page;
    }

    /**
     * Gets the page size of this query
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }
    
    private void parseValues(String json) throws JSONException{
        PoliteJSONObject jOp = new PoliteJSONObject(new JSONObject(json));
        total = jOp.tryGetInt("total", -1);
        page = jOp.tryGetInt("page", -1);
        pageSize = jOp.tryGetInt("pagesize", -1);
    }
}
