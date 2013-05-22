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

package net.sf.stackwrap4j.json;

/**
 * PoliteJSONObject is a wrapper class around JSONObject.<br />
 * It defines similar methods as JSONObject, except none of the methods in PoliteJSONObject will ever
 * throw an exception.<br />
 * They instead favor taking a default value, and returning that in the case of a key not found.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public class PoliteJSONObject {

    private JSONObject obj;

    /**
     * Creates a new PoliteJSONObject from an existing JSONObject
     * 
     * @param obj
     *            The JSONObject to wrap
     */
    public PoliteJSONObject(JSONObject obj) {
        this.obj = obj;
    }

    /**
     * Will attempt to retrieve a JSONObject from the specified key
     * 
     * @param key
     *            the key to retrieve
     * @return the JSONObject found at the specified key, or null otherwise
     */
    public JSONObject tryGetJSONObject(String key) {
        if (!obj.has(key))
            return null;
        try {
            return obj.getJSONObject(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }

    /**
     * Will attempt to retrieve a JSONArray from the specified key
     * 
     * @param key
     *            the key to retrieve
     * @return the JSONArray found at the specified key, or null otherwise
     */
    public JSONArray tryGetJSONArray(String key) {
        if (!obj.has(key))
            return null;
        try {
            return obj.getJSONArray(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }

    /**
     * Will attempt to retrieve an Object from the specified key
     * 
     * @param key
     *            the key to retrieve
     * @return the Object found at the specified key, or null otherwise
     */
    public Object tryGet(String key) {
        if (!obj.has(key))
            return null;
        try {
            return obj.get(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }

    /**
     * Will attempt to retrieve a String from the specified key
     * 
     * @param key
     *            the key to retrieve
     * @return the String found at the specified key, or null otherwise
     */
    public String tryGetString(String key) {
        if (!obj.has(key))
            return null;
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }

    /**
     * Will attempt to retrieve a boolean from the specified key
     * 
     * @param key
     *            the key to retrieve
     * @param defaultRet
     *            the default value to return if the key is not found
     * @return the value at the key if found, or the default value
     */
    public boolean tryGetBoolean(String key, boolean defaultRet) {
        if (!obj.has(key))
            return defaultRet;
        try {
            return obj.getBoolean(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }

    /**
     * Will attempt to retrieve an int from the specified key
     * 
     * @param key
     *            the key to retrieve
     * @param defaultRet
     *            the default value to return if the key is not found
     * @return the value at the key if found, or the default value
     */
    public int tryGetInt(String key, int defaultRet) {
        if (!obj.has(key))
            return defaultRet;
        try {
            return obj.getInt(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }
    
    /**
     * Will attempt to retrieve an Integer from the specified key.
     * @param key the key to retrieve
     * @return the value at the key if found, or null.
     */
    public Integer tryGetInteger(String key) {
        if (!obj.has(key))
            return null;
        try {
            return obj.getInt(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }

    /**
     * Will attempt to retrieve a long from the specified key
     * 
     * @param key
     *            the key to retrieve
     * @param defaultRet
     *            the default value to return if the key is not found
     * @return the value at the key if found, or the default value
     */
    public long tryGetLong(String key, long defaultRet) {
        if (!obj.has(key))
            return defaultRet;
        try {
            return obj.getLong(key);
        } catch (JSONException e) {
            throw new RuntimeException("I dunno why this exception could get thrown.");
        }
    }

}
