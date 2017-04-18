package pt.ulisboa.tecnico.cmov.locmess.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.ulisboa.tecnico.cmov.locmess.R;

/**
 * Created by Rafael Barreira on 03/04/2017.
 */

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.frag_one,container,false);
        return inflater.inflate(R.layout.profile_fragment_about,container,false);
    }
}