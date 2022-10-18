package com.team4mhd.covandapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.team4mhd.covandapp.db.ConfiguracionSQLiteAdmin;


/**
 *  Utilidades de configuración.
 *
 *  @author hfunes.com
 */
public class ConfigUtils {

    public static final String NRO_SERIE_DEFAULT = "0000000000";

    public static String IP_SERVIDOR = "ipServidor";
    private String nombre_tabla = ConfiguracionSQLiteAdmin.nombre_tabla;

    public static String APP_VERSION = "V0.1";

    /**
     *  Recupera un par clave-valor de la configuración de la app.
     *
     *  @author hfunes.com
     */
    public ContentValues getParConfiguracion(Context context, String clave){

        ContentValues par = null;
        Log.i(this.getClass().getSimpleName(),"***** Recuperando pares de configuración *****");
        ConfiguracionSQLiteAdmin dbAdmin = new ConfiguracionSQLiteAdmin(context,"configuracion", null, 1);
        SQLiteDatabase bd = dbAdmin.getWritableDatabase();

        Cursor fila = bd.rawQuery("select * from " + nombre_tabla + " where clave='" + clave + "'", null);
        if (fila.moveToFirst()) {
            if (fila.getString(0) != null) {
                par = new ContentValues();
                par.put(fila.getString(0), fila.getString(1));
            }

        }

        bd.close();
        return par;
    }

    /**
     *  Graba un par clave-valor en la configuración de la app.
     *
     *  @author hfunes.com
     */
    public boolean grabarParConfiguracion(Context context, String clave, String valor) {

        Log.i(this.getClass().getSimpleName(),"***** Guardando par de configuración *****");

        boolean existePar = getParConfiguracion(context, clave) != null? true : false;
        ConfiguracionSQLiteAdmin dbAdmin = new ConfiguracionSQLiteAdmin(context,"configuracion", null, 1);
        SQLiteDatabase bd = dbAdmin.getWritableDatabase();

        ContentValues parConfig = new ContentValues();
        parConfig.put("clave", clave);
        parConfig.put("valor", valor);

        if(existePar){
            Log.i(this.getClass().getSimpleName(),"***** Actualizando par en db*****");
            bd.update(nombre_tabla, parConfig, "clave='"+clave+"'", null);
        } else {
            Log.i(this.getClass().getSimpleName(),"***** Guardando par en db *****");
            bd.insert(nombre_tabla, null, parConfig);
        }

        bd.close();
        Log.i(this.getClass().getSimpleName(),"***** Objeto guardado *****");

        return true;
    }

    /**
     *  Recupera los pares clave-valor de la configuración de la app.
     *
     *  @author hfunes.com
     */
    public ContentValues getConfiguracion(Context context) {
        ConfiguracionSQLiteAdmin dbAdmin = new ConfiguracionSQLiteAdmin(context,"configuracion", null, 1);
        SQLiteDatabase bd = dbAdmin.getWritableDatabase();
        ContentValues pares = null;

        Cursor fila = bd.rawQuery("select * from " + nombre_tabla, null);
        if (fila.moveToFirst()) {
            pares = new ContentValues();
            pares.put(fila.getString(0), fila.getString(1));
            while(fila.moveToNext())
                pares.put(fila.getString(0), fila.getString(1));
        }
        bd.close();

        return pares;
    }
}
