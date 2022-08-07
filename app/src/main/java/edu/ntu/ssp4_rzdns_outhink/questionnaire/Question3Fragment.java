package edu.ntu.ssp4_rzdns_outhink.questionnaire;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.activites.MainActivity;

public class Question3Fragment extends Fragment{
    ArrayList<String> mrtData = new ArrayList<String>();
    Button nextBtn;
    Button btLocation;
    EditText editText2;
    TextView textView1, textView2, textView3, textView4, textView5,textView6,textView7;
    FusedLocationProviderClient fusedLocationProviderClient;
    String addressText = "";
    String latitude = "";
    String longitude = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_question3, container, false);
        nextBtn = (Button) view.findViewById(R.id.qn3_next);
        Button backBtn = (Button) view.findViewById(R.id.qn3_back);

        Bundle bundle = this.getArguments();
        String spaddr = bundle.getString("spaddr");
        nextBtn.setEnabled(false);
        //Assign Variable
        btLocation = (Button) view.findViewById(R.id.bt_location);
       TextView textView1 = (TextView) view.findViewById(R.id.text_view1);


        editText2 = view.findViewById(R.id.edit_text2);
        textView2 = view.findViewById(R.id.text_view2);
        textView3 = view.findViewById(R.id.text_view3);
        textView4 = view.findViewById(R.id.text_view4);
        textView5 = view.findViewById(R.id.text_view5);
        //textView6 = view.findViewById(R.id.text_view6);
//        textView7 = view.findViewById(R.id.text_view7);

        String apiKey = "AIzaSyArXcFH7yxjAsYGQmqqAvXo5fa6Q5ZYg8o";
        //Initialize google places SDK
        Places.initialize(getContext(), apiKey);


        if (spaddr != null && !spaddr.isEmpty()){
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                Optional<Address> addr = geocoder.getFromLocationName(spaddr, 1).stream().findFirst();
                if (addr.isPresent())
                {
                    editText2.setText(spaddr);
                    latitude = String.valueOf(addr.get().getLatitude());
                    longitude = String.valueOf(addr.get().getLongitude());
                    nextBtn.setEnabled(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //set edittext non focusable
        editText2.setFocusable(false);
        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(getContext());
                //Start activity result
                startActivityForResult(intent, 100);
            }
        });


        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test1","test1");
                //getCurrentLocation();
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //when permission granted
                    btLocation.setEnabled(false);
                    getCurrentLocation();
                } else {
                    //when declined
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

       // readMrtData();
        //Line to Set
//        AutoCompleteTextView editText = (AutoCompleteTextView) view.findViewById(R.id.actv);
       // editText.setText(spaddr);
       // ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mrtData);
       // editText.setAdapter(myAdapter);
       // editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        String selectedItem=editText.getAdapter().getItem(position).toString();
        //        nextBtn.setEnabled(true);
        //    }
       // });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Line to get
                String mrt = editText2.getText().toString();
                bundle.putString("spaddr",mrt);
                bundle.putString("longitude",longitude);
                bundle.putString("latitude",latitude);
                Question4Fragment nextFrag= new Question4Fragment();
                nextFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = editText2.getText().toString();
                Question2Fragment nextFrag= new Question2Fragment();
                nextFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        CancellationTokenSource cToken = new CancellationTokenSource();
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cToken.getToken()).addOnSuccessListener(new OnSuccessListener<Location>() {
        @Override
            public void onSuccess(Location location) {
                //Initialize Location
                if (location != null) {
                    String longi = String.valueOf(location.getLongitude());
                    String lati = String.valueOf(location.getLatitude());

                    longitude = longi;
                    latitude = lati;
                    Log.d("long",longi);
                    Log.d("lat",lati);



                    try {
                        //Initialie geocoder
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                               location.getLatitude(), location.getLongitude(), 1
                        );

                       String country2 = String.valueOf(addresses.get(0).getCountryName());
                        String country3 = String.valueOf(addresses.get(0).getLocality());
                        String country4 = String.valueOf(addresses.get(0).getAddressLine(0));
                        Log.d("name",country2);
                        Log.d("name",country3);
                        Log.d("name",country4);

                       // AutoCompleteTextView editText2 = (AutoCompleteTextView) getView().findViewById(R.id.actv);
                        editText2.setText(country4);

                        //Set Latitude on Text View
                       // textView1.setText(Html.fromHtml("<font color ='#6200EE'><b>Latitude :</b><br></font>" + addresses.get(0).getLatitude()));
                        //Set Longitude
                       // textView2.setText(Html.fromHtml("<font color ='#6200EE'><b>Longitude :</b><br></font>" + addresses.get(0).getLongitude()));
                        //Set Country
                        textView3.setText("Country Name :" + addresses.get(0).getCountryName());
                        //Set Locality
                        textView4.setText("Locality :" + addresses.get(0).getLocality());
                        //Set address
                    textView5.setText("Address :" + addresses.get(0).getAddressLine(0));

                    nextBtn.setEnabled(true);
                    btLocation.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            else {
                   // AutoCompleteTextView editText = (AutoCompleteTextView) getView().findViewById(R.id.actv);
                    editText2.setText("Cannot detect");
                    nextBtn.setEnabled(false);
                    btLocation.setEnabled(true);
                }
            }
        });

    }


    private void readMrtData() {
        InputStream is = getResources().openRawResource(R.raw.mrt);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try {
            while ((line = reader.readLine()) != null) {
                String [] tokens = line.split(",");
                mrtData.add(tokens[0]);
                Log.d("Question3Fragment","Just created: " +tokens[0]);
            }
        } catch (IOException e) {
            Log.wtf("Question3Fragment","Error reading file on line"+line, e);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK)
        {
            //when success
            //initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);
            //set address on EditText
            editText2.setText((place.getAddress()));
            //set locality name
            editText2.setText(String.format(place.getName()));
            //textView6.setText(String.format("Locality Name : %s",place.getName()));
            //set lat & long

            String rawlatlong = String.valueOf(place.getLatLng());
            String latlong = rawlatlong.substring(10, rawlatlong.length()-1);
            String[] parts = latlong.split(",");
            latitude = parts[0];
            longitude = parts[1];
            nextBtn.setEnabled(true);
           //textView7.setText("lat :" + parts[0] + " " + "lng :" + parts[1]);
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            addressText = "";
            //when error
            //Initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            //Display toast
            Toast.makeText(getContext(),status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            nextBtn.setEnabled(false);
        }
    }
}
