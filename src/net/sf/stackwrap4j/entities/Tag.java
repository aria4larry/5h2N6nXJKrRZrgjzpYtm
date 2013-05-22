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

import java.util.ArrayList;
import java.util.List;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.enums.Order;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;

/**
 * Represents a tag on on of the Stack Exchange sites.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Tag extends StackObjBase {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -5455407162132896992L;
    
	/** The default page of a result for a Tag. */
    public static final int DEFAULT_PAGE = 1;
    
    /** The default page size of a result for a Tag. */
    public static final int DEFAULT_PAGE_SIZE = 70;
    
    /** The default order for a list of Tags. */
    public static final Order DEFAULT_ORDER = Order.DESC;
    
    /** The default start date for a Tag. */
    public static final long DEFAULT_FROM_DATE = 1;
    
    /** The default end date for a Tag. */
    public static final long DEFAULT_TO_DATE = 9223372036854775807L;
    
    /** The default filter for a Tag. */
    public static final String DEFAULT_FILTER = "";

    /** The name of the tag. */
    private String name = "";
    
    /** The number of questions with the tag. */
    private int count;
    
    /** User associated with this tag, depends on context. Optional. */
    private Integer userId;

    /**
     * Constructs a new Tag from a JSON formatted String.
     * @param json The string containing the Tag data
     * @param originator the StackExchange instance that created this Tag
     * @throws JSONException if the JSON string is not properly formatted
     */
    Tag(final String json, final StackWrapper originator) throws JSONException {
        // assumes one object in the array.
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }

    /**
     * Constructs a new Tag from a JSONObject representing a Tag.
     * @param jT The JSONObject representing a Tag
     * @param originator the StackExchange instance that created this Tag
     * @throws JSONException if something goes really wrong...(The API probably changed without us knowing)
     */
    Tag(final JSONObject jT, final StackWrapper originator) throws JSONException {
        super(originator);
        name = jT.getString("name");
        count = jT.getInt("count");
        PoliteJSONObject pjt = new PoliteJSONObject(jT);
        userId = pjt.tryGetInteger("user_id");
    }

    /**
     * @return The number of instances of this Tag
     */
    public final int getCount() {
        return count;
    }

    /**
     * @return The name of the tag
     */
    public final String getName() {
        return name;
    }

    /**
     * @return id of User that this tag corresponds to.  May be null.
     */
    public final Integer getUserId() {
    	return userId;
    }
    
    /**
     * Creates a list of Tags from a JSON string representing a JSONArray.
     * 
     * @param json the String representing the Tags
     * @param originator the StackExchange instance that created this
     * @return a List of Tags represented by the JSON String
     * @throws JSONException if the JSON string is poorly formatted
     */
    public static List<Tag> fromJSONString(final String json, final StackWrapper originator)
            throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * Creates a list of Tags from a JSONArray.
     * 
     * @param jsonArray the JSONArray representing the Tags
     * @param originator the StackExchange instance that created this
     * @return a List of Tags represented by the JSON String
     * @throws JSONException if the JSON string is poorly formatted
     */
    private static List<Tag> fromJSONArray(final JSONArray jsonArray, final StackWrapper originator)
            throws JSONException {
        List<Tag> ret = new ArrayList<Tag>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            ret.add(new Tag(jsonArray.getJSONObject(i), originator));
        }
        return ret;
    }
}
