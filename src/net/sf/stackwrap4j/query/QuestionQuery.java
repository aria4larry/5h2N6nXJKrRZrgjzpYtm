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
import net.sf.stackwrap4j.query.sort.ISort;

/**
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 * 
 */
public class QuestionQuery extends TaggedQuery {

    // TODO: Parameter validation on all set methods.

    private static final long serialVersionUID = -6104910926423020413L;

    private static final int page = 1;
    private static final int pageSize = 30;
    private static final Order order = Order.DESC;

    // default from date and to date define the min and max date range allowed.
    private static final long DEFAULT_FROM_DATE = 0L;
    private static final long DEFAULT_TO_DATE = 253402300799L;

    public QuestionQuery() {
        super(Sort.activity());
    }

    public QuestionQuery(ISort defaultSort) {
        super(defaultSort);
    }

    @Override
    public QuestionQuery restoreDefaults() {
        put("page", page + "");
        put("pagesize", pageSize + "");
        put("order", order.toString());
        put("fromdate", DEFAULT_FROM_DATE + "");
        put("todate", DEFAULT_TO_DATE + "");
        return this;
    }

    /**
     * @param order
     *            How the current sort should be ordered.
     */
    public QuestionQuery setOrder(Order order) {
        put("order", order.toString());
        return this;
    }

    /**
     * @param fromDate
     *            Unix timestamp of the minimum creation date on a returned
     *            item. unix epoch date, range [0, 253,402,300,799]
     */
    public QuestionQuery setFromDate(long fromDate) {
        validateDateRange(fromDate);
        put("fromdate", fromDate + "");
        return this;
    }

    /**
     * @param toDate
     *            Unix timestamp of the maximum creation date on a returned
     *            item. unix epoch date, range [0, 253,402,300,799]
     */
    public QuestionQuery setToDate(long toDate) {
        validateDateRange(toDate);
        put("todate", toDate + "");
        return this;
    }

    /* Verifies that the date parameter is within the valid range. */
    private void validateDateRange(long date) {
        if (date < DEFAULT_FROM_DATE || date > DEFAULT_TO_DATE) {
            String message = date + " is out of range.\n";
            message += "The argument must be in the range " + DEFAULT_FROM_DATE
                    + " to " + DEFAULT_TO_DATE;
            throw new IllegalArgumentException(message);
        }
    }

    public QuestionQuery setFilter(String filterString) {
        put("filter", filterString);
        return this;
    }

    /**
     * Represents many ways to sort questions
     * 
     * @author Justin Nelson
     * @author Bill Cruise
     */
    public static class Sort extends BaseSort {
        // ACTIVITY("activity"), VOTES("votes"), CREATION("creation"),
        // FEATURED("featured"), HOT("hot"), WEEK("week"), MONTH("month");

        // featured means sorted by the date the bounty closes.

        private static final long serialVersionUID = 1639268298469651097L;

        protected Sort(String name, Object defaultMin, Object defaultMax) {
            super(name, defaultMin, defaultMax);
        }

        public static final Sort creation() {
            return new Sort("creation", 0L + "", 253402300799L + "");
        }

        public static final Sort activity() {
            return new Sort("activity", 0L + "", 253402300799L + "");
        }

        public static final Sort featured() {
            return new Sort("featured", 0L + "", 253402300799L + "");
        }

        public static final Sort votes() {
            return new Sort("votes", Integer.MIN_VALUE + "", Integer.MAX_VALUE
                    + "");
        }

        public static final Sort hot() {
            return new Sort("hot", Integer.MIN_VALUE + "", Integer.MAX_VALUE
                    + "");
        }

        public static final Sort week() {
            return new Sort("week", Integer.MIN_VALUE + "", Integer.MAX_VALUE
                    + "");
        }

        public static final Sort month() {
            return new Sort("month", Integer.MIN_VALUE + "", Integer.MAX_VALUE
                    + "");
        }

    }
}
