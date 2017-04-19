package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
    private ArrayList<List<String>> expandableListDetail;

    public MyPostsExpandableListaAdapter(Context context, ArrayList<String> expandableListTitle, ArrayList<List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String content = this.expandableListDetail.get(listPosition).get(2);
        String contact = this.expandableListDetail.get(listPosition).get(3);
        String date =  this.expandableListDetail.get(listPosition).get(4);
        String time =  this.expandableListDetail.get(listPosition).get(5);
        String deliveryMode =  this.expandableListDetail.get(listPosition).get(6);
        return "Content: " +content + " \n" +"Contact: "+ contact + "\n" + date + " " + time + "\n" + deliveryMode;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return listPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

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
    public View getGroupView(final int listPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        Log.d("GROUP_VIEW", "" + listPosition);
        Log.d("GROUP_VIEW", getGroup(listPosition) == null ? "NULL" : "" + getGroup(listPosition));
        final String listTitle = (String) getGroup(listPosition);

        if(listTitle == null) {
            return convertView;
        }

        LayoutInflater layoutInflater = null;

        if (convertView == null) {
            layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.my_posts_list_group, null);
        }

        final View convert_view = convertView;
        final View layout = convertView.findViewById(R.id.layout_post);

        ViewHolder holder = new ViewHolder(convertView);

        holder.button.setFocusable(false);
        View.OnClickListener clickL = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.delete_popup);
                ((TextView)dialog.findViewById(R.id.text_post)).setText(listTitle + "?");

                dialog.findViewById(R.id.delete).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            String toSend = "RemovePost;:;" + SocketHandler.getToken() + ";:;" + expandableListDetail.get(listPosition).get(0);
                            Socket s = SocketHandler.getSocket();
                            Log.d("CONNECTION", "Connection successful!");
                            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                            dout.writeUTF(toSend);
                            dout.flush();
                            //dout.close();
                            Log.d("MYPosts", toSend);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        expandableListTitle.remove(listPosition);
                        expandableListDetail.remove(listPosition);

                        notifyDataSetChanged();

                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.cancel).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        };
        holder.button.setOnClickListener(clickL);
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.text1);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convert_view;
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
