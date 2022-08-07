package edu.ntu.ssp4_rzdns_outhink.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

//import edu.ntu.ssp4_rzdns_outhink.questionnaire.MainQuestionnaire;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.fragments.AboutUsFragment;
import edu.ntu.ssp4_rzdns_outhink.fragments.AgendaFragment;
import edu.ntu.ssp4_rzdns_outhink.fragments.ExploreFragment;
import edu.ntu.ssp4_rzdns_outhink.fragments.FirstTimeUserScreen;
import edu.ntu.ssp4_rzdns_outhink.fragments.ItineraryFragment;
import edu.ntu.ssp4_rzdns_outhink.questionnaire.MainQuestionnaireFragment;

//
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{

    //    PROFILE FOR NAV_HEADER VARIABLES
    TextView userName,userEmail;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    ImageView profileImage;


    //    NAVIGATION VARIABLES
    private Toolbar toolbar;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    //    NORMAL VARIABLES
    String checkEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        NAVIGATION DRAWER PORTION
        Toolbar toolbar = findViewById(R.id.toolbar);
//        set toolbar as the action bar
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.nav_open, R.string.nav_close);

//        Takes care of the animation when opening the drawer
        drawer.addDrawerListener(toggle);
        toggle.syncState();

       // PROFILE(NAV_HEADER) PORTION

        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        userEmail = headerView.findViewById(R.id.Email);
        profileImage = headerView.findViewById(R.id.profilepict);

        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.explore:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ExploreFragment()).commit();
                break;
            case R.id.question:
                MainQuestionnaireFragment mqf = new MainQuestionnaireFragment();
                Bundle b = new Bundle();
                b.putString("useremail",userEmail.getText().toString());
                mqf.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        mqf).commit();
                break;
            case R.id.agenda:
                AgendaFragment af = new AgendaFragment();
                Bundle ab = new Bundle();
                ab.putString("useremail",userEmail.getText().toString());
                af.setArguments(ab);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        af).commit();
                break;
            case R.id.itinerary:
                ItineraryFragment itf = new ItineraryFragment();
                Bundle ib = new Bundle();
                ib.putString("useremail",userEmail.getText().toString());
                itf.setArguments(ib);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        itf).commit();
                break;
            case R.id.aboutus:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutUsFragment()).commit();
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()){
                                    gotoHomeActivity();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Session not close",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
//        item will be selected
        return true;
    }

    //    When click back button when the navigation bar is open, want to stay on the activity
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



//    PROFILE(NAV_HEADER) PORTION AND LOGOUT
    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            userName.setText(account.getDisplayName());
            userEmail.setText(account.getEmail());

            SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
            editor.putString("useremail",account.getEmail());
            editor.putString("email", EncodeString(account.getEmail()));
            editor.apply();

            try{
                Glide.with(this).load(account.getPhotoUrl()).into(profileImage);
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),"image not found",Toast.LENGTH_LONG).show();
            }

            //Checking For User First Time
            checkEmail = EncodeString(userEmail.getText().toString());
            Query reff= FirebaseDatabase.getInstance().getReference().child("questionnaire")
                    .orderByChild("useremail")
                    .equalTo(checkEmail);

            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists())
                    {
                        toggle.setDrawerIndicatorEnabled(false);
                        Bundle bundle = new Bundle();
                        bundle.putString("useremail",checkEmail);
                        FirstTimeUserScreen nextFrag = new FirstTimeUserScreen();
                        nextFrag.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                nextFrag).commit();

                    }
                    else
                    {
                        toggle.setDrawerIndicatorEnabled(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ExploreFragment()).commit();
//
                        navigationView.getMenu().getItem(0).setChecked(true);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else{
            gotoMainActivity();
        }
    }
    private void gotoHomeActivity(){
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    private void gotoMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onStateNotSaved();
    }
}