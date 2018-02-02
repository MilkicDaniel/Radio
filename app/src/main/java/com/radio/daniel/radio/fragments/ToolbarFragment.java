package com.radio.daniel.radio.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.radio.daniel.radio.MainActivity;
import com.radio.daniel.radio.R;


public class ToolbarFragment extends Fragment {

    public final static String TAG = "ToolbarFragment";

    public ToolbarFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_toolbar, container, false);
        ImageButton menuButton = (ImageButton) root.findViewById(R.id.toolbar_menu_button);
        final DrawerLayout drawerLayout = (DrawerLayout) ((MainActivity) getContext()).findViewById(R.id.main_drawer_layout);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        return root;
    }

}
