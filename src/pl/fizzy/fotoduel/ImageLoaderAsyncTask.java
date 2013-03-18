package pl.fizzy.fotoduel;

import java.io.IOException;
import java.lang.ref.WeakReference;
import org.json.JSONException;

import pl.fizzy.fotoduel.ImageLoader.MemoryCache;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.Photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * This class is responsible for asynchronous download of an image from
 * (URL)String returning a bitmap.
 * 
 * @author Max
 * 
 */
public class ImageLoaderAsyncTask extends AsyncTask<String, Void, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
	private final WeakReference<Intent> intentViewReference;
	private WeakReference<ImageLoader.MemoryCache> cacheReference;
	private String urlStr;
	private int index;

	public String getUrlStr() {
		return urlStr;
	}

	public ImageLoaderAsyncTask(ImageView imageView, Intent intent, int mindex) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		intentViewReference = new WeakReference<Intent>(intent);
		cacheReference = null;
		index = mindex;
	}

	public ImageLoaderAsyncTask(MemoryCache cache, ImageView imageView) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		intentViewReference = null;
		if (cache != null) {
			cacheReference = new WeakReference<ImageLoader.MemoryCache>(cache);
		} else {
			cacheReference = null;
		}
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		urlStr = params[0];

		if (intentViewReference != null) {
			Intent intent = intentViewReference.get();
			try {
				FlickrApi flickrApi = new FlickrApi();
				Photo photo = flickrApi.getRandomPhoto();
				exportToIntent(photo, intent);
				urlStr = photo.getMediumUrl();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FlickrException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Bitmap bitmap = null;
		if (urlStr.length() != 0) {
			Log.d("TASK", "Downloading: " + urlStr);
			bitmap = BitmapUtils.loadBitmap(urlStr);
		}
		if (bitmap == null) {
			Log.d("TASK", "Returning NULL bitmap");
		} else {
			Log.d("TASK", "Returning bitmap");
		}
		return bitmap;
	}

	/**
	 * exporting interesting content of photo to intent of activity
	 * 
	 * @param photo
	 * @param intent
	 */
	private void exportToIntent(Photo photo, Intent intent) {
		intent.putExtra(
				PhotosDatabase.Photos.PHOTO_ID + Integer.toString(index),
				photo.getId());
		String title = photo.getTitle();
		intent.putExtra(
				PhotosDatabase.Photos.PHOTO_TITLE + Integer.toString(index),
				title.substring(0, Math.min(title.length(), 30)));
		intent.putExtra(
				PhotosDatabase.Photos.PHOTO_AUTHOR + Integer.toString(index),
				photo.getOwner().getUsername());
		intent.putExtra(
				PhotosDatabase.Photos.PHOTO_MEDIUM_URL
						+ Integer.toString(index), photo.getMediumUrl());
		intent.putExtra(
				PhotosDatabase.Photos.PHOTO_THUMBNAIL_URL
						+ Integer.toString(index), photo.getSmallSquareUrl());
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (this.isCancelled()) {
			bitmap = null;
		}
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final ImageLoaderAsyncTask imageLoaderAsyncTask = ImageLoader
					.getImageLoaderAsyncTask(imageView);
			if (this == imageLoaderAsyncTask && imageView != null) {
				imageView.setAnimation(null);
				imageView.setImageBitmap(bitmap);
				imageView.setClickable(true);
				if (cacheReference != null) {
					final ImageLoader.MemoryCache memoryCache = cacheReference
							.get();
					memoryCache.put(urlStr, bitmap);
				}
			}
		}
	}
}
