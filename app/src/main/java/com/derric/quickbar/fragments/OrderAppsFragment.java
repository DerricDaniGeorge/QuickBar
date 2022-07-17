package com.derric.quickbar.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.derric.quickbar.QuickBarUtils;
import com.derric.quickbar.R;
import com.derric.quickbar.adapters.ChooseAppsAdapter;
import com.derric.quickbar.adapters.OrderAppsAdapter;
import com.derric.quickbar.helper.BasicItemTouchCallback;
import com.derric.quickbar.helper.DragListener;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderAppsFragment extends Fragment implements DragListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<AppInfo> userSelectedApps;
    private OrderAppsAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    public OrderAppsFragment(ArrayList<AppInfo> userSelectedApps) {
        this.userSelectedApps = userSelectedApps;
    }
    //A public no argument constructor is must for all subclasses of Fragment
    public OrderAppsFragment(){}



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_order_apps, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.order_apps_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider =  new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        adapter = new OrderAppsAdapter(userSelectedApps, getContext(),this);
//        for(AppInfo a: userSelectedApps){
//            System.out.println("userselectdApp ------>"+a.getPackageName()+"position: "+a.getPosition());
//        }
//        if(userSelectedApps.isEmpty()){
//            System.out.println("I am empty===========================");
//        }
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.Callback callback = new BasicItemTouchCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return layout;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}