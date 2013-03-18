package pl.fizzy.fotoduel;

import pl.fizzy.fotoduel.RankingAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class shows list of items from database using <code>rank_item.xml</code>
 * Menu option - delete ranking (deleting database)
 * 
 * @author Max
 * 
 */
public class RankingActivity extends FotoDuelActivity {
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking);

		imageLoader = new ImageLoader(getResources());
		imageLoader.prepareCache();

		fillRankingList();
	}

	/**
	 * Fills ListView with items from database
	 */
	private void fillRankingList() {
		Cursor cursor = getAllRecords();
		RankingAdapter adapter = new RankingAdapter(this, cursor, imageLoader);
		ListView listView = (ListView) findViewById(R.id.ranking_list);
		listView.setAdapter(adapter);
		cursor.close();
	}

	/**
	 * Getting cursor of all records from database
	 * 
	 * @return
	 */
	private Cursor getAllRecords() {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(PhotosDatabase.Photos.PHOTOS_TABLE_NAME);

		String cols[] = {
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos._ID,
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos.PHOTO_TITLE,
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos.PHOTO_AUTHOR,
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos.PHOTO_POINTS,
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos.PHOTO_THUMBNAIL_URL };

		return queryBuilder.query(database, cols, null, null, null, null,
				PhotosDatabase.Photos.DEFAULT_SORT_ORDER);
	}

	public void onPause() {
		super.onPause();
		imageLoader.releaseCache();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rank, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete_db:
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
					RankingActivity.this);
			myAlertDialog.setIcon(R.drawable.ic_menu_delete);
			myAlertDialog.setTitle(R.string.dialog_delete_title);
			myAlertDialog.setMessage(R.string.dialog_delete_message);
			myAlertDialog.setNegativeButton(
					R.string.dialog_delete_button_cancel, null);
			myAlertDialog.setPositiveButton(R.string.dialog_delete_button_yes,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							database.close();
							deleteDatabase(databaseHelper.getName());
							finish();
						}
					});

			myAlertDialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	static class RankingItem {
		TextView titleTextView;
		TextView authorTextView;
		TextView pointsTextView;
		ImageView photoImageView;
	}

}
