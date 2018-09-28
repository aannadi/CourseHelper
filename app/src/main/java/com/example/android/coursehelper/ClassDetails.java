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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ClassDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        final Activity that = this;

        final Typeface ubuntu = Typeface.createFromAsset(getAssets(),  "ubuntu_b.ttf");
        final Typeface eczar = Typeface.createFromAsset(getAssets(),  "eczar_regular.ttf");

//        Circle circle = (Circle) findViewById(R.id.circle);
//        Circle circle2 = (Circle) findViewById(R.id.circle2);
//        Circle circle3 = (Circle) findViewById(R.id.circle3);
//
//        CircleAngleAnimation animation = new CircleAngleAnimation(circle, 350);
//        CircleAngleAnimation animation2 = new CircleAngleAnimation(circle2, 350);
//        CircleAngleAnimation animation3 = new CircleAngleAnimation(circle3, 350);
//        animation.setDuration(1000);
//        animation2.setDuration(1000);
//        animation3.setDuration(1000);
//        circle.startAnimation(animation);
//        circle2.startAnimation(animation2);
//        circle3.startAnimation(animation3);

        Intent intent = getIntent();
        String title = intent.getStringExtra("class");
        final String instructor = intent.getStringExtra("instructor");
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

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference cs = db.child("Teachers");
        String teacher = "";
        cs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(instructor)) {
                        Log.d("name", ds.getKey());
                        addRatingData(that, ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference grades = db.child("CS/" + title);
        grades.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap x = (HashMap) dataSnapshot.child("grades").getValue();
                add_grade_Data(x);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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

    public void addRatingData(final Activity that, String link) {

        final TextView quality = (TextView) findViewById(R.id.quality);
        final TextView recommend = (TextView) findViewById(R.id.recommend);
        final TextView diff = (TextView) findViewById(R.id.difficulty);


        if (link.equals("0")) {
            TextView quality_l = (TextView) findViewById(R.id.quality_label);
            TextView recommend_l = (TextView) findViewById(R.id.recommend_label);
            TextView diff_l = (TextView) findViewById(R.id.difficulty_label);
            quality_l.setText("Overall Quality \n Not Avaliable");
            recommend_l.setText("Would Take \n Again \n Not Avaliable");
            diff_l.setText("Difficultly \n Not Avaliable");
            return;
        }

        String url = "https://4gqfxitphd.execute-api.us-west-1.amazonaws.com/default/CourseHelper?cmd=scores&param=";
        url += link;
        Ion.with(this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray numbers = result.getAsJsonArray("data");
                        String zero = numbers.get(0).toString().replaceAll("^\"|\"$", "");
                        String one = numbers.get(1).toString().replaceAll("^\"|\"$", "").replaceAll("%", "");
                        String two = numbers.get(2).toString().replaceAll("^\"|\"$", "");
                        Double overall = Double.parseDouble(zero.replaceAll("^\"|\"$", ""));
                        Integer again = Integer.parseInt(one.replaceAll("^\"|\"$", "")) ;
                        Double difficulty = Double.parseDouble(two.replaceAll("^\"|\"$", ""));
                        
                        Circle circle = (Circle) findViewById(R.id.circle);
                        quality.setText(overall.toString());
                        double a = (overall/5.0 * 360.0);
                        int angle = (int) a;
                        CircleAngleAnimation animation = new CircleAngleAnimation(circle, angle);
                        animation.setDuration(1000);
                        circle.startAnimation(animation);

                        Circle circle2 = (Circle) findViewById(R.id.circle2);

                        recommend.setText(again.toString() + "%");
                        a = (double) again;
                        a = a/100.0 * 360;
                        angle = (int) a;
                        CircleAngleAnimation animation2 = new CircleAngleAnimation(circle2, angle);
                        animation2.setDuration(1000);
                        circle2.startAnimation(animation2);

                        Circle circle3 = (Circle) findViewById(R.id.circle3);
                        diff.setText(difficulty.toString());
                        a = difficulty/5.0 * 360;
                        angle = (int) a;
                        CircleAngleAnimation animation3 = new CircleAngleAnimation(circle3, angle);
                        animation3.setDuration(1000);
                        circle3.startAnimation(animation3);

                    }

                });
    }

    public void add_grade_Data(HashMap grades) {
        String upper;
        String middle;
        String lower;
        Object[] keys = grades.keySet().toArray();

        HashMap one = (HashMap) grades.get((keys[0]));
        HashMap two =(HashMap) grades.get((keys[1]));
        HashMap three = (HashMap) grades.get((keys[2]));

        HashMap<String, String[]> usable_data = order_grades(one, two, three);

        TextView grade_lower = (TextView) findViewById(R.id.grade_below);
        TextView grade_lower_label = (TextView) findViewById(R.id.grade_below_label);
        grade_lower.setText(usable_data.get("lower")[0]);
        grade_lower_label.setText(usable_data.get("lower")[1]);

        TextView grade_middle = (TextView) findViewById(R.id.grade_middle);
        TextView grade_middle_label = (TextView) findViewById(R.id.grade_middle_label);
        grade_middle.setText(usable_data.get("middle")[0]);
        grade_middle_label.setText(usable_data.get("middle")[1]);

        TextView grade_upper = (TextView) findViewById(R.id.grade_upper);
        TextView grade_upper_label = (TextView) findViewById(R.id.grade_upper_label);
        grade_upper.setText(usable_data.get("upper")[0]);
        grade_upper_label.setText(usable_data.get("upper")[1]);
    }

    public HashMap order_grades(HashMap one, HashMap two, HashMap three) {
        HashMap<String, String[]> usable_data = new HashMap();

        if (((Long) one.get("rank ")).intValue() == 1) {
            usable_data.put("upper", new String[]{(String) one.get("grade"), (String) one.get("text")});
        } else if (((Long) one.get("rank ")).intValue() == 2) {
            usable_data.put("middle", new String[]{(String) one.get("grade"), (String) one.get("text")});
        } else {
            usable_data.put("lower", new String[]{(String) one.get("grade"), (String) one.get("text")});
        }



        if (((Long) two.get("rank ")).intValue() == 1) {
            usable_data.put("upper", new String[]{(String) two.get("grade"), (String) two.get("text")});
        } else if ( ((Long) two.get("rank ")).intValue() == 2) {
            usable_data.put("middle", new String[]{(String) two.get("grade"), (String) two.get("text")});
        } else {
            usable_data.put("lower", new String[]{(String) two.get("grade"), (String) two.get("text")});
        }

        Log.d("testing", three.toString());
        if (((Long) three.get("rank ")).intValue() == 1) {
            usable_data.put("upper", new String[]{(String) three.get("grade"), (String) three.get("text")});
        } else if ( ((Long) three.get("rank ")).intValue() == 2) {
            usable_data.put("middle", new String[]{(String) three.get("grade"), (String) three.get("text")});
        } else {
            usable_data.put("lower", new String[]{(String) three.get("grade"), (String) three.get("text")});
        }
        Log.d("datatata", usable_data.toString());
        return usable_data;
    }
}
