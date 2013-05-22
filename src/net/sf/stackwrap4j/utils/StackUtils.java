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

package net.sf.stackwrap4j.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

/**
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public class StackUtils {
    
    


    /**
     * Format reputation numbers similar to StackExchange sites.
     * @param rep the reputation score to format.
     * @return the formatted reputation string.
     */
    public static String formatRep(int rep) {
        if (rep < 1) 
            throw new IllegalArgumentException("The minimum reputation is 1");
        if(rep < 1000) {
            return rep + "";
        }
        if(rep < 10000) {
            NumberFormat formatter = new DecimalFormat("#,###");
            return formatter.format(rep);
        }
        else {
            NumberFormat formatter = new DecimalFormat("#,###.#k");
            double d = rep / 1000.0;
            return formatter.format(d);
        }
    }
    
    /**
     * Formats the time elapsed between fromDate and now.
     * See http://stackoverflow.com/questions/11/how-do-i-calculate-relative-time/
     * 
     * @param fromDate the start time, a unix epoch datetime stamp.
     * @return the formatted time elapsed.
     */
    public static String formatElapsedTime(long fromDate) {
        int SECOND = 1;
        int MINUTE = 60 * SECOND;
        int HOUR = 60 * MINUTE;
        int DAY = 24 * HOUR;
        int MONTH = 30 * DAY;
        
        long now = (new Date()).getTime();
        long delta = (now / 1000) - fromDate;
        
        if (delta < 1 * MINUTE) {
            return delta == 1 ? "one second ago" : delta + " seconds ago";
        }
        if (delta < 2 * MINUTE) {
            return "a minute ago";
        }
        if (delta < 45 * MINUTE) {
            return (delta / MINUTE) + " minutes ago";
        }
        if (delta < 90 * MINUTE) {
            return "an hour ago";
        }
        if (delta < 24 * HOUR) {
            return (delta / HOUR) + " hours ago";
        }
        if (delta < 48 * HOUR) {
            return "yesterday";
        }
        if (delta < 30 * DAY) {
            return (delta / DAY) + " days ago";
        }
        if (delta < 12 * MONTH) {
            int months = (int)(Math.floor((double) ((delta / DAY) / 30)));
            return months <= 1 ? "one month ago" : months + " months ago";
        } else {
            int years = (int)(Math.floor((double) (delta / DAY) / 365));
            return years <= 1 ? "one year ago" : years + " years ago";
        }
    }

}
