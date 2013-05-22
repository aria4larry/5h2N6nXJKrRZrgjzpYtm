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
public class AnswerQuery extends PageQuery {

	// TODO: Parameter validation on all set methods.
	
    private static final long serialVersionUID = 674891748117685665L;
    
    // Default values below
	private static final int page = 1;
	private static final int pageSize = 30;
	private static final boolean body = false;
	private static final boolean comments = false;
	private static final Order order = Order.DESC;
	private static final long fromdate = 0L;
	private static final long todate = 253402300799L;
	
	public AnswerQuery(){
		super(Sort.activity());
	}
	
	@Override
	public AnswerQuery restoreDefaults(){
	    put("page", page + "");
	    put("pagesize", pageSize + "");
	    put("body", body + "");
	    put("comments", comments + "");
	    put("order", order.toString());
	    put("fromdate", fromdate + "");
	    put("todate", todate + "");
	    return this;
	}

	/**
	 * @param body When "true", a post's body will be included in the response.
	 */
	public AnswerQuery setBody(boolean body) {
		put("body", body + "");
		return this;
	}

	/**
	 * @param comments When "true", any comments on a post will be included in the response.
	 */
	public AnswerQuery setComments(boolean comments) {
		put("comments", comments + "");
		return this;
	}

	/**
	 * @param order How the current sort should be ordered.
	 */
	public AnswerQuery setOrder(Order order) {
		put("order", order.toString());
		return this;
	}

	/**
	 * @param fromdate Unix timestamp of the minimum creation date on a returned item.
	 * unix epoch date, range [0, 253,402,300,799]
	 */
	public AnswerQuery setFromDate(long fromdate) {
		put("fromdate", fromdate + "");
		return this;
	}

	/**
	 * @param todate Unix timestamp of the maximum creation date on a returned item.
	 * unix epoch date, range [0, 253,402,300,799]
	 */
	public AnswerQuery setToDate(long todate) {
		put("todate", todate + "");
		return this;
	}

	/**
     * Sort is used to determine how lists of answers are sorted
     * 
     * @author Justin Nelson, Bill Cruise
     */
    public static class Sort extends BaseSort {
        // ACTIVITY("activity"), VIEWS("views"), CREATION("creation"), VOTES("votes");

        private static final long serialVersionUID = -356385482658677490L;

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
