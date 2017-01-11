package com.sincere.kboss.worker;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.sincere.kboss.ActivityTempl;
import com.sincere.kboss.R;
import com.sincere.kboss.stdata.STJobWorker;
import com.sincere.kboss.worker.FragmentTempl;

import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;


public class MapViewActivity extends ActivityTempl implements MapView.OpenAPIKeyAuthenticationResultListener, MapView.MapViewEventListener {
    private MapView mapView;
    double m_longitude,m_latitude;
    String address = "";

    public final static String ARG_JOB = "job";
    STJobWorker workerjob;

    TextView lblTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map_view);

        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//modified by Adonis

        MapLayout mapLayout = new MapLayout(this);
        mapView = mapLayout.getMapView();

//        MapView mapView = new MapView(this);
//        mMapView = (MapView)findViewById(R.id.map_view);
        mapView.setDaumMapApiKey("c66045c43200c1595d8b9c687b45c55b");
        mapView.setOpenAPIKeyAuthenticationResultListener(this);
        mapView.setMapViewEventListener(this);
        mapView.setMapType(MapView.MapType.Standard);

        try {
            m_longitude = getIntent().getDoubleExtra("logitude",127.005515);
            m_latitude = getIntent().getDoubleExtra("latitude", 37.537229);
            address = getIntent().getStringExtra("position");

        }catch (Exception e) {

        }
        if(m_latitude == 0) m_latitude = 37.537229;
        if(m_longitude == 0) m_longitude =  127.005515;

        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblTitle.setText(address);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapLayout);
    }

    private void controlMapTile(int which) {
        switch (which) {
            case 0: // Standard
            {
                mapView.setMapType(MapView.MapType.Standard);
            }
            break;
            case 1: // Satellite
            {
                mapView.setMapType(MapView.MapType.Satellite);
            }
            break;
            case 2: // Hybrid
            {
                mapView.setMapType(MapView.MapType.Hybrid);
            }
            break;
            case 3: // HD Map Tile On/Off
            {
                if (mapView.getMapTileMode() == MapView.MapTileMode.HD2X) {
                    //Set to Standard Mode
                    mapView.setMapTileMode(MapView.MapTileMode.Standard);
                } else if (mapView.getMapTileMode() == MapView.MapTileMode.HD) {
                    //Set to HD 2X Mode
                    mapView.setMapTileMode(MapView.MapTileMode.HD2X);
                } else {
                    //Set to HD Mode
                    mapView.setMapTileMode(MapView.MapTileMode.HD);
                }
            }
            break;
            case 4: // Clear Map Tile Cache
            {
                MapView.clearMapTilePersistentCache();
            }
            break;
        }
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        // MapView had loaded. Now, MapView APIs could be called safely.
        MapPOIItem poiItem1 = new MapPOIItem();
        poiItem1.setItemName(address);
        poiItem1.setMapPoint(MapPoint.mapPointWithGeoCoord(m_latitude,m_longitude));
        poiItem1.setMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(poiItem1);

        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(m_latitude,m_longitude), 2, true);
    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int resultCode, String resultMessage) {
        Log.i("",	String.format("Open API Key Authentication Result : code=%d, message=%s", resultCode, resultMessage));
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

    }
}
