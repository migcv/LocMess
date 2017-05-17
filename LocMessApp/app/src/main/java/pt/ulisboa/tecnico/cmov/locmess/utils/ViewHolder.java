package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.locmess.R;

/**
 * Created by Rafael Barreira on 06/04/2017.
 */

public class ViewHolder {
    public TextView text;
    public Button button;
    public ViewHolder(View v) {
        this.text = (TextView)v.findViewById(R.id.text_title);
        this.button = (Button)v.findViewById(R.id.button1);
    }
}
