package pl.fizzy.fotoduel;

import pl.fizzy.fotoduel.RankingActivity.RankingItem;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This class is an adapter for ListView of <code>RankingActivity</code>
 * 
 * @author Max
 * 
 */
public class RankingAdapter extends BaseAdapter {

	private RankingAdapterItem[] raItems;
	private Context context;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public RankingAdapter(Context cxt, Cursor crs, ImageLoader imgLdr) {
		context = cxt;
		imageLoader = imgLdr;
		inflater = LayoutInflater.from(context);

		int dbCount = crs.getCount();
		raItems = new RankingAdapterItem[dbCount];

		crs.moveToFirst();
		for (int i = 0; i < dbCount; i++) {
			final int id = crs.getInt(crs
					.getColumnIndex(PhotosDatabase.Photos._ID));
			final String title = crs.getString(crs
					.getColumnIndex(PhotosDatabase.Photos.PHOTO_TITLE));
			final String author = crs.getString(crs
					.getColumnIndex(PhotosDatabase.Photos.PHOTO_AUTHOR));
			final double points = crs.getDouble(crs
					.getColumnIndex(PhotosDatabase.Photos.PHOTO_POINTS));
			final String thumbnail_url = crs.getString(crs
					.getColumnIndex(PhotosDatabase.Photos.PHOTO_THUMBNAIL_URL));

			raItems[i] = new RankingAdapterItem(id, title, author, points,
					thumbnail_url);
			crs.moveToNext();
		}
	}

	@Override
	public int getCount() {
		return raItems.length;
	}

	@Override
	public Object getItem(int position) {
		return raItems[position];
	}

	@Override
	public long getItemId(int position) {
		return raItems[position].get_id();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RankingActivity.RankingItem item;

		if (convertView == null) {
			item = new RankingItem();
			convertView = (RelativeLayout) inflater.inflate(R.layout.rank_item,
					null);
			item.titleTextView = (TextView) convertView
					.findViewById(R.id.rankitem_title);
			item.authorTextView = (TextView) convertView
					.findViewById(R.id.rankitem_author);
			item.pointsTextView = (TextView) convertView
					.findViewById(R.id.rankitem_points);
			item.photoImageView = (ImageView) convertView
					.findViewById(R.id.rankitem_imageview);
			convertView.setTag(item);
		} else {
			item = (RankingActivity.RankingItem) convertView.getTag();
		}

		item.titleTextView.setText(raItems[position].getTitle());
		item.authorTextView.setText(raItems[position].getAuthor());
		item.pointsTextView.setText(Double.toString(raItems[position]
				.getPoints()));
		item.photoImageView.setImageResource(R.drawable.ic_launcher);
		imageLoader.fromUrl(item.photoImageView,
				raItems[position].getThumbnail_url(), false);

		return convertView;
	}

	/**
	 * Simple RankingItem representation.
	 * 
	 * @author Max
	 * 
	 */
	private class RankingAdapterItem {
		private int _id;
		public final String title;
		public final String author;
		public final double points;
		public final String thumbnail_url;

		public RankingAdapterItem(int id, String tte, String atr, double pts,
				String tu) {
			_id = id;
			title = tte;
			author = atr;
			points = pts;
			thumbnail_url = tu;
		}

		public String getTitle() {
			return title;
		}

		public String getAuthor() {
			return author;
		}

		public double getPoints() {
			return points;
		}

		public String getThumbnail_url() {
			return thumbnail_url;
		}

		public int get_id() {
			return _id;
		}
	}

}
