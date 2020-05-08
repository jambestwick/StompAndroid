package com.huawei.jams.testautostart;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
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
    private boolean swIsCheck;


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
                Log.d("aaa", "onSucceed:" + array);
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
        boxDataList.add("Z01");
        boxDataList.add("Z02");
        boxDataList.add("Z03");
        boxDataList.add("Z04");
        boxDataList.add("Z05");
        boxDataList.add("Z06");
        boxDataList.add("Z07");
        boxDataList.add("Z08");
        boxDataList.add("Z09");
        boxDataList.add("A01");
        boxDataList.add("B01");
        boxDataList.add("C01");
        boxDataList.add("D01");
        boxDataList.add("E01");
        boxDataList.add("F01");
        boxDataList.add("G01");
        boxDataList.add("H01");
        boxDataList.add("I01");
        lockAdapter = new LockAdapter();
        binding.mainLockListRcv.setLayoutManager(new LinearLayoutManager(this));
        binding.mainLockListRcv.setAdapter(lockAdapter);

        lockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                LogUtil.d(TAG, "点击第:" + adapterPosition + "个");
                if (swIsCheck) {
                    KeyCabinetReceiver.openBox(MainActivity.this, boxDataList.get(adapterPosition));
                }
                KeyCabinetReceiver.queryBoxState(MainActivity.this, boxDataList.get(adapterPosition), new KeyCabinetReceiver.DataBack() {
                    @Override
                    public void onReceive(Intent intent) {
                        if (intent.getAction().equals("android.intent.action.hal.iocontroller.querydata")) {
                            String boxId = intent.getExtras().getString("boxid");
                            LogUtil.d(TAG, "箱门:" + boxId + ",返回查询操作完成");
                            boolean isOpened = intent.getExtras().getBoolean("isopened");
                            boolean isStoraged = intent.getExtras().getBoolean("isstoraged");
                            LogUtil.d(TAG, "箱门:box" + boxId + "box状态" + isOpened + ",isStoraged:" + isStoraged);
                            // TODO ...
                        }
                    }
                });

            }
        });
        lockAdapter.setData(boxDataList);

        binding.mainLockControlSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开
                    swIsCheck = true;
                } else {
                    //关
                    swIsCheck = false;
                }
            }
        });
        swIsCheck = true;
        binding.mainLockControlSw.setChecked(swIsCheck);
    }


}
