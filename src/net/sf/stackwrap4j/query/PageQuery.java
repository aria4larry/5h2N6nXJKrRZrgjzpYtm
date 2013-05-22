package net.sf.stackwrap4j.query;

import net.sf.stackwrap4j.query.sort.ISort;

/**
 * This class serves as a buffer between the BaseQuery and those query classes that need common paging methods.
 * It's still up to the child classes to set their own unique default values.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 *
 */
public abstract class PageQuery extends BaseQuery {

    private static final long serialVersionUID = 8136853699665621963L;
    
    public static final String PAGE_KEY = "page";
    public static final int MIN_PAGE_SIZE = 0;
    public static final int MAX_PAGE_SIZE = 100;
    
    /* 
     * Set to true if you want to allow the query to auto-increment the page number.
     * Auto-incrementing is done when getUrlParams is called.
     */
    private boolean autoIncrement = false;
    
    
    /**
     * Gets the URL parameters for this query object.
     * Auto-increments the page count if enabled.
     * @return a String of URL parameters in '&key=value' form.
     */
    @Override
    public String getUrlParams() {
        String urlParams = super.getUrlParams();
        if( autoIncrement ) {
            incrementPage();
        }
        return urlParams;
    }
    
    protected PageQuery(ISort defaultSort){
        super(defaultSort);
    }
    
    /**
     * @param page The pagination offset for the current collection. Affected by the specified pagesize.
     */
    public PageQuery setPage(int page) {
        put(PAGE_KEY, page + "");
        return this;
    }

    /**
     * Gets the page setting.
     * @return the page.
     */
    public int getPage() {
        return Integer.parseInt( get(PAGE_KEY) );
    }
    
    /**
     * @param pageSize The number of collection results to display during pagination. Should be between 0 and 100 inclusive.
     */
    public PageQuery setPageSize(int pageSize) {
        if( pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE ) {
            throw new IllegalArgumentException("The page size is out of range (" 
                                      + MIN_PAGE_SIZE + " - " + MAX_PAGE_SIZE + ").");
        }
        put("pagesize", pageSize + "");
        return this;
    }
    
    /**
     * Increment the page by 1.
     * @return this PageQuery object.
     */
    public PageQuery incrementPage() {
        int page = Integer.parseInt( get(PAGE_KEY) );
        put(PAGE_KEY, ++page + "");
        return this;
    }
    
    /**
     * Decrement the page by 1.
     * @return this PageQuery object.
     */
    public PageQuery decrementPage() {
        int page = Integer.parseInt( get(PAGE_KEY) );
        put(PAGE_KEY, --page + "");
        return this;
    }
    
    /**
     * Enable/disable auto-incrementing.
     * @param autoIncrement
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
    
    /**
     * Gets the auto-increment flag.
     * @return true if auto-incrementing is turned on, false otherwise.
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

}
