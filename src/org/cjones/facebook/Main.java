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
import com.facebook.android.Util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

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

        fb.authorize(this, new String[] {"friends_photos" },
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
        if(canWriteSD())
        {
            asyncRunner.request("me/friends", new GetFriendsListener());
        }
    }

    public class GetFriendsListener extends BaseRequestListener
    {
        public void onComplete(final String response, final Object state)
        {
            try
            {
                JSONObject json = Util.parseJson(response);
                JSONArray data = json.getJSONArray("data");
                String names = "";
                for(int i = 0; i < data.length(); i++)
                {
                    JSONObject frnd = data.getJSONObject(i);
                    String name = frnd.getString("name");
                    String id = frnd.getString("id");
                    names += name + "\n";

                    try
                    {
                        URL url = new URL("https://graph.facebook.com/"+id+"/picture");
                        URLConnection conn = url.openConnection();
                        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedOutputStream out = new BufferedOutputStream(
                                new FileOutputStream("/mnt/sdcard/fbsync/"+id+".jpg"));
                        int c;
                        while((c = in.read()) != -1)
                        {
                            out.write(c);
                        }
                    }
                    catch(IOException ex)
                    {
                        Log.w("FacebookSync", ex.getMessage());
                    }
                }
                final String fnames = names;
                Main.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        mText.setText(fnames);
                    }
                });
            }
            catch(JSONException ex)
            {
                Log.w("FacebookSync", ex.getMessage());
            }
            catch(FacebookError ex)
            {
                Log.w("FacebookSync", ex.getMessage());
            }
        }
    }

    public boolean canWriteSD()
    {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

