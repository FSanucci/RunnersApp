package com.app.runners.fragment.racingsFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.runners.R;
import com.app.runners.adapter.ItemRecyclerViewAdapter;

public class TabNavigatorRacings extends Fragment{
    private ItemRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Button mRacings;
    private Button mRacingsToDo;

    public TabNavigatorRacings(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_racings_navigation, container, false);
        Context context = view.getContext();

        mRacings = (Button) view.findViewById(R.id.racings);
        mRacings.setTextColor(Color.WHITE);
        mRacingsToDo = (Button) view.findViewById(R.id.racingsToDo);
        mRacingsToDo.setTextColor(Color.BLACK);

        View mRacings = view.findViewById(R.id.racersList);
        View mRacingsToDo = view.findViewById(R.id.racersToDoList);

        mRacings.setVisibility(View.GONE);
        mRacingsToDo.setVisibility(View.GONE);

        //addFragment();
        loadFragment(new RacingsFragment());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        setupListeners();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setupListeners(){
        mRacings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRacings.setTextColor(Color.WHITE);
                mRacingsToDo.setTextColor(Color.BLACK);
                loadFragment(new RacingsFragment());
            }
        });

        mRacingsToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRacingsToDo.setTextColor(Color.WHITE);
                mRacings.setTextColor(Color.BLACK);
                loadFragment(new RacingsToDoFragment());
            }
        });

    }

    private void showNoContentView(boolean show){

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);

        fragmentTransaction.commit();
    }

}






