package edu.training.wearbountyhunter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by brachialste on 30/11/16.
 */

public class DBProvider {
    private DBhelper oDB;
    private SQLiteDatabase db;
    public DBProvider(Context context)
    {
        oDB = new DBhelper(context);
    }

    public String[][] ObtenerFugitivos(boolean pCapturado)
    {
        int iCnt = 0;
        String[][] aData = null;
        String[] aFils = {(pCapturado?"1":"0")};
        // Se ejecuta la consulta...
        Cursor aRS = querySQL("SELECT * FROM " + DBhelper.TABLE_NAME + " WHERE " + DBhelper.COLUMN_NAME_STATUS + " = ? ORDER BY " + DBhelper.COLUMN_NAME_NAME, aFils);
        if(aRS.getCount() > 0)
        {
            aData = new String[aRS.getCount()][];
            while(aRS.moveToNext())
            {
                // Se carga el arreglo de Datos...
                aData[iCnt] = new String[3];
                aData[iCnt][0] = aRS.getString(aRS.getColumnIndex(DBhelper._ID));
                aData[iCnt][1] = aRS.getString(aRS.getColumnIndex(DBhelper.COLUMN_NAME_NAME));
                aData[iCnt][2] = aRS.getString(aRS.getColumnIndex(DBhelper.COLUMN_NAME_STATUS));
                iCnt++;
            }
        }
        else
        {
            aData = new String[0][];
        }
        // Se cierra el Cursor...
        aRS.close();
        CloseDB();
        return(aData);
    }

    public int ContarFugitivos()
    {
        int iCnt = 0;
        String[] aFils = {""};
        // Se ejecuta la consulta...
        Cursor aRS = querySQL("SELECT " + DBhelper._ID + " FROM " + DBhelper.TABLE_NAME + " WHERE id <> ?", aFils);
        iCnt = aRS.getCount();
        // Se cierra el Cursor...
        aRS.close();
        CloseDB();
        return(iCnt);
    }

    public void InsertFugitivo(String pNombre)
    {
        // Se inserta un nuevo Fugitivo en la Base de Datos...
        Object[] aData = {pNombre,"0"};
        executeSQL("INSERT INTO " + DBhelper.TABLE_NAME + "(" + DBhelper.COLUMN_NAME_NAME + "," + DBhelper.COLUMN_NAME_STATUS + ") VALUES(?,?)", aData);
    }

    public void DeleteFugitivo(String pID)
    {
        // Se elimina el Fugitivo de la Base de Datos...
        Object[] aData = {pID};
        executeSQL("DELETE FROM " + DBhelper.TABLE_NAME + " WHERE " + DBhelper._ID + " = ?", aData);
    }

    public void UpdateFugitivo(String pStatus, String pID)
    {
        // Se actualiza el Fugitivo en la Base de Datos...
        Object[] aData = {pStatus, pID};
        executeSQL("UPDATE " + DBhelper.TABLE_NAME + " SET " + DBhelper.COLUMN_NAME_STATUS + " = ? " +
                "WHERE " + DBhelper._ID + " = ?", aData);
    }

    public long executeSQL(String sql, Object[] bindArgs)
    {
        long iRet = 0;
        // Opens the database object in "write" mode.
        db = oDB.getWritableDatabase();
        db.execSQL(sql, bindArgs);
        CloseDB();
        return(iRet);
    }

    public Cursor querySQL(String sql, String[] selectionArgs)
    {
        Cursor oRet = null;
        // Opens the database object in "write" mode.
        db = oDB.getReadableDatabase();
        oRet = db.rawQuery(sql, selectionArgs);
        return(oRet);
    }

    public void CloseDB()
    {
        if(db.isOpen())
        {
            db.close();
        }
    }

    public boolean isOpenDB()
    {
        return(db.isOpen());
    }

    static class DBhelper extends SQLiteOpenHelper
    {
        // Definición de constantes para el manejo de Base de datos, tablas y columnas...
        private static final String TAG = "DBManager";
        private static final String DATABASE_NAME = "droidBH.db";
        private static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME = "fugitivos";
        public static final String _ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATUS = "status";

        DBhelper(Context context) {
            // calls the super constructor, requesting the default cursor factory.
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        //	    @Override
//	    public void onOpen(SQLiteDatabase db) {
//    		Log.w("[CHECK]", "Opening DB...");
//	    };
        // Creación de la Tabla al crear el Helper...
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w("[CHECK]", "DBHelper.onCreate...");
            db.execSQL("CREATE TABLE " + DBhelper.TABLE_NAME + " ("
                    + DBhelper._ID + " INTEGER PRIMARY KEY,"
                    + DBhelper.COLUMN_NAME_NAME + " TEXT,"
                    + DBhelper.COLUMN_NAME_STATUS + " INTEGER"
                    + ");");
        }
        // Se debe indicar la destruccioón de la BDD anterior al actualizar la App...
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Logs that the database is being upgraded
            Log.w(TAG, "Actualizacion de BDD de la version " + oldVersion + " a la "
                    + newVersion + ", de la que se destruira la información anterior");

            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS " + DBhelper.TABLE_NAME);

            // Recreates the database with a new version
            onCreate(db);
        }
    }
}
