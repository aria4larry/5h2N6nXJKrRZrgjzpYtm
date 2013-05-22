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
 * This represents a Comment on the Stack Exchange family of sites.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 */
public class Comment extends Post {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = 4155537877981116560L;

    /** The default page number to return in a response. */
    public static final int DEFAULT_PAGE = 1;

    /** The default number of Comments to return in a response. */
    public static final int DEFAULT_PAGE_SIZE = 30;

    /** The default max date to include in results. */
    public static final long DEFAULT_TO_DATE = 253402300799L;

    /** The default min date to include in results. */
    public static final long DEFAULT_FROM_DATE = 0L;

    /** The default order to return results. */
    public static final Order DEFAULT_ORDER = Order.ASC;

    /** The user that this comment is a reply to (if applicable). */
    private User replyToUser;
    private boolean edited;

    public boolean isEdited() {
        return edited;
    }

    public String getBody_markdown() {
        return body_markdown;
    }

    public int getQuestion_id() {
        return question_id;
    }

    /** True if this comment was left on a question, false if on an answer. */
    private boolean onQuestion;

    /** Number of times this comment has been edited. */
    /** Type of the parent post. Can be 'answer' or 'question'. */
    private String postType;

    private String body_markdown;
    private int question_id;

    /**
     * Creates a Comment from the JSON string provided.
     * 
     * @param json
     *            the JSON String representing a comment.
     * @param originator
     *            the StackExchange instance that created this.
     * @throws JSONException
     *             if the JSON string was poorly formatted.
     */
    Comment(final String json, final StackWrapper originator)
            throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
        // Assumes array length 0
    }

    /**
     * Creates a Comment from the JSON object provided.
     * 
     * @param object
     *            the JSON object representing a comment.
     * @param originator
     *            the StackExchange instance that created this.
     * @throws JSONException
     *             if an invalid field is requested.
     */
    Comment(final JSONObject object, final StackWrapper originator)
            throws JSONException {
        super(object, originator);
        PoliteJSONObject jCp = new PoliteJSONObject(object);

        super.post_id = object.getInt("comment_id");
        question_id = object.getInt("post_id");
        JSONObject replyJo = jCp.tryGetJSONObject("reply_to_user");
        if (replyJo != null) {
            replyToUser = new User(replyJo, originator);
        }
        body_markdown = jCp.tryGetString("body_markdown");
        edited = jCp.tryGetBoolean("edited", false);
        this.postType = object.getString("post_type");
        this.onQuestion = this.postType == null ? false : this.postType
                .equals("question");
    }

    /**
     * If true, the comment is on a Question. If false, it's on an Answer
     * 
     * @return Whether or not the comment is on a question
     */
    public final boolean isOnQuestion() {
        return onQuestion;
    }

    /**
     * The id of the post this comment belongs to (Question, or Answer).
     * 
     * @return The Id of the Post this comment belongs to
     */
    public final int getParentId() {
        return post_id;
    }

    /**
     * The id of the user this id was comment was replying to.
     * 
     * @return the Id of the user this comment is replying to, -1 if no one
     */
    public final User getReplyToUser() {
        return replyToUser;
    }

    /**
     * The number of times this comment was edited.
     * 
     * @return The number of times within the 5 minute period the comment was
     *         edited
     */

    /**
     * Type of post this is a comment on. Can be one of "answer" or "question".
     * 
     * @return type of post this comment applies to.
     */
    public final String getPostType() {
        return postType;
    }

    /**
     * Extracts a list of Comments from a JSONArray object.
     * 
     * @param arr
     *            containing answers
     * @param originator
     *            the StackExchange instance that created this
     * @return the list of comments.
     * @throws JSONException
     *             if there's a problem communicating with the API.
     */
    protected static List<Comment> fromJSONArray(final JSONArray arr,
            final StackWrapper originator) throws JSONException {
        if (arr == null) {
            return new ArrayList<Comment>(0);
        }

        List<Comment> ret = new ArrayList<Comment>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Comment(arr.getJSONObject(i), originator));
        }
        return ret;
    }

    /**
     * Creates a List of comments from a JSON formated string.
     * 
     * @param json
     *            the string to parse
     * @param originator
     *            the StackExchange instance that called this method
     * @return a list of comments parsed from the JSON string
     * @throws JSONException
     *             if the JSON string was poorly formatted
     */
    public static List<Comment> fromJSONString(final String json,
            final StackWrapper originator) throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }

}
