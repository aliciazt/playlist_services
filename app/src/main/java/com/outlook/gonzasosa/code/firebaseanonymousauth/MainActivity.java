package com.outlook.gonzasosa.code.firebaseanonymousauth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outlook.gonzasosa.code.firebaseanonymousauth.Models.Track;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "ANONYMOUS_FIREBASE"; //referencia a la basae del proyecto del maestro
    public static final int STORAGE_PERMISSIONS = 123; // contraseña para ingresar a la bd

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    RecyclerView recyclerView;

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser ();
            if (user != null) {
                Log.d (TAG, "onAuthStateChanged:signed_in: " + user.getUid ());
            } else {
                Log.d (TAG, "onAuthStateChanged:signed_out");
            }
        }
    };

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle ("List background player");
        }

        auth = FirebaseAuth.getInstance ();
        databaseReference = FirebaseDatabase.getInstance().getReference ();

        recyclerView = (RecyclerView) findViewById (R.id.recview);

        LinearLayoutManager layoutManager = new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false);
      /*dibuja las lineas de division en el recycler*/  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration (this, layoutManager.getOrientation ());

        recyclerView.setLayoutManager (layoutManager);
       /*agrega la decoracion a la vista */ recyclerView.addItemDecoration (dividerItemDecoration);

    }

    @Override
    protected void onStart () {
        super.onStart ();

        auth.addAuthStateListener (authStateListener);
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "OnComplete : " +task.isSuccessful());

                if (!task.isSuccessful ()) {//en caso de no encontrar los datos de la pista
                    Log.w (TAG, "Failed : ", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermissionsNeeded ()) {
                    requestPermissionsNeeded ();
                    return;
                }

                retrieveData ();

            }
        });
    }

    private void retrieveData () {
        databaseReference.child("songs").addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                ArrayList<Track> tracks = new ArrayList<Track> ();

                for (DataSnapshot entry: dataSnapshot.getChildren ()) {
                    /*String info = String.format (Locale.US, "Key: %s, Value %s", entry.getKey (), entry.getValue ());
                    Log.d (TAG, info);

                    DataSnapshot foo = entry.child ("title");
                    info = String.format (Locale.US, "Title: %s", foo.getValue ());
                    Log.d (TAG, info);

                    foo = entry.child ("album");
                    info = String.format (Locale.US, "Album: %s", foo.getValue ());
                    Log.d (TAG, info);

                    foo = entry.child ("author");
                    info = String.format (Locale.US, "Author: %s", foo.getValue ());
                    Log.d (TAG, info);*/

                    Track track = new Track ();
                    DataSnapshot foo = entry.child ("title");
                    track.title = foo.getValue() != null ? foo.getValue().toString () : "";

                    foo = entry.child ("album");
                    track.album = foo.getValue() != null ? foo.getValue().toString () : "";

                    foo = entry.child ("author");
                    track.author = foo.getValue() != null ? foo.getValue().toString () : "";

                    foo = entry.child ("composer");
                    track.composer = foo.getValue() != null ? foo.getValue().toString () : "";

                    foo = entry.child ("company");
                    track.company = foo.getValue() != null ? foo.getValue().toString () : "";

                    foo = entry.child ("cover");
                    track.cover  = foo.getValue() != null ? foo.getValue().toString () : "";

                    foo = entry.child ("year");
                    track.year = foo.getValue() != null ? Integer.parseInt (foo.getValue().toString ()) : 0;

                    tracks.add (track);
                }

                recyclerView.setAdapter (new TracksAdapter (getBaseContext(), tracks));
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStop () {
        super.onStop ();
        if (auth != null) {
            auth.removeAuthStateListener (authStateListener);
        }
    }

    private boolean checkPermissionsNeeded () {
        return ContextCompat.checkSelfPermission (this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionsNeeded () {
        String [] permissions = new String [] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions (this, permissions, STORAGE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults [0] == PackageManager.PERMISSION_GRANTED
                        && grantResults [1] == PackageManager.PERMISSION_GRANTED) {

                    retrieveData ();

                }

                break;
        }

    }
}


