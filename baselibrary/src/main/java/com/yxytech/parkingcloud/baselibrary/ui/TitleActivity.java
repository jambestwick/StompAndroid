package com.yxytech.parkingcloud.baselibrary.ui;

import android.os.Bundle;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/10<p>
 * <p>更新时间：2019/6/10<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class TitleActivity extends BaseTitleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLeftTwoImageVisibity(false);
        setRightOneImageVisibity(false);
        setRightTextViewVisibity(false);
        setRightTwoImageVisibity(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
