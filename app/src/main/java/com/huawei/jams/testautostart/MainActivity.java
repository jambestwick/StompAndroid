package com.huawei.jams.testautostart;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.service.StompService;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName();
    private ActivityMainBinding binding;

    /***输入6位开箱码**/
    private String inputCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, StompService.class));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViews();
    }


    private void initViews() {
        binding.setClick(v -> {
            switch (v.getId()) {
                case R.id.main_code_0_tv:
                    addInputCode(binding.mainCode0Tv.getText().toString());
                    break;
                case R.id.main_code_1_tv:
                    addInputCode(binding.mainCode1Tv.getText().toString());
                    break;
                case R.id.main_code_2_tv:
                    addInputCode(binding.mainCode2Tv.getText().toString());
                    break;
                case R.id.main_code_3_tv:
                    addInputCode(binding.mainCode3Tv.getText().toString());
                    break;
                case R.id.main_code_4_tv:
                    addInputCode(binding.mainCode4Tv.getText().toString());
                    break;
                case R.id.main_code_5_tv:
                    addInputCode(binding.mainCode5Tv.getText().toString());
                    break;
                case R.id.main_code_6_tv:
                    addInputCode(binding.mainCode6Tv.getText().toString());
                    break;
                case R.id.main_code_7_tv:
                    addInputCode(binding.mainCode7Tv.getText().toString());
                    break;
                case R.id.main_code_8_tv:
                    addInputCode(binding.mainCode8Tv.getText().toString());
                    break;
                case R.id.main_code_9_tv:
                    addInputCode(binding.mainCode9Tv.getText().toString());
                    break;
                case R.id.main_code_delete_tv://删除前一位
                    decreaseInputCode();
                    break;
                case R.id.main_code_ok_tv:
                    //校验长度，发送请求开箱
                    if (inputCode.length() == 6) {
//                        StompService.getInstance().sendData(inputCode, new StompService.Callback<String>() {
//                            @Override
//                            public void onDataReceive(String s) {
//                                //成功开箱
//                            }
//                        });
                    } else {
                        //提示码位数不够
                    }
                    break;
                default:
                    break;
            }

        });
    }

    private void refreshCode2View() {
        binding.mainSixCode1Tv.setText("");
        binding.mainSixCode2Tv.setText("");
        binding.mainSixCode3Tv.setText("");
        binding.mainSixCode4Tv.setText("");
        binding.mainSixCode5Tv.setText("");
        binding.mainSixCode6Tv.setText("");
        for (int i = 0; i < inputCode.length(); i++) {
            switch (i) {
                case 0:
                    binding.mainSixCode1Tv.setText("" + inputCode.charAt(i));
                    break;
                case 1:
                    binding.mainSixCode2Tv.setText("" + inputCode.charAt(i));
                    break;
                case 2:
                    binding.mainSixCode3Tv.setText("" + inputCode.charAt(i));
                    break;
                case 3:
                    binding.mainSixCode4Tv.setText("" + inputCode.charAt(i));
                    break;
                case 4:
                    binding.mainSixCode5Tv.setText("" + inputCode.charAt(i));
                    break;
                case 5:
                    binding.mainSixCode6Tv.setText("" + inputCode.charAt(i));
                    break;
                default:
                    break;
            }
        }
    }

    private void addInputCode(String addCode) {
        if (inputCode.length() < 6) {
            inputCode = inputCode + addCode;
            refreshCode2View();
        }

    }

    private void decreaseInputCode() {
        if (inputCode.length() > 0) {
            inputCode = inputCode.substring(0, inputCode.length() - 1);
            refreshCode2View();
        }
    }

    // 按照下面代码示例修改Activity的onResume方法
    @Override
    protected void onResume() {
        /** * 设置横屏幕*/
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, StompService.class));
    }
}
