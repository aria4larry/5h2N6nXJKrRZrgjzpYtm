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

import java.io.IOException;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;
import net.sf.stackwrap4j.query.AnswerQuery;
import net.sf.stackwrap4j.query.QuestionQuery;

/**
 * The Parent class for all types of posts (Comment, Answer, and Question).
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Post extends StackObjBase {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = 2648317960608128067L;
    
	/** Can be question_id, comment_id, or answer_id. */
    protected int post_id;
    
    /** The date this post was created. Unix timestamp. */
    protected long creation_date;
    
    /** The current score of the post. */
    protected int score;
    
    /** The body (content) of the post. */
    protected String body = null;
    
    /** The user that created this Post. */
    protected User owner = null;

    /**
     * Creates a Post from a JSON object.
     * @param jP the JSON object representing a post.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the original JSON string was poorly formatted, or if an invalid parameter is requested.
     */
    Post(final JSONObject jP, final StackWrapper originator) throws JSONException {
        super(originator);
        // postId will be set in child constructors

        PoliteJSONObject jPp = new PoliteJSONObject(jP);
        body = jPp.tryGetString("body");

        JSONObject ownerObj = jPp.tryGetJSONObject("owner");
        if (ownerObj != null) {
            owner = new User(ownerObj, originator);
        }

        creation_date = jP.getLong("creation_date");
        score = jP.getInt("score");
    }

    /**
     * The id of this post.
     * @return the id for this post
     */
    public int getPostId() {
        return post_id;
    }

    /**
     * The id of the user who owns this post.
     * @return the id of the user who created this post
     */
    public int getOwnerId() {
        if (owner == null) {
            return -1;
        }
        return owner.getId();
    }

    /**
     * Returns the object representing the User who created this Post.<br />
     * @return a User object
     */
    public User getOwner() {
        return owner;
    }

    /**
     * The date this post was posted.
     * @return the date this post was created
     */
    public long getCreationDate() {
        return creation_date;
    }

    /**
     * The score of this post.
     * @return the Score of this post (upvotes - downvotes)
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the body associated with this Post. <br />
     * May make another api call if the body has been loaded.
     * @return The body (content) of this Post.
     * @throws IOException if unable to communicate with the API.
     * @throws JSONException if there's a problem parsing the JSON response.
     */
    public String getBody() throws IOException, JSONException {
        if (body == null) {
            // reload this post and set the body.
            // It would seem more appropriate to override this method in Question and Answer,
            // but since the method has to throw IOException and JSONException we
            // might as well do it all right here.
            if (this instanceof Question) {
                QuestionQuery query = new QuestionQuery();
                query.setFilter("withbody").setPageSize(1)
                        .setIds(this.getPostId());
                try {
                    Question q = getCreatingApi().getQuestions(query).get(0);
                    body = q.getBody();
                } catch (ParameterNotSetException pnse) {
                    // Intentionally left empty. I know I set the id.
                } 
            }
            else if (this instanceof Answer) {
                AnswerQuery query = new AnswerQuery();
                query.setBody(true).setPageSize(1).setIds(this.getPostId());
                try {
                    Answer a = getCreatingApi().getAnswers(query).get(0);
                    body = a.getBody();
                } catch (ParameterNotSetException pnse) {
                    // Intentionally left empty. I know I set the id.
                }
            }
        }
        return body;
    }

    /**
     * Use at your own risk.
     * Uses a very naive replace all to remove the text from the html.
     * It worked on my two tests.
     * It also un-encodes some html codes
     * @return the decoded text.
     * @throws IOException if unable to communicate with the API.
     * @throws JSONException if there's a problem parsing the JSON response.
     */
    public String getPlainTextBody() throws IOException, JSONException {
    	String htmlBody = getBody().replaceAll("<hr>", ""); // one off for horizontal rule lines
    	String plainTextBody = htmlBody.replaceAll("<[^<>]+>([^<>]*)<[^<>]+>", "$1");
    	plainTextBody = plainTextBody.replaceAll("<br ?/>", "");
    	return decodeHtml(plainTextBody);
    }
    
    /**
     * Decodes an HTML string.
     * @param in the string to decode.
     * @return a string with HTML entities decoded.
     */
	protected static String decodeHtml(final String in) {
		String[] baad = { "&amp;", "&lsquo;", "&rsquo;", "&ldquo;", "&rdquo;",
		                  "&gt;", "&lt;", "&mdash;", "&hellip;", "&quot;", "&#39;" };
		String[] good = { "&", "‘", "’", "“", "”", ">", "<", "—", "…", "\"", "'"};
		//&‘’“”><—…"'
		String ret = in;
		for (int i = 0; i < baad.length; i++) {
			ret = ret.replaceAll(baad[i], good[i]);
		}
		return ret;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result
                + (int) (creation_date ^ (creation_date >>> 32));
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + post_id;
        result = prime * result + score;
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
        if (!(obj instanceof Post)) {
            return false;
        }
        Post other = (Post) obj;
        if (body == null) {
            if (other.body != null) {
                return false;
            }
        } else if (!body.equals(other.body)) {
            return false;
        }
        if (creation_date != other.creation_date) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (post_id != other.post_id) {
            return false;
        }
        if (score != other.score) {
            return false;
        }
        return true;
    }

}
