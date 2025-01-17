package com.example.sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    private TextView registerTextView;
    private Button loginButton;
    private EditText mEmail,mPassword;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Action bar and Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_screen);
        getSupportActionBar().hide();

        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);

        loginButton = (Button) findViewById(R.id.loginButtonID_login);
        registerTextView = (TextView) findViewById(R.id.registerTextViewID_login);

        mAuth = FirebaseAuth.getInstance();


        ////registerTextView clickListener
        registerTextView.setPaintFlags(registerTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        registerTextView.setText(Html.fromHtml("<u>underlined</u> text"));
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //LoginScreen clickListener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if (email != null && password != null) {
                    if(!TextUtils.isEmpty(mEmail.getText().toString())
                            && !TextUtils.isEmpty(mPassword.getText().toString())){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginScreen.this, "Not registered or internet problem", Toast.LENGTH_LONG)
                                        .show();

                            } else {
                                Toast.makeText(LoginScreen.this, "Login Success", Toast.LENGTH_LONG)
                                        .show();
                                Intent intent = new Intent(LoginScreen.this, DashBoardLayout.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                    }else {
                        Toast.makeText(LoginScreen.this, "Fill all field first", Toast.LENGTH_LONG)
                                .show();
                    }
                }else {
                    Toast.makeText(LoginScreen.this, "Fill all field first", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}
