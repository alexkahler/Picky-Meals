package dk.aau.student.mea_a1b129.picky;

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
 */
public class DinnerListAdapter extends BaseAdapter {

    private List<Dinner> dinnerList;
    private Context context;

    public DinnerListAdapter(Context context, List<Dinner> dinnerList) {
        this.context = context;
        this.dinnerList = dinnerList;
    }

    public void updateDinnerList(List<Dinner> newDinnerList) {
        dinnerList = newDinnerList;
    }

    @Override
    public int getCount() {
        return dinnerList.size();
    }

    @Override
    public Object getItem(int position) {
        return dinnerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dinner dinner = (Dinner) getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dinner_list_item, parent, false);
        }
        TextView dinnerTitle = (TextView) convertView.findViewById(R.id.dinner_list_item_title);
        TextView dinnerDate = (TextView) convertView.findViewById(R.id.dinner_list_item_date);
        TextView dinnerDescription = (TextView) convertView.findViewById(R.id.dinner_list_item_description);
        RatingBar dinnerRating = (RatingBar) convertView.findViewById(R.id.dinner_list_item_rating);

        dinnerTitle.setText(dinner.getName());
        dinnerDate.setText(context.getResources().getText(R.string.dinner_list_item_date_pretext) + " " + new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(dinner.getDate()));
        dinnerRating.setRating(dinner.getRating());
        dinnerDescription.setText(dinner.getDescription());

        return convertView;
    }
}
