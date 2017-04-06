package pt.ulisboa.tecnico.cmov.locmess;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Rafael Barreira on 05/04/2017.
 */

public class ProfileIntererstsFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.frag_one,container,false);
        return inflater.inflate(R.layout.profile_fragment2_view,container,false);
    }
}
