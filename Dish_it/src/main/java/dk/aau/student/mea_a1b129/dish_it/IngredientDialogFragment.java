package dk.aau.student.mea_a1b129.dish_it;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IngredientDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class IngredientDialogFragment extends DialogFragment {

    private static final String TAG = IngredientDialogFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private int ingredientID;
    private Context context;
    private boolean inEditMode = false;

    public IngredientDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_edit_ingredient, container, false);
        context = view.getContext();


        Button confirmButton = (Button) view.findViewById(R.id.dialog_ingredient_confirm_button);
        Button cancelButton = (Button) view.findViewById(R.id.dialog_ingredient_cancel_button);
        final EditText ingredientName = (EditText) view.findViewById(R.id.dialog_ingredient_ingredient_name_edit);
        final Spinner ingredientCategory = (Spinner) view.findViewById(R.id.dialog_ingredient_choose_category);
        final IngredientRepository ir = new IngredientRepository(context);

        final ArrayAdapter<Ingredient.Category> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, Ingredient.Category.values());

        ingredientCategory.setAdapter(adapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredientCategory.getSelectedItem() == null) {
                    Toast.makeText(context, getResources().getText(R.string.ingredient_dialog_toast_missing_category_error), Toast.LENGTH_SHORT).show();
                    return;
                } else if (ingredientName.getText().toString().equals("")) {
                    Toast.makeText(context, getResources().getText(R.string.ingredient_dialog_toast_missing_name_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (inEditMode) {
                    if (ir.updateIngredient(ingredientID, ingredientName.getText().toString(), (Ingredient.Category) ingredientCategory.getSelectedItem())) {
                        Toast.makeText(context, getResources().getText(R.string.ingredient_dialog_toast_success), Toast.LENGTH_SHORT).show();
                        mListener.onFragmentInteraction(true);
                        dismiss();

                    } else {
                        Toast.makeText(context, getResources().getText(R.string.ingredient_dialog_toast_unknown_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (ir.insertIngredient(ingredientName.getText().toString(), (Ingredient.Category) ingredientCategory.getSelectedItem())) {
                        Toast.makeText(context, getResources().getText(R.string.ingredient_dialog_toast_success), Toast.LENGTH_SHORT).show();
                        mListener.onFragmentInteraction(true);
                        dismiss();
                    } else {
                        Toast.makeText(context, getResources().getText(R.string.ingredient_dialog_toast_unknown_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (getArguments() != null) {
            ingredientID = getArguments().getInt("ingredientID");
            ingredientName.setText(ir.getIngredient(ingredientID).getName());
            ingredientCategory.setSelection(adapter.getPosition(ir.getIngredient(ingredientID).getCategory()));
            inEditMode = true;
        }

        return view;
    }

    @Override
    public void onPause() {
        mListener = null;
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean newIngredientsAvailable);
    }

}
