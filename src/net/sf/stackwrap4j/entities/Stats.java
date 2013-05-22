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
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.stackauth.entities.Site;

/**
 * Summary statistics for a particular Stack Exchange site.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class Stats extends StackObjBase {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = 2791464414362708711L;
    
    /** Total number of questions posted on the site. */
	private int totalQuestions;
	
	/** Total number of unanswered questions posted on the site. */
    private int totalUnanswered;
    
    /** Total number of accepted questions posted on the site. */
    private int totalAccepted;
    
    /** Total number of answers posted on the site. */
    private int totalAnswers;
    
    /** Total number of comments posted on the site. */
    private int totalComments;
    
    /** Total number of votes cast on the site. */
    private int totalVotes;
    
    /** Total number of badges awarded on the site. */
    private int totalBadges;
    
    /** Total number of users on the site. */
    private int totalUsers;
    
    /** Average questions asked per minute on the site. */
    private double questionsPerMinute;
    
    /** Average answers posted per minute on the site. */
    private double answersPerMinute;
    
    /** Average badges awarded per minute on the site. */
    private double badgesPerMinute;
    
    /** Average pages viewed per day on the site. */
    private double viewsPerDay;
    
    /** API version used to request these stats. */
    private ApiVersion apiVersion = new ApiVersion();
    
    /** Static information (name, logo, URL) about the site. */
    private Site site;

    /**
     * Creates a Stats object from a JSON string.
     * @param json the JSON String representing stats about a specific site.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the JSON string was poorly formatted.
     */
    Stats(final String json, final StackWrapper originator) throws JSONException {
        super(originator);

        JSONObject jsonObj = new JSONObject(json);
        JSONArray statsArray = jsonObj.getJSONArray("items");
        JSONObject statsObj = statsArray.getJSONObject(0);

        totalQuestions = statsObj.getInt("total_questions");
        totalUnanswered = statsObj.getInt("total_unanswered");
        totalAccepted = statsObj.getInt("total_accepted");
        totalAnswers = statsObj.getInt("total_answers");
        totalComments = statsObj.getInt("total_comments");
        totalVotes = statsObj.getInt("total_votes");
        totalBadges = statsObj.getInt("total_badges");
        totalUsers = statsObj.getInt("total_users");
        questionsPerMinute = statsObj.getDouble("questions_per_minute");
        answersPerMinute = statsObj.getDouble("answers_per_minute");
        badgesPerMinute = statsObj.getDouble("badges_per_minute");
        viewsPerDay = statsObj.getDouble("views_per_day");
        JSONObject apiVerObj = statsObj.getJSONObject("api_version");
        apiVersion.version = apiVerObj.getString("version");
        apiVersion.revision = apiVerObj.getString("revision");
        site = new Site(statsObj.getJSONObject("site"));
    }

    /**
     * A class to represent the API version used to
     * request stats for a Stack Exchange site.
     * @author Bill Cruise
     */
    class ApiVersion {
        String version;
        String revision;
    }

    /**
     * @return the totalAccepted
     */
    public final int getTotalAccepted() {
        return totalAccepted;
    }

    /**
     * @return the site
     */
    public final Site getSite() {
        return site;
    }

    /**
     * @return the answers per minute
     */
    public final double getAnswersPerMinute() {
        return answersPerMinute;
    }

    /**
     * @return the API revision
     */
    public final String getApiRevision() {
        return apiVersion.revision;
    }

    /**
     * @return the API version
     */
    public final String getApiVersion() {
        return apiVersion.version;
    }

    /**
     * @return the badges per minute
     */
    public final double getBadgesPerMinute() {
        return badgesPerMinute;
    }

    /**
     * @return the questions per minute
     */
    public final double getQuestionsPerMinute() {
        return questionsPerMinute;
    }
    
    /**
     * @return the reviews per day
     */
    public final double getViewsPerDay() {
        return viewsPerDay;
    }

    /**
     * @return the total number of answers
     */
    public final int getTotalAnswers() {
        return totalAnswers;
    }

    /**
     * @return the total number of badges awarded
     */
    public final int getTotalBadges() {
        return totalBadges;
    }

    /**
     * @return the total number of comments
     */
    public final int getTotalComments() {
        return totalComments;
    }

    /**
     * @return the total number of questions
     */
    public final int getTotalQuestions() {
        return totalQuestions;
    }

    /**
     * @return the total number of unanswered questions
     */
    public final int getTotalUnanswered() {
        return totalUnanswered;
    }

    /**
     * @return the total number of users
     */
    public final int getTotalUsers() {
        return totalUsers;
    }

    /**
     * @return the total number of votes cast
     */
    public final int getTotalVotes() {
        return totalVotes;
    }

    /**
     * Parses a JSON string into a list of Stats.
     * @param json string containing stats.
     * @param originator the StackExchange instance that created this
     * @return a list of stats.
     * @throws JSONException if the string cannot be parsed.
     */
    public static Stats fromJSONString(final String json, final StackWrapper originator) throws JSONException {
        return new Stats(json, originator);
    }
}
