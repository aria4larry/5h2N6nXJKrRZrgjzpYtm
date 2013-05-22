package net.sf.stackwrap4j.query;

import net.sf.stackwrap4j.query.sort.BaseSort;

public class FavoriteQuery extends QuestionQuery {
    
    private static final long serialVersionUID = -1128351250307421626L;


    public FavoriteQuery(){
        super(Sort.activity());
    }
    
    
    /**
     * Represents many ways to sort favorite questions
     * one of activity (default), views, creation, added, or votes
     * 
     * @author Justin Nelson
     * @author Bill Cruise
     */
    public static class Sort extends BaseSort {

        private static final long serialVersionUID = -4331575702170248678L;

        protected Sort(String name, Object defaultMin, Object defaultMax) {
            super(name, defaultMin, defaultMax);
        }
        
        public static final Sort activity() {
            return new Sort("activity", 0L + "", 253402300799L + "");
        }
        
        public static final Sort views() {
            return new Sort("views", Integer.MIN_VALUE + "", Integer.MAX_VALUE + "");
        }

        public static final Sort creation() {
            return new Sort("creation", 0L + "", 253402300799L + "");
        }
        
        public static final Sort added() {
            return new Sort("added", 0L + "", 253402300799L + "");
        }

        public static final Sort votes() {
            return new Sort("votes", Integer.MIN_VALUE + "", Integer.MAX_VALUE + "");
        }
    }
}
