package com.huawei.jams.testautostart.db;


import com.raizlabs.android.dbflow.annotation.Database;

// 使用注解声明数据库名和版本号
// 将其作为静态常量放在类中
@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public class AppDataBase {
    public static final String NAME = "AppDataBase";
    public static final int VERSION = 1;

    /**
     *
     * DBFlow 的表结构修改是通过 Migration 实现的，通过对它的实现，来进行对表的操作。
     *
     * AlterTableMigration 用于重命名表，增加列
     * IndexMigration/IndexPropertyMigration 用于索引创建和删除
     * UpdateTableMigration 升级数据库的时候更新数据
     * 下面用AlterTableMigration举例：
     *
     *        首先，新建一个StudentMigration类，继承AlterTableMigration
     *
     *        然后，添加 @Migration 注解，标明新版本号和所在数据库名
     *
     *        最后，重写 onPreMigrate() 方法，在该方法中添加新的字段
     *
     * 下面在Student表中添加一个新的列mom_id
     *
     *
     * ————————————————
     * 版权声明：本文为CSDN博主「float_yy」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/sinat_37205087/article/details/102962966
     *
     * **/

    /******
     *
     *
     * ****/
//    @Migration(version = VERSION,database = AppDataBase.class)
//    public class StudentMigration extends AlterTableMigration<Advise> {
//
//        public StudentMigration(Class<Advise> table) {
//            super(table);
//        }
//
//        @Override
//        public void onPreMigrate() {
//            addColumn(SQLiteType.INTEGER,"mom_id");
//        }
//    }
}
