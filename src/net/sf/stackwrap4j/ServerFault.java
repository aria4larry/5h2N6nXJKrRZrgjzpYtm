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

package net.sf.stackwrap4j;

/**
 * Represents the Server Fault API access point.
 * 
 * @author Bill Cruise,
 * @author Justin Nelson
 */
public class ServerFault extends StackWrapper {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -5386480367065452217L;

	/**
     * Creates a StackWrapper object with the URL set to the Server Fault API access point.
     */
    public ServerFault() {
        super("http://api.serverfault.com/");
    }
    
    /**
     * Creates a StackWrapper object with the URL set to the Server Fault API access point.
     * This constructor accepts the client's API key.
     * @param apiKey the client program's API key.
     */
    public ServerFault(final String apiKey) {
        super("http://api.serverfault.com/", apiKey);
    }
}
