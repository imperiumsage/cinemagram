package com.cinemagram;

import java.util.Locale;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnStatusChangedListener.STATUS;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TabActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(R.color.brown));
        bar.setStackedBackgroundDrawable(new ColorDrawable(R.color.green));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
    	MapView map;

    	ArcGISFeatureLayer featureLayer;

    	ArcGISTiledMapServiceLayer tileLayer;

    	GraphicsLayer graphicsLayer;

    	private int m_calloutStyle;

    	private Callout m_callout;

    	private ViewGroup calloutContent;

    	private boolean m_isMapLoaded;

    	private Graphic m_identifiedGraphic;

    	private String featureServiceURL;


        public DummySectionFragment() {

        	
        }

    	/**
    	 * Takes in the screen location of the point to identify the feature on map.
    	 * 
    	 * @param x
    	 *            x co-ordinate of point
    	 * @param y
    	 *            y co-ordinate of point
    	 */
    	private void identifyLocation(float x, float y, View rootView) {

    		// Hide the callout, if the callout from previous tap is still showing
    		// on map
    		System.out.println("Clicked! at "+x+","+y);
    		Log.d("Sai","clicked at "+x+","+y);
    		if (m_callout.isShowing()) {
    			m_callout.hide();
    		}

    		// Find out if the user tapped on a feature
    		SearchForFeature(x, y);

    		// If the user tapped on a feature, then display information regarding
    		// the feature in the callout
    		if (m_identifiedGraphic != null) {
    			Point mapPoint = map.toMapPoint(x, y);
    			// Show Callout
    			Log.d("Sai","showing callout");
    			ShowCallout(m_callout, m_identifiedGraphic, mapPoint, rootView);
    		} else {
    			Log.d("Sai","Not found");
    		}
    	}

    	/**
    	 * Sets the value of m_identifiedGraphic to the Graphic present on the
    	 * location of screen tap
    	 * 
    	 * @param x
    	 *            x co-ordinate of point
    	 * @param y
    	 *            y co-ordinate of point
    	 */
    	private void SearchForFeature(float x, float y) {

    		Point mapPoint = map.toMapPoint(x, y);
    		Log.d("Sai",mapPoint.toString());

    		if (mapPoint != null) {

    			for (Layer layer : map.getLayers()) {
    				if (layer == null)
    					continue;

    				if (layer instanceof ArcGISFeatureLayer) {
    					ArcGISFeatureLayer fLayer = (ArcGISFeatureLayer) layer;
    					// Get the Graphic at location x,y
    					m_identifiedGraphic = GetFeature(fLayer, x, y);
    				} else
    					continue;
    			}
    		}
    	}

    	/**
    	 * Returns the Graphic present the location of screen tap
    	 * 
    	 * @param fLayer
    	 * @param x
    	 *            x co-ordinate of point
    	 * @param y
    	 *            y co-ordinate of point
    	 * @return Graphic at location x,y
    	 */
    	private Graphic GetFeature(ArcGISFeatureLayer fLayer, float x, float y) {

    		// Get the graphics near the Point.
    		Log.d("Sai","In getfeature");
    		int[] ids = fLayer.getGraphicIDs(x,y,10,1);
    		Log.d("Sai","count:"+fLayer.getGraphicIDs().length);
    		if (ids == null || ids.length == 0) {
    			return null;
    		}
    		
    		Graphic g = fLayer.getGraphic(ids[0]);
    		Log.d("Sai",g.toString());
    		System.out.println(g);
    		return g;
    	}

    	/**
    	 * Shows the Attribute values for the Graphic in the Callout
    	 * 
    	 * @param calloutView
    	 * @param graphic
    	 * @param mapPoint
    	 */
    	private void ShowCallout(Callout calloutView, Graphic graphic,
    			Point mapPoint, View rootView) {
    		Log.d("Sai","In show callout");
    		// Get the values of attributes for the Graphic
    		String title = (String) graphic.getAttributeValue("Title");
    		String releaseYear = (String) ((Integer)graphic.getAttributeValue("Release_Year")+"");
    		String locations = (String) graphic.getAttributeValue("Locations");
    		Log.d("Sai","Title:"+title);

    		// Set callout properties
    		calloutView.setCoordinates(mapPoint);
    		calloutView.setStyle(m_calloutStyle);
    		calloutView.setMaxWidth(1000);

    		// Compose the string to display the results
    		StringBuilder header = new StringBuilder();
    		header.append(title);
    		header.append(", ");
    		header.append(releaseYear);

    		TextView calloutTextLine1 = (TextView) rootView.findViewById(R.id.header);
    		calloutTextLine1.setText(header);
    		calloutTextLine1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	startActivity(new Intent(getActivity().getApplicationContext(), BaseActivity.class));
                }
            });

    		// Compose the string to display the results
    		StringBuilder location = new StringBuilder();
    		location.append("Location: ");
    		location.append(locations);

    		TextView calloutTextLine2 = (TextView) rootView.findViewById(R.id.location);
    		calloutTextLine2.setText(location);
    		calloutView.setContent(calloutContent);
    		map.centerAt(mapPoint, false);
    		calloutView.refresh();

    		calloutView.show();
    		
    		
    	}        
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	Log.d("Sai","onCreateView called");
        	Log.d("Sai","featureServiceURL:"+featureServiceURL);
            final View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            int position = getArguments().getInt(ARG_SECTION_NUMBER);
            
        	switch(position) {
	        	case 1: featureServiceURL = getActivity().getResources().getString(
	    				R.string.featureServiceURL1950);
	        			break;
	        	case 2: featureServiceURL = getActivity().getResources().getString(
	    				R.string.featureServiceURL1970);
						break;
	        	case 3: featureServiceURL = getActivity().getResources().getString(
	    				R.string.featureServiceURL1990);
						break;
				default:featureServiceURL = getActivity().getResources().getString(
		    				R.string.featureServiceURL1990);
						break;
	    	
	    	}
            //startActivity(new Intent(getActivity().getApplicationContext(), BaseActivity.class));
    		map = (MapView) rootView.findViewById(R.id.map);

    		map.enableWrapAround(true);
    		
    		Log.d("Sai","map found and not null");
    		
    		Drawable myIcon = getActivity().getResources().getDrawable(R.drawable.map_pin);
    		Bitmap bitmap = ((BitmapDrawable) myIcon).getBitmap();
    		// Scale it to 50 x 50
    		Drawable d = new BitmapDrawable(getActivity().getResources(), Bitmap.createScaledBitmap(bitmap, 25, 25, true));

    		// Add Tile layer to the MapView
    		tileLayer = new ArcGISTiledMapServiceLayer(getActivity().getResources()
    				.getString(R.string.tileServiceURL));
    		map.addLayer(tileLayer);
    		// Add Feature layer to the MapView
    		featureLayer = new ArcGISFeatureLayer(featureServiceURL,
    				ArcGISFeatureLayer.MODE.ONDEMAND);
    		// create a symbol for renderer
    		Log.d("Sai","Trying to create a symbol");
    		SimpleRenderer simRenderer = null;
    			
    		PictureMarkerSymbol pms = new PictureMarkerSymbol(d);
    		// instantiate a simple renderer with the symbol created above
    		Log.d("Sai","Created:"+pms.toString());
    		simRenderer = new SimpleRenderer(pms);
    		Log.d("Sai","Adding renderer to layer");
    		// add the renderer to the graphics layer
    		featureLayer.setRenderer(simRenderer);
    		map.addLayer(featureLayer);
    		// Add Graphics layer to the MapView
    		graphicsLayer = new GraphicsLayer();

    		map.addLayer(graphicsLayer);

    		// Set the initial extent of the map
    		final Envelope initExtent = new Envelope(-13644162.422956869,4534148.971590092,-13615995.37803382,4556697.894934189);
    		map.setExtent(initExtent);
    		

    		// Get the MapView's callout from xml->identify_calloutstyle.xml
    		m_calloutStyle = R.xml.identify_calloutstyle;
    		m_callout = map.getCallout();
    		// Get the layout for the Callout from
    		// layout->identify_callout_content.xml
    		calloutContent = (ViewGroup) inflater.inflate(
    				R.layout.identify_callout_content, null);
    		m_callout.setContent(calloutContent);

    		map.setOnStatusChangedListener(new OnStatusChangedListener() {

    			private static final long serialVersionUID = 1L;

    			public void onStatusChanged(Object source, STATUS status) {
    				// Check to see if map has successfully loaded
    				if ((source == map) && (status == STATUS.INITIALIZED)) {
    					// Set the flag to true
    					m_isMapLoaded = true;
    				}
    			}
    		});

    		map.setOnSingleTapListener(new OnSingleTapListener() {

    			private static final long serialVersionUID = 1L;

    			public void onSingleTap(float x, float y) {

    				if (m_isMapLoaded) {
    					// If map is initialized and Single tap is registered on
    					// screen
    					// identify the location selected
    					identifyLocation(x, y, rootView);
    				}
    			}
    		});

            return rootView;
        }
    }

}
