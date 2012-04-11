package org.cjones.facebook;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.facebook.android.Facebook;
import com.facebook.android.AsyncFacebookRunner;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Albums extends ListActivity implements Observer
{
    private ArrayAdapter arr;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("friend_id");
        Facebook fb = FacebookFactory.getInstance();
        
        ProgressObservable po = ProgressObservable.getInstance();
        po.addObserver(this);

        AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(fb);
        GetAlbumsListener gal = new GetAlbumsListener(this, po);
        Log.w("FacebookSync", "Album ID: " + id + "/albums");
        asyncRunner.request(id+"/albums", gal);

        arr = new ArrayAdapter(this, R.layout.albums_list, R.id.album_name);
        setListAdapter(arr);
        setContentView(R.layout.albums);
    }
    
    public void update(Observable obj, Object arg)
    {
        if(arg instanceof Album)
        {
            final Album a = (Album) arg;
            runOnUiThread(new Runnable() {
                public void run() {
                    arr.add(a);
                }
            });
        }
    }
}

