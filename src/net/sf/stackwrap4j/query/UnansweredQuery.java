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

import net.sf.stackwrap4j.query.sort.BaseSort;

/**
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public class UnansweredQuery extends QuestionQuery {
	
    private static final long serialVersionUID = -8521833680710472733L;

    public UnansweredQuery(){
		super();
		setSort(Sort.creation());
	}

	/**
     * Represents many ways to sort unanswered questions
     * 
     * @author Justin Nelson
     * @author Bill Cruise
     */
    public static class Sort extends BaseSort {
        // VOTES("votes"), CREATION("creation");
        
        private static final long serialVersionUID = -6933932996351876361L;

        protected Sort(String name, Object defaultMin, Object defaultMax) {
            super(name, defaultMin, defaultMax);
        }

    	public static final Sort creation() {
            return new Sort("creation", 0L + "", 253402300799L + "");
        }
    	
    	public static final Sort votes() {
            return new Sort("votes", Integer.MIN_VALUE + "", Integer.MAX_VALUE + "");
        }
    }
}
