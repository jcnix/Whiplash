package org.cjones.facebook;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.util.Log;
import com.facebook.android.FacebookError;
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

public class GetFriendsListener extends BaseRequestListener
{
    private Activity activity;

    public GetFriendsListener(Activity activity)
    {
        this.activity = activity;
    }

    public void onComplete(final String response, final Object state)
    {
        String[] projection = new String[] {
            People._ID,
                People.NAME
        };
        Uri contacts = People.CONTENT_URI;
        Cursor cursor = activity.managedQuery(
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
                            ContactManager.setPhoto(per, out.toByteArray(), activity);
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

