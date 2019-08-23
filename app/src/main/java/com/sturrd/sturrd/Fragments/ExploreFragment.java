package com.sturrd.sturrd.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private DatabaseReference mPlaces, placesDb, usersDb;

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
        usersDb = FirebaseDatabase.getInstance().getReference("Users");
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

                            mLatLng.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                                    final CameraPosition googlePlex = CameraPosition.builder()
                                            .target(new LatLng(latitude, longitude))
                                            .zoom(17)
                                            .bearing(0)
                                            .build();

                                    Glide.with(getContext())
                                            .asBitmap().load(profileImageUrl).apply(RequestOptions.circleCropTransform().override(150, 150))
                                            .listener(new RequestListener<Bitmap>() {
                                                          @Override
                                                          public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                                              Toast.makeText(getContext(),getResources().getString(R.string.unexpected_error_occurred_try_again),Toast.LENGTH_SHORT).show();
                                                              return false;
                                                          }

                                                          @Override
                                                          public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                                              mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

                                                              mMap.addMarker(new MarkerOptions()
                                                                      .position(new LatLng(latitude, longitude))
                                                                      .title("Me")
                                                                      .icon(BitmapDescriptorFactory.fromBitmap(BitmapMarker(bitmap))));
                                                              return false;
                                                          }
                                                      }
                                            ).submit();



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });







                        }
                    }
                });
                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()){
                            final LatLngObject user = s.getValue(LatLngObject.class);
                            final LatLng location=new LatLng(user.Latitude, user.Longitude);
                            String profileImageUrl = user.profileImageUrl;

                            Glide.with(getContext())
                                    .asBitmap().load(profileImageUrl).apply(RequestOptions.circleCropTransform().override(150, 150))
                                    .listener(new RequestListener<Bitmap>() {
                                                  @Override
                                                  public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                                      Toast.makeText(getContext(),getResources().getString(R.string.unexpected_error_occurred_try_again),Toast.LENGTH_SHORT).show();
                                                      return false;
                                                  }

                                                  @Override
                                                  public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {




                                                      mMap.addMarker(new MarkerOptions()
                                                              .position(location)
                                                              .title(user.name)
                                                              .icon(BitmapDescriptorFactory.fromBitmap(BitmapMarker(bitmap))));
                                                      return false;
                                                  }
                                              }
                                    ).submit();
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

    public Bitmap BitmapMarker(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.parseColor("#9EFF4664"));
        p.setStrokeWidth(10);
        //p.setShadowLayer(12, 0, 0, Color.parseColor("#CE504F4F"));
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
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