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
import net.sf.stackwrap4j.json.PoliteJSONObject;


/**
 * Represents a timeline record for a user.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public class UserTimeline extends Timeline {
	
    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -8483559713386148234L;
    
    /** The id for the user. */
	private int userId;
	
	/** The type of timeline. */
	private Type timelineType;
	
	/** The type of post. */
	private PostType postType;
	
	/** The description for display. */
	private String description;
	
	/** Details about the timeline. */
	private String detail;
	
	/**
     * Creates a user timeline from a JSON string.
     * @param json the JSON String representing a user timeline.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the JSON string was poorly formatted.
     */
	UserTimeline(final String json, final StackWrapper originator) throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }
	
	/**
     * Creates a user timeline from a JSON object.
     * @param jUT the JSON object representing a timeline.
     * @param originator the StackExchange instance that created this Answer.
     * @throws JSONException if the JSON string was poorly formatted.
     */
	UserTimeline(final JSONObject jUT, final StackWrapper originator) throws JSONException {
		super(jUT, originator);
		
		PoliteJSONObject jUTp = new PoliteJSONObject(jUT);
		
		userId = jUTp.tryGetInt("user_id", -1);
		
		String type = jUTp.tryGetString("timeline_type");
		if (type != null && !type.equals("")) {
			timelineType = Type.valueOf(type.toUpperCase());
		}
		
		type = jUTp.tryGetString("post_type");
		if (type != null && !type.equals("")) {
			postType = PostType.valueOf(type.toUpperCase());
		}
		
		description = jUTp.tryGetString("description");
		detail = jUTp.tryGetString("detail");
	}
	
	
	
	/**
	 * @return the userId
	 */
	public final int getUserId() {
		return userId;
	}

	/**
	 * @return the timelineType
	 */
	public final Type getTimelineType() {
		return timelineType;
	}

	/**
	 * @return the postType
	 */
	public final PostType getPostType() {
		return postType;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @return the detail
	 */
	public final String getDetail() {
		return detail;
	}


	/**
	 * Enumeration for the types of timeline events.
	 * @author Bill Cruise
	 */
	public static enum Type {
	    /** Enumerate the available types. */
        ACCEPTED("accepted"), ASKORANSWERED("askoranswered"), BADGE("badge"), COMMENT("comment"), REVISION("revision");

        /** The description for display. */
        private String description;

        /**
         * Creates a Type from the description provided.
         * @param desc of the type.
         */
        private Type(final String desc) {
            this.description = desc;
        }

        @Override
        public String toString() {
            return description;
        }
    }
	
	/**
	 * The type of post.
	 * @author Bill Cruise
	 */
	public static enum PostType {
	    /** Enumerate the types of posts. */
        QUESTION("question"), ANSWER("answer");

        /** The description for display. */
        private String description;

        /**
         * Creates a post type from the description provided.
         * @param desc of the post type
         */
        private PostType(final String desc) {
            this.description = desc;
        }

        @Override
        public String toString() {
            return description;
        }
    }
	
	/**
     * Extracts a user timeline from a JSONArray object.
     * @param arr containing timeline events
     * @param originator the StackExchange instance that created this
     * @return the list of timeline events.
     * @throws JSONException if there's a problem parsing the string.
     */
	protected static List<UserTimeline> fromJSONArray(final JSONArray arr, final StackWrapper originator) 
	        throws JSONException {
        List<UserTimeline> ret = new ArrayList<UserTimeline>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new UserTimeline(arr.getJSONObject(i), originator));
        }
        return ret;
    }

	/**
     * Parses a JSON string into a list of timeline events.
     * @param json string containing timeline events.
     * @param originator the StackExchange instance that created this
     * @return a List of answers.
     * @throws JSONException if the string cannot be parsed.
     */
    public static List<UserTimeline> fromJSONString(final String json, final StackWrapper originator) 
            throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }
}
