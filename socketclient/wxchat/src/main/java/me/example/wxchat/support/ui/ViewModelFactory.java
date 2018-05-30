package me.example.wxchat.support.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import me.example.wxchat.support.persistence.MessageDatasource;

/**
 * Created by xmai on 18-3-23.
 */

public class ViewModelFactory implements ViewModelProvider.Factory {
    public ViewModelFactory(MessageDatasource mDataSource) {
        this.mDataSource = mDataSource;
    }

    private final MessageDatasource mDataSource;
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MsgViewModel.class)) {
            return (T) new MsgViewModel(mDataSource);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
