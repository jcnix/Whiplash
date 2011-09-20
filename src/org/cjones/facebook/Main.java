package org.cjones.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
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
    public static final String APP_ID = "159488880804695";
    private Facebook fb = new Facebook(APP_ID);
    private AsyncFacebookRunner asyncRunner;
    private TextView mText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        asyncRunner = new AsyncFacebookRunner(fb);
        SessionStore.restore(fb, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        
        // Use this if we want to let users choose a photo instead
        // of the Profile Picture in the future
        //fb.authorize(this, new String[] {"friends_photos" },
        //        new DialogListener()
        
        fb.authorize(this, new DialogListener()
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

        final Button sync = (Button) findViewById(R.id.sync_button);
        mText = (TextView) findViewById(R.id.mText);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fb.authorizeCallback(requestCode, resultCode, data);
    }

    public void sync(View view)
    {
        asyncRunner.request("me/friends", new GetFriendsListener(this));
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

