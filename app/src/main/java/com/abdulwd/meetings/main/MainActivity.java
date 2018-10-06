package com.abdulwd.meetings.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.abdulwd.meetings.R;
import com.abdulwd.meetings.base.BaseActivity;
import com.abdulwd.meetings.data.remote.MeetingsService;
import com.abdulwd.meetings.models.Slot;
import com.abdulwd.meetings.schedule.ScheduleActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.abdulwd.meetings.utils.DateTimeUtils.NETWORK_DATE_FORMAT;
import static com.abdulwd.meetings.utils.DateTimeUtils.SIMPLE_DATE_FORMAT;

public class MainActivity extends BaseActivity {

  private static final String TAG = "MainActivity";
  private static final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
  @BindView(R.id.toolbar_date)
  TextView date;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.activity_main_schedule_meeting)
  AppCompatButton scheduleMeeting;
  @Inject
  MeetingsService meetingsService;
  private Calendar calendar = Calendar.getInstance();
  private Calendar today = Calendar.getInstance();

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
    intent.putExtra(ScheduleActivity.EXTRA_MEETING_DATE, NETWORK_DATE_FORMAT.format(calendar.getTime()));
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
    setToolbarDate();
    getSlot();
  }

  private void setToolbarDate() {
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      String dayDate = days[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + SIMPLE_DATE_FORMAT.format(calendar.getTime());
      date.setText(dayDate);
    } else {
      date.setText(SIMPLE_DATE_FORMAT.format(calendar.getTime()));
    }
  }

  private void getSlot() {
    meetingsService.getSlot(NETWORK_DATE_FORMAT.format(calendar.getTime()))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(d -> {
          setToolbarDate();
          calendar.clear(Calendar.HOUR_OF_DAY);
          calendar.clear(Calendar.MINUTE);
          calendar.clear(Calendar.SECOND);
          calendar.clear(Calendar.MILLISECOND);
          today.clear(Calendar.HOUR_OF_DAY);
          today.clear(Calendar.MINUTE);
          today.clear(Calendar.SECOND);
          today.clear(Calendar.MILLISECOND);
          if (calendar.before(today)) {
            scheduleMeeting.setEnabled(false);
            ViewCompat.setBackgroundTintList(scheduleMeeting, ContextCompat.getColorStateList(this, R.color.disabled));
          } else {
            scheduleMeeting.setEnabled(true);
            ViewCompat.setBackgroundTintList(scheduleMeeting, ContextCompat.getColorStateList(this, R.color.colorPrimary));
          }
        })
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
  protected void onDestroy() {
    super.onDestroy();
    if (disposable != null && !disposable.isDisposed()) disposable.dispose();
  }
}
