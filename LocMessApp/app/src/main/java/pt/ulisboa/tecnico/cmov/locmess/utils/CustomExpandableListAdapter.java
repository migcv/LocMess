package pt.ulisboa.tecnico.cmov.locmess.utils;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.locmess.R;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> expandableListTitle;
    private HashMap<Integer, List<String>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, ArrayList<String> expandableListTitle,
                                       HashMap<Integer, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String content = this.expandableListDetail.get(listPosition).get(1);
        String contact = this.expandableListDetail.get(listPosition).get(2);
        String date =  this.expandableListDetail.get(listPosition).get(3);
        String time =  this.expandableListDetail.get(listPosition).get(4);
        String deliveryMode =  this.expandableListDetail.get(listPosition).get(5);

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
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView text_content = (TextView) convertView.findViewById(R.id.text_content);
        text_content.setText(expandableListDetail.get(listPosition).get(1));
        TextView text_contact = (TextView) convertView.findViewById(R.id.text_contact);
        text_contact.setText(this.expandableListDetail.get(listPosition).get(2));
        TextView text_date = (TextView) convertView.findViewById(R.id.text_date);
        text_date.setText(this.expandableListDetail.get(listPosition).get(3) + " " + this.expandableListDetail.get(listPosition).get(4));
        if(NewPost.LOCATION.equals(this.expandableListDetail.get(listPosition).get(5))) {

        } else if(NewPost.WIFI_DIRECT.equals(this.expandableListDetail.get(listPosition).get(5))) {

        }
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
        return this.expandableListDetail.keySet().size();
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