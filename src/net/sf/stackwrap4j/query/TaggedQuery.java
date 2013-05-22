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

package net.sf.stackwrap4j.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.query.sort.ISort;

/**
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public abstract class TaggedQuery extends PageQuery {

    private static final long serialVersionUID = 1589654098440384374L;
    
    protected List<String> tags = new ArrayList<String>();

    protected TaggedQuery(ISort defaultSort) {
        super(defaultSort);
    }
    
    @Override
    public String getUrlParams() {
        String urlParams = super.getUrlParams();
        return addTaggedParam(urlParams);
    };

    /**
     * @return the tags
     */
    public List<String> getTags() {
        return tags;
    }
    
    /**
     * Combines all tags into a semi-colon delimited list
     * @return the delimited list of tags
     */
    public String getTagList() throws ParameterNotSetException {
        if(tags.size() == 0) {
            throw new ParameterNotSetException("No tags have been added to the query.");
        }
        String ret = "";
        for (String tag: getTags()) {
            ret += tag + ";";
        }
        return ret.substring(0, ret.length()-1);
    }

    /**
     * Can be semicolon or space delimited
     * @param tags Required text in returned tags
     * @return Returns the calling object
     */
    public TaggedQuery setTags(String tags) {
        if (tags.contains(" ")){
            setTags(tags.toLowerCase().split(" "));
        } else {
            setTags(tags.toLowerCase().split(";"));
        }
        return this;
    }
    
    /**
     * Set the tags on this query
     * @param tags Required text in returned tags
     * @return Returns the calling object
     */
    public TaggedQuery setTags(String[] tags){
        this.tags = new ArrayList<String>(tags.length);
        for (String tag: tags){
            this.tags.add(tag.toLowerCase());
        }
        return this;
    }
    
    /**
     * Set the tags on this query
     * @param tags Required text in returned tags
     * @return Returns the calling object
     */
    public TaggedQuery setTags(List<String> tags){
        this.tags = tags;
        return this;
    }
    
    /**
     * Adds a new tag to the query
     * @param tag Required text in returned tags
     * @return Returns the calling object
     */
    public TaggedQuery addTag(String tag){
        if (!tags.contains("tag"))
        tags.add(tag.toLowerCase());
        return this;
    }

    private String addTaggedParam(String urlParameters){
        if (tags == null || tags.isEmpty()) 
            return urlParameters;
        StringBuilder ret = new StringBuilder();
        for (String tag: tags){
            ret.append(tag);
            ret.append(";");
        }
        urlParameters = addParameter( urlParameters, "tagged", ret.substring(0, ret.length()-1) );
        return urlParameters;
    }
    
    private static String addParameter(String urlParameters, String name, String value) {
        return urlParameters + "&" + name + "=" + value;
    }
}
