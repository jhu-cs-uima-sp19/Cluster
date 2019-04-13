package com.example.cluster;

import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ManagedViewHolder> {

    private static final int MAX_TITLE_LEN = 35;
    private static final int MAX_DESC_LEN = 35;

    private List<Event> managedEvents;

    public class ManagedViewHolder extends RecyclerView.ViewHolder {
        public TextView title, startTime, description;

        public ManagedViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            startTime = (TextView) view.findViewById(R.id.start);
        }
    }


    public EventAdapter(List<Event> eventList) {
        this.managedEvents = eventList;
    }

    @Override
    public ManagedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row, parent, false);

        return new ManagedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManagedViewHolder holder, int position) {
        Event e = managedEvents.get(position);
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
        return managedEvents.size();
    }
}