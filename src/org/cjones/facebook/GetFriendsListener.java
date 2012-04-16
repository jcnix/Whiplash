package org.cjones.facebook;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetFriendsListener extends BaseRequestListener
{
    private Activity activity;
    private ProgressObservable observable;

    public GetFriendsListener(Activity activity, ProgressObservable observable)
    {
        this.activity = activity;
        this.observable = observable;
    }

    public void onComplete(final String response, final Object state)
    {
        Log.w("FacebookSync", "START SYNC");
        try
        {
            JSONObject json = Util.parseJson(response);
            JSONArray data = null;
            
            /* If request is /me turn response into an
             * "array */
            if(json.has("first_name"))
            {
                String edited_response = "["+response+"]";
                data = new JSONArray(edited_response);
            }
            else
            {
                data = json.getJSONArray("data");
            }
            for(int i = 0; i < data.length(); i++)
            {
                JSONObject frnd = data.getJSONObject(i);
                String fname = frnd.getString("name");
                long id = frnd.getLong("id");
                fname = pruneName(fname);
                fname = fname.replace("'", "\\'");

                Uri contacts = ContactsContract.Data.CONTENT_URI;
                String[] projection = new String[] {
                    ContactsContract.Data.RAW_CONTACT_ID,
                    ContactsContract.Contacts.DISPLAY_NAME
                };
                String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP +
                    " =? AND " + ContactsContract.Contacts.DISPLAY_NAME +
                    " =?";
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME +
                    " COLLATE LOCALIZED ASC";
                Cursor cursor = activity.managedQuery(
                    contacts,
                    projection,
                    selection,
                    new String[] {"1", fname},
                    sortOrder
                    );
                
                cursor.moveToFirst();
                if(cursor.getCount() == 0)
                {
                    continue;
                }

                String cname = cursor.getString(1);
                if(cname == null)
                {
                    continue;
                }
                
                byte[] photo = ContactManager.downloadPhoto(id);

                int cid = cursor.getInt(0);
                Uri per = ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI, cid);
                Friend f = new Friend(id, fname, photo, per);
                observable.notify(f);
            }
            //observable.clear();
        }
        catch(JSONException ex)
        {
            Log.w("FacebookSync", "JSONException: " + ex.getMessage());
        }
        catch(FacebookError ex)
        {
            Log.w("FacebookSync", "FacebookError: " + ex.getMessage());
        }
    }

    /**
     * Cuts out middle names
     * The Android phonebook doesn't have a middle name field
     * So this function splits on spaces, and attaches the first
     * and last elements of the split up array.
     */
    private String pruneName(String name)
    {
        String ret = "";
        String ar[] = name.split(" ");
        ret = ar[0] + " " + ar[ar.length - 1];

        return ret;
    }
}

