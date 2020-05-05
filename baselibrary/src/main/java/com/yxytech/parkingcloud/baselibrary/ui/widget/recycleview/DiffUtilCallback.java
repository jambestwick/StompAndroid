package com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview;

/**
 * Created by Water on 2017/4/20.
 */

public abstract class DiffUtilCallback<T> {
    /**
     * 判断是否为同一个对象
     */
    public abstract boolean areItemsTheSame(T oldItem, T newItem);

    /**
     * 如果{@link DiffUtilCallback#areItemsTheSame(Object, Object)}返回true，则会调用此方法判断内容是否发生改变
     */
    public abstract boolean areContentsTheSame(T oldItem, T newItem);

    public Object getChangePayload(T oldItem, T newItem){
        return null;
    }
}
