package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView image;
    private EditText name, CNIC, phoneNumber, address,mpassword;
    private Button registerButton;

    private DatabaseReference mPostDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Caller Data");


        image = findViewById(R.id.profile_image);
        name = findViewById(R.id.nameEditTextID);
        phoneNumber = findViewById(R.id.phoneEditTextNoID);
        CNIC = findViewById(R.id.CNICEditTextID);
        address = findViewById(R.id.addressEditTextID);
        mpassword = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButtonID);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = phoneNumber.getText().toString();
                final String password = mpassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Not registered/error", Toast.LENGTH_LONG)
                                    .show();

                        } else {
                            Toast.makeText(RegisterActivity.this, "Login Success", Toast.LENGTH_LONG)
                                    .show();
                            startPosting();

                        }
                    }
                });
            }
        });

        //        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //posting to our database
//
//
//                startPosting();
//
//            }
//        });

//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RegisterActivity.this, DashBoardLayout.class);
//                startActivity(intent);
//            }
//        });

    } //  end of on create method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            image.setImageURI(mImageUri);
        }else{
            Toast.makeText(RegisterActivity.this,"something went wrong...",Toast.LENGTH_LONG).show();
        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting to blog...");
        mProgress.show();

        final String mName = name.getText().toString().trim();
        final String mNumber = phoneNumber.getText().toString().trim();
        final String mCNIC = CNIC.getText().toString().trim();
        final String maddress = address.getText().toString().trim();


        if (!TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mNumber)
                && mImageUri != null && !TextUtils.isEmpty(maddress)
                && !TextUtils.isEmpty(mCNIC)) {
            // start uploading...
            StorageReference filepath = mStorageRef.child("Caller")
                    .child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    DatabaseReference newPost = mPostDatabase.push();

                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("CNIC", mCNIC);
                    dataToSave.put("phoneNumber", mNumber);
                    dataToSave.put("name", mName);
                    dataToSave.put("address", maddress);
                    dataToSave.put("image", downloadUrl.toString());
                    dataToSave.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userId", mUser.getUid());

                    newPost.setValue(dataToSave);
                    mProgress.dismiss();
                    Toast.makeText(RegisterActivity.this,"registered ",Toast.LENGTH_LONG).show();
                    //foloww
                    startActivity(new Intent(RegisterActivity.this, Deletable.class));
                    finish();
                }
            });
        }else {
            Toast.makeText(RegisterActivity.this,"please Fill all field",Toast.LENGTH_LONG).show();
        }
    }// end on startPointing


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}// end of class
