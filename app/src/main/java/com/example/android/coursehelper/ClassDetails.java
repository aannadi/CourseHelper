package com.example.android.coursehelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ClassDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        final Activity that = this;

        final Typeface ubuntu = Typeface.createFromAsset(getAssets(),  "ubuntu_b.ttf");
        final Typeface eczar = Typeface.createFromAsset(getAssets(),  "eczar_regular.ttf");


        Intent intent = getIntent();
        String title = intent.getStringExtra("class");
        String instructor = intent.getStringExtra("instructor");
        String description = intent.getStringExtra("description");
        String link = intent.getStringExtra("link");

        TextView class_title = (TextView) findViewById(R.id.class_name);
        class_title.setText(title);


        TextView class_instructor = (TextView) findViewById(R.id.instructor_name);
        class_instructor.setText(instructor);

        TextView class_description = (TextView) findViewById(R.id.description);
        class_description.setText(description);

        class_title.setTypeface(eczar);
        class_description.setTypeface(ubuntu);

        addEnrollmentData(this, link);



    }

    public void addEnrollmentData(final Activity that, String link) {
        String url = "https://4gqfxitphd.execute-api.us-west-1.amazonaws.com/default/CourseHelper?cmd=enrollment&param=";
        url += link;
        Ion.with(this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        String enrolled = result.get("enrolledCount").toString();
                        String capacity = result.get("maxEnroll").toString();
                        Integer avaliable = Integer.parseInt(capacity) - Integer.parseInt(enrolled);
                        TextView total_spots = (TextView) findViewById(R.id.total_spots);
                        total_spots.setText(avaliable.toString() + " spots avaliable");


                        View linearLayout =  findViewById(R.id.availability);
                        JsonArray groups = result.getAsJsonArray("seatReservations");

                        for (int i = 0; i < groups.size(); i++) {
                            JsonObject group = groups.get(i).getAsJsonObject();
                            JsonObject details = group.get("requirementGroup").getAsJsonObject();
                            String en = group.get("enrolledCount").toString();
                            String ca = group.get("maxEnroll").toString();
                            String name = details.get("description").toString();

                            Integer av  = Integer.parseInt(ca) - Integer.parseInt(en);
                            TextView newgroup = new TextView(that);
                            newgroup.setText(av.toString() + " spots left for " + name);
                            newgroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            newgroup.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                            newgroup.setPadding(0, 5, 0 , 0);
                            ((LinearLayout) linearLayout).addView(newgroup);
                        }


                    }
                });
    }
}
