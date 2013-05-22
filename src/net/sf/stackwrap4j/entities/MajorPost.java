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
import java.util.List;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.datastructures.CommentsFromPostList;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;
import net.sf.stackwrap4j.query.CommentQuery;

/**
 * Super class for Question and Answer. Contains common methods and members.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public abstract class MajorPost extends Post {

    /**
     * body string unchanged in unsafe filters
     * 
     * comments an array of comments may be absent
     * 
     * creation_date date
     * 
     * down_vote_count integer
     * 
     * last_activity_date date
     * 
     * last_edit_date date may be absent
     * 
     * link 2.1 string
     * 
     * owner shallow_user may be absent
     * 
     * post_id integer, refers to a post
     * 
     * post_type one of question, or answer
     * 
     * score integer
     * 
     * up_vote_count integer
     * */

    /** List of comments left on this post. */
    protected List<Comment> comments;
    protected int down_vote_count;
    protected long last_activity_date;
    protected long last_edit_date;
    protected String link;
    protected String post_type;
    protected int up_vote_count;
    /**
     * The universal version identifier for a Serializable class.
     */
    private static final long serialVersionUID = -1356543893716595603L;

    /**
     * Creates a MajorPost from a JSON object.
     * 
     * @param jP
     *            the JSON object representing a post.
     * @param originator
     *            the StackExchange instance that created this.
     * @throws JSONException
     *             if the original JSON string was poorly formatted, or if an
     *             invalid parameter is requested.
     */
    MajorPost(final JSONObject jP, final StackWrapper originator)
            throws JSONException {
        super(jP, originator);
        PoliteJSONObject jPp = new PoliteJSONObject(jP);
        JSONArray jarr = jPp.tryGetJSONArray("comments");
        if (jarr != null) {
            comments = Comment.fromJSONArray(jarr, originator);
        }
        down_vote_count = jPp.tryGetInt("down_vote_count", 0);
        last_activity_date = jPp.tryGetLong("last_activity_date", -1);
        last_edit_date = jPp.tryGetLong("last_edit_date", -1);
        link = jPp.tryGetString("link");
        post_type = jPp.tryGetString("post_type");
        up_vote_count = jPp.tryGetInt("up_vote_count", 0);
    }

    /**
     * The list of comments associated with this post.<br />
     * Will make another call to the API if necessary.
     * 
     * @return the comments associated with this post.
     * @throws IOException
     *             if there's a problem communicating with the API.
     * @throws JSONException
     *             if the JSON string is incorrectly formatted.
     */
    public List<Comment> getComments() throws IOException, JSONException {
        final int defaultPageSize = 50;
        if (comments != null) {
            return comments;
        }
        CommentQuery query = new CommentQuery();
        query.setPageSize(defaultPageSize).addId(this.getPostId());
        comments = new CommentsFromPostList(getCreatingApi(), query);
        return comments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + (int) (last_activity_date ^ (last_activity_date >>> 32));
        result = prime * result
                + (int) (last_edit_date ^ (last_edit_date >>> 32));
        return result;
    }

    public int getDown_vote_count() {
        return down_vote_count;
    }

    public long getLast_activity_date() {
        return last_activity_date;
    }

    public long getLast_edit_date() {
        return last_edit_date;
    }

    public String getLink() {
        return link;
    }

    public String getPost_type() {
        return post_type;
    }

    public int getUp_vote_count() {
        return up_vote_count;
    }

    /*
     * (non-Javadoc)
     * 
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
        if (!(obj instanceof MajorPost)) {
            return false;
        }
        MajorPost other = (MajorPost) obj;
        return post_id == other.getPostId();
    }

}
