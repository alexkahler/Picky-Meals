package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * A list adapter for Dinners.
 */
class DinnerListAdapter extends BaseAdapter {

    private final Context context;
    private List<Dinner> dinnerList;

    /**
     * Constructor for the DinnerListAdapter.
     * @param context the Application context
     * @param dinnerList the Dinner list, to be used in the adapter.
     * @see Context
     * @see Dinner
     */
    public DinnerListAdapter(Context context, List<Dinner> dinnerList) {
        this.context = context;
        this.dinnerList = dinnerList;
    }

    /**
     * Update the Dinner list with new Dinners. Remember to call notifyDataSetChanged method after.
     * @param newDinnerList
     */
    public void updateDinnerList(List<Dinner> newDinnerList) {
        dinnerList = newDinnerList;
    }

    /**
     *
     * @return
     */
    @Override
    public int getCount() {
        return dinnerList.size();
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return dinnerList.get(position);
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //The Dinner at the current position in the list.
        Dinner dinner = (Dinner) getItem(position);
        //If the view in the list hasn't been set and is still null
        if (convertView == null) {
            //Inflate the list item with a layout by using the Layout_Inflater_Service.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dinner_list_item, parent, false);
        }
        //Find the views in the layout.
        TextView dinnerTitle = (TextView) convertView.findViewById(R.id.dinner_list_item_title);
        TextView dinnerDate = (TextView) convertView.findViewById(R.id.dinner_list_item_date);
        TextView dinnerDescription = (TextView) convertView.findViewById(R.id.dinner_list_item_description);
        RatingBar dinnerRating = (RatingBar) convertView.findViewById(R.id.dinner_list_item_rating);

        //Populate the Views with information.
        dinnerTitle.setText(dinner.getName());
        if (dinner.getDate() != null) {
            dinnerDate.setText(context.getResources().getText(R.string.dinner_list_item_date_pretext) + " " + new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(dinner.getDate()));
        }
        dinnerRating.setRating(dinner.getRating());
        dinnerDescription.setText(dinner.getDescription());

        return convertView;
    }
}
