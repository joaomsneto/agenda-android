package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by joao on 12/04/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper mInstance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "agenda_expotec.db";
    private static final String AGENDA_TABLE_CREATE =
            "CREATE TABLE agenda (" +
                    " id INTEGER , " +
                    " dia TEXT , " +
                    " hora_inicial TEXT, " +
                    " hora_final TEXT, " +
                    " sala TEXT, " +
                    " cor TEXT, " +
                    " link TEXT, " +
                    " titulo TEXT, " +
                    " texto TEXT);";
    private static final Object FILE_DIR = "Android/data/developer.joao.agendaexpotec";

    public static DBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + FILE_DIR
                + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AGENDA_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXIST " + DATABASE_NAME);
//        onCreate(db);
    }

}
