package com.radio.daniel.radio;

import android.support.v7.util.DiffUtil;
import java.util.List;


public class RadioStationCallback extends DiffUtil.Callback {

    private List<RadioStation> oldList;
    private List<RadioStation> newList;

    public RadioStationCallback(List<RadioStation> oldList, List<RadioStation> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());

    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
