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

package net.sf.stackwrap4j.stackauth.entities;

import java.util.ArrayList;
import java.util.List;

import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;

/**
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 *
 */
public class Account {

    private Site onSite;
    private int userId;
    private String userType;
    private String displayName;
    private int reputation;
    private String emailHash;

    public Account(JSONObject jA) throws JSONException {
        userId = jA.getInt("user_id");
        userType = jA.getString("user_type");
        displayName = jA.getString("display_name");
        reputation = jA.getInt("reputation");
        emailHash = jA.getString("email_hash");

        PoliteJSONObject jAp = new PoliteJSONObject(jA);
        JSONObject siteObj = jAp.tryGetJSONObject("on_site");
        if (siteObj == null) {
            onSite = null;
        } else {
            onSite = new Site(siteObj);
        }
    }

    public Site getOnSite() {
        return onSite;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getReputation() {
        return reputation;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public static List<Account> fromJSONArray(JSONArray arr) throws JSONException {
        List<Account> ret = new ArrayList<Account>();
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Account(arr.getJSONObject(i)));
        }
        return ret;
    }
}
