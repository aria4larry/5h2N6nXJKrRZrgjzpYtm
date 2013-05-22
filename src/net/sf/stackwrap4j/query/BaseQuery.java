/**
 * StackWrap4J - A Java wrapper for the Stack Exchange API.
 * 
 * Copyright (c) 2010 Bill Cruise and Justin Nelson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.sf.stackwrap4j.query;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.query.sort.BaseSort;
import net.sf.stackwrap4j.query.sort.ISort;

//TODO: Getters maybe?

/**
 * Class representing a general query to the Stack Exchange API.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 *
 */
public abstract class BaseQuery implements Serializable {
	
    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = 3269490908968253120L;

    /** A map of the URL parameters for this query. */
	private Map<String, String> urlParamMap = new HashMap<String, String>();
	
	/** The set of ids for this query. */
	protected Set<Integer> idSet = new HashSet<Integer>();
	
	/** The way to sort the results. */
	private ISort sort = null;
	
	/**
	 * Creates a Query using the provided sort implementation.
	 * @param defaultSort order to sort the returned elements.
	 */
	protected BaseQuery(final ISort defaultSort) {
		sort = defaultSort;
		restoreDefaults();
	}
	
	/**
	 * Abstract method to restore defaults that must be implemented by child classes.
	 * @return a new Query with the default settings restored.
	 */
	public abstract BaseQuery restoreDefaults();
	
	/**
	 * Gets the URL parameters for this query object.
	 * @return a String of URL parameters in '&key=value' form.
	 */
	public String getUrlParams() {
		String urlParams = "";
		for (String key : urlParamMap.keySet()) {
			urlParams += "&" + key + "=" + urlParamMap.get(key);
		}
		urlParams = addSortParams(urlParams, sort);
        return urlParams;
	}
	
	/**
	 * Adds an id or multiple ids to this query.
	 * @param id
	 */
	public void addId(final int... id) {
		for (int i : id) {
			idSet.add(i);
		}
	}
	
	/**
	 * Sets the ids for this query to the one(s) provided.
	 * Removes any ids that are set before the method is called.
	 * @param id an id or multiple ids to set.
	 * @return the same Query with the id values set.
	 */
	public BaseQuery setIds(final int...id){
	    removeIds();
	    addId(id);
	    return this;
	}
	
	/**
	 * Empties the set of ids for this Query.
	 */
	public void removeIds() {
	    idSet = new HashSet<Integer>();
	}
	
	/**
	 * Gets the String representation of the List of ids for this query.
	 * @return a semicolon-delimited list of identifiers.
	 * @throws ParameterNotSetException if not identifiers were set in this query.
	 */
	public String getIds() throws ParameterNotSetException {
	    if (idSet.size() == 0) {
	        throw new ParameterNotSetException("No ids have been added to the query.");
	    }
		
	    StringBuilder sb = new StringBuilder("");
		for (int id : idSet) {
		    sb.append(id + ";");
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	
	/**
	 * Sets the current way to sort the query.
	 * @param sort How a collection should be sorted.
	 */
	public BaseQuery setSort(final BaseSort sort) {
		this.sort = sort;
		return this;
	}
	
	/**
	 * Gets the sort currently used in this query.
	 * @return the current sort order of the collection.
	 */
	public ISort getSort(){
		return sort;
	}
	
	/**
	 * puts a new value into this query.
	 * @param key
	 * @param value
	 * @return
	 */
	protected String put(final String key, final String value) {
	    String encodedVal = value;
	    try {
	        encodedVal = URLEncoder.encode(value, "UTF-8");
	    } catch (UnsupportedEncodingException uee) {
	        // do nothing.  if UTF-8 isn't supported, don't encode the input.
	    }
	    
	    return urlParamMap.put(key, encodedVal);
	}
	
	/**
	 * Gets the current value for this key.
	 * @param key the name of the parameter.
	 * @return the value associated with the provided key.
	 */
	protected String get(final String key){
	    return urlParamMap.get(key);
	}

	/**
	 * Build a vectorized list of ids.
	 * @param id an id or multiple ids to add to the list.
	 * @return a string containing all ids.
	 */
	protected static String buildVectorizedList(final int... id) {
        StringBuilder sb = new StringBuilder("" + id[0]);
        for (int i = 1; i < id.length; ++i) {
            sb.append(";" + id[i]);
        }
        return sb.toString();
    }

    /**
     * Appends a parameter to the URL string provided.
     * 
     * @param urlParameters original string of parameters to append a new name/value pair to.
     * @param name to add to the URL.
     * @param value to add to the URL.
     * @return the URL with the new parameter appended.
     */
	private static String addParameter(final String urlParameters, final String name, final String value) {
        return urlParameters + "&" + name + "=" + value;
    }
	
	/**
	 * Appends all of the necessary sort parameters to the urlParameters.
	 * @param urlParameters original string of parameters to append a new name/value pairs to.
	 * @param sortParams object containing the sort value parameters.
	 * @return old URL parameters with sort parameters appended.
	 */
	private String addSortParams(final String urlParameters, final ISort sortParams) {
	    String params = urlParameters;
		if (sortParams != null) {
		    params = addParameter(params, "sort", sortParams.getName());
		    params = addParameter(params, "min", sortParams.getMin());
		    params = addParameter(params, "max", sortParams.getMax());
		}
		return params;
	}
}
