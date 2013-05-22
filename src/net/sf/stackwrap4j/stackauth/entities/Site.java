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

import com.droidcat.stackranger.cache.SitesCache;
import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public class Site {
    private String aliases;
    private String api_site_parameter;
    private String audience;
    private String closed_beta_date;
    private String favicon_url;
    private String high_resolution_icon_url;
    private String icon_url;
    private long launch_date;
    private String logo_url;
    private String markdown_extensions;
    private String name;
    private long open_beta_date;
    private String related_sites;
    private String site_state;
    private String site_type;
    private String site_url;
    private Styling styling;
    private String twitter_account;

    /**
     * { "items": [ { "site_type": "main_site", "name": "Stack Overflow",
     * "logo_url": "http://cdn.sstatic.net/stackoverflow/img/logo.png",
     * "api_site_parameter": "stackoverflow", "site_url":
     * "http://stackoverflow.com", "audience":
     * "professional and enthusiast programmers", "icon_url":
     * "http://cdn.sstatic.net/stackoverflow/img/apple-touch-icon.png",
     * "aliases": [ "http://www.stackoverflow.com" ], "site_state": "normal",
     * "styling": { "link_color": "#0077CC", "tag_foreground_color": "#3E6D8E",
     * "tag_background_color": "#E0EAF1" }, "launch_date": 1221436800,
     * "favicon_url": "http://cdn.sstatic.net/stackoverflow/img/favicon.ico",
     * "related_sites": [ { "name": "Stack Overflow Chat", "site_url":
     * "http://chat.stackoverflow.com", "relation": "chat" } ],
     * "markdown_extensions": [ "Prettify" ], "high_resolution_icon_url":
     * "http://cdn.sstatic.net/stackoverflow/img/apple-touch-icon@2.png" } }
     * **/
    public Site(JSONObject jS) throws JSONException {
        PoliteJSONObject jSp = new PoliteJSONObject(jS);
        aliases = jSp.tryGetString("aliases");
        api_site_parameter = jS.getString("api_site_parameter");
        audience = jS.getString("audience");
        closed_beta_date = jSp.tryGetString("closed_beta_date");
        favicon_url = jS.getString("favicon_url");
        high_resolution_icon_url = jSp.tryGetString("high_resolution_icon_url");
        icon_url = jS.getString("icon_url");
        launch_date = jSp.tryGetLong("launch_date", 0);
        logo_url = jS.getString("logo_url");
        markdown_extensions = jSp.tryGetString("markdown_extensions");
        name = jS.getString("name");
        open_beta_date = jSp.tryGetLong("open_beta_date", 0);
        related_sites = jSp.tryGetString("related_sites");
        site_state = jS.getString("site_state");
        site_type = jS.getString("site_type");
        twitter_account = jSp.tryGetString("twitter_account");
        styling = new Styling(jS.getJSONObject("styling"));
        site_url = jS.getString("site_url");
        try {
            SitesCache.getInstance().cacheSite(this,jS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getAliases() {
        return aliases;
    }

    public String getApi_site_parameter() {
        return api_site_parameter;
    }

    public String getAudience() {
        return audience;
    }

    public String getClosed_beta_date() {
        return closed_beta_date;
    }

    public String getFavicon_url() {
        return favicon_url;
    }

    public String getHigh_resolution_icon_url() {
        return high_resolution_icon_url;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public long getLaunch_date() {
        return launch_date;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public String getMarkdown_extensions() {
        return markdown_extensions;
    }

    public long getOpen_beta_date() {
        return open_beta_date;
    }

    public String getRelated_sites() {
        return related_sites;
    }

    public String getSite_state() {
        return site_state;
    }

    public String getSite_type() {
        return site_type;
    }

    public String getSite_url() {
        return site_url;
    }

    public String getTwitter_account() {
        return twitter_account;
    }

    public Styling getStyling() {
        return styling;
    }

    public StackWrapper getStackWrapper(String key) {
        return new StackWrapper(this.getApi_site_parameter(), key);
    }

    public StackWrapper getStackWrapper() {
        return getStackWrapper(null);
    }

    @Override
    public String toString() {
        return this.getSite_url();
    }

    public static List<Site> fromJSONString(String jsonStr)
            throws JSONException {
        return fromJSONArray(new JSONArray(jsonStr));
    }

    public static List<Site> fromJSONArray(JSONArray arr) throws JSONException {
        List<Site> ret = new ArrayList<Site>();
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Site(arr.getJSONObject(i)));
        }
        return ret;
    }
}
