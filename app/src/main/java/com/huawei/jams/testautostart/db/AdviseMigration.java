//package com.huawei.jams.testautostart.db;
//
//import com.huawei.jams.testautostart.entity.Advise;
//import com.raizlabs.android.dbflow.annotation.Migration;
//import com.raizlabs.android.dbflow.sql.SQLiteType;
//import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
//
//import static com.huawei.jams.testautostart.db.AppDataBase.VERSION;
//
///**
// * <p>文件描述：<p>
// * <p>作者：jambestwick<p>
// * <p>创建时间：2020/6/19<p>
// * <p>更新时间：2020/6/19<p>
// * <p>版本号：${VERSION}<p>
// * <p>邮箱：jambestwick@126.com<p>
// */
////----------------升级-新增表：改版本好即可
////----------------升级-新增列：新增下面代码
//@Migration(version = VERSION, database = AppDataBase.class)//=2的升级
//public class AdviseMigration extends AlterTableMigration<Advise> {
//
//    public AdviseMigration(Class<Advise> table) {
//        super(table);
//    }
//
//    @Override
//    public void onPreMigrate() {
//        addColumn(SQLiteType.get(String.class.getName()), "url");//基本数据类型:浮点数
//    }
//}
