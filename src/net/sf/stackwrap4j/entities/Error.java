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

package net.sf.stackwrap4j.entities;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;

/**
 * Represents an error.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public class Error extends StackObjBase {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -2659788451363020513L;
    
    /** The numeric ID for this error. */
	private int code;
	
	/** The display message for this error. */
    private String message;

    /**
     * Creates a new Error object from the JSON string provided.
     * @param json string containing the error.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the JSON string was poorly formatted.
     */
    public Error(final String json, final StackWrapper originator) throws JSONException {
        this(new JSONObject(json), originator);
    }

    /**
     * Creates a new Error object from the JSON object provided.
     * @param jE object containing the error.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the original JSON string was poorly formatted.
     */
    public Error(final JSONObject jE, final StackWrapper originator) throws JSONException {
        super(originator);
        code = jE.getInt("code");
        message = jE.getString("message");
    }

    /**
     * Gets the numeric ID code for this error.
     * @return the id code.
     */
    public final int getCode() {
        return code;
    }

    /**
     * Gets the display message for this error.
     * @return the display message.
     */
    public final String getMessage() {
        return message;
    }

    @Override
    public final String toString() {
        final int charactersToDisplay = 20;
        return "Code: " + code + ", Message: " + message.substring(0, charactersToDisplay) + "...";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + code;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Error)) {
            return false;
        }
        Error other = (Error) obj;
        if (code != other.code) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        return true;
    }
}
