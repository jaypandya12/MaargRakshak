package com.example.myapplication.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView title;
    TextView body;
    ImageView image;
    TextView like, like_count;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("phpdemo");
    DatabaseReference eventref;
    int likes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = findViewById(R.id.name);
        body = findViewById(R.id.desc);
        image = findViewById(R.id.image);
        like = findViewById(R.id.like);
        like_count = findViewById(R.id.likes_count);
        eventref = rootRef.child(getIntent().getStringExtra("key"));




        eventref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String ename = dataSnapshot.child("Event Name: ").getValue(String.class);

                String edesc = dataSnapshot.child("Event Description: ").getValue(String.class);

                String eimage = dataSnapshot.child("Image: ").getValue(String.class);

                final DatabaseReference event1 = rootRef.child(ename);
                String elike_count1 = dataSnapshot.child("Likes: ").getValue(String.class);
                like_count.setText(elike_count1 + " Likes");



                title.setText(ename);
                body.setText(edesc);
//                like_count.setText(elike_count + "Likes");
                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (like.getText() == "Unlike"){
                            like.setText("Like");
                            String elike_count = dataSnapshot.child("Likes: ").getValue(String.class);
                            int count = Integer.parseInt(elike_count);
                            count--;
                            elike_count = String.valueOf(count);
                            like_count.setText(elike_count + " Likes");
                            DatabaseReference likes_count = event1.child("Likes: ");
                            likes_count.setValue(elike_count);
                        }
                        else {
                            like.setText("Unlike");
                            String elike_count = dataSnapshot.child("Likes: ").getValue(String.class);
                            int count = Integer.parseInt(elike_count);
                            count++;
                            elike_count = String.valueOf(count);
                            like_count.setText(elike_count + " Likes");
                            DatabaseReference likes_count = event1.child("Likes: ");
                            likes_count.setValue(elike_count);
                        }
                    }
                });

                Picasso.get()
                        .load(eimage)
                        .into(image);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
