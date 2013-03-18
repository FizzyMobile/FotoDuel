package pl.fizzy.fotoduel;

import android.provider.BaseColumns;

/**
 * Implementation of database scheme. Contains tables Photos and PhotoPoints
 * 
 * @author Max
 * 
 */
public final class PhotosDatabase {

	private PhotosDatabase() {
	}

	public static final int DB_MIN_COUNT = 20;
	public static final int DB_MAX_COUNT = 5000;

	/**
	 * Photos table which implements BaseComlumns
	 * 
	 * @author Max
	 * 
	 */
	public static final class Photos implements BaseColumns {
		Photos() {
		}

		public static final String PHOTOS_TABLE_NAME = "tb_photos";

		public static final String PHOTO_ID = "photo_id";
		public static final String PHOTO_TITLE = "photo_title";
		public static final String PHOTO_AUTHOR = "photo_author";
		public static final String PHOTO_MEDIUM_URL = "photo_medium_url";
		public static final String PHOTO_THUMBNAIL_URL = "photo_thumbnail_url";
		public static final String PHOTO_POINTS = "photo_points";

		public static final String DEFAULT_SORT_ORDER = "photo_points DESC";
	}
}
