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
import com.facebook.android.Util;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
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
        asyncRunner.request("me/friends", new GetFriendsListener());
    }

    public class GetFriendsListener extends BaseRequestListener
    {
        public void onComplete(final String response, final Object state)
        {
            String[] projection = new String[] {
                People._ID,
                People.NAME
            };
            Uri contacts = People.CONTENT_URI;
            Cursor cursor = managedQuery(
                    contacts,
                    projection,
                    null,
                    null,
                    People.NAME + " ASC"
                    );

            try
            {
                JSONObject json = Util.parseJson(response);
                JSONArray data = json.getJSONArray("data");
                for(int i = 0; i < data.length(); i++)
                {
                    JSONObject frnd = data.getJSONObject(i);
                    String fname = frnd.getString("name");
                    String id = frnd.getString("id");

                    try
                    {
                        URL url = new URL("https://graph.facebook.com/"+id+"/picture");
                        URLConnection conn = url.openConnection();
                        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        
                        int c;
                        while((c = in.read()) != -1)
                        {
                            out.write(c);
                        }
                        
                        cursor.moveToFirst();
                        while(cursor.moveToNext())
                        {
                            String cname = cursor.getString(1);
                            if(cname == null)
                            {
                                continue;
                            }
                            if(cname.equals(fname))
                            {
                                int cid = cursor.getInt(0);
                                Uri per = ContentUris.withAppendedId(People.CONTENT_URI, cid);
                                setPhoto(per, out.toByteArray(), Main.this);
                            }
                        }
                    }
                    catch(IOException ex)
                    {
                        Log.w("FacebookSync", ex.getMessage());
                    }
                }
            }
            catch(JSONException ex)
            {
                Log.w("FacebookSync", ex.getMessage());
            }
            catch(FacebookError ex)
            {
                Log.w("FacebookSync", ex.getMessage());
            }
            cursor.close();
        }
    }

    public void setPhoto(Uri personUri, byte[] photo, Context context)
    {
        ContentValues values = new ContentValues(); 
        int photoRow = -1; 
        String where = ContactsContract.Data.RAW_CONTACT_ID + " == " + 
            ContentUris.parseId(personUri) + " AND " + Data.MIMETYPE + "=='" + 
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'"; 
            Cursor cursor = context.getContentResolver().query 
        (ContactsContract.Data.CONTENT_URI, null, where, null, null); 
        int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID); 
        if(cursor.moveToFirst())
        { 
            photoRow = cursor.getInt(idIdx); 
        } 
        cursor.close(); 
        values.put(ContactsContract.Data.RAW_CONTACT_ID, 
            ContentUris.parseId(personUri)); 
        values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1); 
        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photo); 
        values.put(ContactsContract.Data.MIMETYPE, 
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE); 
        if(photoRow >= 0)
        { 
            context.getContentResolver().update 
                (ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data._ID 
                + " = " + photoRow, null); 
        } else { 
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values); 
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

