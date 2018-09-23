package com.example.android.coursehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
        classes.put("CS61A", new String[]{"John Denero", "An introduction to programming and computer science focused on abstraction techniques as means to manage program complexity. "});
        classes.put("CS61B", new String[]{"Josh Hug", "Fundamental dynamic data structures, including linear lists, queues, trees, and other linked structures; arrays strings, and hash tables. "});
        classes.put("CS61C", new String[]{"Dan Garcia", "The internal organization and operation of digital computers. Machine architecture, support for high-level languages, and operating systems."});
        classes.put("CS188", new String[]{"Peter Abbiel", "Ideas and techniques underlying the design of intelligent computer systems."});
        classes.put("CS70", new String[]{"Anant Sahai", "Misery and Death"});



        LinearLayout layout = (LinearLayout) findViewById(R.id.class_list);
        for (String clas : classes.keySet()) {
            addClass(clas, layout);
        }


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
                startActivity(intent);
            }
        });

        layout.addView(list_item);
    }


}
