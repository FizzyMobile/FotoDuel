package pl.fizzy.fotoduel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Contains implementation of creating SQLite DB table named
 * <code>PhotosDatabase.Photos.PHOTOS_TABLE_NAME</code>
 * 
 * @author Max
 * 
 */
public class PhotosDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "photosDB.db";
	private static final int DATABASE_VERSION = 1;

	public PhotosDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public String getName() {
		return DATABASE_NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * PHOTOS TABLE CREATION
		 */
		db.execSQL("CREATE TABLE " + PhotosDatabase.Photos.PHOTOS_TABLE_NAME
				+ " (" + PhotosDatabase.Photos._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ PhotosDatabase.Photos.PHOTO_ID + " TEXT,"
				+ PhotosDatabase.Photos.PHOTO_TITLE + " TEXT,"
				+ PhotosDatabase.Photos.PHOTO_AUTHOR + " TEXT,"
				+ PhotosDatabase.Photos.PHOTO_MEDIUM_URL + " TEXT,"
				+ PhotosDatabase.Photos.PHOTO_THUMBNAIL_URL + " TEXT,"
				+ PhotosDatabase.Photos.PHOTO_POINTS + " REAL" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(getClass().getName().toString(),
				"onUpgrade on DB from v" + Integer.toString(oldVersion)
						+ " to v" + Integer.toString(newVersion));
		db.execSQL("DROP TABLE IF EXISTS"
				+ PhotosDatabase.Photos.PHOTOS_TABLE_NAME);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
