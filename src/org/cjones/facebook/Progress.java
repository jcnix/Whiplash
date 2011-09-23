package org.cjones.facebook;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context; 
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Progress extends ListActivity implements Observer
{
    private FriendsAdapter arr;
    //private ArrayAdapter<Friend> arr;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        arr = new FriendsAdapter(this);
        //arr = new ArrayAdapter<Friend>(this, R.id.text);
        setListAdapter(arr);

        ProgressObservable observable = ProgressObservable.getInstance();
        observable.addObserver(this);
    }

    public void update(Observable obj, Object arg)
    {
        if(arg instanceof Friend)
        {
            final Friend f = (Friend) arg;
            runOnUiThread(new Runnable() {
                public void run() {
                    arr.add(f);
                }
            });
        }
    }

    private class FriendsAdapter extends BaseAdapter 
    {
        private Activity activity;
        private ArrayList<Friend> friends;

        public FriendsAdapter(Activity a)
        {
            activity = a;
            friends = new ArrayList<Friend>();
        }

        public int getCount() {
            return friends.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        
        public void add(Friend f)
        {
            friends.add(f);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.progress, null);
            }

            Friend fri = friends.get(position);
            TextView text = (TextView) v.findViewById(R.id.text);
            ImageView image = (ImageView) v.findViewById(R.id.image);
            text.setText(fri.getName());
            image.setImageBitmap(fri.getPhoto());

            return v;
        }
    }
}

