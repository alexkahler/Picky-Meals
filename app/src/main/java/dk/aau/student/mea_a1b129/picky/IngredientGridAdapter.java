package dk.aau.student.mea_a1b129.picky;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class IngredientGridAdapter extends BaseAdapter {
    private Context context;
    public List<Ingredient> ingredients = new ArrayList<>();

    /**
     * Constructer
     * @param context
     */
    public IngredientGridAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            textView = new TextView(context);
            //textView.setLayoutParams(new GridView.LayoutParams(90, 90));
            //textView.setPadding(5, 5, 5, 5);
        }
        else {
            textView = (TextView) convertView;
        }

        System.out.println("IGA: Setting text");
        textView.setText(ingredients.get(position).getName());
        return textView;
    }

    @Override
    public int getCount() {
        System.out.println("IGA: Getting size");
        return ingredients.size();
    }

    @Override
    public Object getItem(int position) {
        System.out.println("IGA: " + ingredients.get(position).getName());
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
