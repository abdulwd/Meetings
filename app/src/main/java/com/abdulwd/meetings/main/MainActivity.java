package com.abdulwd.meetings.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.abdulwd.meetings.R;
import com.abdulwd.meetings.base.BaseActivity;
import com.abdulwd.meetings.data.remote.MeetingsService;
import com.abdulwd.meetings.models.Slot;
import com.abdulwd.meetings.schedule.ScheduleActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity {

  private static final String TAG = "MainActivity";
  @BindView(R.id.toolbar_date)
  TextView date;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @Inject
  MeetingsService meetingsService;
  private Calendar calendar = Calendar.getInstance();
  private SimpleDateFormat networkDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
  private SimpleDateFormat toolbarDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
  private List<Slot> slots = new ArrayList<>();
  private MeetingsAdapter meetingsAdapter;
  private Disposable disposable;

  @OnClick(R.id.toolbar_previous)
  void gotoPrevious() {
    calendar.add(Calendar.DATE, -1);
    getSlot();
  }

  @OnClick(R.id.toolbar_next)
  void gotoNext() {
    calendar.add(Calendar.DATE, 1);
    getSlot();
  }

  @OnClick(R.id.activity_main_schedule_meeting)
  void scheduleMeeting() {
    Intent intent = new Intent(this, ScheduleActivity.class);
    intent.putExtra(ScheduleActivity.EXTRA_MEETING_DATE, networkDateFormat.format(calendar.getTime()));
    startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    meetingsAdapter = new MeetingsAdapter(slots);
    recyclerView.setAdapter(meetingsAdapter);
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
    recyclerView.addItemDecoration(itemDecoration);
    date.setText(toolbarDateFormat.format(calendar.getTime()));
  }

  @Override
  protected void onResume() {
    super.onResume();
    getSlot();
  }

  private void getSlot() {
    meetingsService.getSlot(networkDateFormat.format(calendar.getTime()))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(d -> MainActivity.this.date.setText(toolbarDateFormat.format(calendar.getTime())))
        .doAfterTerminate(() -> meetingsAdapter.notifyDataSetChanged())
        .subscribe(new SingleObserver<List<Slot>>() {
          @Override
          public void onSubscribe(Disposable d) {
            disposable = d;
            MainActivity.this.slots.clear();
          }

          @Override
          public void onSuccess(List<Slot> slots) {
            MainActivity.this.slots.addAll(slots);
          }

          @Override
          public void onError(Throwable e) {
            Log.e(TAG, "Unable to fetch the slots", e);
          }
        });
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (disposable != null && !disposable.isDisposed()) disposable.dispose();
  }
}
