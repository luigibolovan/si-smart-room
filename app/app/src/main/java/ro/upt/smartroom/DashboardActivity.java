package ro.upt.smartroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Optional;

import static ro.upt.smartroom.LoginActivity.LOGGED_IN;
import static ro.upt.smartroom.LoginActivity.LOGIN_PREFERENCES;
import static ro.upt.smartroom.LoginActivity.USER_EMAIL;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseReference mDoorLockReference;
    private DatabaseReference mTemperatureReference;
    private DatabaseReference mHumidityReference;
    private DatabaseReference mLightsReference;
    private final static String DATABASE_ERROR_TAG  = "FirebaseDBConnection";
    private final static String INVALID_VALUE_TAG   = "InvalidFieldValue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent mSourceIntent = getIntent();
        TextView info = findViewById(R.id.tv_logInfo);
        info.setText("Logged in as " + mSourceIntent.getStringExtra(USER_EMAIL));

        initDatabaseConnections();

        handleDoorLockData();
        handleTemperatureData();
        handleHumidityData();
        handleLightsData();
    }

    private void initDatabaseConnections() {
        FirebaseDatabase mDatabase  = FirebaseDatabase.getInstance();
        mDoorLockReference          = mDatabase.getReference("auth/isLocked");
        mTemperatureReference       = mDatabase.getReference("air/temperature");
        mHumidityReference          = mDatabase.getReference("air/humidity");
        mLightsReference            = mDatabase.getReference("lights");
    }

    @Override
    protected void onResume() {
        super.onResume();

        handleDoorLockData();
        handleTemperatureData();
        handleHumidityData();
        handleLightsData();

        handleLogOut();
    }

    private void handleLogOut() {
        ImageView logoutButton = findViewById(R.id.iv_logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markUserLogOut();
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private void markUserLogOut() {
        SharedPreferences loginPreferences          = getSharedPreferences(LOGIN_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor loginPrefsEditor   = loginPreferences.edit();
        loginPrefsEditor.putBoolean(LOGGED_IN, false);
        loginPrefsEditor.putString(USER_EMAIL, null);
        loginPrefsEditor.apply();
    }

    private void handleLightsData() {
        final Switch lightsSwitch       = findViewById(R.id.switch_lights);
        mLightsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long lightsStatus = dataSnapshot.getValue(Long.class);
                if(lightsStatus == 0){
                    lightsSwitch.setChecked(false);
                    setDarkBackground();
                }else if(lightsStatus == 1){
                    setLightBackground();
                    lightsSwitch.setChecked(true);
                }else{
                    Log.e(INVALID_VALUE_TAG, "Invalid value for lights");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(DATABASE_ERROR_TAG, "Error occurred - lights data");
            }
        });

        lightsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mLightsReference.setValue(1);
                }else{
                    mLightsReference.setValue(0);
                }
            }
        });
    }

    private void handleHumidityData() {
        final TextView humidityText     = findViewById(R.id.tv_humidityValue);
        mHumidityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double humidity = dataSnapshot.getValue(Double.class);
                humidityText.setText(humidity.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(DATABASE_ERROR_TAG, "Error occurred - humidity data");
            }
        });
    }

    private void handleTemperatureData() {
        final TextView temperatureText  = findViewById(R.id.tv_temperatureValue);
        mTemperatureReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double temperature = dataSnapshot.getValue(Double.class);
                if(temperature >= (double) 20){
                    setWarmTempCardView();
                }else{
                    setCoolTempCardView();
                }
                temperatureText.setText(temperature.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(DATABASE_ERROR_TAG, "Error occurred - temperature data");
            }
        });
    }

    private void handleDoorLockData() {
        final Switch doorLockSwitch     = findViewById(R.id.switch_doorLock);
        mDoorLockReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long doorLock = dataSnapshot.getValue(Long.class);
                if(doorLock == 0){
                    doorLockSwitch.setChecked(false);
                }else if(doorLock == 1){
                    doorLockSwitch.setChecked(true);
                }else{
                    Log.e(INVALID_VALUE_TAG, "Invalid value for lock");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(DATABASE_ERROR_TAG, "Error occurred - door lock data");
            }
        });

        doorLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDoorLockReference.setValue(1);
                }else{
                    mDoorLockReference.setValue(0);
                }
            }
        });
    }

    private void setDarkBackground() {
        RelativeLayout lightsLayout         = findViewById(R.id.rlayout_lightsLayout);
        Drawable backgroundDark             = getDrawable(R.drawable.lights_card_dark_background);
        TextView cardTitleText              = findViewById(R.id.tv_lightsTitle);
        TextView cardDescriptionText        = findViewById(R.id.tv_lightsDescription);

        lightsLayout.setBackground(backgroundDark);
        cardTitleText.setTextColor(getColor(R.color.colorWhite));
        cardDescriptionText.setTextColor(getColor(R.color.colorWhite));
    }

    private void setLightBackground() {
        RelativeLayout lightsLayout         = findViewById(R.id.rlayout_lightsLayout);
        Drawable backgroundLight            = getDrawable(R.drawable.lights_card_light_background);
        TextView cardTitleText              = findViewById(R.id.tv_lightsTitle);
        TextView cardDescriptionText        = findViewById(R.id.tv_lightsDescription);

        lightsLayout.setBackground(backgroundLight);
        cardTitleText.setTextColor(getColor(R.color.colorDarkBlue));
        cardDescriptionText.setTextColor(getColor(R.color.colorDarkBlue));
    }

    private void setWarmTempCardView() {
        RelativeLayout temperatureLayout    = findViewById(R.id.rlayout_temperatureCard);
        Drawable backgroundWarm             = getDrawable(R.drawable.temperature_card_warm_background);
        TextView cardTitleText              = findViewById(R.id.tv_Temperature);
        TextView cardDescriptionText        = findViewById(R.id.tv_temperatureCardDescription);
        TextView degreesText                = findViewById(R.id.tv_degrees);
        TextView temperatureValueText       = findViewById(R.id.tv_temperatureValue);

        temperatureLayout.setBackground(backgroundWarm);
        cardTitleText.setTextColor(getColor(R.color.colorDarkRed));
        cardDescriptionText.setTextColor(getColor(R.color.colorDarkRed));
        degreesText.setTextColor(getColor(R.color.colorDarkRed));
        temperatureValueText.setTextColor(getColor(R.color.colorDarkRed));
    }

    private void setCoolTempCardView() {
        RelativeLayout temperatureLayout    = findViewById(R.id.rlayout_temperatureCard);
        Drawable backgroundCool             = getDrawable(R.drawable.temperature_card_cool_background);
        TextView cardTitleText              = findViewById(R.id.tv_Temperature);
        TextView cardDescriptionText        = findViewById(R.id.tv_temperatureCardDescription);
        TextView degreesText                = findViewById(R.id.tv_degrees);
        TextView temperatureValueText       = findViewById(R.id.tv_temperatureValue);

        temperatureLayout.setBackground(backgroundCool);
        cardTitleText.setTextColor(getColor(R.color.colorDarkBlue));
        cardDescriptionText.setTextColor(getColor(R.color.colorDarkBlue));
        degreesText.setTextColor(getColor(R.color.colorDarkBlue));
        temperatureValueText.setTextColor(getColor(R.color.colorDarkBlue));
    }
}
