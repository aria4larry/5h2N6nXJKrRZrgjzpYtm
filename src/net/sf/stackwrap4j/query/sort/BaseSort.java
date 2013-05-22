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

import java.io.Serializable;

/**
 * Implements everything necessary for a Sort.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public abstract class BaseSort implements ISort, Serializable {

    private static final long serialVersionUID = -8756530435595635505L;

    private final Object dMin, dMax;

    private Object min;
    private Object max;
    private String name;

    protected BaseSort(String name, Object defaultMin, Object defaultMax) {
        this.name = name;
        this.min = this.dMin = defaultMin;
        this.max = this.dMax = defaultMax;
    }

    public final String getDefaultMax() {
        return dMax.toString();
    }

    public final String getDefaultMin() {
        return dMin.toString();
    }

    public String getName() {
        return name;
    }

    public String getMax() {
        return max.toString();
    }

    public String getMin() {
        return min.toString();
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public void restoreDefaults() {
        min = getDefaultMin();
        max = getDefaultMax();
    }

}
