package pl.fizzy.fotoduel;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Main screen in FotoDuel application. Random (or from database) photos are set
 * for duel. User decides which she/he likes the most. On first time onCreate
 * Toast with network status is being shown. As for menu there are: (a)
 * RankingActvity starter <i>Ranking</i>, (b) re-randomize duel button
 * <i>Refresh</i>, (c) Network settings <i>Network</i>, (d) About dialog creator
 * <i>About</i>
 * 
 * @author Max
 * 
 */
public class DuelActivity extends FotoDuelActivity {
	private static final String TAG = "DuelActivity";
	private static final String DB_TAG = "DB";

	private final double[] DRAW = { 0.5, 0.5 };
	private final double[] IMAGE1 = { 1.0, -1.0 };
	private final double[] IMAGE2 = { -1.0, 1.0 };
	private final double RANDOMNESS = 0.4;

	private ImageView imageView1;
	private ImageView imageView2;
	private ImageLoader imageLoader;
	private Button drawButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_duel);

		imageView1 = (ImageView) findViewById(R.id.imageview1);
		imageView2 = (ImageView) findViewById(R.id.imageview2);
		imageLoader = new ImageLoader(getResources());

		if (checkForFirstRun(savedInstanceState)) {
			Toast.makeText(getApplicationContext(), getNetworkInfo(),
					Toast.LENGTH_SHORT).show();
			randomizeDuel();
		} else {
			resolveDuel();
		}

		drawButton = (Button) findViewById(R.id.button_draw);
		setOnClickListeners();
	}

	/**
	 * Randomization of duel. Rules: if number of elements in database is
	 * <i>less</i> than <code>DB_MIN_COUNT</code> then always new images are
	 * shown if number of elements in database is <i>more or equal</i> to
	 * <code>DB_MAX_COUNT</code> then always images from db are shown else new
	 * image is shown with probability less or equal to <code>RANDOMNESS</code>
	 */
	private void randomizeDuel() {
		setImgViewsClickable(false);

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(PhotosDatabase.Photos.PHOTOS_TABLE_NAME);
		Cursor c = queryBuilder.query(database, null, null, null, null, null,
				null);
		int dbCount = c.getCount();
		Log.d(DB_TAG, "Database count = " + dbCount);

		if (dbCount < PhotosDatabase.DB_MIN_COUNT) {
			imageLoader.random(imageView1, getIntent(), 1);
			imageLoader.random(imageView2, getIntent(), 2);
		} else if (dbCount >= PhotosDatabase.DB_MAX_COUNT) {
			imageLoader.fromUrl(imageView1, randomFromDb(dbCount, 1), true);
			imageLoader.fromUrl(imageView2, randomFromDb(dbCount, 2), true);
		} else {
			if (RANDOMNESS <= Math.random()) {
				imageLoader.random(imageView1, getIntent(), 1);
			} else {
				imageLoader.fromUrl(imageView1, randomFromDb(dbCount, 1), true);
			}
			if (RANDOMNESS <= Math.random()) {
				imageLoader.random(imageView2, getIntent(), 2);
			} else {
				imageLoader.fromUrl(imageView2, randomFromDb(dbCount, 2), true);
			}
		}

		c.close();
	}

	/**
	 * Getting url of random item from database.
	 * 
	 * @param dbCount
	 *            - number of db elements
	 * @param i
	 *            - index of imageView
	 * @return url of image from db
	 */
	private String randomFromDb(int dbCount, int i) {
		int index = 0 + (int) (Math.random() * (dbCount - 1));
		String url = "";

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(PhotosDatabase.Photos.PHOTOS_TABLE_NAME);

		String cols[] = {
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos._ID,
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos.PHOTO_ID,
				PhotosDatabase.Photos.PHOTOS_TABLE_NAME + "."
						+ PhotosDatabase.Photos.PHOTO_MEDIUM_URL };

		Cursor c = queryBuilder.query(database, cols, null, null, null, null,
				null);

		if (c.moveToPosition(index)) {
			url += c.getString(c
					.getColumnIndex(PhotosDatabase.Photos.PHOTO_MEDIUM_URL));
			getIntent().putExtra(
					PhotosDatabase.Photos.PHOTO_ID + Integer.toString(i),
					c.getString(c
							.getColumnIndex(PhotosDatabase.Photos.PHOTO_ID)));
			getIntent().putExtra(
					PhotosDatabase.Photos.PHOTO_MEDIUM_URL
							+ Integer.toString(i), url);

			c.close();
			return url;
		} else {
			c.close();
			return null;
		}
	}

	/**
	 * Refreshing images that are set for duel
	 */
	private void resolveDuel() {
		setImgViewsClickable(false);
		imageLoader.fromUrl(
				imageView1,
				getIntent().getStringExtra(
						PhotosDatabase.Photos.PHOTO_MEDIUM_URL
								+ Integer.toString(1)), true);
		imageLoader.fromUrl(
				imageView2,
				getIntent().getStringExtra(
						PhotosDatabase.Photos.PHOTO_MEDIUM_URL
								+ Integer.toString(2)), true);
	}

	private void setOnClickListeners() {
		setImgViewsClickable(true);

		drawButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg) {
				if (imageView1.isClickable() && imageView2.isClickable()) {
					Log.d(TAG, "Draw button clicked");
					setImgViewsClickable(false);
					vibrate();
					voteToDb(DRAW);
					randomizeDuel();
				}
			}
		});

		imageView1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (imageView2.isClickable()) {
					Log.d(TAG, "imageView1 clicked");
					setImgViewsClickable(false);
					vibrate();
					voteToDb(IMAGE1);
					randomizeDuel();
				}
			}
		});

		imageView2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (imageView1.isClickable()) {
					Log.d(TAG, "imageView2 clicked");
					setImgViewsClickable(false);
					vibrate();
					voteToDb(IMAGE2);
					randomizeDuel();
				}
			}
		});
	}

	/**
	 * Vote method saving votes (and new records) to database.
	 * 
	 * @param votes
	 *            - <code>DRAW</code> or <code>IMAGE1</code> or
	 *            <code>IMAGE2</code>
	 */
	protected void voteToDb(double[] votes) {
		database.beginTransaction();
		Intent intent = getIntent();
		try {
			Boolean areTheSame = false;
			String index;

			// Photo1 and Photo2 are the same?
			if ((intent.getStringExtra(PhotosDatabase.Photos.PHOTO_ID + "1")
					.contains(intent
							.getStringExtra(PhotosDatabase.Photos.PHOTO_ID
									+ "2")))) {
				areTheSame = true;
				Log.d(DB_TAG, "Photos are the same");
			}

			// database.insert two times
			for (int i = 1; i <= 2; i++) {
				if (areTheSame) {
					i++; // insert just one time (if is new)
					votes[0] = 0.0;
					votes[1] = 0.0;
				}
				index = Integer.toString(i);
				Log.d(DB_TAG, "Managing photo number " + index);

				// Check if photo is already in db
				SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(PhotosDatabase.Photos.PHOTOS_TABLE_NAME);
				queryBuilder.appendWhere(PhotosDatabase.Photos.PHOTO_ID
						+ " = '"
						+ intent.getStringExtra(PhotosDatabase.Photos.PHOTO_ID
								+ index) + "'");

				Cursor c = queryBuilder.query(database, null, null, null, null,
						null, null);
				ContentValues newRecord = new ContentValues();

				if (c.getCount() == 0) { // photo is new
					Log.d(DB_TAG, "Photo is NEW");
					newRecord.put(
							PhotosDatabase.Photos.PHOTO_ID,
							getIntent().getStringExtra(
									PhotosDatabase.Photos.PHOTO_ID + index));
					newRecord.put(
							PhotosDatabase.Photos.PHOTO_TITLE,
							getIntent().getStringExtra(
									PhotosDatabase.Photos.PHOTO_TITLE + index));
					newRecord
							.put(PhotosDatabase.Photos.PHOTO_AUTHOR,
									getIntent().getStringExtra(
											PhotosDatabase.Photos.PHOTO_AUTHOR
													+ index));
					newRecord.put(
							PhotosDatabase.Photos.PHOTO_MEDIUM_URL,
							getIntent().getStringExtra(
									PhotosDatabase.Photos.PHOTO_MEDIUM_URL
											+ index));
					newRecord.put(
							PhotosDatabase.Photos.PHOTO_THUMBNAIL_URL,
							getIntent().getStringExtra(
									PhotosDatabase.Photos.PHOTO_THUMBNAIL_URL
											+ index));
					newRecord.put(PhotosDatabase.Photos.PHOTO_POINTS,
							votes[i - 1]);
					database.insert(PhotosDatabase.Photos.PHOTOS_TABLE_NAME,
							null, newRecord);
					Log.d(DB_TAG, "Insert OK");
				} else {
					Log.d(DB_TAG, "Photo is NOT NEW");
					c.moveToFirst();
					double vote = votes[i - 1]
							+ c.getDouble(c
									.getColumnIndex(PhotosDatabase.Photos.PHOTO_POINTS));
					newRecord.put(PhotosDatabase.Photos.PHOTO_POINTS, vote);
					database.update(
							PhotosDatabase.Photos.PHOTOS_TABLE_NAME,
							newRecord,
							PhotosDatabase.Photos._ID
									+ " = "
									+ c.getInt(c
											.getColumnIndex(PhotosDatabase.Photos._ID)),
							null);
					Log.d(DB_TAG, "Update OK");
				}

				c.close();
			}
			Log.d(DB_TAG, "DB OK");
			database.setTransactionSuccessful();
		} finally {
			Log.d(DB_TAG, "END vote");
			database.endTransaction();
		}
	}

	/**
	 * Set imageViews clickable flag to value.
	 * 
	 * @param bool
	 */
	private void setImgViewsClickable(boolean bool) {
		imageView1.setClickable(bool);
		imageView2.setClickable(bool);
	}

	/**
	 * Generates vibration with length of <code>R.integer.vibration_time</code>
	 */
	private void vibrate() {
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(getResources().getInteger(R.integer.vibration_time));
		}
	}

	/**
	 * Getting all network information.
	 * 
	 * @return - string network information
	 */
	private String getNetworkInfo() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		String networkInfo = new String();
		if (wifiInfo.isConnected()) {
			networkInfo += getResources().getString(
					R.string.network_info_wifi_conn);
		} else if (mobileInfo.isConnected()) {
			networkInfo += getResources().getString(
					R.string.network_info_mobile_conn);
			if (wifiInfo.isAvailable()) {
				networkInfo += getResources().getString(
						R.string.network_info_wifi_aval);
			}
		} else {
			networkInfo += getResources().getString(
					R.string.network_info_no_conn);
			if (wifiInfo.isAvailable()) {
				networkInfo += getResources().getString(
						R.string.network_info_wifi_aval);
			}
		}
		return networkInfo;
	}

	/**
	 * Check if <code>DuelActivity<code> is created for the first time.
	 * 
	 * @param savedInstanceState
	 * @return
	 */
	private Boolean checkForFirstRun(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getBoolean(getResources().getString(
					R.string.pref_isFirstRun));
		}
		return true;
	}

	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(
				getResources().getString(R.string.pref_isFirstRun), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.duel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Resources res = getResources();

		switch (item.getItemId()) {
		case R.id.action_ranking:
			Log.d(TAG, res.getString(R.string.action_ranking)
					+ " action selected");
			Intent rankingActivity = new Intent(getApplicationContext(),
					RankingActivity.class);
			startActivity(rankingActivity);
			return true;
		case R.id.action_refresh:
			Log.d(TAG, res.getString(R.string.action_network)
					+ " action selected");
			randomizeDuel();
			return true;
		case R.id.action_network:
			Log.d(TAG, res.getString(R.string.action_network)
					+ " action selected");
			Intent networkSettingsActivity = new Intent(
					Settings.ACTION_WIRELESS_SETTINGS);
			startActivity(networkSettingsActivity);
			return true;
		case R.id.action_about:
			Log.d(TAG, res.getString(R.string.action_about)
					+ " action selected");
			new AlertDialog.Builder(DuelActivity.this)
					.setTitle(
							DuelActivity.this.getResources().getString(
									R.string.app_name))
					.setMessage(R.string.dialog_about_message)
					.setIcon(R.drawable.ic_launcher)
					.setPositiveButton(R.string.dialog_about_button, null)
					.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
