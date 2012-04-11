package org.cjones.facebook;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context; 
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Progress extends ListActivity implements Observer
{
    private FriendsAdapter arr;
    private HashMap<Friend, Boolean> checked;
    private TextView statusText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        checked = new HashMap<Friend, Boolean>();
        arr = new FriendsAdapter(this, checked);
        setListAdapter(arr);
        setContentView(R.layout.progress);

        ProgressObservable observable = ProgressObservable.getInstance();
        observable.addObserver(this);

        statusText = (TextView) findViewById(R.id.sync_status);
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

    /* Activated by Apply Button */
    public void apply_changes(View v)
    {
        statusText.setText("");
        Set<Friend> keys = checked.keySet();
        for(Friend f : keys)
        {
            if(checked.get(f))
            {
                f.updateContact(this);
            }
        }
        statusText.setText("Update complete");
    }

    private class FriendsAdapter extends BaseAdapter 
    {
        private Activity activity;
        private ArrayList<Friend> friends;
        private LayoutInflater vi;
        private HashMap<Friend, Boolean> checked;

        public FriendsAdapter(Activity a, HashMap<Friend, Boolean> checked)
        {
            activity = a;
            this.checked = checked;
            friends = new ArrayList<Friend>();
            vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return friends.size();
        }

        public Object getItem(int position) {
            return friends.get(position);
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
            final ViewHolder holder;

            View v = convertView;
            if (v == null) {
                v = vi.inflate(R.layout.progress_list, null);
                
                holder = new ViewHolder();
                holder.image = (ImageView) v.findViewById(R.id.picture);
                holder.cb = (CheckBox) v.findViewById(R.id.check);
                holder.text = (TextView) v.findViewById(R.id.name);
                
                v.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) v.getTag();
            }

            Friend fri = friends.get(position);
            holder.fri = fri;
            holder.image.setImageBitmap(fri.getPhoto());
            holder.text.setText(fri.getName());
            
            holder.text.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent showAlbums = new Intent(v.getContext(), Albums.class);
                    showAlbums.putExtra("friend_id", holder.fri.getID());
                    startActivity(showAlbums);
                }
            });

            boolean isChecked = false;
            if(checked.containsKey(holder.fri))
            {
                isChecked = checked.get(holder.fri);
            }
            holder.cb.setChecked(isChecked);

            holder.cb.setOnCheckedChangeListener(new OnCheckChangeListener(holder, checked));
            return v;
        }
    }
}

class OnCheckChangeListener implements OnCheckedChangeListener
{
    private ViewHolder holder = null;
    private HashMap<Friend, Boolean> checked;

    public OnCheckChangeListener(ViewHolder viHolder, HashMap<Friend, Boolean> checked)
    {
        holder =  viHolder;
        this.checked = checked;
    }
    
    @Override 
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if(holder.cb.equals(buttonView))
        {       
            checked.put(holder.fri, isChecked);
        }
    }
}

class ViewHolder
{
    public ImageView image;
    public CheckBox cb;
    public TextView text;
    public Friend fri;
}

