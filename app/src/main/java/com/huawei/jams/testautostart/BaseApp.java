package com.huawei.jams.testautostart;

import android.os.Environment;
import android.util.Log;

import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.utils.Constants;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.http.common.RxRetrofitApp;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;
import com.yxytech.parkingcloud.baselibrary.utils.ExceptionHelper;
import com.yxytech.parkingcloud.baselibrary.utils.FileUtils;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import io.reactivex.plugins.RxJavaPlugins;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/4/23<p>
 * <p>更新时间：2020/4/23<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class BaseApp extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);//初始化dbflow
        ExceptionHelper.getInstance().init();//初始化未知异常捕获
        RxJavaPlugins.setErrorHandler(throwable -> {
            //异常处理
            LogUtil.e(this.getClass().getName(), "setErrorHandler:" + Log.getStackTraceString(throwable));
        });
        initVideo();
    }

    @Override
    protected void initRxRetrofitApp() {
        RxRetrofitApp.init(IdeaApiService.SERVER_HOST);
    }

    /***
     * 新设备初始化本地广告
     * **/
    private void initVideo() {
        List<Advise> adviseList = SQLite.select().from(Advise.class).queryList();
        if (adviseList.size() <= 0) {
            Advise advise = new Advise();
            advise.setAdvNo("1");
            advise.setAdvDate(new Date());
            advise.setAdvVersion("1.0.0");
            advise.setUuid(UUID.randomUUID());
            advise.setCreateTime(new Date());
            advise.setFileName("adv000");
            List<File> fileList = FileUtils.listFilesInDirWithFilter(Constants.ADVISE_DIR, ".mp4");
            if (null != fileList && fileList.size() > 0) {
                advise.setFilePath(fileList.get(0).getAbsolutePath());
                advise.save();
            }

        }
//        PreferencesManager.getInstance(this).put(Constants.PASSWORD,"abcsdccc");
//        PreferencesManager.getInstance(this).put(Constants.ACCOUNT,"888");
    }


}
