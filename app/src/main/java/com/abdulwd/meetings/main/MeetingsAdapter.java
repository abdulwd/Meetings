package com.abdulwd.meetings.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abdulwd.meetings.R;
import com.abdulwd.meetings.models.Slot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.Item> {

  private final List<Slot> slots;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
  private SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm", Locale.US);

  MeetingsAdapter(List<Slot> slots) {
    this.slots = slots;
  }

  @NonNull
  @Override
  public Item onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    return new Item(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slot, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull Item item, int position) {
    Slot slot = slots.get(position);

    item.startDate.setText(getFormattedTime(slot.getStartTime()));
    item.endDate.setText(getFormattedTime(slot.getEndTime()));
    item.description.setText(slot.getDescription());
  }

  private String getFormattedTime(String time) {
    try {
      time = dateFormat.format(parseFormat.parse(time));
    } catch (ParseException e) {
      Log.e("MeetingsAdapter", "Unable to parse date", e);
    }
    return time;
  }

  @Override
  public int getItemCount() {
    return slots.size();
  }

  static class Item extends RecyclerView.ViewHolder {
    @BindView(R.id.start_date)
    TextView startDate;
    @BindView(R.id.end_date)
    TextView endDate;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.attendees)
    TextView attendees;

    Item(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
