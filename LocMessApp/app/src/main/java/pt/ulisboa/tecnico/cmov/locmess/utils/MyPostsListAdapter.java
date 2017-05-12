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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;

/**
 * Created by Rafael Barreira on 06/04/2017.
 */

public class MyPostsListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> expandableListTitle;
    private ArrayList<List<String>> expandableListDetail;

    public MyPostsListAdapter(Context context, ArrayList<String> expandableListTitle, ArrayList<List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String content = this.expandableListDetail.get(listPosition).get(2);
        String contact = this.expandableListDetail.get(listPosition).get(3);
        String post_time =  this.expandableListDetail.get(listPosition).get(4);
        String post_lifetime =  this.expandableListDetail.get(listPosition).get(5);
        String deliveryMode =  this.expandableListDetail.get(listPosition).get(6);
        return "Content: " +content + " \n" +"Contact: "+ contact + "\n" + post_time + " " + post_lifetime + "\n" + deliveryMode;
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
            convertView = layoutInflater.inflate(R.layout.my_post_list_item, null);
        }
        TextView text_content = (TextView) convertView.findViewById(R.id.text_content);
        text_content.setText(expandableListDetail.get(listPosition).get(2));

        TextView text_contact = (TextView) convertView.findViewById(R.id.text_contact);
        text_contact.setText(this.expandableListDetail.get(listPosition).get(3));

        TextView text_post_time = (TextView) convertView.findViewById(R.id.text_post_time);
        long post_time = Long.parseLong(this.expandableListDetail.get(listPosition).get(4));
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        Log.d("MY_POST", "Post Time: " + formatter.format(new Date(post_time)));
        text_post_time.setText(formatter.format(new Date(post_time)));

        TextView text_post_deadline = (TextView) convertView.findViewById(R.id.text_post_deadline);
        long post_deadline = Long.parseLong(this.expandableListDetail.get(listPosition).get(5));
        Log.d("MY_POST", "Post LifeTime: " + formatter.format(new Date(post_deadline)));
        Log.d("MY_POST", "Current Time: " + formatter.format(new Date(System.currentTimeMillis())));
        formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        if(post_deadline - System.currentTimeMillis() > 0) {
            text_post_deadline.setText(formatter.format(new Date(post_deadline)));
        } else {
            text_post_deadline.setText("EXPIRED!");
        }

        TextView text_post_location = (TextView) convertView.findViewById(R.id.text_post_location);
        text_post_location.setText(this.expandableListDetail.get(listPosition).get(7));

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
        ImageView icon_delivery = (ImageView) convertView.findViewById(R.id.icon_delivery);
        if(this.expandableListDetail.get(listPosition).get(6).equals(NewPost.GPS)) {
            icon_delivery.setImageResource(R.drawable.ic_gps_black);
        } else if(this.expandableListDetail.get(listPosition).get(6).equals(NewPost.WIFI)) {
            icon_delivery.setImageResource(R.drawable.ic_wifi_black);
        } else {
            icon_delivery.setImageResource(R.drawable.ic_wifi_direct_black);
        }
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
