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

import java.util.Arrays;
import java.util.List;

import net.sf.stackwrap4j.enums.Order;
import net.sf.stackwrap4j.query.sort.BaseSort;

/**
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public class SearchQuery extends TaggedQuery {

	// TODO: Parameter validation on all set methods.
	
    private static final long serialVersionUID = -1260731004294644012L;
    
    private static final int page = 1;
	private static final int pageSize = 30;
	private static final String inTitle = "";
	private static final String notTagged = "";
	private static final Order order = Order.DESC;
	
	public SearchQuery(){
		super(Sort.activity());
	}
	
    @Override
    public SearchQuery restoreDefaults() {
        put("page", page + "");
        put("pagesize", pageSize + "");
        put("order", order.toString());

        put("intitle", inTitle + "");

        put("nottagged", notTagged + "");
        return this;
    }
	

	/**
	 * @param inTitle A string that must appear verbatim in the title of a question
	 */
	public SearchQuery setInTitle(String inTitle) {
	    put("intitle", inTitle + "");
	    return this;
	}
    
	/**
	 * @param notTagged List of tags that must not be on a question. Tags can be space, comma, or semi-colon delimited.
	 */
	public SearchQuery setNotTagged(String notTagged) {
	    if( notTagged == null || notTagged.equals("") ) {
	        return this;
	    }
	    notTagged = notTagged.trim();
	    notTagged = notTagged.replaceAll(" ", ";");
	    put("nottagged", notTagged);
	    return this;
	}

	/**
	 * 
	 * @param tags List of tags that must not be on a question.
	 * @return
	 */
	public SearchQuery setNotTagged(String[] tags){
	    return setNotTagged(Arrays.asList(tags));
	}
	
	/**
     * 
     * @param tags List of tags that must not be on a question.
     * @return
     */
	public SearchQuery setNotTagged(List<String> tags){
	    return setNotTagged(combine(tags, ';'));
	}
	
	private static String combine(List<String> tags, char delim){
	    StringBuilder ret= new StringBuilder();
	    for(String tag: tags){
	        ret.append(tag).append(delim);
	    }
	    return ret.substring(0, ret.length() - 1);
	}
	
	/**
	 * @param order How the current collection should be ordered.
	 */
	public SearchQuery setOrder(Order order) {
	    put("order", order.toString());
	    return this;
	}

	// How a collection should be sorted. Various values include activity, creation, views, votes.
	// one of activity (default), views, creation, or votes
	public static class Sort extends BaseSort {
	    
        private static final long serialVersionUID = 9048461541954269038L;

        // ACTIVITY("activity"), VIEWS("views"), CREATION("creation"), VOTES("votes");
    	protected Sort(String name, Object defaultMin, Object defaultMax) {
            super(name, defaultMin, defaultMax);
        }

    	public static final Sort creation() {
            return new Sort("creation", 0L + "", 253402300799L + "");
        }
    	
    	public static final Sort activity() {
            return new Sort("activity", 0L + "", 253402300799L + "");
        }

    	public static final Sort votes() {
            return new Sort("votes", Integer.MIN_VALUE + "", Integer.MAX_VALUE + "");
        }
    	
    	public static final Sort views() {
            return new Sort("views", 0 + "", Integer.MAX_VALUE + "");
        }
	}
}
