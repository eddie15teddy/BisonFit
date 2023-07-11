package funkyflamingos.bisonfit.ui;

import funkyflamingos.bisonfit.logic.GymHoursHandler;
import funkyflamingos.bisonfit.logic.IUserRegistrationHandler;
import funkyflamingos.bisonfit.logic.UserRegistrationHandler;
import funkyflamingos.bisonfit.logic.WaterHandler;
import funkyflamingos.bisonfit.logic.RoutineHandler;

import funkyflamingos.bisonfit.dso.RoutineHeader;

import funkyflamingos.bisonfit.persistence.utils.DBHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import funkyflamingos.bisonfit.R;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

public class HomePageActivity extends AppCompatActivity implements AddWorkoutDialog.AddWorkoutDialogListener {
    private CircularProgressIndicator waterTrackerProgress;
    private WaterHandler waterHandler;
    private GymHoursHandler gymHoursHandler;
    private TextView lblGreetings;
    private Button btnGymHours;
    private IUserRegistrationHandler userNameHandler;
    private RoutineHandler workoutManager;
    private MyWorkoutsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        waterHandler = new WaterHandler();
        gymHoursHandler = new GymHoursHandler();
        waterTrackerProgress = findViewById(R.id.circularProgressView);
        btnGymHours = findViewById(R.id.btnGymHours);
        lblGreetings = findViewById(R.id.lblGreetings);
        userNameHandler = new UserRegistrationHandler();

        workoutManager = new RoutineHandler();
        List<RoutineHeader> listOfWorkouts = workoutManager.getAllRoutineHeaders();
        adapter = new MyWorkoutsListAdapter(listOfWorkouts, this);
        RecyclerView recyclerView = findViewById(R.id.lstMyWorkouts);

        setWaterTrackerProgress();
        waterTrackerProgress.setMax(waterHandler.getGoal());

        // display user name
        lblGreetings.setText(getGreetingsMessage());

        // recyclerVew setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // start refresher thread
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        String newStatus = gymHoursHandler.getTimeUntilOpenOrClose();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnGymHours.setText(newStatus);
                            }
                        });
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void incrementAndUpdateWaterTracker(View v) {
        waterHandler.increment();
        setWaterTrackerProgress();
    }

    private void setWaterTrackerProgress() {
        waterTrackerProgress.setProgress(waterHandler.getProgress());
        if (waterHandler.reachedGoal()) { // set goal  as accomplished
            waterTrackerProgress.setIndicatorColor(ContextCompat.getColor(this, R.color.success_green));
        }
    }

    @Override
    public void onBackPressed() {
        // exit app on back press
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    public String getGreetingsMessage() {
       return "Hi " + userNameHandler.getUserName() + "!";
    }

    public void openGymHoursActivity(View v) {
        Intent intent = new Intent(this, GymHoursActivity.class);
        startActivity(intent);
    }

    public void toggleEditMode(View v) {
        Button editButton = (Button)v;
        String buttonText = editButton.getText().toString();

        ViewGroup parentView = (ViewGroup) v.getParent();
        RecyclerView recyclerView = parentView.findViewById(R.id.lstMyWorkouts);

        if (buttonText.equals("Edit"))
        {
            toggleButtonVisibilities(recyclerView, View.GONE, View.VISIBLE);
            editButton.setText("Done");
        }
        else if (buttonText.equals("Done"))
        {
            toggleButtonVisibilities(recyclerView, View.VISIBLE, View.GONE);
            editButton.setText("Edit");
        }
    }

    public void toggleButtonVisibilities(RecyclerView recyclerView, int arrowImageVisibility, int deleteWorkoutVisibility)
    {
        int itemCount = recyclerView.getAdapter().getItemCount();

        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                View deleteWorkoutButton = viewHolder.itemView.findViewById(R.id.deleteWorkoutButton);
                View arrowImage = viewHolder.itemView.findViewById(R.id.imgArrow);

                arrowImage.setVisibility(arrowImageVisibility);
                deleteWorkoutButton.setVisibility(deleteWorkoutVisibility);
            }
        }

    }

    @Override
    public void createNewWorkout(String newWorkoutName)
    {
        workoutManager.addNewRoutine(newWorkoutName);
        adapter.updateWorkoutList(workoutManager.getAllRoutineHeaders());
    }

    public void newWorkoutDialog(View view) {
        AddWorkoutDialog addWorkoutDialog = new AddWorkoutDialog();
        addWorkoutDialog.show(getSupportFragmentManager(), "Add Workout Dialog");
    }
}