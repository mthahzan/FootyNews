package taz.starz.footynews.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taz.starz.footynews.R;

/**
 * Created by Thahzan on 6/27/2014.
 */
public class SerieAFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_serie, container, false);

        return rootView;
    }

}
