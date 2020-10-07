package com.huawei.jams.testautostart.presenter.impl;

import android.util.Log;

import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.entity.Advise_Table;
import com.huawei.jams.testautostart.entity.vo.AdviseVO;
import com.huawei.jams.testautostart.model.impl.AdviseModel;
import com.huawei.jams.testautostart.model.inter.IAdviseModel;
import com.huawei.jams.testautostart.presenter.inter.HttpDownloadCallBack;
import com.huawei.jams.testautostart.presenter.inter.IAdvisePresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.view.inter.IAdviseView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.http.common.ErrorCode;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.FileUtils;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AdvisePresenter implements IAdvisePresenter {
    private static final String TAG = AdvisePresenter.class.getName();
    private IAdviseModel mAdviseModel;//Model接口
    private IAdviseView adviseView;//View接口

    public AdvisePresenter(BaseActivity baseActivity, IAdviseView adviseView) {
        this.mAdviseModel = new AdviseModel(baseActivity);
        this.adviseView = adviseView;
    }

    @Override
    public void topicAdviseInfo() {
        mAdviseModel.subscribeVersion(new StompCallBack<AdviseVO>() {
            @Override
            public void onCallBack(int errorCode, String msg, AdviseVO data) {
                switch (errorCode) {
                    case ErrorCode.SUCCESS:
                        Advise currentAdv = SQLite.select().from(Advise.class).orderBy(Advise_Table.create_time, false).limit(1).querySingle();
                        if (null == currentAdv) {
                            adviseView.onTopicAdviseSuccess(data.getDownloadUrl(), data.getVersion());
                        } else {
                            LogUtil.d(TAG, Thread.currentThread().getName() + ",广告版本比对:" + data.getVersion() + ",旧版本:" + currentAdv.getAdvVersion());
                            if (StrUtil.compareVerName(data.getVersion(), currentAdv.getAdvVersion())) {//新版本比旧版本大则下载，否则不下载
                                adviseView.onTopicAdviseSuccess(data.getDownloadUrl(), data.getVersion());
                            }
                        }
                        break;
                    default:
                        adviseView.onTopicAdviseFail(msg);
                        break;
                }
            }
        });

    }

    @Override
    public void downloadAdvise(String url, String newVer) {
        mAdviseModel.downloadAdvise(url, new HttpDownloadCallBack<File>() {
            @Override
            public void onDownLoadSuccess(File o) {
                try {
                    List<File> unZipFiles = ZipUtils.unzipFile(o.getAbsolutePath(), Constants.ADVISE_DIR + File.separator + newVer);
                    if (unZipFiles.size() > 0) {
                        Date currentDate = new Date();
                        String unFilePath = unZipFiles.get(0).getAbsolutePath();
                        String unFileName = unZipFiles.get(0).getName();
                        Advise advise = new Advise(UUID.randomUUID(), newVer, currentDate, unFilePath, unFileName, true, currentDate, url);
                        advise.save();
                        adviseView.onDownloadAdviseSuccess(unFilePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, Thread.currentThread().getName() + ",downloadAdvise onDownLoadSuccess:" + Log.getStackTraceString(e));
                }
            }

            @Override
            public void onDownLoadFail(Throwable throwable) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",downloadAdvise onDownLoadFail:" + Log.getStackTraceString(throwable));
                adviseView.onDownloadAdviseFail(throwable.getMessage());
            }

            @Override
            public void onProgress(int progress, long total) {

            }
        });
    }

    @Override
    public boolean deleteOldAdvise() {
        List<Advise> adviseList = SQLite.select().from(Advise.class).queryList();
        if (adviseList.size() > 1) {
            Advise oldAdv = SQLite.select().from(Advise.class).orderBy(Advise_Table.create_time, true).limit(1).querySingle();
            if (oldAdv != null) {
                return FileUtils.deleteFile(oldAdv.getFilePath()) && oldAdv.delete();
            }
        }
        return false;
    }
}
