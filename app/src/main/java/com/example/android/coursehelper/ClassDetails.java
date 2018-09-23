package com.example.android.coursehelper;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;

public class ClassDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);


        Intent intent = getIntent();
        String title = intent.getStringExtra("class");
        String instructor = intent.getStringExtra("instructor");
        String description = intent.getStringExtra("description");

        TextView class_title = (TextView) findViewById(R.id.class_name);
        class_title.setText(title);


        TextView class_instructor = (TextView) findViewById(R.id.instructor_name);
        class_instructor.setText(instructor);

        TextView class_description = (TextView) findViewById(R.id.description);
        class_description.setText(description);

        Typeface ubuntu = Typeface.createFromAsset(getAssets(),  "ubuntu_b.ttf");
        Typeface eczar = Typeface.createFromAsset(getAssets(),  "eczar_regular.ttf");

        class_title.setTypeface(eczar);
        class_description.setTypeface(ubuntu);


    }
}
