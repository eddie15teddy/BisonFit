package funkyflamingos.bisonfit.logic;

import java.util.ArrayList;
import java.util.List;

import funkyflamingos.bisonfit.application.Services;
import funkyflamingos.bisonfit.dso.ExerciseHeader;
import funkyflamingos.bisonfit.dso.Workout;
import funkyflamingos.bisonfit.dso.WorkoutHeader;
import funkyflamingos.bisonfit.persistence.IExerciseLookupPersistence;
import funkyflamingos.bisonfit.persistence.IWorkoutPersistence;
import funkyflamingos.bisonfit.persistence.ISavedWorkoutExercises;

public class WorkoutHandler implements IWorkoutHandler {
    private IWorkoutPersistence workoutsPersistence;
    private ISavedWorkoutExercises savedWorkoutExercisesPersistence;
    private IExerciseLookupPersistence exerciseLookupPersistence;
    ArrayList<ExerciseHeader> exerciseList;

    // Constructor for the database
    public WorkoutHandler() {
        workoutsPersistence = Services.getWorkoutsPersistence();
        savedWorkoutExercisesPersistence = Services.getSavedWorkoutExercises();
        exerciseLookupPersistence = Services.getExerciseLookupPersistence();

        exerciseList = exerciseLookupPersistence.getAllExerciseHeaders();
    }

    public WorkoutHandler(IWorkoutPersistence r, ISavedWorkoutExercises s, IExerciseLookupPersistence e) {
        workoutsPersistence = r;
        savedWorkoutExercisesPersistence = s;
        exerciseLookupPersistence = e;

        exerciseList = exerciseLookupPersistence.getAllExerciseHeaders();
    }

    @Override
    public List<WorkoutHeader> getAllWorkoutHeaders() {
        return workoutsPersistence.getAllWorkoutHeaders();
    }

    @Override
    public ArrayList<ExerciseHeader> getAllExerciseHeaders() {
        return exerciseList;
    }

    @Override
    public ArrayList<ExerciseHeader> getAllSelectedExercises() {
        ArrayList<ExerciseHeader> selectedExerciseHeaders = new ArrayList<>();
        exerciseList.forEach(exerciseHeader -> {
            if (exerciseHeader.isSelected()) {
                selectedExerciseHeaders.add(exerciseHeader);
            }
        });

        return selectedExerciseHeaders;
    }

    @Override
    public Workout getWorkoutByID(int workoutID) {
        return workoutsPersistence.getWorkoutByID(workoutID);
    }

    @Override
    public void addNewWorkout(String workoutName) {
        workoutsPersistence.addWorkout(workoutName);
    }

    @Override
    public void deleteWorkout(int workoutID) {
        if (getWorkoutByID(workoutID) != null) {
            savedWorkoutExercisesPersistence.deleteWorkout(getWorkoutByID(workoutID).getHeader());
            workoutsPersistence.deleteWorkoutById(workoutID);
        }
    }

    @Override
    public void addSelectedExercisesToWorkout(WorkoutHeader workoutHeader)
    {
        savedWorkoutExercisesPersistence.addExercises(getAllSelectedExercises(), workoutHeader);

        unselectAllExercises();
    }

    @Override
    public void unselectAllExercises()
    {
        exerciseList.forEach(exerciseHeader -> {
            if (exerciseHeader.isSelected()) {
                exerciseHeader.toggleSelected();
            }
        });
    }

    @Override
    public ArrayList<ExerciseHeader> getExerciseHeaders(WorkoutHeader workoutHeader)
    {
        return savedWorkoutExercisesPersistence.getExercisesByWorkout(workoutHeader);
    }

    @Override
    public void deleteExercise(ExerciseHeader exerciseHeader, WorkoutHeader workoutHeader)
    {
        savedWorkoutExercisesPersistence.deleteExercise(exerciseHeader, workoutHeader);
    }
}
