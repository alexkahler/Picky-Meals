package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 */
class IngredientGridAdapter extends BaseAdapter {
    private static final String TAG = "IGA";
    private final Context context;
    private List<Ingredient> ingredients = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context the context of the app
     * @see Context
     */
    public IngredientGridAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }


    @SuppressWarnings("deprecation")
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(15, 15, 15, 15);
            textView.setTextSize(18);
            textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
        } else {
            textView = (TextView) convertView;
        }

        textView.setBackgroundResource(R.drawable.rounded_corners);
        GradientDrawable d = (GradientDrawable) textView.getBackground();
        if (ingredients.isEmpty()) {
            ingredients.add(new Ingredient("No ingredients to show.", Ingredient.Category.Other));
        }
        if (Build.VERSION.SDK_INT > 23) {
            //Set colors on the ingredients tags
            switch (ingredients.get(position).getCategory()) {
                case Vegetable: {
                    d.setColor(context.getResources().getColor(R.color.colorVegetable, null));
                    break;
                }
                case Meat: {
                    d.setColor(context.getResources().getColor(R.color.colorMeat, null));
                    break;
                }
                case Spice: {
                    d.setColor(context.getResources().getColor(R.color.colorSpice, null));
                    break;
                }
                case Poultry: {
                    d.setColor(context.getResources().getColor(R.color.colorPoultry, null));
                    break;
                }
                case Oil: {
                    d.setColor(context.getResources().getColor(R.color.colorOil, null));
                    break;
                }
                case Herb: {
                    d.setColor(context.getResources().getColor(R.color.colorHerb, null));
                    break;
                }
                case Fruit: {
                    d.setColor(context.getResources().getColor(R.color.colorFruit, null));
                    break;
                }
                case Nuts_Seeds: {
                    d.setColor(context.getResources().getColor(R.color.colorNuts_Seeds, null));
                    break;
                }
                case Dairy: {
                    d.setColor(context.getResources().getColor(R.color.colorDairy, null));
                    break;
                }
                case Cereal: {
                    d.setColor(context.getResources().getColor(R.color.colorCereal, null));
                    break;
                }
                case Seafood: {
                    d.setColor(context.getResources().getColor(R.color.colorSeafood, null));
                }
                default: {
                    d.setColor(context.getResources().getColor(R.color.colorOther, null));
                    break;
                }
            }
        } else {
            //Set colors on the ingredients tags
            switch (ingredients.get(position).getCategory()) {
                case Vegetable: {
                    d.setColor(context.getResources().getColor(R.color.colorVegetable));
                    break;
                }
                case Meat: {
                    d.setColor(context.getResources().getColor(R.color.colorMeat));
                    break;
                }
                case Spice: {
                    d.setColor(context.getResources().getColor(R.color.colorSpice));
                    break;
                }
                case Poultry: {
                    d.setColor(context.getResources().getColor(R.color.colorPoultry));
                    break;
                }
                case Oil: {
                    d.setColor(context.getResources().getColor(R.color.colorOil));
                    break;
                }
                case Herb: {
                    d.setColor(context.getResources().getColor(R.color.colorHerb));
                    break;
                }
                case Fruit: {
                    d.setColor(context.getResources().getColor(R.color.colorFruit));
                    break;
                }
                case Nuts_Seeds: {
                    d.setColor(context.getResources().getColor(R.color.colorNuts_Seeds));
                    break;
                }
                case Dairy: {
                    d.setColor(context.getResources().getColor(R.color.colorDairy));
                    break;
                }
                case Cereal: {
                    d.setColor(context.getResources().getColor(R.color.colorCereal));
                    break;
                }
                case Seafood: {
                    d.setColor(context.getResources().getColor(R.color.colorSeafood));
                }
                default: {
                    d.setColor(context.getResources().getColor(R.color.colorOther));
                    break;
                }
            }
        }
        textView.setText(ingredients.get(position).getName());
        return textView;
    }

    @Override
    public int getCount() {
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
