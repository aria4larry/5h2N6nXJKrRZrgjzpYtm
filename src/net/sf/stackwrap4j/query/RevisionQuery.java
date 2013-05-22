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

import net.sf.stackwrap4j.exceptions.ParameterNotSetException;

/**
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public class RevisionQuery extends BaseQuery {
	
	// TODO: Parameter validation on all set methods.
	
    private static final long serialVersionUID = -8659710454779423223L;
    
    /* The revision guid. */
	private String guid = "";
	
	public RevisionQuery() {
		super(null);
	}

    @Override
    public RevisionQuery restoreDefaults() {
        put("fromdate", Long.toString(0));
        put("todate", Long.toString(253402300799L));
        return this;
    }
	

	/**
	 * Sets the revision guid.
	 * @param guid the guid to set
	 * @throws IllegalArgumentException if the guid is not in the correct format.
	 */
	public RevisionQuery setGuid(String guid) throws IllegalArgumentException {
	    if( !isValidGuid(guid) ) {
	        throw new IllegalArgumentException("Invalid GUID format: " + guid);
	    }
	    this.guid = guid;
	    return this;
	}

	/**
	 * @param fromDate start date to list revisions from
	 * unix epoch date, range [0, 253,402,300,799]
	 */
	public RevisionQuery setFromDate(long fromDate) {
		put( "fromdate", Long.toString(fromDate) );
		return this;
	}

	/**
	 * @param toDate date to stop listing revisions at
	 * unix epoch date, range [0, 253,402,300,799]
	 */
	public RevisionQuery setTodate(long toDate) {
		put( "todate", Long.toString(toDate) );
		return this;
	}

    public String getGuid() throws ParameterNotSetException {
        if( guid == null || guid.equals("") ) {
            throw new ParameterNotSetException("The revision GUID was not set.");
        }
        return guid;
    }
    
    public static boolean isValidGuid(String guid) {
        return guid.matches("^[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}$");
    }
}
