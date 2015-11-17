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
 * A fragment representing a list of Items.
 */
public class IngredientChooseFragment extends DialogFragment {
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
        if (getArguments() != null) {
            Log.d(TAG, "Found arguments!");
            try {
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
            listener = (DialogDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogDoneListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_choose_ingredient, container);
        Dialog dialog = getDialog();
        dialog.setTitle(getResources().getString(R.string.dialog_title));


        Button cancelButton = (Button) view.findViewById(R.id.dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDone(false, ila.getChosenIngredients());
                dismiss();
            }
        });

        Button confirmButton = (Button) view.findViewById(R.id.dialog_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDone(true, ila.getChosenIngredients());
                dismiss();
            }
        });

        updateIngredients(view);
        return view;
    }


    private void updateIngredients(View view) {
        IngredientRepository ir = new IngredientRepository(getActivity().getApplicationContext());
        List<Ingredient> ingredientList = ir.getAllIngredients();
        List<Ingredient.Category> ingredientCategories = new ArrayList<>(Arrays.asList(Ingredient.Category.values()));
        HashMap<Ingredient.Category, List<Ingredient>> ingredients = new HashMap<>();
        Log.v(TAG, "Size on ingredientList: " + ingredientList.size());
        for (Ingredient.Category c : ingredientCategories) {
            List<Ingredient> temp = new ArrayList<>();
            for (Ingredient i : ingredientList) {
                Log.v(TAG, "Comparing " + c.toString() + " to " + i.getCategory());
                if (c.equals(i.getCategory())) {
                    temp.add(i);
                    Log.v(TAG, "Adding ingredient: " + i.toString());
                }
            }
            if (temp.size() != 0) {
                ingredients.put(c, temp);
                Log.v(TAG, "Adding list " + temp.toString() + " " + c.toString());
            }

        }
        ila = new IngredientListAdapter(getActivity().getApplicationContext(), ingredientCategories, ingredients, chosenIngredients);
        try {
            ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.dialog_ingredient_listview);
            expandableListView.setAdapter(ila);
        } catch (NullPointerException e) {
            Log.e(TAG, "Couldn't find expandableListView! " + e.toString());
            e.printStackTrace();
        }
    }

    public interface DialogDoneListener {
        void onDone(boolean status, ArrayList<Ingredient> chosenIngredients);
    }
}
