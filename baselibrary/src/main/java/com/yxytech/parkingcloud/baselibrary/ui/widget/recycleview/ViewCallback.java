package com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Water on 2017/4/20.
 */

public interface ViewCallback<T extends View> {

    void callback(@NonNull T view);
}
