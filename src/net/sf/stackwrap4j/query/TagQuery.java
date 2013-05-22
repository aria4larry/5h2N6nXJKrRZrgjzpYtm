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

import net.sf.stackwrap4j.enums.Order;
import net.sf.stackwrap4j.query.sort.BaseSort;

/**
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public class TagQuery extends PageQuery {

	// TODO: Parameter validation on all set methods.
	
    private static final long serialVersionUID = -2300899500828435451L;

    public TagQuery() {
		super(Sort.popular());
		
		// filter is not added by default so it won't be sent in the URL.
	}

    @Override
    public TagQuery restoreDefaults() {
        put("page", Integer.toString(1));
        put("pagesize", Integer.toString(70));
        put("order", Order.DESC.toString());
        put("fromdate", Long.toString(0));
        put("todate", Long.toString(253402300799L));
        return this;
    }
    

	/**
	 * @param order How a collection should be ordered.
	 */
	public TagQuery setOrder(Order order) {
		put( "order", order.toString() );
		return this;
	}

	/**
	 * @param filter Required text in returned tags.
	 */
	public TagQuery setFilter(String filter) {
		put( "filter", filter );
		return this;
	}

	/**
	 * @param fromDate Unix timestamp of the minimum creation date on a returned item.
	 * unix epoch date, range [0, 253,402,300,799]
	 */
	public TagQuery setFromDate(long fromDate) {
		put( "fromdate", Long.toString(fromDate) );
		return this;
	}

	/**
	 * @param toDate Unix timestamp of the maximum creation date on a returned item.
	 * unix epoch date, range [0, 253,402,300,799]
	 */
	public TagQuery setToDate(long toDate) {
		put( "todate", Long.toString(toDate) );
		return this;
	}

	public static class Sort extends BaseSort {

        private static final long serialVersionUID = 7465472393658051288L;
        
        //POPULAR("popular"), ACTIVITY("activity"), NAME("name");
		protected Sort(String name, Object defaultMin, Object defaultMax) {
            super(name, defaultMin, defaultMax);
        }

		public static final Sort activity() {
            return new Sort("activity", 0L + "", Long.MAX_VALUE + "");
        }
		
		public static final Sort popular() {
            return new Sort("popular", 0 + "", Integer.MAX_VALUE + "");
        }
		
		public static final Sort name() {
            return new Sort("name", "", DEFAULT_NAME_MAX);
        }
		
        private static final char mv = Character.MAX_VALUE;
        public final static String DEFAULT_NAME_MAX = "" + mv + mv + mv + mv + mv;
    }
}
