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

package net.sf.stackwrap4j.stackauth;

import com.droidcat.stackranger.util.Config;
import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.http.HttpClient;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.query.PageQuery;
import net.sf.stackwrap4j.stackauth.entities.Account;
import net.sf.stackwrap4j.stackauth.entities.Site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Access point for the StackAuth data.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 * 
 */
public class StackAuth {

    private static final String VERSION = "2.1/";

    private static final HttpClient client = new HttpClient();

    // Disallow instantiation
    private StackAuth() {
    }

    public static List<Site> getAllSites(PageQuery pageQuery) throws IOException, JSONException {
        String json = client.sendGetRequest(null, VERSION, "sites/",
                Config.APIKEY,pageQuery.getUrlParams());
        return Site.fromJSONArray(new JSONObject(json).getJSONArray("items"));
    }

    /**
     * Convenience method that allows you to get a map of Sites keyed by name.
     * 
     * @return a Map of Site objects keyed by their site name.
     * @throws IOException
     * @throws JSONException
     */
    public static Map<String, Site> getNameSiteMap(PageQuery pageQuery) throws IOException,
            JSONException {
        List<Site> sites = getAllSites(pageQuery);
        Map<String, Site> sitesMap = new HashMap<String, Site>();

        for (Site site : sites) {
            sitesMap.put(site.getName(), site);
        }
        return sitesMap;
    }

    public static List<Account> getAssociatedAccounts(String userId)
            throws IOException, JSONException {
        String json = client.sendGetRequest(null, VERSION, "users/" + userId
                + "/associated/", null);
        return Account.fromJSONArray(new JSONObject(json)
.getJSONArray("items"));
    }

    public static List<StackWrapper> getAllWrappers(String key,PageQuery pageQuery)
            throws IOException, JSONException {
        List<StackWrapper> ret = new ArrayList<StackWrapper>();
        for (Site s : getAllSites(pageQuery)) {
            ret.add(s.getStackWrapper(key));
        }
        return ret;
    }

    public static List<StackWrapper> getAllWrappers() throws IOException,
            JSONException {
        return getAllWrappers(null,null);
    }
}
