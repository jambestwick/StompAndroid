package com.huawei.jams.testautostart;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.entity.Advise_Table;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.view.adapter.LockAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview.OnItemClickListener;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;
import com.yxytech.parkingcloud.baselibrary.utils.RxPermissionsUtil;

import java.util.*;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private LockAdapter lockAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toast.makeText(this, "哈哈，我成功启动了！", Toast.LENGTH_LONG).show();
        Log.e("AutoRun", "哈哈，我成功启动了！");
        RxPermissionsUtil.check(this, RxPermissionsUtil.STORAGE, "申请读写", new RxPermissionsUtil.OnPermissionRequestListener() {
            @Override
            public void onSucceed() {
                buildAds();
                List<Advise> array = SQLite.select().from(Advise.class).where(Advise_Table.create_time.lessThan(new Date())).queryList();
                Log.d("aaa", "onSucceed:"+array);
            }

            @Override
            public void onFailed() {

            }
        });

        RxPermissionsUtil.check(this, RxPermissionsUtil.READ_PHONE, "申请读取设备权限", new RxPermissionsUtil.OnPermissionRequestListener() {
            @Override
            public void onSucceed() {
                //LocationUtils.getLocationManager(MainActivity.this).initLocation();
                binding.latLngTv.setText(PackageUtils.getIMEI(MainActivity.this));
            }

            @Override
            public void onFailed() {

            }
        });
        binding.latLngTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        initViews();
        //startTimeTask();
    }

//    public void startTimeTask() {
//        timerTask = new Timer();
//        buildAds();
//        //List<Advise> array = SQLite.select().from(Advise.class).where(Advise_Table.create_time.lessThan(new Date())).queryList();
//        timerTask.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvLatLng.append(TimeUtil.date2Str(new Date(), TimeUtil.DEFAULT_TIME_FORMAT) + ",Lat:" + LocationUtils.latitude + ",Lng:" + LocationUtils.longitude + "\n");
//                    }
//                });
//            }
//        }, 0, 500);
//    }

    public void buildAds() {
        List<Advise> arrList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Advise advise = new Advise();
            advise.setUuid(UUID.randomUUID());
            advise.setAdvDate(new Date());
            advise.setAdvNo("no:" + i);
            advise.setAdvVersion("version_" + i);
            advise.setCreateTime(new Date());
            advise.setCreateUser("张三" + i);
            advise.setValid(i % 2 == 0);
            advise.setFilePath("mnt/sdcard/aaa" + i + ".zip");
            advise.setFileName("mdmdmd" + i);
            advise.setModifyUser("mdmdmd" + i);
            advise.setModifyTime(new Date());
            advise.save();

            //arrList.add(advise);
        }
        //SQLite.insert(Advise.class).values(arrList).executeInsert();
    }

    void initViews() {
        List<String> boxDataList = new ArrayList<>();
        boxDataList.add("A01");
        boxDataList.add("A02");
        boxDataList.add("A03");
        boxDataList.add("A04");
        boxDataList.add("A05");
        boxDataList.add("A06");
        boxDataList.add("A07");
        boxDataList.add("A08");
        boxDataList.add("A09");
        boxDataList.add("CH5");
        boxDataList.add("CH6");
        boxDataList.add("CH7");
        boxDataList.add("CH8");
        boxDataList.add("CH9");
        boxDataList.add("CH10");
        boxDataList.add("CH11");
        boxDataList.add("CH12");
        boxDataList.add("CH25");
        lockAdapter = new LockAdapter();
        binding.mainLockListRcv.setLayoutManager(new LinearLayoutManager(this));
        binding.mainLockListRcv.setAdapter(lockAdapter);

        lockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                LogUtil.d(TAG, "点击第:" + adapterPosition + "个");
                KeyCabinetReceiver.openBox(MainActivity.this, boxDataList.get(adapterPosition));
            }
        });
        lockAdapter.setData(boxDataList);
    }


}
