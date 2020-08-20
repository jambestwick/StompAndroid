package com.huawei.jams.testautostart.utils;

import android.content.Context;

import com.huawei.jams.testautostart.R;
import com.yxytech.parkingcloud.baselibrary.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ResourceFileUtil {

//    public static void saveAdv2SDCard(Context context, String name) throws Throwable {
//        InputStream inStream = context.getResources().openRawResource(R.raw.first_advise);
//        File file = new File(Constants.ADVISE_DIR, name);
//        FileUtils.createOrExistsFile(file);
//        FileOutputStream fileOutputStream = new FileOutputStream(file);//存入SDCard
//        byte[] buffer = new byte[2048];
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        int len = 0;
//        while ((len = inStream.read(buffer)) != -1) {
//            outStream.write(buffer, 0, len);
//        }
//        byte[] bs = outStream.toByteArray();
//        fileOutputStream.write(bs);
//        outStream.close();
//        inStream.close();
//        fileOutputStream.flush();
//        fileOutputStream.close();
//    }
}
