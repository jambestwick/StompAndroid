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
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class AdvisePresenter implements IAdvisePresenter {
    private static final String TAG = AppInfoPresenter.class.getName();
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
                        Advise currentAdv = SQLite.select().from(Advise.class).orderBy(Advise_Table.adv_version, false).limit(1).querySingle();
                        if (null == currentAdv) {
                            adviseView.onTopicAdviseSuccess(data.getDownloadUrl(), data.getVersion());
                        } else {
                            if (StrUtil.compareVerName(data.getVersion(), currentAdv.getAdvVersion())) {
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
                    ZipUtils.unzipFile(o.getAbsolutePath(), Constants.ADVISE_DIR + File.separator + newVer);
                    Date currentDate = new Date();
                    Advise advise = new Advise(UUID.randomUUID(), newVer, currentDate, o.getAbsolutePath(), o.getName(), currentDate);
                    advise.save();
                    adviseView.onDownloadAdviseSuccess(o.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDownLoadFail(Throwable throwable) {
                LogUtil.e(TAG, "downloadAdvise onDownLoadFail:" + Log.getStackTraceString(throwable));
                adviseView.onDownloadAdviseFail(throwable.getMessage());
            }

            @Override
            public void onProgress(int progress, long total) {

            }
        });
    }
}
