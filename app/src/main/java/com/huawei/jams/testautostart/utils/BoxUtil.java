package com.huawei.jams.testautostart.utils;

/**
 * <p>文件描述：柜门工具类<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/11/10<p>
 * <p>更新时间：2020/11/10<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class BoxUtil {
    public static boolean boxListAllClose(boolean[] isOpens) {
        for (int i = 0; i < isOpens.length; i++) {
            if (isOpens[i]) {
                return false;
            }
        }
        return true;
    }

    public static int boxOpenIndex(boolean[] isOpens) {
        int index = -1;
        for (int i = 0; i < isOpens.length; i++) {
            if (isOpens[i]) {
                return i;
            }
        }
        return index;

    }
}
