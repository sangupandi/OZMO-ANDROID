package com.ozu.ozmo.ozmopol;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.ozu.ozmo.ozmopol.Models.Contributor;
import com.ozu.ozmo.ozmopol.Models.OzmoService;
import com.ozu.ozmo.ozmopol.Models.Post;
import com.ozu.ozmo.ozmopol.Models.User;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;




public class FragmentFrontPage extends Fragment {

    StaggeredGridView gridView;
    SwipeRefreshLayout swipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_front_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        addSwipeRefreshFunction();


        refreshTheFrontPage();


    }
    void refreshTheFrontPage(){
        User user=((MyApplication)getActivity().getApplication()).user;


        final List<Post> myPostCards=new ArrayList<Post>();

        ((MyApplication) getActivity().getApplication()).ozmoService().getFrontPagePosts(user, new Callback<List<Post>>() {
            @Override
            public void success(List<Post> posts, Response response) {
                myPostCards.addAll(posts);
                PostsAdapter pAdapter=new PostsAdapter(getActivity(),myPostCards, FragmentFrontPage.this.getFragmentManager());
                gridView = (StaggeredGridView)getView().findViewById(R.id.grid_view);
                gridView.setAdapter(pAdapter);
                updateColumnCountForFrontPage();
                ProgressWheel progressWheel=(ProgressWheel)getActivity().findViewById(R.id.progress_wheel);
                progressWheel.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }
    public void addSwipeRefreshFunction(){ //functionality to be set
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeLayout.setRefreshing(false);


                        Fragment newFragment = new FragmentFrontPage();
                        FragmentTransaction transaction =getActivity().getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container,newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }, 5000);
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public boolean isTablet(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        if (width > 720 && height > 1184){
            return true;
        } else {
            return false;
        }
    }

    public void updateColumnCountForFrontPage(){
        if(isTablet()){
            Log.d("Tablet spotted", "sizes won't change");
        } else {
            gridView.setColumnCountLandscape(1);
            gridView.setColumnCountPortrait(1);
        }
    }

}

