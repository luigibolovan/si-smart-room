package ro.upt.smartroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class StartActivity extends AppCompatActivity {

    private CountDownTimer mTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Intent dashboardIntent = new Intent(StartActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mTimer.start();
    }
}
