package com.muhammedalikocabey.reminders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private Context context = this;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailAddressEditText;
    private EditText password1EditText;
    private EditText password2EditText;
    private Button signUpButton;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firstNameEditText = findViewById(R.id.first_name_EditText);
        lastNameEditText = findViewById(R.id.last_name_EditText);
        emailAddressEditText = findViewById(R.id.email_EditText);
        password1EditText = findViewById(R.id.password1_EditText);
        password2EditText = findViewById(R.id.password2_EditText);
        signUpButton = findViewById(R.id.sign_up_Button);


        firebaseAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("loginOrWithoutSignIn", MODE_PRIVATE);



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailAddressEditText.getText().toString();
                String password = password1EditText.getText().toString();
                String password2 = password2EditText.getText().toString();


                ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                boolean internetConnected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                if(!internetConnected) {
                    Toast.makeText(context, context.getString(R.string.alert_internetInactive), Toast.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(context, context.getString(R.string.alert_emailEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, context.getString(R.string.alert_emailFormatWrong), Toast.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
                    Toast.makeText(context, context.getString(R.string.alert_firstLastNameEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
                    Toast.makeText(context, context.getString(R.string.alert_passwordEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!TextUtils.equals(password, password2)) {
                    Toast.makeText(context, context.getString(R.string.alert_passwordDontMatch), Toast.LENGTH_SHORT).show();
                    return;
                }


                if(password.length() < 8) {
                    Toast.makeText(context, context.getString(R.string.alert_passwordShort), Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {


                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(firstName + " " + lastName)
                                            .build();

                                    currentUser.updateProfile(profileUpdate);


                                    currentUser.sendEmailVerification();

                                    Intent goToLoginIntent = new Intent(context, LoginActivity.class);

                                    goToLoginIntent.putExtra("from", "register");

                                    startActivity(goToLoginIntent);
                                }

                                // Success
                                else {
                                    Toast.makeText(context, context.getString(R.string.alert_registerFailed), Toast.LENGTH_SHORT).show();
                                    return;

                                }
                            }
                        });


            }
        });



    }
}