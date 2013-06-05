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
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;
import net.sf.stackwrap4j.query.QuestionQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Question in the Stack Exchange family of sites.
 *
 * @author Justin Nelson
 * @author Bill Cruise
 */
public class Question extends MajorPost {

    /**
     * The default page to be returned in results.
     */
    public static final int DEFAULT_PAGE = 1;
    /**
     * The default size of the page returned in a result.
     */
    public static final int DEFAULT_PAGE_SIZE = 30;
    /**
     * The default option to include the body of a Question.
     */
    public static final boolean DEFAULT_BODY = false;
    /**
     * The default option to include the comments of a Question.
     */
    public static final boolean DEFAULT_COMMENTS = false;
    /**
     * The default order to return results.
     */
    public static final Order DEFAULT_ORDER = Order.ASC;
    /**
     * The default max date.
     */
    public static final long DEFAULT_TO_DATE = Long.MAX_VALUE;
    /**
     * The default min date.
     */
    public static final long DEFAULT_FROM_DATE = 1L;
    /**
     * The default in title value.
     */
    public static final String DEFAULT_IN_TITLE = null;
    /**
     * The default string for tagged option.
     */
    public static final String DEFAULT_TAGGED = null;
    /**
     * The default string for not tagged option.
     */
    public static final String DEFAULT_NOT_TAGGED = null;
    /**
     * The default search min.
     */
    public static final int DEFAULT_SEARCH_MIN = Integer.MIN_VALUE;
    /**
     * The default search max.
     */
    public static final int DEFAULT_SEARCH_MAX = Integer.MAX_VALUE;
    /**
     * The default option on whether or not to return answers.
     */
    public static final boolean DEFAULT_ANSWERS = true;
    private static final String COMMENT_FILTER_STRING = "!bULULTgRQnYH9u";
    /**
     * The universal version identifier for a Serializable class.
     */
    private static final long serialVersionUID = -4569236296352955838L;
    boolean tryedAnswer = false;
    boolean tryedComments = false;
    /**
     * The number of answers on this question.
     */
    private int answer_count;
    /**
     * The list of answers for this question.
     */
    private List<Answer> answers;
    /**
     * The id of the accepted answer to this question.
     */
    private int accepted_answer_id;
    private String notice;
    /**
     * The number of times this questions has been favorited (starred).
     */
    private int favorite_count;
    /**
     * The end date of the bounty on this question.
     */
    private long bounty_closes_date;
    /**
     * The bounty amount for this question.
     */
    private int bounty_amount;
    private int close_vote_count;
    private long community_owned_date;
    private long creation_date;
    private int delete_vote_count;
    /**
     * The date this question was closed.
     */
    private long closed_date;
    private boolean is_answered;
    private long locked_date;
    private String title;
    /**
     * The reason this question was closed.
     */
    private String closed_reason;
    private long protected_date;
    /**
     * The list of tags on this question.
     */
    private List<String> tags;
    private String migrated_from;
    private int reopen_vote_count;
    private int view_count;

    /**
     * Creates a Question from a JSON string.
     *
     * @param json       string containing questions.
     * @param originator the StackExchange instance that created this
     * @throws JSONException if there's a problem parsing the string.
     */
    Question(final String json, final StackWrapper originator)
            throws JSONException {
        this(new JSONObject(json).getJSONArray("items").getJSONObject(0),
                originator);
    }

    /**
     * Creates a Question from a JSON object.
     *
     * @param jQ         object containing questions.
     * @param originator originator the StackExchange instance that created this
     * @throws JSONException if there's a problem communicating with the API or if an
     *                       invalid attribute is requested.
     */
    Question(final JSONObject jQ, final StackWrapper originator)
            throws JSONException {
        super(jQ, originator);
        PoliteJSONObject jQp = new PoliteJSONObject(jQ);
        bounty_closes_date = jQp.tryGetLong("bounty_closes_date", -1);
        bounty_amount = jQp.tryGetInt("bounty_amount", 0);
        close_vote_count = jQp.tryGetInt("close_vote_count", 0);
        closed_date = jQp.tryGetLong("closed_date", -1);
        closed_reason = jQp.tryGetString("closed_reason");
        community_owned_date = jQp.tryGetLong("community_owned_date", -1);
        delete_vote_count = jQp.tryGetInt("delete_vote_count", 0);
        creation_date = jQp.tryGetLong("creation_date", -1);
        post_id = jQ.getInt("question_id"); // inherited member
        answer_count = jQ.getInt("answer_count");
        title = jQ.getString("title");
        view_count = jQp.tryGetInt("view_count", 0);
        is_answered = jQp.tryGetBoolean("is_answered", false);
        JSONArray answersArray = jQp.tryGetJSONArray("answers");
        if (answersArray != null) {
            answers = Answer.fromJSONArray(answersArray, originator);
        } else {
            answers = new ArrayList<Answer>();
        }
        protected_date = jQp.tryGetLong("protected_date", -1);
        migrated_from = jQp.tryGetString("migrated_from");
        locked_date = jQp.tryGetLong("locked_date", -1);
        accepted_answer_id = jQp.tryGetInt("accepted_answer_id", -1);
        favorite_count = jQp.tryGetInt("favorite_count", 0);
        tags = new ArrayList<String>();
        JSONArray tagsArray = jQ.getJSONArray("tags");
        for (int i = 0; i < tagsArray.length(); i++) {
            tags.add(tagsArray.getString(i));
        }
        notice = jQp.tryGetString("notice");
        reopen_vote_count = jQp.tryGetInt("reopen_vote_count", 0);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public static int getDefaultPage() {
        return DEFAULT_PAGE;
    }

    public static int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public static boolean isDefaultBody() {
        return DEFAULT_BODY;
    }

    public static boolean isDefaultComments() {
        return DEFAULT_COMMENTS;
    }

    public static Order getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    public static long getDefaultToDate() {
        return DEFAULT_TO_DATE;
    }

    public static long getDefaultFromDate() {
        return DEFAULT_FROM_DATE;
    }

    public static String getDefaultInTitle() {
        return DEFAULT_IN_TITLE;
    }

    public static String getDefaultTagged() {
        return DEFAULT_TAGGED;
    }

    public static String getDefaultNotTagged() {
        return DEFAULT_NOT_TAGGED;
    }

    public static int getDefaultSearchMin() {
        return DEFAULT_SEARCH_MIN;
    }

    public static int getDefaultSearchMax() {
        return DEFAULT_SEARCH_MAX;
    }

    public static boolean isDefaultAnswers() {
        return DEFAULT_ANSWERS;
    }

    /**
     * Extracts a list of Questions from a JSONArray object.
     *
     * @param jsonArray  containing questions
     * @param originator the StackExchange instance that created this
     * @return the list of questions.
     * @throws JSONException if there's a problem communicating with the API.
     */
    protected static List<Question> fromJSONArray(final JSONArray jsonArray,
                                                  final StackWrapper originator) throws JSONException {
        List<Question> ret = new ArrayList<Question>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            ret.add(new Question(jsonArray.getJSONObject(i), originator));
        }
        return ret;
    }

    /**
     * Parses a JSON string into a list of questions.
     *
     * @param json       string containing questions.
     * @param originator the StackExchange instance that created this
     * @return a List of questions.
     * @throws JSONException if there's a problem communicating with the API.
     */
    public static List<Question> fromJSONString(final String json,
                                                final StackWrapper originator) throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("items"),
                originator);
    }

    public int getAnswer_count() {
        return answer_count;
    }

    public int getAccepted_answer_id() {
        return accepted_answer_id;
    }

    public String getNotice() {
        return notice;
    }

    public int getFavorite_count() {
        return favorite_count;
    }

    public long getBounty_closes_date() {
        return bounty_closes_date;
    }

    public int getBounty_amount() {
        return bounty_amount;
    }

    public int getClose_vote_count() {
        return close_vote_count;
    }

    public long getCommunity_owned_date() {
        return community_owned_date;
    }

    public long getCreation_date() {
        return creation_date;
    }

    public int getDelete_vote_count() {
        return delete_vote_count;
    }

    public long getClosed_date() {
        return closed_date;
    }

    public boolean isIs_answered() {
        return is_answered;
    }

    public long getLocked_date() {
        return locked_date;
    }

    public String getTitle() {
        return title;
    }

    public String getClosed_reason() {
        return closed_reason;
    }

    public long getProtected_date() {
        return protected_date;
    }

    public String getMigrated_from() {
        return migrated_from;
    }

    public int getReopen_vote_count() {
        return reopen_vote_count;
    }

    public int getView_count() {
        return view_count;
    }

    /**
     * The id of the answer that was accepted for this question.
     *
     * @return the id of the accepted answer, or -1 if there was no accepted
     *         answer
     */
    public final int getAcceptedAnswerId() {
        return accepted_answer_id;
    }

    /**
     * The number of times this question has been favorited.
     *
     * @return the fav count
     */
    public final int getFavoriteCount() {
        return favorite_count;
    }

    /**
     * The tags this question was tagged with.
     *
     * @return list of tags
     */
    public final List<String> getTags() {
        return tags;
    }

    /**
     * The number of answers on this question.
     *
     * @return the answer count
     */
    public final int getAnswerCount() {
        return answer_count;
    }

    /**
     * Gets the answers associated with this Question. <br />
     * May make another API call if no answers have been loaded yet.
     *
     * @return list of answers
     * @throws IOException   if the connection to the API fails
     * @throws JSONException if the JSON returned by the api is bad
     */
    public final List<Answer> getAnswers() throws IOException, JSONException {
        if ((answers == null || answers.size() == 0) && !tryedAnswer) {
            tryedAnswer = true;
            answers = getCreatingApi().getAnswersByQuestionId(getPostId());
            answer_count = answers.size();
        }
        return answers;
    }

    /**
     * The date the bounty was closed on this question.
     *
     * @return the bountyClosesDate or -1 if no bounty
     */
    public final long getBountyClosesDate() {
        return bounty_closes_date;
    }

    /**
     * Status of bountyness.
     *
     * @return whether or not this question had a bounty
     */
    public final boolean hasBounty() {
        return bounty_closes_date < 0;
    }

    /**
     * The amount of bounty on this question.
     *
     * @return the bountyAmount
     */
    public final int getBountyAmount() {
        return bounty_amount;
    }

    /**
     * The date the question was closed.
     *
     * @return the closedDate
     */
    public final long getClosedDate() {
        return closed_date;
    }

    /**
     * The reason for closing this question.
     *
     * @return the closedReason
     */
    public final String getClosedReason() {
        return closed_reason;
    }

    /**
     * Gets a list of comments on this question.
     *
     * @return the list of answers.
     * @throws IOException   if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed.
     */
    @Override
    public final List<Comment> getComments() throws IOException, JSONException {
        if (comments == null && !tryedComments) {
            tryedComments = true;
            QuestionQuery query = new QuestionQuery();
            query.setFilter(COMMENT_FILTER_STRING);
            comments = getCreatingApi().getQuestionById(this.getPostId(), null).comments;
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
        result = prime * result + accepted_answer_id;
        result = prime * result + favorite_count;
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
        if (!(obj instanceof Question)) {
            return false;
        }
        Question other = (Question) obj;
        if (accepted_answer_id != other.accepted_answer_id) {
            return false;
        }
        if (tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!tags.equals(other.tags)) {
            return false;
        }
        return true;
    }

}
