package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;

/**
 * Created by Rafael Barreira on 06/04/2017.
 */

public class MyPostsExpandableListaAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> expandableListTitle;
    private HashMap<Integer, List<String>> expandableListDetail;
    private PopupWindow popupMessage;

    public MyPostsExpandableListaAdapter(Context context, ArrayList<String> expandableListTitle,
                                       HashMap<Integer, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String content = this.expandableListDetail.get(listPosition).get(1);
        String contact = this.expandableListDetail.get(listPosition).get(2);
        return "Content: " +content + " \n" +"Contact: "+ contact;
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
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);
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
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final View convert_view = convertView;
        final String listTitle = getGroup(listPosition).toString();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.my_posts_list_group, null);
        }

        ViewHolder holder = new ViewHolder(convertView);

        holder.button.setFocusable(false);
        View.OnClickListener clickL = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "OLAAAAAA");
                final Dialog dialog = new Dialog(context);

                dialog.setContentView(R.layout.delete_popup);
                ((TextView)dialog.findViewById(R.id.text_post)).setText(listTitle + "?");

                dialog.findViewById(R.id.delete).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("DEBUG", "DELETE");
                    }
                }
                );
                dialog.findViewById(R.id.cancel).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }
                );



                dialog.show();
            }
        };
        holder.button.setOnClickListener(clickL);
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.text1);
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
