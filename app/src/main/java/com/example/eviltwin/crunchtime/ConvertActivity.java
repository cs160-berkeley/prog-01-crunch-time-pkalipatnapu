package com.example.eviltwin.crunchtime;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

public class ConvertActivity extends AppCompatActivity {
    private TextView exercise_name;
    private EditText calorie_amount, exercise_amount, weight_amount;
    int selected_exercise=0;

    private String[] exercises = {"Cycling", "Jogging", "Jumping Jacks", "Leg Lift", "Plank", "Pull Up", "Push Up", "Sit Up", "Squats", "Stair Climbing", "Swimming", "Walking"};
    private boolean[] measured_in_reps = {false, false, false, false, false, true, true, true, true, false, false, false};
    private float[] cal100_equivalents = {12, 12, 10, 25, 25, 100, 350, 200, 225,15, 13, 20};
    private float weight=150;

    Map<String, TextView> converted_cals;

    private void CreateTopHSV(LinearLayout ll) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i=0; i<exercises.length; i++) {
            ImageButton ib = new ImageButton(this);
            ib.setImageResource(this.getResources().getIdentifier(exercises[i].replace(' ', '_').toLowerCase(), "drawable", getPackageName()));
            ib.setLayoutParams(lp);
            ib.setBackgroundColor(Color.TRANSPARENT);
            ib.setOnClickListener(imgButtonHandler);
            ib.setId(i);
            ll.addView(ib);
        }
    }

    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selected_exercise = view.getId();
            String units;
            if (measured_in_reps[selected_exercise]) {
                units = "reps";
            } else {
                units = "mins";
            }
            exercise_name.setText(exercises[selected_exercise]+" ("+units+"):");
            exercise_amount.setText("");
        }
    };

    private void CreateBottomHSV(LinearLayout ll) {
        for (int i=0; i<exercises.length; i++) {
            // Use exercise image as background.
            RelativeLayout.LayoutParams rp =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            RelativeLayout rl = new RelativeLayout(this);
            rl.setLayoutParams(rp);
            ImageView iv = new ImageView(this);
            iv.setImageResource(this.getResources().getIdentifier(exercises[i].replace(' ', '_').toLowerCase(), "drawable", getPackageName()));
            iv.setLayoutParams(rp);
            rl.addView(iv);

            // Show equivalent amount of exercise in a large font.
            rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            TextView tv = new TextView(this);
            tv.setLayoutParams(rp);
            tv.setTextSize(40);
            tv.setTextColor(Color.WHITE);
            tv.setText("0");
            converted_cals.put(exercises[i], tv);
            tv.setId(View.generateViewId());
            rl.addView(tv);

            // Add the units for clarity.
            rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView units = new TextView(this);
            rp.addRule(RelativeLayout.BELOW, tv.getId());
            rp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            units.setLayoutParams(rp);
            units.setTextColor(Color.WHITE);
            if (measured_in_reps[i]) {
                units.setText("reps");
            } else {
                units.setText("mins");
            }
            rl.addView(units);
            ll.addView(rl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        exercise_name = (TextView) findViewById(R.id.exercise);
        exercise_amount = (EditText) findViewById(R.id.exercise_amount);
        exercise_amount.addTextChangedListener(exerciseWatcher);
        weight_amount = (EditText) findViewById(R.id.weight_amount);
        weight_amount.addTextChangedListener(weightWatcher);
        calorie_amount = (EditText) findViewById(R.id.calorie_amount);
        calorie_amount.addTextChangedListener(calorieWatcher);


        converted_cals = new HashMap<String, TextView>();

        LinearLayout ll1 = (LinearLayout) findViewById(R.id.ll1);
        LinearLayout ll2 = (LinearLayout) findViewById(R.id.ll2);

        // Add exercise images.
        CreateTopHSV(ll1);
        CreateBottomHSV(ll2);
    }

    private final TextWatcher exerciseWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        // Invoked as the user is typing text.
        public void afterTextChanged(Editable s) {
            // We will edit calories, so temporarily remove its listener.
            calorie_amount.removeTextChangedListener(calorieWatcher);
            if (s.length() != 0) {
                // Compute using unitary method how many calories were burnt.
                calorie_amount.setText(FindCaloriesBurnt());
            } else {
                // Need to reset all calculations.
                calorie_amount.setText("0");
                FindEquivalentWork(0);
            }
            calorie_amount.addTextChangedListener(calorieWatcher);
        }
    };

    private final TextWatcher calorieWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            exercise_amount.removeTextChangedListener(exerciseWatcher);
            if (s.length() != 0) {
                // Compute using unitary method how exercise to do.
                float calories_burned = Float.parseFloat(calorie_amount.getText().toString());
                float exercise_needed = calories_burned*(cal100_equivalents[selected_exercise]/100)*(150/weight);
                exercise_amount.setText(Integer.toString(Math.round(exercise_needed)));
                FindEquivalentWork(calories_burned);
            } else {
                // Need to reset all calculations.
                exercise_amount.setText("");
                FindEquivalentWork(0);
            }
            exercise_amount.addTextChangedListener(exerciseWatcher);
        }
    };

    private final TextWatcher weightWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        // Invoked as the user is typing text.
        public void afterTextChanged(Editable s) {
            calorie_amount.removeTextChangedListener(calorieWatcher);
            if (s.length() != 0) {
                weight=Float.parseFloat(weight_amount.getText().toString());
                calorie_amount.setText(FindCaloriesBurnt());
            } else {
                weight =150;
                calorie_amount.setText(FindCaloriesBurnt());
            }
            calorie_amount.addTextChangedListener(calorieWatcher);
        }
    };

    private String FindCaloriesBurnt() {
        // Compute using unitary method how many calories were burnt.
        String ex = exercise_amount.getText().toString();
        if (ex.length()==0) return "0";
        float calories_burnt = Float.parseFloat(ex)*(100/cal100_equivalents[selected_exercise])*(weight/150);
        // We always update equivalent work.
        FindEquivalentWork(calories_burnt);
        return Integer.toString(Math.round(calories_burnt));
    }

    private void FindEquivalentWork(float calories_burnt) {
        for (int i=0; i<exercises.length; i++) {
            int eq_units = Math.round(calories_burnt * (cal100_equivalents[i]/100)*(150/weight));
            TextView tv = converted_cals.get(exercises[i]);
            tv.setText(Integer.toString(eq_units));
        }
    }
}
