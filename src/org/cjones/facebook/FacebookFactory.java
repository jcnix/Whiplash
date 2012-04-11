package org.cjones.facebook;

import com.facebook.android.Facebook;

public class FacebookFactory
{
    public static final String APP_ID = "159488880804695";
    private static Facebook fb = null;

    protected FacebookFactory()
    {
    }

    public static Facebook getInstance()
    {
        if(fb == null)
        {
            fb = new Facebook(APP_ID);
        }
        return fb;
    }
}

