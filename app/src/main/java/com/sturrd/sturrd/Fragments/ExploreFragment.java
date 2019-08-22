package com.sturrd.sturrd.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sturrd.sturrd.DateLocation.DateLocAdapter;
import com.sturrd.sturrd.DateLocation.DateLocObject;
import com.sturrd.sturrd.LatLngObject;
import com.sturrd.sturrd.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ExploreFragment extends Fragment {

    private RecyclerView recyclerView;

    //adapter object
    private RecyclerView.Adapter adapter;

    //database reference
    private DatabaseReference mPlaces, placesDb;

    //progress dialog
    private ProgressDialog progressDialog;

    //list to hold all the uploaded images
    private List<DateLocObject> dateLocObjects;

    private String currentUserID, currentUId;
    private FirebaseAuth mAuth;
    private View view;
    private GoogleMap mMap;
    private RecyclerView mLocationCards;
    private RecyclerView.Adapter mLocationCardsAdapter;
    private FusedLocationProviderClient client;
    private LinearLayoutManager mLocationCardsLayoutManager;
    private ChildEventListener mChildEventListener;
    Marker marker;

    public ExploreFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_explore, container, false);


        mPlaces = FirebaseDatabase.getInstance().getReference("Places");
        placesDb = FirebaseDatabase.getInstance().getReference("Places");
        mPlaces.push().setValue(marker);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        recyclerView = (RecyclerView) view.findViewById(R.id.dating_locations_recycler);
        recyclerView.setHasFixedSize(true);
        mLocationCardsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLocationCardsLayoutManager);

        progressDialog = new ProgressDialog(getContext());

        dateLocObjects = new ArrayList<>();

        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        placesDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    DateLocObject image = postSnapshot.getValue(DateLocObject.class);
                    dateLocObjects.add(image);
                }
                //creating adapter
                adapter = new DateLocAdapter(getContext(), dateLocObjects);

                //adding adapter to recyclerview
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });




    mAuth = FirebaseAuth.getInstance();

        client = LocationServices.getFusedLocationProviderClient(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(final GoogleMap mMap) {

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    getContext(), R.raw.style_json));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            final double longitude = location.getLongitude();
                            final double latitude = location.getLatitude();

                            String latitudeString = String.valueOf(latitude);
                            String longitudeString = String.valueOf(longitude);

                            //usersDb.child("latitude").child(latitudeString).setValue(true);
                            //usersDb.child("longitude").child(longitudeString).setValue(true);

                            Map userLatLng = new HashMap();
                            userLatLng.put("latitude", latitudeString);
                            userLatLng.put("longitude", longitudeString);

                            DatabaseReference mLatLng = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

                            mLatLng.updateChildren(userLatLng);

                            CameraPosition googlePlex = CameraPosition.builder()
                                    .target(new LatLng(latitude, longitude))
                                    .zoom(17)
                                    .bearing(0)
                                    .build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_blue_dot)));


                        }
                    }
                });
                mPlaces.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()){
                            LatLngObject user = s.getValue(LatLngObject.class);
                            LatLng location=new LatLng(user.Latitude, user.Longitude);
                            mMap.addMarker(new MarkerOptions().position(location).title(user.name)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_marker));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }





        });




        return view;
    }




    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    private ArrayList<LatLngObject> resultsPlaceImages = new ArrayList<>();
    private List<LatLngObject> getDataSetLocations(){

        return resultsPlaceImages;
    }


}