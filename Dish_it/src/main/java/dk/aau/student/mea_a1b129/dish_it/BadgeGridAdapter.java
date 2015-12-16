package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * An adapter for GridView to show Achievement badges.
 */
class BadgeGridAdapter extends BaseAdapter {

    private Context context;
    private List<GameEngine.BadgeType> badges;
    private List<GameEngine.Achievement> achievements;

    public BadgeGridAdapter(Context context, List<GameEngine.Achievement> achievements) {
        this.context = context;
        this.badges = Arrays.asList(GameEngine.BadgeType.values());
        this.achievements = achievements;
    }

    @Override
    public int getCount() {
        return badges.size();
    }

    @Override
    public Object getItem(int position) {
        return badges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the type of badge and save it.
        GameEngine.BadgeType badge = (GameEngine.BadgeType) getItem(position);
        //If we haven't made a layout for the curren grid-space, then do so.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.badge_grid_item, parent, false);
        }
        //Save our Views from the Layout.
        ImageView image = (ImageView) convertView.findViewById(R.id.badge_grid_item_image);
        TextView badgeDescription = (TextView) convertView.findViewById(R.id.badge_grid_item_description);

        //iterate through the Achievements and get the badgess.
        for(GameEngine.Achievement achievement : achievements) {
            //If the badge is != null and status is "true", then show it.
            if(achievement.getBadges().get(badge) != null && achievement.getBadges().get(badge)) {
                image.setImageResource(badge.getBadgeImageID());
                image.setContentDescription(badge.toString());
                badgeDescription.setText(badge.toString());
            }
        }
        //Return the view to the adapter.
        return convertView;
    }
}