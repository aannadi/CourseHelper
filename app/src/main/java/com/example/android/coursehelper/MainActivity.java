package com.example.android.coursehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HashMap<String, String[]> classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        classes = new HashMap<>();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference cs = db.child("CS");

        cs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String[] data = new String[]{(String) ds.child("instructor").getValue(),
                                (String) ds.child("description").getValue(), (String) ds.child("link").getValue()};
                    classes.put((String) ds.child("name").getValue(), data);
                }
                LinearLayout layout = (LinearLayout) findViewById(R.id.class_list);
                for (String clas : classes.keySet()) {
                    addClass(clas, layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    private void addClass(final String classname, LinearLayout layout) {
        Typeface ubuntu = Typeface.createFromAsset(getAssets(),  "ubuntu_b.ttf");
        Typeface eczar = Typeface.createFromAsset(getAssets(),  "eczar_regular.ttf");

        View list_item = getLayoutInflater().inflate(R.layout.list_item, null);
        String[] info = classes.get(classname);

        TextView class_title = (TextView) list_item.findViewById(R.id.class_name);
        class_title.setText(classname);
        class_title.setTypeface(eczar);

        TextView class_description = (TextView) list_item.findViewById(R.id.description);
        class_description.setText(info[1]);
        class_description.setTypeface(ubuntu);

        TextView instructor = (TextView) list_item.findViewById(R.id.instructor_name);
        instructor.setText(info[0]);


        list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClassDetails.class);
                intent.putExtra("class", classname);
                intent.putExtra("instructor", classes.get(classname)[0]);
                intent.putExtra("description", classes.get(classname)[1]);
                intent.putExtra("link", classes.get(classname)[2]);
                startActivity(intent);
            }
        });

        layout.addView(list_item);
    }


}
