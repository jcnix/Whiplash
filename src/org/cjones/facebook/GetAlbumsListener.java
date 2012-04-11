package org.cjones.facebook;

import android.app.Activity;
import android.util.Log;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetAlbumsListener extends BaseRequestListener
{
    private Activity activity;
    private ProgressObservable observable;

    public GetAlbumsListener(Activity activity, ProgressObservable observable)
    {
        this.activity = activity;
        this.observable = observable;
    }

    public void onComplete(final String response, final Object state)
    {
        Log.w("FacebookSync", "START SYNC");
        try
        {
            Log.w("FacebookSync", response);
            JSONObject json = Util.parseJson(response);
            JSONArray data = json.getJSONArray("data");

            for(int i = 0; i < data.length(); i++)
            {
                JSONObject al = data.getJSONObject(i);
                int id    = al.getInt("id");
                String name  = al.getString("name");
                Album album = new Album(id, name);
                observable.notify(album);
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
}

