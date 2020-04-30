package ro.upt.smartroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class DashboardActivity extends AppCompatActivity {
    private final static String USER_EMAIL = "UserEmail";
    private Intent mSourceIntent;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDoorLockReference;
    private DatabaseReference mTemperatureReference;
    private DatabaseReference mHumidityReference;
    private DatabaseReference mLightsReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initDatabaseConnections();
        mSourceIntent = getIntent();
        TextView info = (TextView)findViewById(R.id.tv_logInfo);
        info.setText("Logged in as " + mSourceIntent.getStringExtra(USER_EMAIL));
    }

    private void initDatabaseConnections() {
        mDatabase               = FirebaseDatabase.getInstance();
        mDoorLockReference      = mDatabase.getReference("auth/isLocked");
        mTemperatureReference   = mDatabase.getReference("air/temperature");
        mHumidityReference      = mDatabase.getReference("air/humidity");
        mLightsReference        = mDatabase.getReference("lights");
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Switch doorLockSwitch     = (Switch)findViewById(R.id.switch_doorLock);
        final TextView temperatureText  = (TextView)findViewById(R.id.tv_temperatureValue);
        final TextView humidityText     = (TextView)findViewById(R.id.tv_humidityValue);
        final Switch lightsSwitch       = (Switch)findViewById(R.id.switch_lights);

        mDoorLockReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long doorLock = dataSnapshot.getValue(Long.class);
                if(doorLock.equals("0")){
                    doorLockSwitch.setChecked(false);
                }else if(doorLock.equals("1")){
                    doorLockSwitch.setChecked(true);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // failed to read
            }
        });


        mTemperatureReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double temperature = dataSnapshot.getValue(Double.class);
                if(temperature >= (double) 20){
                    //set background to warm colors
                }else{
                    //set background to cold colors
                }
                temperatureText.setText(temperature.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // failed to read
            }
        });

        mHumidityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double humidity = dataSnapshot.getValue(Double.class);
                humidityText.setText(humidity.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // failed to read
            }
        });

        mLightsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long lightsStatus = dataSnapshot.getValue(Long.class);
                if(lightsStatus.equals("0")){
                    lightsSwitch.setChecked(false);
                    // set background to darker colours
                }else if(lightsStatus.equals("1")){
                    // set background to light colours
                    lightsSwitch.setChecked(true);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // failed to read
            }
        });

        ImageView logoutButton = (ImageView) findViewById(R.id.iv_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}
