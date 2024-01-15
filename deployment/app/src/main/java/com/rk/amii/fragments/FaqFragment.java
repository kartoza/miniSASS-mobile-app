package com.rk.amii.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.rk.amii.R;
import com.rk.amii.about.DataItems;
import com.rk.amii.adapters.DataItemsAdapter;
import com.rk.amii.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create an instance of this fragment.
 */
public class FaqFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ExpandableListView expandableListViewExample;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableTitleList;
    private HashMap<String, List<String>> expandableDetailList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        expandableListViewExample = (ExpandableListView) view.findViewById(R.id.expandableListViewSample);
        expandableDetailList = DataItems.getData(this.getContext());
        expandableTitleList = new ArrayList<String>(expandableDetailList.keySet());
        expandableListAdapter = new DataItemsAdapter(this.getContext(), expandableTitleList, expandableDetailList);
        expandableListViewExample.setAdapter(expandableListAdapter);

        return view;
    }
}