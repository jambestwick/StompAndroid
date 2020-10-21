package com.huawei.jams.testautostart.utils;

import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ShellUtils;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/8/20<p>
 * <p>更新时间：2020/8/20<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class NetState {
    /**
     * 判断后台服务是否联通
     **/
    public static boolean isConnectServer() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd("ping -c 3 " + "47.114.168.180", false);
        if (commandResult.result == 0) {//ping后台失败
            //提示框:后台通信失败，请联系后台人员处理(按键重试)点击重试继续判断
            return true;
        }
        LogUtil.d(NetState.class.getName(), Thread.currentThread().getName() + ",ping -c 3 47.114.168.180 结果:" + commandResult);
        return false;
    }
}
