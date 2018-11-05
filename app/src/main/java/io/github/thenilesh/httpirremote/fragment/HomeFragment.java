package io.github.thenilesh.httpirremote.fragment;


import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.thenilesh.httpirremote.R;
import io.github.thenilesh.httpirremote.RButtonAdapter;
import io.github.thenilesh.httpirremote.dao.AppDatabase;
import io.github.thenilesh.httpirremote.dto.RButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private List<RButton> rButtons;
    private RecyclerView recyclerView;
    private RButtonAdapter rButtonAdapter;

    public HomeFragment() {
        System.out.println("onConstruct");
        rButtons = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreate");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("onCreated");
        boolean firstCreated = false;
        if (rButtonAdapter == null) {
            firstCreated = true;
        }
        rButtonAdapter = new RButtonAdapter(rButtons, getContext());
        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rButtonAdapter);
        if (firstCreated) {
            addButtonsFromDb();
        }

    }

    private void addButtonsFromDb() {
        (new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                rButtonAdapter.notifyDataSetChanged();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                List<RButton> rBs = AppDatabase.getInstance(getContext()).rButtonDao().findAll();
                for (RButton rb : rBs) {
                    rButtons.add(rb);
                }
                return null;
            }
        }).execute();
    }

    public void addNewRButton(final RButton rButton) {
        rButtons.add(rButton);
        (new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase.getInstance(getContext()).rButtonDao().insert(rButton);
                return null;
            }
        }).execute();

    }
}
