package pt.ulisboa.tecnico.cmov.locmess.utils;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.locmess.R;

public class PostsListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> expandableListTitle;
    private ArrayList<List<String>> expandableListDetail;

    public PostsListAdapter(Context context, ArrayList<String> expandableListTitle, ArrayList<List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String content = this.expandableListDetail.get(listPosition).get(1);
        String contact = this.expandableListDetail.get(listPosition).get(2);
        String date =  this.expandableListDetail.get(listPosition).get(3);
        String time =  this.expandableListDetail.get(listPosition).get(4);
        String deliveryMode =  this.expandableListDetail.get(listPosition).get(5);
        notifyDataSetChanged();
        return "Content: " +content + " \n" +"Contact: "+ contact + "\n" + date + " " + time + "\n" + deliveryMode;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return listPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.post_list_item, null);
        }
        TextView text_content = (TextView) convertView.findViewById(R.id.text_content);
        text_content.setText(expandableListDetail.get(listPosition).get(1));

        TextView text_contact = (TextView) convertView.findViewById(R.id.text_contact);
        text_contact.setText(this.expandableListDetail.get(listPosition).get(2));

        TextView text_username = (TextView) convertView.findViewById(R.id.text_username);
        text_username.setText(this.expandableListDetail.get(listPosition).get(3));

        TextView text_post_time = (TextView) convertView.findViewById(R.id.text_post_time);
        long post_time = Long.parseLong(this.expandableListDetail.get(listPosition).get(4));
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        Log.d("MY_POST", "Post Time: " + formatter.format(new Date(post_time)));
        text_post_time.setText(formatter.format(new Date(post_time)));

        TextView text_post_lifetime = (TextView) convertView.findViewById(R.id.text_post_lifetime);
        long post_lifetime = Long.parseLong(this.expandableListDetail.get(listPosition).get(5));
        Log.d("MY_POST", "Post LifeTime: " + formatter.format(new Date(post_lifetime)));
        Log.d("MY_POST", "Current Time: " + formatter.format(new Date(System.currentTimeMillis())));
        DateFormat dayFormatter = new SimpleDateFormat("d");
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        if(post_lifetime - System.currentTimeMillis() > 0) {
            text_post_lifetime.setText(dayFormatter.format(new Date(post_lifetime - System.currentTimeMillis())) + " days, " + timeFormatter.format(new Date(post_lifetime - System.currentTimeMillis())));
        } else {
            text_post_lifetime.setText("EXPIRED!");
        }

        TextView text_location_name = (TextView) convertView.findViewById(R.id.text_location_name);
        text_location_name.setText(this.expandableListDetail.get(listPosition).get(7));

        /*if(NewPost.GPS.equals(this.expandableListDetail.get(listPosition).get(5))) {

        } else if(NewPost.WIFI_DIRECT.equals(this.expandableListDetail.get(listPosition).get(5))) {

        }*/
        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListDetail.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = getGroup(listPosition).toString();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}