package com.example.raajesharunachalam.communityserviceconnector;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Class that is an adapter to a RecyclerView which binds data from Event objects to their
 * associated Viewholders.
 * <p>
 * Created by raajesharunachalam on 4/9/17.
 *
 * @author arnchlm2
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventListHolder> {
    private static final int IMAGE_WIDTH = 130;
    private static final int IMAGE_HEIGHT = 175;

    private Context context;
    private ArrayList<Event> events;
    long calendarID;

    public EventAdapter(Context context, ArrayList<Event> events, long calendarID) {
        this.context = context;
        this.events = events;
        this.calendarID = calendarID;
    }

    public void setEvents(ArrayList<Event> newEvents) {
        this.events = newEvents;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Inflates an XML file for an EventListHolder ViewHolder so it can be displayed on screen and
     * returns this set of newly inflated XML elements.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return An EventListHolder ViewHolder that has been properly created and its associated XML
     * file has been properly inflated.
     */
    @Override
    public EventListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        int movieItemLayout = R.layout.event_list_item;
        boolean shouldAttachToParent = false;
        View view = inflater.inflate(movieItemLayout, parent, shouldAttachToParent);
        return new EventListHolder(view);
    }

    /**
     * Takes data from a given event object and places it in the proper XML elements of the
     * associated Viewholder. Also, initializes onClickListener to get further event details.
     *
     * @param holder   An EventListHolder ViewHolder object that represents each Event in the
     *                 Recyclerview.
     * @param position The index of the current Event element that is being bound with data and
     *                 displayed.
     */
    @Override
    public void onBindViewHolder(final EventListHolder holder, final int position) {
        final Event event = events.get(position);

        Picasso.with(context).load(event.getImageURL()).resize(IMAGE_WIDTH, IMAGE_HEIGHT).into(holder.eventLogo);
        holder.eventTitle.setText(event.getTitle());

        //Gets a properly formatted description with the appropriate maximum length
        String description = Format.formatDescription(event.getDescription());
        holder.eventDescription.setText(description);

        holder.eventAddress.setText(event.getAddress());
        holder.eventStart.setText(event.getStartDate() + " At " + event.getStartTime());
        holder.eventEnd.setText(event.getEndDate() + " At " + event.getEndTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra(context.getString(R.string.event_key), event);
                context.startActivity(intent);
            }
        });
    }

    /**
     * Returns the number of events contained in this adapter.
     *
     * @return The number of Event objects that this adapter has.
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Represents a ViewHolder for each Event object with its associated XML elements where data
     * will be bound.
     */
    class EventListHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView eventLogo;
        TextView eventTitle;
        TextView eventDescription;
        TextView eventAddress;
        TextView eventStart;
        TextView eventEnd;

        public EventListHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            eventLogo = (ImageView) (itemView.findViewById(R.id.event_logo));
            eventTitle = (TextView) (itemView.findViewById(R.id.event_title));
            eventDescription = (TextView) (itemView.findViewById(R.id.event_description));
            eventAddress = (TextView) (itemView.findViewById(R.id.event_address));
            eventStart = (TextView) (itemView.findViewById(R.id.event_start));
            eventEnd = (TextView) (itemView.findViewById(R.id.event_end));
        }
    }
}
