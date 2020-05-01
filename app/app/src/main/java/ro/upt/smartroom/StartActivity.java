package ro.upt.smartroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import static ro.upt.smartroom.LoginActivity.LOGGED_IN;
import static ro.upt.smartroom.LoginActivity.LOGIN_PREFERENCES;
import static ro.upt.smartroom.LoginActivity.USER_EMAIL;

public class StartActivity extends AppCompatActivity {

    private CountDownTimer mTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            // do nothing
        }

        @Override
        public void onFinish() {
            Intent mNextActivityIntent;

            if(isLoggedIn()){
                mNextActivityIntent = new Intent(StartActivity.this, DashboardActivity.class);
                mNextActivityIntent.putExtra(USER_EMAIL, getEmail());
            }else {
                mNextActivityIntent = new Intent(StartActivity.this, LoginActivity.class);
            }
            startActivity(mNextActivityIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mTimer.start();
    }

    private String getEmail() {
        SharedPreferences loginPreferences = getSharedPreferences(LOGIN_PREFERENCES, MODE_PRIVATE);
        return loginPreferences.getString(USER_EMAIL, null);
    }

    private boolean isLoggedIn() {
        SharedPreferences loginPreferences = getSharedPreferences(LOGIN_PREFERENCES, MODE_PRIVATE);
        return loginPreferences.getBoolean(LOGGED_IN, false);
    }
}
