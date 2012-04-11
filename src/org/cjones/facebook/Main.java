package org.cjones.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.FacebookError;
import com.facebook.android.DialogError;
import org.cjones.facebook.SessionEvents.AuthListener;
import org.cjones.facebook.SessionEvents.LogoutListener;

public class Main extends Activity
{
    private Facebook fb;
    private AsyncFacebookRunner asyncRunner;
    private TextView mText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        fb = FacebookFactory.getInstance();
        asyncRunner = new AsyncFacebookRunner(fb);
        mText = (TextView) findViewById(R.id.mText);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fb.authorizeCallback(requestCode, resultCode, data);
    }

    /* Activated by "Connect to Facebook" Button */
    public void fb_auth(View view)
    {
        SessionStore.restore(fb, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        
        fb.authorize(this, new String[] {"friends_photos"},
                new DialogListener()
        {
            @Override
            public void onComplete(Bundle values)
            {
            }

            @Override
            public void onFacebookError(FacebookError error)
            {
            }

            @Override
            public void onError(DialogError e)
            {
            }

            @Override
            public void onCancel()
            {
            }
        });
    }

    /* Activated by Sync Button */
    public void sync(View view)
    {
        if(!fb.isSessionValid())
        {
            mText.setText("You must connect to Facebook.");
        }
        else
        {
            mText.setText("");
            ProgressObservable po = ProgressObservable.getInstance();
            GetFriendsListener gfl = new GetFriendsListener(this, po);
            asyncRunner.request("me/friends", gfl);
            asyncRunner.request("me", gfl);
            Intent showProgress = new Intent(view.getContext(), Progress.class);
            startActivity(showProgress);
        }
    }


    public class SampleAuthListener implements AuthListener {
        public void onAuthSucceed() {
        }

        public void onAuthFail(String error) {
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
        }

        public void onLogoutFinish() {
        }
    }
}

