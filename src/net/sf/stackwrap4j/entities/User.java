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
import java.util.ArrayList;
import java.util.List;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.datastructures.ReputationByUserList;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;
import net.sf.stackwrap4j.query.AnswerQuery;
import net.sf.stackwrap4j.query.CommentQuery;
import net.sf.stackwrap4j.query.ReputationQuery;
import net.sf.stackwrap4j.query.UserQuestionQuery;
import net.sf.stackwrap4j.stackauth.StackAuth;
import net.sf.stackwrap4j.stackauth.entities.Account;


/**
 * Represents one user of a StackOverflow family site.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class User extends StackObjBase {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -2672492877177944263L;
    
    /** The default page returned in a response. */
	public static final int DEFAULT_PAGE = 1;
	
	/** The default page size for a response. */
    public static final int DEFAULT_PAGE_SIZE = 35;
    
    /** The default filter options to use when requesting a page of users. */
    public static final String DEFAULT_FILTER = null;

    /** The user's unique numeric id. */
    private int id;
    
    /** The user's current reputation score. */
    private int reputation;
    
    /** The date the user account was created. */
    private long creationDate;
    
    /** The user's display name. */
    private String displayName = "";
    /** The user's profile_image url */
    private String profile_image="http://www.gravatar.com/avatar/?s=36&d=identicon&r=PG";
    
    /** The user's age (calculated from their birth date). */
    private int age;
    
    /** The last time the user was active. */
    private long lastAccessDate;
    
    /** The URL to the user's web site. */
    private String websiteUrl = "";
    
    /** The user's location. */
    private String location = "";
    
    /** The text entered in the user's "about me" section. */
    private String aboutMe = "";
    
    /** The number of times the user's profile has been viewed. */
    private int views;
    
    /** The number of upvotes cast by the user. */
    private int upVotes;
    
    /** The number of downvotes cast by the user. */
    private int downVotes;
    
    /** The number of questions posted by the user. */
    private int questionCount;
    
    /** The number of answers posted by the user. */
    private int answerCount;
    
    /** True if the user is a moderator on this site. */
    private boolean isModerator;
    
    /** The user's current acceptance rate. */
    private int acceptRate;
    
    /** The number of badges earned by this user. */
    private BadgeCounts badgeCounts;

    /** The id used to associate user accounts across the network. */
    private String associationId;
    
    /** The JSON string parsed to create this user. */
    private String json;

    /**
     * Constructs a new User from a String that represents a JSONObject.
     * @param jsonString A properly formatted JSON String
     * @param originator the StackExchange instance that created this User.
     * @throws JSONException If the string was not properly formatted, or if it wasn't in the expected format
     */
    User(final String jsonString, final StackWrapper originator) throws JSONException {
        this(new JSONObject(jsonString).getJSONArray("items").getJSONObject(0),
                originator);
        this.json = jsonString;
    }

    /**
     * Constructs a new User from a JSONObject that represents a User.
     * @param jU A properly formatted JSON object
     * @param originator the StackExchange instance that created this User.
     * @throws JSONException if an invalid parameter is requested.
     */
    User(final JSONObject jU, final StackWrapper originator) throws JSONException {
        super(originator);
        // Optional return values use the 'Polite JSON Object to get away from exceptions
        PoliteJSONObject jUp = new PoliteJSONObject(jU);
        age = jUp.tryGetInt("age", -1);
        isModerator = jUp.tryGetBoolean("is_moderator", false);
        websiteUrl = jUp.tryGetString("website_url");
        acceptRate = jUp.tryGetInt("accept_rate", -1);
        location = jUp.tryGetString("location");
        aboutMe = jUp.tryGetString("about_me");
        displayName = jUp.tryGetString("display_name");
        acceptRate = jUp.tryGetInt("accept_rate", -1);
        creationDate = jUp.tryGetLong("creation_date", -1);
        lastAccessDate = jUp.tryGetLong("last_access_date", -1);
        views = jUp.tryGetInt("view_count", 0);
        upVotes = jUp.tryGetInt("up_vote_count", 0);
        downVotes = jUp.tryGetInt("down_vote_count", 0);
        questionCount = jUp.tryGetInt("question_count", 0);
        answerCount = jUp.tryGetInt("answer_count", 0);
        
        associationId = jUp.tryGetString("association_id");

        JSONObject badgeCountsJSON = jUp.tryGetJSONObject("badge_counts");
        if (badgeCountsJSON == null) {
        	badgeCounts = new BadgeCounts(originator);
        } else {
        	badgeCounts = new BadgeCounts(badgeCountsJSON, originator);
        }
        
        id = jUp.tryGetInt("user_id",-1);
        reputation = jUp.tryGetInt("reputation",0);
        profile_image = jUp.tryGetString("profile_image");
    }

    /**
     * Convenience method for turning a JSONArray of JSONOBjects representing Users into a list of Users.
     * 
     * @param users JSONArray containing JSONObjects representing Users
     * @param originator the StackWrapper object from which the list of users originates
     * @return - A List<User> contained in the supplied JSONArray
     * @throws JSONException If the JSONObject was not properly formatted
     */
    public static List<User> fromJSONArray(final JSONArray users, final StackWrapper originator)
            throws JSONException {
        List<User> ret = new ArrayList<User>(users.length());
        for (int i = 0; i < users.length(); i++) {
            JSONObject jo = users.getJSONObject(i);
            User u = new User(jo, originator);
            ret.add(u);
        }
        return ret;
    }

    /**
     * @return - HTML String containing information about a User
     */
    public String getAboutMe() {
        return aboutMe;
    }

    /**
     * @return - The current age of the user or -1 if an age isn't supplied
     */
    public int getAge() {
        return age;
    }

    /**
     * @return - A Unix epoch time stamp of the date and time the user was created
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * @return - The name that a User displays on their profile
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return - The number of downvotes a user has cast
     */
    public int getDownVotes() {
        return downVotes;
    }

    /**
     * @return - returns the MD5 hash of the User's email address.
     */
    public String getprofile_image() {
        return profile_image;
    }

    /**
     * @return - The id for the User
     */
    public int getId() {
        return id;
    }

    /**
     * @return - The JSONString that was used to create this user if one was supplied
     */
    protected String getJson() {
        return json;
    }

    /**
     * @return - A Unix epoch time stamp representing the last time this User accessed its account
     */
    public long getLastAccessDate() {
        return lastAccessDate;
    }

    /**
     * @return - The location this User is at, or null if no location is supplied
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return - The current reputation of the User. This number is not 100% correct in all cases.
     */
    public int getReputation() {
        return reputation;
    }

    /**
     * @return - The number of upvotes this user has cast
     */
    public int getUpVotes() {
        return upVotes;
    }

    /** @return the number of questions this user has asked. */
    public int getQuestionCount() {
        return questionCount;
    }

    /** @return the number of answers this user has posted. */
    public int getAnswerCount() {
        return answerCount;
    }

    /**
     * @return - The total number of upvotes / downvotes for this user
     */
    public double getVoteRatio() {
        return getUpVotes() / (double) getDownVotes();
    }

    /**
     * @return - The number of times this profile has been viewed
     */
    public int getViews() {
        return views;
    }

    /**
     * @return - The url for this User's website, or null if none is supplied
     */
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    /**
     * @return - The accept rate for this user
     */
    public int getAcceptRate() {
        return acceptRate;
    }

    /**
     * @return - Whether or not this user has moderator powers for this website
     */
    public boolean isIsModerator() {
        return isModerator;
    }

    /**
     * Gets a list of comments left by this user.
     * @param query the query to filter comments by.
     * @return a list of comments
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Comment> getComments(final CommentQuery query) throws IOException, JSONException {
        query.addId(getId());
        List<Comment> comments = new ArrayList<Comment>();
        // will return an empty list in the unlikely event that something bad happens
        try {
            comments = getCreatingApi().getCommentsByUserId(query);
        } catch (ParameterNotSetException pnse) {
            // Intentionally left empty.  I just set the id.
        }
        return comments;
    }

    /**
     * Gets a list of comments left by the specified user to this user.
     * @param fromId the id of the other user
     * @param query the query to filter comments by.
     * @return a list of comments
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Comment> getCommentsFromUser(final int fromId, final CommentQuery query) 
            throws IOException, JSONException {
        query.addId(getId());
        List<Comment> comments = new ArrayList<Comment>();
        // will return an empty list in the unlikely event that something bad happens
        try {
            comments = getCreatingApi().getCommentsFromUsersToUser(query, fromId);
        } catch (ParameterNotSetException pnse) {
            // Intentionally left empty.  I just set the id.
        } 
        return comments;
    }

    /**
     * Gets a list of comments left by this user to the specified user.
     * @param toId the id of the user that the comments are written to.
     * @param query the query to filter comments by.
     * @return a list of comments
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Comment> getCommentsToUser(final int toId, final CommentQuery query) throws IOException, JSONException {
        query.addId(getId());
        List<Comment> comments = new ArrayList<Comment>();
        // will return an empty list in the unlikely event that something bad happens
        try {
            comments = getCreatingApi().getCommentsFromUsersToUser(query, toId);
        } catch (ParameterNotSetException pnse) {
            // Intentionally left empty.  I just set the id.
        }
        return comments;
    }

    /**
     * Gets a list of this user's badges.
     * @return a list of badges.
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Badge> getBadges() throws IOException, JSONException {
        return getCreatingApi().getBadgesByUserId(getId());
    }

    /**
     * Gets a list of tags frequently used by this user.
     * @return a list of tags
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Tag> getTags() throws IOException, JSONException {
        return getCreatingApi().getTagsByUserId(this.getId());
    }

    /**
     * Gets a list of this user's reputation changes.
     * @return a list of reputation changes.
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     * @throws ParameterNotSetException
     */
    public List<Reputation> getReputationInfo() throws JSONException, IOException {
        ReputationQuery query = new ReputationQuery();
        List<Reputation> reputationInfo = null;
        query.addId(getId());
        try {
            reputationInfo = getCreatingApi().getReputationByUserId(query);
        } catch (ParameterNotSetException pnse) {
            // Intentionally left empty.  I just set the id.
        }
        return reputationInfo;
    }

    /**
     * Gets a list of this user's reputation changes.
     * @param query the query to filter the list by.
     * @return a list of reputation changes.
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     * @throws ParameterNotSetException
     */
    public List<Reputation> getReputationInfo(final ReputationQuery query) throws JSONException, IOException {
        query.addId(getId());
        return new ReputationByUserList(getCreatingApi(), query);
    }

    /** A list of questions that this user has marked as favorite. */
    private List<Question> favs;

    /**
     * Gets questions favorited by this user.
     * @return a List of questions.
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Question> getFavorites() throws IOException, JSONException {
        if (favs == null) {
            favs = getCreatingApi().getFavoriteQuestionsByUserId(id);
        }
        return favs;
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
        result = prime * result + (int) (creationDate ^ (creationDate >>> 32));
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
        result = prime * result + ((profile_image == null) ? 0 : profile_image.hashCode());
        result = prime * result + id;
        result = prime * result + ((websiteUrl == null) ? 0 : websiteUrl.hashCode());
        return result;
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
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        if (creationDate != other.creationDate) {
            return false;
        }
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (profile_image == null) {
            if (other.profile_image != null) {
                return false;
            }
        } else if (!profile_image.equals(other.profile_image)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (websiteUrl == null) {
            if (other.websiteUrl != null) {
                return false;
            }
        } else if (!websiteUrl.equals(other.websiteUrl)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the questions belonging to the user.
     * @param query the parameters to use
     * @return a list of the questions based on what the query parameters were
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Question> getAllQuestions(final UserQuestionQuery query) throws IOException, JSONException {
        query.setIds(this.getId());
        List<Question> questions = new ArrayList<Question>();
        try {
            questions = getCreatingApi().getQuestionsByUserId(query);
        } catch (ParameterNotSetException pnse) {
            // Intentionally left empty.  I just set the id.
        }
        return questions;
    }

    /**
     * Gets a list of all questions asked by this user.
     * @return a list of questions.
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Question> getAllQuestions() throws IOException, JSONException {
        return getCreatingApi().getQuestionsByUserId(getId());
    }

    /**
     * Gets a list of answers left by this user.
     * @param query the filters to apply to the request.
     * @return a list of answers
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Answer> getUserAnswers(final AnswerQuery query) throws IOException, JSONException {
        query.addId(this.getId());
        List<Answer> answers = new ArrayList<Answer>();
        try {
            answers = getCreatingApi().getAnswersByUserId(query);
        } catch (ParameterNotSetException pnse) {
            // Intentionally left empty.  I just set the id.
        }
        return answers;
    }

    /**
     * Gets a list of all reputation changes for this user.
     * @return a list of reputation changes.
     */
    public List<Reputation> getAllReputationPoints() {
    	long start = this.getCreationDate();
		long end = System.currentTimeMillis() / 1000L;
		ReputationQuery query = new ReputationQuery();
		query.setToDate(end).setFromDate(start).setIds(this.getId());
    	return new ReputationByUserList(getCreatingApi(), query);
    }
    
    /**
     * @return this user's association id.
     */
    public String getAssociationId() {
        return associationId;
    }

    /**
     * Gets all accounts on all other Stack Exchange sites.
     * @return list of the user's associated accounts
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed correctly.
     */
    public List<Account> getAssociatedAccounts() throws IOException, JSONException {
        String assocId = getAssociationId();
        if (assocId == null) {
            return new ArrayList<Account>();
    	}
    	return StackAuth.getAssociatedAccounts(assocId);
    }

    /**
     * @return this user's badge counts.
     */
    public BadgeCounts getBadgeCounts() {
    	return badgeCounts;
    }

    /**
     * A class to store a user's badge counts.
     * @author Bill Cruise
     */
	public static class BadgeCounts extends StackObjBase {
	    /** The universal version identifier for a Serializable class. */
    	private static final long serialVersionUID = 6670910336247003658L;

    	/** Declare the types of badges. */
    	public final int bronze, silver, gold;
    	
    	/**
    	 * Creates an object from the originating API provided.
    	 * @param originator the StackWrapper object that provided this information.
    	 */
    	protected BadgeCounts(final StackWrapper originator) {
    		super(originator);
    		bronze = 0;
    		gold = 0;
    		silver = 0;
    	}
    	
    	/**
    	 * Creates an object from the JSON provided.
    	 * @param jB A properly formatted JSON object
    	 * @param originator the StackExchange instance that created this User.
    	 */
		public BadgeCounts(final JSONObject jB, final StackWrapper originator) {
	        super(originator);
	        PoliteJSONObject jBP = new PoliteJSONObject(jB);
	        bronze = jBP.tryGetInt("bronze", 0);
	        silver = jBP.tryGetInt("silver", 0);
	        gold = jBP.tryGetInt("gold", 0);
        }
    }
}
