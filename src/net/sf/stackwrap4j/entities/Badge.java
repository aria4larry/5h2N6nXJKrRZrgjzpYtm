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
 * Represents a badge in the Stack Exchange family of sites.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Badge extends StackObjBase {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -3990124684780217030L;
    
    /** The id number for this type of badge. */
	private int id;
	
	/** Badge rank, e.g., bronze, silver, gold. */
    private String rank;
    
    /** The name of the badge. */
    private String name = "";
    
    /** The description of the badge. */
    private String description = "";
    
    /** The number of times this badge has been awarded. */
    private int awardCount;
    
    /** True if this badge is for a tag, false otherwise. */
    private boolean tagBased;

    /**
     * Construct a Badge object from a JSON string.
     * @param json a string containing badge information
     * @param originator the API that the JSON string originated from.
     * @throws JSONException if the JSON string can't be parsed correctly.
     */
    Badge(final String json, final StackWrapper originator) throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }

    /**
     * Construct a Badge object from a JSON object.
     * @param jsonObject an object containing badge information
     * @param originator the API that the JSON string originated from.
     * @throws JSONException if an invalid attribute is requested.
     */
    Badge(final JSONObject jsonObject, final StackWrapper originator) throws JSONException {
        super(originator);
        this.id = jsonObject.getInt("badge_id");
        this.rank = jsonObject.getString("rank");
        this.name = jsonObject.getString("name");
        this.description = jsonObject.getString("description");
        this.awardCount = jsonObject.getInt("award_count");
        this.tagBased = jsonObject.getBoolean("tag_based");
    }

    /**
     * The number of times this badge has been awarded.
     * @return the number of times this badge has been awarded.
     */
    public final int getAwardCount() {
        return awardCount;
    }

    /**
     * The description of how this badge is earned.
     * @return The description of how to earn this badge.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * The id of the badge.
     * @return the id associated with this badge.
     */
    public final int getId() {
        return id;
    }

    /**
     * The rank of this badge.
     * @return The rank of this badge. (Gold, Silver, Bronze)
     */
    public final String getRank() {
        return rank;
    }

    /**
     * The name of this badge.
     * @return the name of the badge.
     */
    public final String getName() {
        return name;
    }

    /**
     * Whether or not this badge is earned by tag based score.
     * @return whether or not this tag is awarded to performance in a tag.
     */
    public final boolean isTagBased() {
        return tagBased;
    }

    /**
     * Creates a List of badges from a JSON String.
     * @param json a JSON string representing an array of badges
     * @param originator the StackExchange instance that created this list
     * @return a List of badges
     * @throws JSONException if the JSON string is poorly formatted
     */
    public static List<Badge> fromJSONString(final String json, final StackWrapper originator)
            throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }

    /**
     * Creates a List of badges from a JSON array.
     * 
     * @param arr a JSONArray representing an array of badges
     * @param originator the StackExchange instance that created this list
     * @return a List of badges
     * @throws JSONException if the JSON string is poorly formatted
     */
    protected static List<Badge> fromJSONArray(final JSONArray arr, final StackWrapper originator)
            throws JSONException {
        List<Badge> ret = new ArrayList<Badge>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Badge(arr.getJSONObject(i), originator));
        }
        return ret;
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
        result = prime * result + awardCount;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((rank == null) ? 0 : rank.hashCode());
        result = prime * result + (tagBased ? 1231 : 1237);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Badge)) {
            return false;
        }
        Badge other = (Badge) obj;
        if (awardCount != other.awardCount) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (rank == null) {
            if (other.rank != null) {
                return false;
            }
        } else if (!rank.equals(other.rank)) {
            return false;
        }
        if (tagBased != other.tagBased) {
            return false;
        }
        return true;
    }
}