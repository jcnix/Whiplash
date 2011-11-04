package org.cjones.facebook;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context; 
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Progress extends ListActivity implements Observer
{
    private FriendsAdapter arr;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        arr = new FriendsAdapter(this);
        setListAdapter(arr);
        setContentView(R.layout.progress);

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
        private LayoutInflater vi;
        private HashMap<int, boolean> checked;

        public FriendsAdapter(Activity a)
        {
            activity = a;
            friends = new ArrayList<Friend>();
            vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            checked = new HashMap<int, bool>();
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
            final ViewHolder holder;

            View v = convertView;
            if (v == null) {
                v = vi.inflate(R.layout.progress_list, null);
                
                holder = new ViewHolder();
                holder.image = (ImageView) v.findViewById(R.id.picture);
                holder.cb = (CheckBox) v.findViewById(R.id.check);
                holder.text = (TextView) v.findViewById(R.id.name);
                
                holder.text.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        holder.cb.setChecked(true);
                    }
                });

                v.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) v.getTag();
            }

            Friend fri = friends.get(position);
            holder.ref = position;
            holder.image.setImageBitmap(fri.getPhoto());
            holder.text.setText(fri.getName());
            
            if(checked.containsKey(""+holder.ref))
            { 
                if(checked.get(""+holder.ref).equals("true"))
                {
                    holder.cb.setChecked(true);
                }
                else
                    holder.cb.setChecked(false);
            }
            else
                holder.cb.setChecked(false);

            holder.cb.setOnCheckedChangeListener(new OncheckchangeListner(holder, checked));
            return v;
        }
    }
}

class OncheckchangeListner implements OnCheckedChangeListener
{
    private ViewHolder holder = null;
    private HashMap<String, String> checked;

    public OncheckchangeListner(ViewHolder viHolder, HashMap<String, String> checked)
    {
        holder =  viHolder;
        this.checked = checked;
    }
    
    @Override 
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if(holder.cb.equals(buttonView))
        {       
            if(!isChecked)
            {
                checked.put(""+holder.ref,"false");
            }
            else
                checked.put(""+holder.ref,"true");
        }
    }
}

class ViewHolder
{
    public ImageView image;
    public CheckBox cb;
    public TextView text;
    public int ref;
}

