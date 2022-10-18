package com.team4mhd.covandapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Conector a la DB SQLite para controlar la configuración general.
 *
 * @author hfunes.com
 */
public class ConfiguracionSQLiteAdmin extends SQLiteOpenHelper {

    public static String nombre_tabla = "configuracion_gral";

    public ConfiguracionSQLiteAdmin(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //Métodos de la clase
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + nombre_tabla + "(clave text primary key, valor text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + nombre_tabla);
        onCreate(sqLiteDatabase);
    }
}