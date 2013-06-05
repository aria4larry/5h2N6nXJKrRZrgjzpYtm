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
import net.sf.stackwrap4j.enums.Order;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;
import net.sf.stackwrap4j.query.CommentQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one answer in the Stack Exchange family of sites.
 *
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Answer extends MajorPost {

    /**
     * The default page returned in a response.
     */
    public static final int DEFAULT_PAGE = 1;
    /**
     * The default page size for a response.
     */
    public static final int DEFAULT_PAGE_SIZE = 30;
    /**
     * The default option to return the body of the answer.
     */
    public static final boolean DEFAULT_BODY = false;
    /**
     * The default option to return the comments for the Answer.
     */
    public static final boolean DEFAULT_COMMENTS = false;
    /**
     * The default return order.
     */
    public static final Order DEFAULT_ORDER = Order.ASC;
    /**
     * The default date to start.
     */
    public static final long DEFAULT_FROM_DATE = 0L;
    /**
     * The default date to end.
     */
    public static final long DEFAULT_TO_DATE = Long.MAX_VALUE;
    /**
     * The universal version identifier for a Serializable class.
     */
    private static final long serialVersionUID = -7133000911614192700L;
    boolean tryedComment = false;
    /**
     * The parent question that this answer belongs to.
     */
    private Question parent;
    /**
     * True if this is the accepted answer to the parent question.
     */
    private boolean is_accepted;
    private long community_owned_date;
    /**
     * The id for the question that this answer belongs to.
     */
    private int question_id;

    /**
     * Creates an Answer from a JSON string.
     *
     * @param json       the JSON String representing an answer.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the JSON string was poorly formatted.
     */
    Answer(final String json, final StackWrapper originator)
            throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }

    /**
     * Creates an Answer from a JSON string.
     *
     * @param json       the JSON String representing an answer.
     * @param originator the StackExchange instance that created this Answer.
     * @param parentPost The Question that owns this Answer.
     * @throws JSONException if the JSON string was poorly formatted.
     */
    Answer(final String json, final StackWrapper originator,
           final Question parentPost) throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
        this.parent = parentPost;
    }

    /**
     * Creates an Answer from a JSONObject.
     *
     * @param jA         the JSON object representing an Answer.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if something goes really wrong...(The API probably changed
     *                       without us knowing).
     */
    Answer(final JSONObject jA, final StackWrapper originator)
            throws JSONException {
        super(jA, originator);
        PoliteJSONObject jAp = new PoliteJSONObject(jA);
        post_id = jA.getInt("answer_id");
        is_accepted = jA.getBoolean("is_accepted");
        question_id = jA.getInt("question_id");
        community_owned_date = jAp.tryGetLong("community_owned_date", -1);
    }

    /**
     * Extracts a list of Answers from a JSONArray object.
     *
     * @param arr        containing answers
     * @param originator the StackExchange instance that created this
     * @return the list of answers.
     * @throws JSONException if there's a problem parsing the string.
     */
    protected static List<Answer> fromJSONArray(final JSONArray arr,
                                                final StackWrapper originator) throws JSONException {
        List<Answer> ret = new ArrayList<Answer>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Answer(arr.getJSONObject(i), originator));
        }
        return ret;
    }

    /**
     * Parses a JSON string into a list of Answers.
     *
     * @param json       string containing answers.
     * @param originator the StackExchange instance that created this
     * @return a List of answers.
     * @throws JSONException if the string cannot be parsed.
     */
    public static List<Answer> fromJSONString(final String json,
                                              final StackWrapper originator) throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }

    /**
     * Whether or not this answer is accepted.
     *
     * @return true if this answer has been accepted by its parent.
     */
    public final boolean isAccepted() {
        return is_accepted;
    }

    /**
     * Gets the id of this Answer's parent.
     *
     * @return the id of this answer's parent.
     */
    public final int getQuestionId() {
        return question_id;
    }

    /**
     * Will return the Question that owns this answer. <br />
     * If the question was not originally provided, a call will be made to the
     * API to get the info.
     *
     * @return the Question that owns this answer
     * @throws IOException   if the call to the REST API fails
     * @throws JSONException if the JSON returned cannot be parsed into a Question
     */
    public final Question getParentQuestion() throws IOException, JSONException {
        if (parent == null) {
            parent = getCreatingApi().getQuestionById(getQuestionId(), null);
        }
        return parent;
    }

    /**
     * Gets a list of the comments on this post.
     *
     * @return a list of comments.
     * @throws IOException   if the call to the REST API fails
     * @throws JSONException if the JSON returned cannot be parsed into a Question
     */
    @Override
    public final List<Comment> getComments() throws IOException, JSONException {
        if (comments == null && !tryedComment) {
            tryedComment = true;
            CommentQuery q = new CommentQuery();
            q.setIds(this.getPostId());
            try {
                comments = getCreatingApi().getCommentsByPostId(q);
            } catch (ParameterNotSetException pnse) {
                // Intentionally left empty. The id parameter was explicitly
                // set.
            }
        }
        return comments;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (is_accepted ? 1231 : 1237);
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + question_id;
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
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Answer)) {
            return false;
        }
        Answer other = (Answer) obj;
        if (is_accepted != other.is_accepted) {
            return false;
        }
        if (parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!parent.equals(other.parent)) {
            return false;
        }
        if (question_id != other.question_id) {
            return false;
        }
        return true;
    }

    public Question getParent() {
        return parent;
    }

    public boolean isIs_accepted() {
        return is_accepted;
    }

    public long getCommunity_owned_date() {
        return community_owned_date;
    }

    public int getQuestion_id() {
        return question_id;
    }

}
