package net.sf.stackwrap4j.stackauth.entities;

import android.graphics.Color;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;

public class Styling {
    private static final String TAG = Styling.class.getSimpleName();
    private String linkColor;
    private String tagForegroundColor;
    private String tagBackgroundColor;
    private int mLinkColor;
    private int mTagForegroundColor;
    private int mTagBackgroundColor;

    protected Styling(JSONObject jS) throws JSONException {
        linkColor = jS.getString("link_color");
        linkColor = fixColorString(linkColor);
        mLinkColor = Color.parseColor(linkColor);
        tagForegroundColor = jS.getString("tag_foreground_color");
        tagForegroundColor = fixColorString(tagForegroundColor);
        mTagForegroundColor = Color.parseColor(tagForegroundColor);
        tagBackgroundColor = jS.getString("tag_background_color");
        tagBackgroundColor = fixColorString(tagBackgroundColor);
        mTagBackgroundColor = Color.parseColor(tagBackgroundColor);
    }

    private String fixColorString(String color) {
        StringBuilder colorBuilder = new StringBuilder();
        colorBuilder.append(color);
        //456 -> #456
        if (!color.startsWith("#")) {
            color = "#" + color;
            colorBuilder.insert(0, '#');
        }
        //#456 -> #445566
        if (color.length() < 6) {
            colorBuilder.insert(1, color.charAt(1));//#4456
            colorBuilder.insert(3, color.charAt(2));//#44556
            colorBuilder.insert(5, color.charAt(3));//#445566
        }
        return colorBuilder.toString();
    }

    public int getLinkColor() {
        return mLinkColor;
    }

    public int getTagForegroundColor() {
        return mTagForegroundColor;
    }

    public int getTagBackgroundColor() {
        return mTagBackgroundColor;
    }

    public String getLinkColorString() {
        return linkColor;
    }

    public String getTagForegroundColorString() {
        return tagForegroundColor;
    }

    public String getTagBackgroundColorString() {
        return tagBackgroundColor;
    }

}
