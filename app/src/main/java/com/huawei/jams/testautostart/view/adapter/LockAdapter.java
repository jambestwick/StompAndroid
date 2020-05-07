package com.huawei.jams.testautostart.view.adapter;

import android.databinding.DataBindingUtil;
import com.huawei.jams.testautostart.R;
import com.huawei.jams.testautostart.databinding.ItemLockListBinding;
import com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview.BaseAdapter;
import com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview.BaseViewHolder;


/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/26<p>
 * <p>更新时间：2019/6/26<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class LockAdapter extends BaseAdapter<String> {

    @Override
    public int getLayoutRes(int index) {
        return R.layout.item_lock_list;
    }

    @Override
    public void convert(BaseViewHolder holder, String data, int index) {
        ItemLockListBinding bind = DataBindingUtil.bind(holder.itemView);
        bind.setViewModel(data);
    }

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {

    }
}
