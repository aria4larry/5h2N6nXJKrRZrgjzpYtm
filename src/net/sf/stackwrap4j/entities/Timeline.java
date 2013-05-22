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
import net.sf.stackwrap4j.json.PoliteJSONObject;

/**
 * Represents a timeline record for a post.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Timeline extends StackObjBase {
	
    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -2522546875090496762L;
    
    /** The id for the post. */
	private int postId;
	
	/** The question id for the timeline. */
	private int questionId;
	
	/** The comment Id. */
	private int commentId;
	
	/** The creation date. */
	private long creationDate;
	
	/** The action taken. */
	private String action;
	
	/**
     * Creates a Timeline from a JSONObject.
     * @param jT the JSON object representing an Timeline.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if something goes really wrong...(The API probably changed without us knowing).
     */
	Timeline(final JSONObject jT, final StackWrapper originator) throws JSONException {
		super(originator);
		
		PoliteJSONObject jTp = new PoliteJSONObject(jT);
		
		postId = jTp.tryGetInt("post_id", -1);
		questionId = jTp.tryGetInt("question_id", -1);
		commentId = jTp.tryGetInt("comment_id", -1);
    	creationDate = jTp.tryGetLong("creation_date", -1);
    	action = jTp.tryGetString("action");
    }
	

	/**
	 * @return the postId
	 */
	public final int getPostId() {
		return postId;
	}
	
	/**
     * @return the questionId
     */
    public final int getQuestionId() {
        return questionId;
    }

	/**
	 * @return the commentId
	 */
	public final int getCommentId() {
		return commentId;
	}

	/**
	 * @return the creationDate
	 */
	public final long getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the action
	 */
	public final String getAction() {
		return action;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + commentId;
		result = prime * result + (int) (creationDate ^ (creationDate >>> 32));
		result = prime * result + questionId;
		result = prime * result + postId;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Timeline)) {
			return false;
		}
		Timeline other = (Timeline) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
		if (commentId != other.commentId) {
			return false;
		}
		if (creationDate != other.creationDate) {
			return false;
		}
		if (questionId != other.questionId) {
            return false;
        }
		if (postId != other.postId) {
			return false;
		}
		return true;
	}
}
