package net.sf.stackwrap4j.http;

/**
 * Defines different throttling methods for HTTP requests.
 * NONE - User managed.
 * THREADED - Uses a request queue and makes requests on a separate thread.
 * NON_THREADED - Uses a simple timer to throttle requests to a maximum rate.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public enum Throttle {
    NONE, THREADED, NON_THREADED;
}
