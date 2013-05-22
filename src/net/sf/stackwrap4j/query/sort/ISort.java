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

package net.sf.stackwrap4j.query.sort;

/**
 * 
 * Interface for providing all of the necessary components of a sort
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public interface ISort {

    /**
     * Gets the name of the sort. Used in the params 'sort' term
     * 
     * @return the name of the sort
     */
    public String getName();

    /**
     * Min value for the sort
     * 
     * @return min value
     */
    public String getMin();

    /**
     * Sets the min value to use
     * 
     * @param min
     *            the min value
     */
    public void setMin(Object min);

    /**
     * Max value for the sort
     * 
     * @return the max value
     */
    public String getMax();

    /**
     * Sets the max value for the sort
     * 
     * @param max
     *            the max
     */
    public void setMax(Object max);

    /**
     * Gets the default value used for the Min
     * 
     * @return the default min
     */
    public String getDefaultMin();

    /**
     * Gets the default value used for the Max
     * 
     * @return the default max
     */
    public String getDefaultMax();

    /**
     * Sets the min and max of this sort back to the defaults.<br />
     * The defaults are usually set to return the maximum number of results possible.
     */
    public void restoreDefaults();
}
