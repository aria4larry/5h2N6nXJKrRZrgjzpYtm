/**
 * StackWrap4J - A Java wrapper for the Stack Exchange API.
 * 
 * Copyright (c) 2010 Bill Cruise, Justin Nelson, and Ari Gesher.
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

package net.sf.stackwrap4j;

import android.util.SparseArray;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.stackwrap4j.datastructures.MetadataList;
import net.sf.stackwrap4j.entities.Answer;
import net.sf.stackwrap4j.entities.Badge;
import net.sf.stackwrap4j.entities.Comment;
import net.sf.stackwrap4j.entities.PostTimeline;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.entities.Reputation;
import net.sf.stackwrap4j.entities.Revision;
import net.sf.stackwrap4j.entities.Stats;
import net.sf.stackwrap4j.entities.Tag;
import net.sf.stackwrap4j.entities.User;
import net.sf.stackwrap4j.entities.UserTimeline;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.http.HttpClient;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.query.AnswerQuery;
import net.sf.stackwrap4j.query.BadgeQuery;
import net.sf.stackwrap4j.query.CommentQuery;
import net.sf.stackwrap4j.query.FavoriteQuery;
import net.sf.stackwrap4j.query.QuestionQuery;
import net.sf.stackwrap4j.query.ReputationQuery;
import net.sf.stackwrap4j.query.RevisionQuery;
import net.sf.stackwrap4j.query.SearchQuery;
import net.sf.stackwrap4j.query.TagQuery;
import net.sf.stackwrap4j.query.TimelineQuery;
import net.sf.stackwrap4j.query.UnansweredQuery;
import net.sf.stackwrap4j.query.UserQuery;
import net.sf.stackwrap4j.query.UserQuestionQuery;

/**
 * StackWrap4J is a Java wrapper around the Stack Exchange REST API. The
 * StackWrapper class contains methods that expose the functionality of that
 * API.
 * 
 * See the API documentation and help for information on what the API does.
 * Documentation: http://stackapps.com/questions/1/api-documentation-and-help
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 * @author Ari Gesher <alephbass@users.sourceforge.net>
 */
public class StackWrapper implements Serializable {

    private static final long serialVersionUID = -8206261687224998064L;

    protected final String VERSION = "2.1/";
    protected String soApiKey;
    protected String site;

    // https://api.stackexchange.com/2.1/questions?key=Emye2I6DuazXXE0c%295jfVg%28%28&site=stackoverflow

    /**
     * Initializes a newly created StackWrapper object with the base URL of the
     * SE site to communicate with.
     * 
     * @param url
     *            The base URL of a Stack Exchange site.
     */
    public StackWrapper(String site) {
        this(site, "");
    }

    /**
     * Initializes a newly created StackWrapper object with the base URL of the
     * SE site and an API key.
     * 
     * @param url
     *            The base URL of a Stack Exchange site.
     * @param apiKey
     *            The API key for your application.
     */
    public StackWrapper(String site, String apiKey) {
        this.site = site;
        this.soApiKey = apiKey;
    }

    // private static String fixUrl(String url) {
    // url = url.replace("http://", "");
    // if (url.startsWith("api")) {
    // url = "http://" + url;
    // } else {
    // url = "http://api." + url;
    // }
    // return url;
    // }

    /**************************/
    /***** Answer Methods *****/
    /**************************/

    private final HttpClient answerClient = new HttpClient();

    /**
     * Gets a single answer by its id.
     * 
     * @param id
     *            a single answer id.
     * @return the answer specified by id.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public Answer getAnswerById(int id) throws IOException, JSONException {
        String json = answerClient.sendGetRequest(site, VERSION, "answers/"
                + id, soApiKey);
        MetadataList<Answer> hopefullyOneAnswer = new MetadataList<Answer>(
                json, Answer.fromJSONString(json, this));
        if (hopefullyOneAnswer.size() > 1)
            throw new JSONException("The query returned more than one result.");
        if (hopefullyOneAnswer.size() == 0)
            throw new IllegalArgumentException(
                    "The provided id did not match any Answers.");
        return hopefullyOneAnswer.get(0);
    }

    /**
     * Gets a set of answers matching the given ids
     * 
     * @param ids
     *            A single answer id or a list of ids.
     * @return a list of answers enumerated by ids.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Answer> getAnswersById(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = answerClient.sendGetRequest(site, VERSION, "answers/"
                + vectorizedList, soApiKey);
        return new MetadataList<Answer>(json, Answer.fromJSONString(json, this));
    }

    /**
     * Gets a list of answers.
     * 
     * @param query
     * @return a list of answers.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Answer> getAnswers(AnswerQuery query) throws IOException,
            JSONException, ParameterNotSetException {
        String json = answerClient.sendGetRequest(site, VERSION, "answers/"
                + query.getIds(), soApiKey, query.getUrlParams());
        return new MetadataList<Answer>(json, Answer.fromJSONString(json, this));
    }

    /**
     * Gets the comments associated with an answer or list of answers.
     * 
     * @param answerIds
     *            A single answer id or a list of ids.
     * @return a list of comments for the specified answers.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Comment> getCommentsByAnswerId(int... answerIds)
            throws IOException, JSONException {
        String vectorizedList = buildVectorizedList(answerIds);
        String json = answerClient.sendGetRequest(site, VERSION, "answers/"
                + vectorizedList + "/comments", soApiKey);
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets the comments associated with an answer or list of answers.
     * 
     * @param query
     * @return a list of comments for the specified answers.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Comment> getCommentsByAnswerId(CommentQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = answerClient.sendGetRequest(site, VERSION, "answers/"
                + query.getIds() + "/comments", soApiKey, query.getUrlParams());
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /***** Badges Methods *****/

    private final HttpClient badgeClient = new HttpClient();

    /**
     * Get a list of all badges. Default sort order is by name.
     * 
     * @return a list of badges.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Badge> listBadges() throws IOException, JSONException {
        String json = badgeClient.sendGetRequest(site, VERSION, "badges",
                soApiKey);
        return Badge.fromJSONString(json, this);
    }

    /**
     * Gets a list of users who received the specified badge.
     * 
     * @param ids
     *            A single badge id or a list of ids.
     * @return a list of users who received the specified badge.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<User> getBadgeRecipients(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = badgeClient.sendGetRequest(site, VERSION, "badges/"
                + vectorizedList, soApiKey);
        JSONObject jo = new JSONObject(json);
        JSONArray ja = jo.getJSONArray("users");
        return new MetadataList<User>(json, User.fromJSONArray(ja, this));
    }

    /**
     * Gets a list of users who received the specified badge.
     * 
     * @param query
     * @return a list of users who received the specified badge.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<User> getBadgeRecipients(BadgeQuery query) throws IOException,
            JSONException, ParameterNotSetException {
        String json = badgeClient.sendGetRequest(site, VERSION, "badges/"
                + query.getIds(), soApiKey, query.getUrlParams());
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this));
    }

    /**
     * Get a list of all badges sorted by name.
     * 
     * @return a list of all badges sorted by name
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Badge> listStandardBadges() throws IOException, JSONException {
        String json = badgeClient.sendGetRequest(site, VERSION, "badges/name",
                soApiKey);
        return Badge.fromJSONString(json, this);
    }

    /**
     * Get a list of all the badges awarded for tags (i.e., c#, .net, java).
     * 
     * @return a list of all the badges awarded for tags.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Badge> listTagBadges() throws IOException, JSONException {
        String json = badgeClient.sendGetRequest(site, VERSION, "badges/tags",
                soApiKey);
        return Badge.fromJSONString(json, this);
    }

    /***** Comment Methods *****/

    private final HttpClient commentClient = new HttpClient();

    /**
     * Get a comment specified by its id.
     * 
     * @param id
     *            A single comment id.
     * @return a single comment.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public Comment getCommentById(int id) throws IOException, JSONException {
        String json = commentClient.sendGetRequest(site, VERSION, "comments/"
                + id, soApiKey);
        return Comment.fromJSONString(json, this).get(0);
    }

    /**
     * Get a comment(s) specified by comment ids.
     * 
     * @param ids
     *            A single comment id or a list of ids.
     * @return a list of comments.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Comment> getCommentsById(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = commentClient.sendGetRequest(site, VERSION, "comments/"
                + vectorizedList, soApiKey);
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Get a list of comments specified by id.
     * 
     * @param query
     * @return a list of comments specified by id.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Comment> getComments(CommentQuery query) throws IOException,
            JSONException, ParameterNotSetException {
        String json = commentClient.sendGetRequest(site, VERSION, "comments/"
                + query.getIds(), soApiKey, query.getUrlParams());
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /***** Simulate Error Method *****/

    /**
     * Simulates an error given a code.
     * 
     * @param errorId
     */
    public Error simulateError(int errorId) throws IOException, JSONException {
        String json = new HttpClient().sendGetRequest(site, VERSION, "errors/"
                + errorId, soApiKey, "type=jsontext");
        throw new IOException(
                "This should have thrown an error from the error code: " + json);
    }

    /***** Post Methods *****/

    private final HttpClient postClient = new HttpClient();

    /**
     * Gets the comments associated with a post (question or answer).
     * 
     * @param ids
     *            A single post (question or answer) id or a list of ids.
     * @return a list of comments associated with a post.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Comment> getCommentsByPostId(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = postClient.sendGetRequest(site, VERSION, "posts/"
                + vectorizedList + "/comments", soApiKey);
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets the comments associated with a post (question or answer).
     * 
     * @param query
     * @return a list of comments associated with a post.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Comment> getCommentsByPostId(CommentQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = postClient.sendGetRequest(site, VERSION,
                "posts/" + query.getIds() + "/comments", soApiKey,
                query.getUrlParams());
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /***** Question Methods *****/

    private final HttpClient questionClient = new HttpClient();

    /**
     * Gets a list of questions and summary information. By default, ordered by
     * last activity, date descending.
     * 
     * @return a list of questions.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> listQuestions() throws IOException, JSONException {
        String json = questionClient.sendGetRequest(site, VERSION,
                "/questions", soApiKey);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets a list of questions and summary information.
     * 
     * @param query
     * @return a list of questions.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> listQuestions(QuestionQuery query)
            throws IOException, JSONException {
        String urlParams = query.getUrlParams();
        String json = questionClient.sendGetRequest(site, VERSION,
                "/questions", soApiKey, urlParams);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets a single question specified by its id.
     * 
     * @param id
     *            A single question id.
     * @return a single question specified by 'id'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public Question getQuestionById(int id, String params) throws IOException,
            JSONException {
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + id, soApiKey, params);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this)).get(0);
    }

    /**
     * Gets a list of questions specified by 'ids'.
     * 
     * @param ids
     *            A single question id or a list of ids.
     * @return a list of questions specified by 'ids'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> getQuestionsById(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + vectorizedList, soApiKey);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets a list of questions specified by 'ids'.
     * 
     * @param query
     * @return a list of questions specified by 'ids'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Question> getQuestions(QuestionQuery query) throws IOException,
            JSONException, ParameterNotSetException {
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + query.getIds(), soApiKey, query.getUrlParams());
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets any answers to the questions specified by 'ids'.
     * 
     * @param ids
     *            A single question id or a list of ids.
     * @return a list of answers to the questions specified by 'ids'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Answer> getAnswersByQuestionId(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + vectorizedList + "/answers", soApiKey);
        return new MetadataList<Answer>(json, Answer.fromJSONString(json, this));
    }

    /**
     * Gets any answers to the question with 'id'.
     * 
     * @param query
     * @return a list of answers to the questions specified by 'ids'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Answer> getAnswersByQuestionId(AnswerQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + query.getIds() + "/answers", soApiKey, query.getUrlParams());
        return new MetadataList<Answer>(json, Answer.fromJSONString(json, this));
    }

    /**
     * Gets any comments to the questions specified by 'ids'.
     * 
     * @param ids
     *            A single question id or a list of ids.
     * @return a list of comments to the questions specified by 'ids'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Comment> getCommentsByQuestionId(int... ids)
            throws IOException, JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + vectorizedList + "/comments", soApiKey);
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets any comments to the questions specified by 'ids'.
     * 
     * @param query
     * @return a list of comments to the questions specified by 'ids'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Comment> getCommentsByQuestionId(CommentQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + query.getIds() + "/comments", soApiKey, query.getUrlParams());
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets the timeline of events for the questions with the given 'ids'.
     * 
     * @param ids
     *            A single question id or a list of ids.
     * @return a list of post timeline events.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<PostTimeline> getQuestionTimeline(int... ids)
            throws JSONException, IOException {
        String vectorizedList = buildVectorizedList(ids);
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + vectorizedList + "/timeline", soApiKey);
        return PostTimeline.fromJSONString(json, this);
    }

    /**
     * Gets the timeline of events for the question with the given 'id'.
     * 
     * @param query
     * @return a list of post timeline events.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<PostTimeline> getQuestionTimeline(TimelineQuery query)
            throws JSONException, IOException, ParameterNotSetException {
        String json = questionClient.sendGetRequest(site, VERSION, "questions/"
                + query.getIds() + "/timeline", soApiKey, query.getUrlParams());
        return PostTimeline.fromJSONString(json, this);
    }

    /**
     * Gets questions that have no upvoted answers.
     * 
     * @return a list of questions that have no upvoted answers.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> listUnansweredQuestions() throws IOException,
            JSONException {
        String json = questionClient.sendGetRequest(site, VERSION,
                "/questions/unanswered", soApiKey);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets questions that have no upvoted answers.
     * 
     * @param query
     * @return a list of questions that have no upvoted answers.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> listUnansweredQuestions(UnansweredQuery query)
            throws IOException, JSONException {
        String urlParams = query.getUrlParams();
        String json = questionClient.sendGetRequest(site, VERSION,
                "/questions/unanswered", soApiKey, urlParams);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /***** Revision Methods *****/

    private final HttpClient revisionClient = new HttpClient();

    /**
     * Gets the post history revisions for a set of posts in 'ids'.
     * 
     * @param ids
     *            A single post id or a list of ids.
     * @return a list of post history revisions for a set of posts in 'ids'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Revision> getRevisionsByPostId(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = revisionClient.sendGetRequest(site, VERSION,
                "/revisions/" + vectorizedList, soApiKey);
        return Revision.fromJSONString(json, this);
    }

    /**
     * Gets the post history revisions for a set of posts.
     * 
     * @param query
     * @return a list of post history revisions for a set of posts.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Revision> getRevisionsByPostId(RevisionQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = revisionClient.sendGetRequest(site, VERSION,
                "/revisions/" + query.getIds(), soApiKey, query.getUrlParams());
        return Revision.fromJSONString(json, this);
    }

    /**
     * Get a specific post revision.
     * 
     * @param guid
     *            A specific revision GUID.
     * @param ids
     *            A single post id or a list of ids.
     * @return a specific post revision.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public Revision getRevisionByGuid(String guid, int... ids)
            throws IOException, JSONException {
        if (!RevisionQuery.isValidGuid(guid)) {
            throw new IllegalArgumentException("Invalid GUID format: " + guid);
        }
        String vectorizedList = buildVectorizedList(ids);
        String json = revisionClient.sendGetRequest(site, VERSION,
                "/revisions/" + vectorizedList + "/" + guid, soApiKey);
        return Revision.fromJSONString(json, this).get(0);
    }

    /**
     * Get a specific post revision.
     * 
     * @param query
     * @return a specific post revision.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids and/or no guid are set in the query.
     */
    public Revision getRevisionByGuid(RevisionQuery query) throws IOException,
            JSONException, ParameterNotSetException {
        String json = revisionClient.sendGetRequest(site, VERSION,
                "/revisions/" + query.getIds() + "/" + query.getGuid(),
                soApiKey, query.getUrlParams());
        return Revision.fromJSONString(json, this).get(0);
    }

    /***** Search Method *****/

    /**
     * Searches questions. One of intitle, tagged, or nottagged must be set.
     * TODO: This should throw an exception if one of the above conditions is
     * not met. Searches that are purely text based should be routed through a
     * third-party search engine, for performance reasons.
     * 
     * @param query
     * @return a list of questions that match the search criteria.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> search(SearchQuery query) throws IOException,
            JSONException {
        String json = new HttpClient().sendGetRequest(site, VERSION, "search",
                soApiKey, query.getUrlParams());
        MetadataList<Question> questions = null;
        try {
            questions = new MetadataList<Question>(json,
                    Question.fromJSONString(json, this));
        } catch (JSONException je) {
            // if the Error can't be parsed, a generic JSONException is thrown.
            net.sf.stackwrap4j.entities.Error error = new net.sf.stackwrap4j.entities.Error(
                    json, this);
            throw new JSONException(error.getMessage());
        }
        return questions;
    }

    /***** Stats Method *****/

    /**
     * Gets various system statistics.
     * 
     * @return various system statistics.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public Stats getStats() throws JSONException, IOException {
        String response = new HttpClient().sendGetRequest(site, VERSION,
                "stats/", soApiKey);
        Stats stats = Stats.fromJSONString(response, this);
        return stats;
    }

    /***** Tags Methods *****/

    private final HttpClient tagsClient = new HttpClient();

    /**
     * Gets the tags on all questions, along with their usage counts.
     * 
     * @return a list of tags on all questions.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Tag> listTags() throws IOException, JSONException {
        String json = tagsClient.sendGetRequest(site, VERSION, "tags/",
                soApiKey);
        return Tag.fromJSONString(json, this);
    }

    /**
     * Gets the tags along with their usage counts.
     * 
     * @param query
     * @return a list of tags.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Tag> listTags(TagQuery query) throws IOException, JSONException {
        String urlParams = query.getUrlParams();
        String json = tagsClient.sendGetRequest(site, VERSION, "tags/",
                soApiKey, urlParams);
        return Tag.fromJSONString(json, this);
    }

    /***** User Methods *****/

    private final HttpClient userClient = new HttpClient();

    /**
     * Get the default list of Users.
     * 
     * @return a list of users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<User> listUsers() throws IOException, JSONException {
        String json = userClient.sendGetRequest(site, VERSION, "users",
                soApiKey);
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this));
    }

    /**
     * Gets user summary information. Does not take into account provided ids. <br />
     * Will return th edefault list of users given page, pageSize, and other
     * params
     * 
     * @param query
     * @return a list of users.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public List<User> listUsers(UserQuery query) throws IOException,
            JSONException {
        String urlParams = query.getUrlParams();
        String json = userClient.sendGetRequest(site, VERSION, "users",
                soApiKey, urlParams);
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this));
    }

    /**
     * Gets user summary information.
     * 
     * @param id
     *            A single user id.
     * @return a single user's summary information.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public User getUserById(int id) throws JSONException, IOException {
        String json = userClient.sendGetRequest(site, VERSION, "users/" + id,
                soApiKey);
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this)).get(0);
    }

    /**
     * Gets user summary information.
     * 
     * @param ids
     *            A single user id or multiple ids.
     * @return a single user's summary information.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public List<User> getUsersById(int... ids) throws JSONException,
            IOException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList, soApiKey);
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this));
    }

    /**
     * Gets user summary information.
     * 
     * @param query
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<User> getUsersById(UserQuery query) throws JSONException,
            IOException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds(), soApiKey, query.getUrlParams());
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this));
    }

    /**
     * Gets a list of answers by the specified user.
     * 
     * @param ids
     *            A single user id or list of ids.
     * @return a list of answers by the specified users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Answer> getAnswersByUserId(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/answers", soApiKey);
        return new MetadataList<Answer>(json, Answer.fromJSONString(json, this));
    }

    /**
     * Gets a list of answers by the specified user.
     * 
     * @param query
     * @return a list of answers by the specified users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Answer> getAnswersByUserId(AnswerQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/answers", soApiKey,
                query.getUrlParams());
        return new MetadataList<Answer>(json, Answer.fromJSONString(json, this));
    }

    /**
     * Get the badges for the specified user.
     * 
     * @param ids
     *            A single user id or list of ids.
     * @return a list of badges for the specified users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Badge> getBadgesByUserId(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/badges", soApiKey);
        return Badge.fromJSONString(json, this);
    }

    /**
     * Get a list of comments by the specified user.
     * 
     * @param ids
     *            A single user id or list of ids.
     * @return a list of badges for the specified users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Comment> getCommentsByUserId(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/comments/", soApiKey);
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets the comments that a set of users have made.
     * 
     * @param query
     * @return a list of comments by the specified users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Comment> getCommentsByUserId(CommentQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/comments/", soApiKey,
                query.getUrlParams());
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Get a list of comments from one user to another.
     * 
     * @param toId
     *            id of the user referred to.
     * @param fromIds
     *            A single user id or a list of ids.
     * @return a list of comments by a set of users that mention the user with
     *         'toid'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Comment> getCommentsFromUsersToUser(int toId, int... fromIds)
            throws IOException, JSONException {
        String vectorizedList = buildVectorizedList(fromIds);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/comments/" + toId + "/", soApiKey);
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets the comments by a set of users that mention the user with 'toid'.
     * 
     * @param query
     * @param toId
     *            id of the user referred to.
     * @return a list of comments by a set of users that mention the user with
     *         'toid'.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Comment> getCommentsFromUsersToUser(CommentQuery query, int toId)
            throws IOException, JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/comments/" + toId + "/",
                soApiKey, query.getUrlParams());
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets questions marked as favorite by a specific user. Answers, comments,
     * and the question body are included in the response by default.
     * 
     * @param ids
     *            A single user id or a list of ids.
     * @return a list of questions marked as favorite by a specific user.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> getFavoriteQuestionsByUserId(int... ids)
            throws IOException, JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/favorites", soApiKey);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets questions marked as favorite by a specific user or list of users.
     * 
     * @param query
     * @return a list of questions marked as favorite by a specific user.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Question> getFavoriteQuestionsByUserId(FavoriteQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/favorites", soApiKey,
                query.getUrlParams());
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets comments that are directed at a set of users.
     * 
     * @param ids
     *            A single user id or a list of ids.
     * @return a List of Comments mentioning a user or users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Comment> getUserMentions(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/mentioned", soApiKey);
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets comments that are directed at a set of users.
     * 
     * @param query
     * @return a List of Comments mentioning a user or users.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Comment> getUserMentions(CommentQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/mentioned", soApiKey,
                query.getUrlParams());
        return new MetadataList<Comment>(json, Comment.fromJSONString(json,
                this));
    }

    /**
     * Gets Questions created by a specific user.
     * 
     * @param userIds
     *            A single user id or a list of ids.
     * @return a list of Questions created by a specific user.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Question> getQuestionsByUserId(int... userIds)
            throws IOException, JSONException {
        String vectorizedList = buildVectorizedList(userIds);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/questions", soApiKey);
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets Questions created by a specific user.
     * 
     * @param query
     * @return a list of Questions created by a specific user.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Question> getQuestionsByUserId(UserQuestionQuery query)
            throws IOException, JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/questions", soApiKey,
                query.getUrlParams());
        return new MetadataList<Question>(json, Question.fromJSONString(json,
                this));
    }

    /**
     * Gets reputation gain/loss over a given time period.
     * 
     * @param ids
     *            A single user id or a list of ids.
     * @return reputation gain/loss over a given time period.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Reputation> getReputationByUserId(int... ids)
            throws JSONException, IOException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/reputation", soApiKey);
        return new MetadataList<Reputation>(json, Reputation.fromJSONString(
                json, this));
    }

    /**
     * Gets reputation gain/loss over a given time period.
     * 
     * @param query
     * @return reputation gain/loss over a given time period.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Reputation> getReputationByUserId(ReputationQuery query)
            throws JSONException, IOException, ParameterNotSetException {
        String queryParams = query.getUrlParams();
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/reputation", soApiKey,
                queryParams);
        return new MetadataList<Reputation>(json, Reputation.fromJSONString(
                json, this));
    }

    /**
     * Get the tags that a user participated in.
     * 
     * @param ids
     *            A single user id or a list of ids.
     * @return a list of tags that a user participated in.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     */
    public List<Tag> getTagsByUserId(int... ids) throws IOException,
            JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/tags", soApiKey);
        return Tag.fromJSONString(json, this);
    }

    /**
     * Get the tags that a user participated in.
     * 
     * @param query
     * @return a list of tags that a user participated in.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<Tag> getTagsByUserId(TagQuery query) throws IOException,
            JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/tags", soApiKey,
                query.getUrlParams());
        return Tag.fromJSONString(json, this);
    }

    /**
     * Get the tags that one or more users participated in.
     * 
     * @param query
     * @return a map of user ids to a list of tags that a user participated in.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public SparseArray<List<Tag>> getTagMapByUserId(int... ids)
            throws IOException, JSONException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/tags", soApiKey);
        
        SparseArray<List<Tag>> rc = new SparseArray<List<Tag>>();
        List<Tag> allTags = Tag.fromJSONString(json, this);
        for (Tag t : allTags) {
            final Integer userId = t.getUserId();
            List<Tag> userTags = rc.get(userId);
            if (userTags == null) {
                userTags = new ArrayList<Tag>();
                rc.put(userId, userTags);
            }
            userTags.add(t);
        }
        return rc;
    }

    /**
     * Get the tags that one or more users participated in.
     * 
     * @param query
     * @return a map of user ids to a list of tags that a user participated in.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public SparseArray<List<Tag>> getTagMapByUserId(TagQuery q)
            throws IOException, JSONException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + q.getIds() + "/tags", soApiKey);
        SparseArray<List<Tag>> rc = new SparseArray<List<Tag>>();
        List<Tag> allTags = Tag.fromJSONString(json, this);
        for (Tag t : allTags) {
            final Integer userId = t.getUserId();
            List<Tag> userTags = rc.get(userId);
            if (userTags == null) {
                userTags =  new ArrayList<Tag>();
                rc.put(userId,userTags);
            }
            userTags.add(t);
        }
        return rc;
    }

    /**
     * Gets a timeline for a user. This is the same information available via
     * the users activity tab.
     * 
     * @param ids
     *            a single user id or a list of ids.
     * @return a list of user timeline events.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public List<UserTimeline> getUserTimeline(int... ids) throws JSONException,
            IOException {
        String vectorizedList = buildVectorizedList(ids);
        String json = userClient.sendGetRequest(site, VERSION, "users/"
                + vectorizedList + "/timeline", soApiKey);
        return UserTimeline.fromJSONString(json, this);
    }

    /**
     * Gets a timeline for a user. This is the same information available via
     * the users activity tab.
     * 
     * @param query
     * @return a list of user timeline events.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws ParameterNotSetException
     *             if no ids are set in the query.
     */
    public List<UserTimeline> getUserTimeline(TimelineQuery query)
            throws JSONException, IOException, ParameterNotSetException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/" + query.getIds() + "/timeline", soApiKey,
                query.getUrlParams());
        return UserTimeline.fromJSONString(json, this);
    }

    /**
     * Gets all the moderators on this site.
     * 
     * @return a List of users who are moderators.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public List<User> listModerators() throws JSONException, IOException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/moderators", soApiKey);
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this));
    }

    /**
     * Gets all the moderators on this site.
     * 
     * @return a List of users who are moderators.
     * @throws JSONException
     *             If there is a problem parsing the response.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public List<User> listModerators(UserQuery query) throws JSONException,
            IOException {
        String json = userClient.sendGetRequest(site, VERSION,
                "users/moderators", soApiKey, query.getUrlParams());
        return new MetadataList<User>(json, User.fromJSONArray(new JSONObject(
                json).getJSONArray("users"), this));
    }

    /******* Utility methods *********/

    /* Build a vectorized list of ids. */
    private static String buildVectorizedList(int... id) {
        StringBuilder sb = new StringBuilder("" + id[0]);
        for (int i = 1; i < id.length; ++i) {
            sb.append(";" + id[i]);
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((site == null) ? 0 : site.hashCode());
        result = prime * result + ((VERSION == null) ? 0 : VERSION.hashCode());
        result = prime * result
                + ((soApiKey == null) ? 0 : soApiKey.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof StackWrapper)) {
            return false;
        }
        StackWrapper other = (StackWrapper) obj;
        if (site == null) {
            if (other.site != null) {
                return false;
            }
        } else if (!site.equals(other.site)) {
            return false;
        }
        if (VERSION == null) {
            if (other.VERSION != null) {
                return false;
            }
        } else if (!VERSION.equals(other.VERSION)) {
            return false;
        }
        if (soApiKey == null) {
            if (other.soApiKey != null) {
                return false;
            }
        } else if (!soApiKey.equals(other.soApiKey)) {
            return false;
        }
        return true;
    }

}
