package pl.fizzy.fotoduel;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * Provides an instance of database for extending Activities
 * 
 * @author Max
 * 
 */
public class FotoDuelActivity extends Activity {
	protected PhotosDatabaseHelper databaseHelper = null;
	protected SQLiteDatabase database = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseHelper = new PhotosDatabaseHelper(this.getApplicationContext());
		database = databaseHelper.getWritableDatabase();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (database != null) {
			database.close();
		}

		if (databaseHelper != null) {
			databaseHelper.close();
		}
	}
}
