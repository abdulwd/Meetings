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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.abdulwd.meetings.utils.DateTimeUtils.TIME_FORMAT_12;
import static com.abdulwd.meetings.utils.DateTimeUtils.TIME_FORMAT_24;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.Item> {

  private final List<Slot> slots;

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
      time = TIME_FORMAT_12.format(TIME_FORMAT_24.parse(time));
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
