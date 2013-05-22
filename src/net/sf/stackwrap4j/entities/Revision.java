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
 * Represents a specific revision to a question or answer.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Revision extends StackObjBase {
	
    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -6047088490208373937L;
    
    /** The body of the post after the revision. */
	private String body;
	
	/** The revision comment. E.g., ""deleted 4 characters in body." */
	private String comment;
	
	/** The date the revision was made. */
	private long creationDate;
	
	/** True if the revision was to a question, false otherwise. */
	private boolean isQuestion;
	
	/** True if this is a rollback to an earlier revision. */
	private boolean isRollback;
	
	/** The body of the post before this revision. */
	private String lastBody;
	
	/** The title of the post before this revision. */
	private String lastTitle;
	
	/** A list of tags on the post before this revision. */
	private List<String> lastTags;
	
	/** GUID for this revision. */
    private String revisionGuid;
    
    /** Number of the revision for this post. */
    private int revisionNumber;
    
    /** The current list of tags on the post. */
    private List<String> tags;
    
    /** The current title of the post. */
    private String title;
    
    /** The type of revision (single user or vote based). */
    private Type revisionType;
    
    /** True if this revision caused the community wiki flag to be set. */
    private boolean setCommunityWiki;
    
    /** The user who created this revision. */
    private User user;
    
    /** The numeric post id that the revision was made on. */
    private int postId;

    
    /**
     * Construct a Revision object from a JSON string.
     * @param json the JSON String representing a revision.
     * @param originator the StackExchange instance that created this revision.
     * @throws JSONException if the JSON string cannot be parsed.
     */
    Revision(final String json, final StackWrapper originator) throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }
    
    /**
     * Construct a Revision object from a JSON object.
     * @param jsonObj the object String representing a revision.
     * @param originator the StackExchange instance that created this revision.
     * @throws JSONException if an invalid parameter is requested from the JSON object.
     */
    Revision(final JSONObject jsonObj, final StackWrapper originator) throws JSONException {
        super(originator);
        PoliteJSONObject jRp = new PoliteJSONObject(jsonObj);
        
        this.body = jRp.tryGetString("body");
        this.comment = jsonObj.getString("comment");
        this.creationDate = jsonObj.getLong("creation_date");
        this.isQuestion = jsonObj.getBoolean("is_question");
        this.isRollback = jsonObj.getBoolean("is_rollback");
        this.lastBody = jRp.tryGetString("last_body");
        this.lastTitle = jRp.tryGetString("last_title");
        
        this.lastTags = new ArrayList<String>();
        JSONArray lastTagsArray = jsonObj.getJSONArray("last_tags");
        for (int i = 0; i < lastTagsArray.length(); i++) {
        	lastTags.add(lastTagsArray.getString(i));
        }
        
        this.revisionGuid = jsonObj.getString("revision_guid");
        this.revisionNumber = jRp.tryGetInt("revision_number", -1);
        
        this.tags = new ArrayList<String>();
        JSONArray tagsArray = jsonObj.getJSONArray("tags");
        for (int i = 0; i < tagsArray.length(); i++) {
            tags.add(tagsArray.getString(i));
        }
        
        this.title = jRp.tryGetString("title");
        
        String type = jsonObj.getString("revision_type");
        this.revisionType = Type.valueOf(type.toUpperCase());
        
        this.setCommunityWiki = jsonObj.getBoolean("set_community_wiki");

        JSONObject usrObj = jsonObj.getJSONObject("user");
        this.user = new User(usrObj, originator);
        
        this.postId = jsonObj.getInt("post_id");
    }
    
    
    /**
	 * @return the body
	 */
	public final String getBody() {
		return body;
	}

	/**
	 * @return the comment
	 */
	public final String getComment() {
		return comment;
	}

	/**
	 * @return the creationDate
	 */
	public final long getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the isQuestion
	 */
	public final boolean isQuestion() {
		return isQuestion;
	}

	/**
	 * @return the isRollback
	 */
	public final boolean isRollback() {
		return isRollback;
	}

	/**
	 * @return the lastBody
	 */
	public final String getLastBody() {
		return lastBody;
	}

	/**
	 * @return the lastTitle
	 */
	public final String getLastTitle() {
		return lastTitle;
	}

	/**
	 * @return the lastTags
	 */
	public final List<String> getLastTags() {
		return lastTags;
	}

	/**
	 * @return the revisionGuid
	 */
	public final String getRevisionGuid() {
		return revisionGuid;
	}

	/**
	 * @return the revisionNumber
	 */
	public final int getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * @return the tags
	 */
	public final List<String> getTags() {
		return tags;
	}

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @return the revisionType
	 */
	public final Type getRevisionType() {
		return revisionType;
	}

	/**
	 * @return the setCommunityWiki
	 */
	public final boolean isSetCommunityWiki() {
		return setCommunityWiki;
	}

	/**
	 * @return the user
	 */
	public final User getUser() {
		return user;
	}

	/**
	 * @return the post_id
	 */
	public final int getPostId() {
		return postId;
	}
	
	/**
	 * Enum representing the types of Revision.
	 * @author Bill Cruise
	 */
	public static enum Type {
	    /** Enumerate the possible types. */
        SINGLE_USER("single_user"), VOTE_BASED("vote_based");

        /** The display description for the type of revision. */
        private String description;

        /**
         * Creates a revision type from the description provided.
         * @param desc for display.
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
     * Extracts a list of Revisions from a JSON string.
     * @param json string containing revisions
     * @param originator the StackExchange instance that created this
     * @return the list of revisions.
     * @throws JSONException if there's a problem parsing the string.
     */
	public static List<Revision> fromJSONString(final String json, final StackWrapper originator) 
	        throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }

	/**
     * Extracts a list of Revisions from a JSONArray object.
     * @param arr containing revisions
     * @param originator the StackExchange instance that created this
     * @return the list of revisions.
     * @throws JSONException if there's a problem parsing the string.
     */
    protected static List<Revision> fromJSONArray(final JSONArray arr, final StackWrapper originator) 
            throws JSONException {
        List<Revision> ret = new ArrayList<Revision>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Revision(arr.getJSONObject(i), originator));
        }
        return ret;
    }

}
