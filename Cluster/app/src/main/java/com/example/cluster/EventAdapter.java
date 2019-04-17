package com.example.cluster;

import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.TextView;

        import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    //this is going to be specifically for managed events I guess? Because we need to add an edit button to these but not to events on other activities
    private static final int MAX_TITLE_LEN = 35;
    private static final int MAX_DESC_LEN = 35;
    private static ClickListener cl;

    private List<Event> events;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView title, startTime, description;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            startTime = (TextView) view.findViewById(R.id.start);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (cl != null) cl.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            if (cl != null) cl.onItemLongClick(getAdapterPosition(), view);
            return false;
        }
    }

    public void setClickListener(ClickListener cl) {
        this.cl = cl;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public EventAdapter(List<Event> eventList) {
        this.events = eventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event e = events.get(position);
        String dispTitle, dispDesc, dispStart;

        dispTitle = e.getTitle();
        if (dispTitle.length() > MAX_TITLE_LEN) {
            dispTitle = dispTitle.substring(0,MAX_TITLE_LEN).trim() + "...";
        }
        holder.title.setText(dispTitle);

        dispDesc = e.getDescription();
        if (dispDesc.length() > MAX_DESC_LEN) {
            dispDesc = dispDesc.substring(0, MAX_DESC_LEN).trim() + "...";
        }
        holder.description.setText(dispDesc);

        // startTime will always be in a format we expect
        holder.startTime.setText(e.getStartTime());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}