package pt.ulisboa.tecnico.cmov.locmess;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Rafael Barreira on 06/04/2017.
 */

public class ViewHolder {
    public TextView text;
    public Button button;
    public ViewHolder(View v) {
        this.text = (TextView)v.findViewById(R.id.text1);
        this.button = (Button)v.findViewById(R.id.button1);
    }
}
