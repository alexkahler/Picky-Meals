package dk.aau.student.mea_a1b129.picky;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
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
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(5, 5, 5, 5);
            textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));

        }
        else {
            textView = (TextView) convertView;
        }

        Log.d("IGA", "Setting text");
        textView.setBackgroundResource(R.drawable.rounded_corners);
        GradientDrawable d = (GradientDrawable) textView.getBackground();

        //Set colors on the ingredients tags
        switch (ingredients.get(position).getCategory()) { //TODO: Make API test for getColor() method.
            case "Vegetable": {
                d.setColor(context.getResources().getColor(R.color.colorVegetable));
                break;
            }
            case "Meat": {
                d.setColor(context.getResources().getColor(R.color.colorMeat));
                break;
            }
            case "Spice": {
                d.setColor(context.getResources().getColor(R.color.colorSpice));
                break;
            }
            case "Poultry": {
                d.setColor(context.getResources().getColor(R.color.colorPoultry));
                break;
            }
            case "Oil": {
                d.setColor(context.getResources().getColor(R.color.colorOil));
                break;
            }
            case "Herb": {
                d.setColor(context.getResources().getColor(R.color.colorHerb));
                break;
            }
            case "Fruit": {
                d.setColor(context.getResources().getColor(R.color.colorFruit));
                break;
            }
            case "Nuts_Seeds": {
                d.setColor(context.getResources().getColor(R.color.colorNuts_Seeds));
                break;
            }
            case "Dairy": {
                d.setColor(context.getResources().getColor(R.color.colorDairy));
                break;
            }
            case "Cereal": {
                d.setColor(context.getResources().getColor(R.color.colorCereal));
                break;
            }
            default: {
                d.setColor(context.getResources().getColor(R.color.colorOther));
                break;
            }
        }
        textView.setText(ingredients.get(position).getName());

        return textView;
    }

    @Override
    public int getCount() {
        Log.d("IGA", "Getting count");
        return ingredients.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
