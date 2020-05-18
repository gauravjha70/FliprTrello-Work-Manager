package com.example.flipr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CardsPagerAdapater extends FragmentPagerAdapter {

    ArrayList<ListClass> items;
    String boardId, type;
    ArrayList<String> listIds;

    public CardsPagerAdapater(@NonNull FragmentManager fm, ArrayList<ListClass> items, String boardId, ArrayList<String> listIds, String type) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.items = items;
        this.boardId = boardId;
        this.listIds = listIds;
        this.type = type;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ListFragment(items.get(position),boardId,listIds.get(position),type);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
