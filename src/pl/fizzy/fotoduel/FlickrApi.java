package pl.fizzy.fotoduel;

import java.io.IOException;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * Simple api for flickr.
 * 
 * @author Max
 * 
 */
public class FlickrApi {
	private static final String FLICKR_APIKEY = "fa770047effbb6f328b978ef81c7d40a";
	private static final String FLICKR_SECRET = "6b7e67ea51c37641";
	private int PER_PAGE = 20;
	private Flickr flickr;

	public FlickrApi() {
		flickr = new Flickr(FLICKR_APIKEY);
	}

	/**
	 * Getting random <code>Photo</code> using flickr.api service
	 * 
	 * @return
	 * @throws IOException
	 * @throws FlickrException
	 * @throws JSONException
	 */
	public Photo getRandomPhoto() throws IOException, FlickrException,
			JSONException {

		PhotosInterface photosInterface = flickr.getPhotosInterface();

		// setting search parameters
		/*
		 * SearchParameters searchParams = new SearchParameters();
		 * searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC); String[]
		 * tags = new String[] { "duel" }; searchParams.setTags(tags); PhotoList
		 * photoList = photosInterface.search(searchParams, PER_PAGE, 0);
		 */

		// getting search results
		PhotoList photoList = photosInterface.getRecent(null, PER_PAGE, 0);
		Photo photo = (Photo) photoList.get(randomize(0, PER_PAGE));
		photo = photosInterface.getInfo(photo.getId(), FLICKR_SECRET);

		return photo;
	}

	private int randomize(int minimum, int maximum) {
		return minimum + (int) (Math.random() * maximum);
	}
}
