package com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.BaseAdapter;

/**
 * Created by Water on 2017/4/20.
 */

public interface IMultiItem {

    /**
     * 不同类型的item请使用不同的布局文件，
     * 即使它们的布局是一样的，也要copy多一份出来。
     *
     * @return 返回item对应的布局id
     */
    @LayoutRes
    int getLayoutRes();

    /**
     * 进行数据处理，显示文本，图片等内容
     *
     * @param holder Holder Helper
     */
    void convert(BaseViewHolder holder);

    /**
     * 在布局为{@link android.support.v7.widget.GridLayoutManager}时才有用处，
     * 返回当前布局所占用的SpanSize
     *
     * @return 如果返回的SpanSize 小于或等于 0 或者 大于 {@link GridLayoutManager#getSpanCount()}
     * 则{@link BaseAdapter} 会在{@link BaseAdapter#onAttachedToRecyclerView(RecyclerView)}
     * 自适应为1或者{@link GridLayoutManager#getSpanCount()},详情参考{@link BaseAdapter#onAttachedToRecyclerView(RecyclerView)}
     */
    int getSpanSize();
}
