package ro.upt.smartroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    public final static String LOGIN_PREFERENCES    = "LoginPreferences";
    private final static String USER_TOKEN          = "UIDToken";
    private final static String USER_EMAIL          = "UserEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button loginButton                  = (Button)      findViewById(R.id.btn_login);
        final EditText emailEditText        = (EditText)    findViewById(R.id.et_username);
        final EditText passwordEditText     = (EditText)    findViewById(R.id.et_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString        = emailEditText.getText().toString();
                final String passwordString     = passwordEditText.getText().toString();
                checkEmailAndPassword(emailString, passwordString);
                mFirebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                    storeUserToken(user);
                                    changeToDashboard(user);
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Failed to log in", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void changeToDashboard(FirebaseUser user) {
        Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
        dashboardIntent.putExtra(USER_EMAIL, user.getEmail());
        startActivity(dashboardIntent);
        finish();
    }

    private void storeUserToken(FirebaseUser user) {
        SharedPreferences loginSharedPreferences = getApplicationContext().getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor loginSharedPrefsEditor = loginSharedPreferences.edit();
        loginSharedPrefsEditor.putString(USER_TOKEN, user.getUid());
        loginSharedPrefsEditor.putString(USER_EMAIL, user.getEmail());
        loginSharedPrefsEditor.apply();
    }

    private void checkEmailAndPassword(String emailString, String passwordString) {

    }
}
