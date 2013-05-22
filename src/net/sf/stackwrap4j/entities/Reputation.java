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
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;


/**
 * Represents a reputation change record for a user.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Reputation extends StackObjBase {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -3262340595944119809L;
    
    /** Default page number (start at the first page). */
	public static final int DEFAULT_PAGE = 1;
	
	/** Default page size. */
    public static final int DEFAULT_PAGE_SIZE = 30;
    
    /** Default from date is the Unix epoch. */
    public static final long DEFAULT_FROM_DATE = 0L;
    
    /** Default to date is the maximum date value. */
    public static final long DEFAULT_TO_DATE = 253402300799L;
    
    /** The numeric ID of the post. */
    private int postId;
    
    /** The type of post. */
    private String postType = "";
    
    /** The title of the post. */
    private String title = "";
    
    /** Positive reputation earned on the post. */
    private int positiveRep;
    
    /** Negative reputation earned on the post. */
    private int negativeRep;
    
    /** The date of the reputation change. */
    private long onDate;

    
    /**
     * Construct a Reputation object from a JSON string.
     * @param json string containing reputation changes.
     * @param originator the StackExchange instance that created this
     * @throws JSONException if there's a problem parsing the string.
     */
    Reputation(final String json, final StackWrapper originator) throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }

    /**
     * Construct a Reputation object from a JSON object.
     * @param repObj JSON object containing reputation changes.
     * @param originator the StackExchange instance that created this
     * @throws JSONException if an invalid parameter is requested.
     */
    Reputation(final JSONObject repObj, final StackWrapper originator) throws JSONException {
        super(originator);
        postId = repObj.getInt("post_id");
        postType = repObj.getString("post_type");
        title = repObj.getString("title");
        positiveRep = repObj.getInt("positive_rep");
        negativeRep = repObj.getInt("negative_rep");
        onDate = repObj.getLong("on_date");
    }
    
    /**
     * Gets the negative reputation change.
     * @return negative rep
     */
    public final int getNegativeRep() {
        return negativeRep;
    }

    /**
     * Gets the date the change occurred.
     * @return the date
     */
    public final long getOnDate() {
        return onDate;
    }

    /**
     * Gets the positive reputation change.
     * @return positive rep
     */
    public final int getPositiveRep() {
        return positiveRep;
    }

    /**
     * Gets the post id for this change.
     * @return the post id
     */
    public final int getPostId() {
        return postId;
    }

    /**
     * Gets the post type.
     * @return the post type.
     */
    public final String getPostType() {
        return postType;
    }

    /**
     * Gets the title of the post.
     * @return the title
     */
    public final String getTitle() {
        return title;
    }
    
    /**
     * Extracts a list of Reputation changes from a JSON string.
     * @param json string containing changes
     * @param originator the StackExchange instance that created this
     * @return the list of changes.
     * @throws JSONException if there's a problem parsing the string.
     */
    public static List<Reputation> fromJSONString(final String json, final StackWrapper originator) 
            throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }

    /**
     * Extracts a list of Reputation changes from a JSON object.
     * @param arr array containing changes
     * @param originator the StackExchange instance that created this
     * @return the list of changes.
     * @throws JSONException if there's a problem parsing the original string.
     */
    protected static List<Reputation> fromJSONArray(final JSONArray arr, final StackWrapper originator) 
            throws JSONException {
        List<Reputation> ret = new ArrayList<Reputation>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Reputation(arr.getJSONObject(i), originator));
        }
        return ret;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + negativeRep;
        result = prime * result + (int) (onDate ^ (onDate >>> 32));
        result = prime * result + positiveRep;
        result = prime * result + postId;
        result = prime * result
                + ((postType == null) ? 0 : postType.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        if (!(obj instanceof Reputation)) {
            return false;
        }
        Reputation other = (Reputation) obj;
        if (negativeRep != other.negativeRep) {
            return false;
        }
        if (onDate != other.onDate) {
            return false;
        }
        if (positiveRep != other.positiveRep) {
            return false;
        }
        if (postId != other.postId) {
            return false;
        }
        if (postType == null) {
            if (other.postType != null) {
                return false;
            }
        } else if (!postType.equals(other.postType)) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        return true;
    }
}
