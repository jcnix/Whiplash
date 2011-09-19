/**
 *  BaseRequestListener .java
 */ 

package org.cjones.facebook;

import android.util.Log;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public abstract class BaseRequestListener implements RequestListener {

    public void onFacebookError(FacebookError ex, final Object state) {
        Log.e("FacebookSync", ex.getMessage());
    }

    public void onFileNotFoundException(FileNotFoundException ex,
            final Object state) {
        Log.e("FacebookSync", ex.getMessage());
    }

    public void onIOException(IOException ex, final Object state) {
        Log.e("FacebookSync", ex.getMessage());
    }

    public void onMalformedURLException(MalformedURLException ex,
            final Object state) {
        Log.e("FacebookSync", ex.getMessage());
    }

}

