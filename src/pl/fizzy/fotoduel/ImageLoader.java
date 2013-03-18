package pl.fizzy.fotoduel;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * ImageLoader is <code>ImageLoaderAsyncTask</code> constructor. Decides if
 * image must be loaded or download.
 * 
 * @author Max
 * 
 */
public class ImageLoader {

	private Resources resources;
	MemoryCache cache;

	public ImageLoader(Resources res) {
		resources = res;
		cache = null;
	}

	/**
	 * Sets download image and animation on <i>imageView</i>, creates
	 * <i>ImageLoaderAsyncTask</i> to load random photo from flickr or database.
	 * 
	 * @param imageView
	 * @param intent
	 *            - intent for passing data back
	 * @param index
	 *            - id of imageView
	 */
	public void random(ImageView imageView, Intent intent, int index) {
		if (cancelPotentialWork(new String(), imageView)) {
			final ImageLoaderAsyncTask task = new ImageLoaderAsyncTask(
					imageView, intent, index);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(resources,
					BitmapFactory.decodeResource(resources,
							R.drawable.ic_launcher), task);
			imageView.setImageDrawable(asyncDrawable);
			setAnimation(imageView);
			task.execute(new String());
		}
	}

	/**
	 * Downloads and sets image from <i>url</i> on <i>imageView</i>.
	 * 
	 * @param imageView
	 * @param url
	 *            - URL address of photo to download
	 */
	public void fromUrl(ImageView imageView, String url, Boolean animate) {
		if (cancelPotentialWork(url, imageView)) {
			if (cache != null && cache.containsKey(url)) {
				Log.d("CACHE", "Bitmap loaded from cache");
				if (cache.get(url) == null) {
					cache.remove(url);
					fromUrl(imageView, url, animate);
				} else {
					imageView.setImageBitmap(cache.get(url));
				}
			} else {
				final ImageLoaderAsyncTask task = new ImageLoaderAsyncTask(
						cache, imageView);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(
						resources, BitmapFactory.decodeResource(resources,
								R.drawable.ic_launcher), task);
				imageView.setImageDrawable(asyncDrawable);

				if (animate)
					setAnimation(imageView);
				task.execute(url);
			}
		}
	}

	/**
	 * Cancels potential download of <code>url</code> image
	 * 
	 * @param url
	 * @param imageView
	 * @return
	 */
	public static boolean cancelPotentialWork(String url, ImageView imageView) {
		final ImageLoaderAsyncTask imageLoaderAsyncTask = getImageLoaderAsyncTask(imageView);

		if (imageLoaderAsyncTask != null) {
			final String bitmapUrl = imageLoaderAsyncTask.getUrlStr();
			if (bitmapUrl != url) {
				imageLoaderAsyncTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Getting task that is using (and setting image on) <code>imageView<code>
	 * 
	 * @param imageView
	 * @return
	 */
	public static ImageLoaderAsyncTask getImageLoaderAsyncTask(
			ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getImageLoaderAsyncTask();
			}
		}
		return null;
	}

	/**
	 * Sets custom animation on <i>imageView</i>.
	 * 
	 * @param imageView
	 */
	private void setAnimation(ImageView imageView) {
		RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(1000);
		imageView.startAnimation(anim);
	}

	/**
	 * 
	 * @author Max
	 * 
	 */
	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<ImageLoaderAsyncTask> imageLoaderTaskReference;

		public AsyncDrawable(Resources resources, Bitmap bitmap,
				ImageLoaderAsyncTask imageLoaderTask) {
			super(resources, bitmap);
			imageLoaderTaskReference = new WeakReference<ImageLoaderAsyncTask>(
					imageLoaderTask);
		}

		public ImageLoaderAsyncTask getImageLoaderAsyncTask() {
			return imageLoaderTaskReference.get();
		}
	}

	/**
	 * Initializing cache
	 */
	public void prepareCache() {
		Log.d("CACHE", "Cache init");
		cache = new MemoryCache();
	}

	/**
	 * Releasing cache
	 */
	public void releaseCache() {
		Log.d("CACHE", "Cache gone");
		cache.clear();
	}

	/**
	 * Simple memory cache for keeping bitmaps and theirs urls. This cache must
	 * be released when not needed.
	 * 
	 * @author Max
	 * 
	 */
	public class MemoryCache {
		private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

		public Bitmap get(String id) {
			if (!cache.containsKey(id))
				return null;
			Bitmap b = cache.get(id);
			return b;
		}

		public boolean containsKey(String id) {
			return cache.containsKey(id);
		}

		public void put(String id, Bitmap bitmap) {
			Log.d("CACHE", "Cache new elem");
			cache.put(id, bitmap);
		}

		public void clear() {
			cache.clear();
		}

		public void remove(String id) {
			Log.d("CACHE", "Cache new elem");
			cache.remove(id);
		}
	}
}
