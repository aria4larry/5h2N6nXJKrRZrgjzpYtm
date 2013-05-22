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
 * Represents the timeline of a post in the Stack Exchange family of sites.
 * 
 * @author Bill Cruise,
 * @author Justin Nelson
 * 
 */
public class PostTimeline extends Timeline {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -1989840776575627916L;
    
    /** The type of timeline that this is. */
	private Type timelineType;
	
	/** The revision GUID for this timeline. */
    private String revisionGuid;
    
    /** The user who made the revision. */
    private User user;
    
    /** The owner of the post. */
    private User owner;
    
    /** A URL to the specific revision. */
    private String postRevisionUrl;
    
    /** A URL to the post. */
    private String postUrl;
    
    /** A URL to the comments on the post. */
    private String commentUrl;

    /**
     * Creates a PostTimeline from a JSON object.
     * @param json the JSON string representing a post timeline.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the original JSON string was poorly formatted, or if an invalid parameter is requested.
     */
    PostTimeline(final String json, final StackWrapper originator) throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }

    /**
     * Creates a PostTimeline from a JSON object.
     * @param jPT the JSON object representing a post timeline.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the original JSON string was poorly formatted, or if an invalid parameter is requested.
     */
    PostTimeline(final JSONObject jPT, final StackWrapper originator) throws JSONException {
        super(jPT, originator);

        PoliteJSONObject jPTp = new PoliteJSONObject(jPT);

        timelineType = Type.valueOf(jPT.getString("timeline_type").toUpperCase());
        revisionGuid = jPTp.tryGetString("revision_guid");

        JSONObject usrObj = jPTp.tryGetJSONObject("user");
        if (usrObj != null) {
            this.user = new User(usrObj, originator);
        }

        JSONObject ownerObj = jPTp.tryGetJSONObject("owner");
        if (ownerObj != null) {
            this.owner = new User(ownerObj, originator);
        }

        postRevisionUrl = jPTp.tryGetString("post_revision_url");
        postUrl = jPTp.tryGetString("post_url");
        commentUrl = jPTp.tryGetString("post_comment_url");
    }

    /**
     * Gets the type of this timeline.
     * @return the timelineType
     */
    public final Type getTimelineType() {
        return timelineType;
    }

    /**
     * Gets the guid that uniquely identifies this revision.
     * @return the revisionGuid
     */
    public final String getRevisionGuid() {
        return revisionGuid;
    }

    /**
     * Gets the user that made this revision.
     * @return the user.
     */
    public final User getUser() {
        return user;
    }

    /**
     * Gets the owner of the post.
     * @return the owner
     */
    public final User getOwner() {
        return owner;
    }

    /**
     * The url to view revisions of this post online.
     * @return the postRevisionUrl
     */
    public final String getPostRevisionUrl() {
        return postRevisionUrl;
    }

    /**
     * The url to view the post this timeline corresponds to.
     * @return the postUrl
     */
    public final String getPostUrl() {
        return postUrl;
    }

    /**
     * The url to view the comments on the post this timeline corresponds to.
     * @return the commentUrl
     */
    public final String getCommentUrl() {
        return commentUrl;
    }

    /**
     * Enum representing the possible types of post timelines available.
     * @author Justin Nelson, Bill Cruise
     */
    public static enum Type {
        /** Enumerate the possible types of timelines. */
        QUESTION("question"), ANSWER("answer"), COMMENT("comment"), REVISION("revision"), VOTES("votes"), ACCEPTED("accepted"), UNACCEPTED("unaccepted");

        /** Description of this type. */
        private String description;

        /**
         * Creates a Type from the provided description.
         * @param desc for display
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
     * Extracts a List of PostTimeline objects from the JSON array provided.
     * @param arr containing post timelines.
     * @param originator the StackExchange instance that created this
     * @return the list of post timelines
     * @throws JSONException if there's a problem communicating with the API.
     */
    protected static List<PostTimeline> fromJSONArray(final JSONArray arr, final StackWrapper originator)
            throws JSONException {
        List<PostTimeline> ret = new ArrayList<PostTimeline>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new PostTimeline(arr.getJSONObject(i), originator));
        }
        return ret;
    }

    /**
     * Creates a list of PostTimelines from a JSON string.
     * @param json string containing the post timelines.
     * @param originator the StackExchange instance that created this
     * @return a List of timeline records.
     * @throws JSONException if there's a problem communicating with the API or if an invalid attribute is requested.
     */
    public static List<PostTimeline> fromJSONString(final String json, final StackWrapper originator)
            throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }
    

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
    public final int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((postUrl == null) ? 0 : postUrl.hashCode());
		result = prime * result
				+ ((revisionGuid == null) ? 0 : revisionGuid.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (!(obj instanceof PostTimeline)) {
			return false;
		}
		PostTimeline other = (PostTimeline) obj;
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		if (postUrl == null) {
			if (other.postUrl != null) {
				return false;
			}
		} else if (!postUrl.equals(other.postUrl)) {
			return false;
		}
		if (revisionGuid == null) {
			if (other.revisionGuid != null) {
				return false;
			}
		} else if (!revisionGuid.equals(other.revisionGuid)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}
}
