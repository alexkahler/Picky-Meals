package dk.aau.student.mea_a1b129.dish_it;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * A fragment to show multiple Ingredients.
 * TODO: Debug multiple ingredient-s
 */
public class IngredientChooseFragment extends DialogFragment { //We extend the DialogFragment so we can easily inherit the parent methods.
    private static final String TAG = "ChooseIngredientFrag";

    private ArrayList<Ingredient> chosenIngredients = new ArrayList<>();
    private DialogDoneListener listener;
    private IngredientListAdapter ila;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IngredientChooseFragment() {
    }

    @Override
    @SuppressWarnings("unchecked assignemt")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //If we have arguments from an earlier activity..
        if (getArguments() != null) {
            Log.d(TAG, "Found arguments!");
            try {
                //.. Then we try to get the previously chosen ingredients from the arguments. Our List is serializable - so this is easily retrieved.
                chosenIngredients = (ArrayList) getArguments().getSerializable("ingredientsID");
                Log.d(TAG, "chosenIngredient: " + chosenIngredients);
            } catch (NullPointerException e) {
                Log.e(TAG, "Couldn't get Bundle arguments from AddDinner " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, "I caught an exception! Gotta catch em all!" + e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            //Here we try to get the listener from the earlier activity, so we can do a call-back.
            listener = (DialogDoneListener) activity;
        } catch (ClassCastException e) {
            //If the earlier Activity hasn't implemented the listener, then we throw a new exception.
            throw new ClassCastException(activity.toString()
                    + " must implement DialogDoneListener");
        }
    }

    //This method is called if the fragment is detached from our activity
    @Override
    public void onDetach() {
        super.onDetach();
        //Set the listener to null.
        listener = null;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Now we try to inflage our fragment with our layout in our ViewGroup container.
        View view = inflater.inflate(R.layout.fragment_dialog_choose_ingredient, container);
        //We get a new Dialog from the parent class.
        Dialog dialog = getDialog();
        //We set the title in the Dialog.
        dialog.setTitle(getResources().getString(R.string.dialog_title));

        //Attach listeners to the cancel button
        Button cancelButton = (Button) view.findViewById(R.id.dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the cancel button is pressed, then we notify the listener and dismiss this fragment.
                listener.onDone(false, ila.getChosenIngredients());
                dismiss();
            }
        });

        //Attach listeners to the confirm button.
        Button confirmButton = (Button) view.findViewById(R.id.dialog_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the save button was pressed, then notify the listener and dismiss this fragment.
                listener.onDone(true, ila.getChosenIngredients());
                dismiss();
            }
        });

        updateIngredients(view);
        return view;
    }

    /**
     * Private helper method to update the ingredients in our IngredientList.
     * @param view
     */
    private void updateIngredients(View view) {
        //Make an IngredientRepository, which holds all of our Ingredients.
        IngredientRepository ir = new IngredientRepository(getActivity().getApplicationContext());
        //Make a List with Ingredients, which we'll get from our IngredientRepository.
        List<Ingredient> tempIngredientList = ir.getAllIngredients();
        //Make a list with all of the Ingredient Categories.
        List<Ingredient.Category> ingredientCategories = new ArrayList<>(Arrays.asList(Ingredient.Category.values()));
        //And lastly, we make a HashMap with all of our Ingredients, where each Ingredient value is attached to a Category KeySet.
        HashMap<Ingredient.Category, List<Ingredient>> ingredients = new HashMap<>();
        //Now we iterate through each Category..
        for (Ingredient.Category c : ingredientCategories) {
            List<Ingredient> temp = new ArrayList<>();
            //.. And for each Category we iterate through each Ingredient in the temporary ingredient list..
            for (Ingredient i : tempIngredientList) {
                //.. If the Category equals the Ingredient Category..
                if (c.equals(i.getCategory())) {
                    //.. Then we add that to our list.
                    temp.add(i);
                }
            }
            //If the temp List of Ingredients if bigger than 0...
            if (temp.size() != 0) {
                //.. Then add that List to our HashMap.
                ingredients.put(c, temp);
            }
        }

        //Now we initialize the adapter for the ExpandableListView with the necessary information.
        ila = new IngredientListAdapter(getActivity().getApplicationContext(), ingredientCategories, ingredients, chosenIngredients);
        try {
            //We try to set the adapter for the ExpandableListView
            ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.dialog_ingredient_listview);
            expandableListView.setAdapter(ila);
        } catch (NullPointerException e) {
            //Catch any unforeseen errors here, so it doesn't crash the program.
            Log.e(TAG, "Couldn't find expandableListView! " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Interface to implement for notifying when the fragment is done.
     */
    public interface DialogDoneListener {
        void onDone(boolean status, ArrayList<Ingredient> chosenIngredients);
    }
}
