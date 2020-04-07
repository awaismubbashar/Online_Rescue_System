<<<<<<< HEAD:app/src/main/java/com/example/sign/ProfileActivity.java
package com.example.sign;
=======
package com.example.OnlineRescueSystem;

import androidx.appcompat.app.AppCompatActivity;
>>>>>>> origin/master:app/src/main/java/com/example/OnlineRescueSystem/ProfileActivity.java

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
<<<<<<< HEAD:app/src/main/java/com/example/sign/ProfileActivity.java
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sign.Model.Registration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
=======
>>>>>>> origin/master:app/src/main/java/com/example/OnlineRescueSystem/ProfileActivity.java

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
<<<<<<< HEAD:app/src/main/java/com/example/sign/ProfileActivity.java
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                startActivity(new Intent(ProfileActivity.this,LoginScreen.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Registration registration = dataSnapshot.getValue(Registration.class);

                // showing progress dialog
                mProgress.setMessage("please wait.. ");
                mProgress.show();

                //fetching data from database
                mName = registration.getName();
                mPhoneNumber = registration.getPhoneNumber();
                mEmail = registration.getEmail();
                mAddress = registration.getAddress();
                mImage =registration.getImage();

                namePofile.setText(mName);
                phoneNumberP.setText(mPhoneNumber);
                emailP.setText(mEmail);
                addressP.setText(mAddress);


                ///profileImage.setImageResource(R.drawable.accidentview);

                //Picasso.get().load(registration.getImage()).into(profileImage);
                //Picasso.with(getApplicationContext()).load(mImage).into(profileImage);
                //profileImage.setImageResource(mImage);
                mProgress.dismiss();
//                Toast.makeText(ProfileActivity.this,"1st here"+mImage,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }// end of onStart
=======
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        final MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        MenuItem main = menu.findItem(R.id.icon_only);
//        main.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
//                startActivity(intent);
//                return false;
//            }
//        });
//        return true;
//    }
>>>>>>> origin/master:app/src/main/java/com/example/OnlineRescueSystem/ProfileActivity.java
}
