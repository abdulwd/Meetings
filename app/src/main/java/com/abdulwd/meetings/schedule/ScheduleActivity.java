package com.abdulwd.meetings.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.abdulwd.meetings.R;
import com.abdulwd.meetings.base.BaseActivity;
import com.abdulwd.meetings.data.remote.MeetingsService;
import com.abdulwd.meetings.models.Slot;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.abdulwd.meetings.utils.DateTimeUtils.NETWORK_DATE_FORMAT;
import static com.abdulwd.meetings.utils.DateTimeUtils.TIME_FORMAT_12;
import static com.abdulwd.meetings.utils.DateTimeUtils.TIME_FORMAT_24;

public class ScheduleActivity extends BaseActivity {

  public static final String EXTRA_MEETING_DATE = "meetingDate";
  private static final String TAG = "ScheduleActivity";
  @BindView(R.id.meeting_date)
  TextView meetingDate;
  @BindView(R.id.start_time)
  TextView startTimeText;
  @BindView(R.id.end_time)
  TextView endTimeText;
  @Inject
  MeetingsService meetingsService;
  private Calendar calendar = Calendar.getInstance();
  private Calendar startTimeCalendar = Calendar.getInstance();
  private Calendar endTimeCalendar = Calendar.getInstance();
  private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      calendar.set(Calendar.MONTH, month);
      calendar.set(Calendar.YEAR, year);
      meetingDate.setText(NETWORK_DATE_FORMAT.format(calendar.getTime()));
    }
  };

  private TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      Calendar calendar = Calendar.getInstance();
      startTimeCalendar.set(Calendar.DAY_OF_MONTH, ScheduleActivity.this.calendar.get(Calendar.DAY_OF_MONTH));
      startTimeCalendar.set(Calendar.MONTH, ScheduleActivity.this.calendar.get(Calendar.MONTH));
      startTimeCalendar.set(Calendar.YEAR, ScheduleActivity.this.calendar.get(Calendar.YEAR));
      startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
      startTimeCalendar.set(Calendar.MINUTE, minute);

      startTimeCalendar.clear(Calendar.MILLISECOND);
      startTimeCalendar.clear(Calendar.SECOND);
      calendar.clear(Calendar.MILLISECOND);
      calendar.clear(Calendar.SECOND);
      if (startTimeCalendar.before(calendar)) {
        Toast.makeText(ScheduleActivity.this, "Cannot schedule a meeting for past", Toast.LENGTH_LONG).show();
        return;
      }
      startTimeText.setText(TIME_FORMAT_12.format(startTimeCalendar.getTime()));
    }
  };

  private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      endTimeCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
      endTimeCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
      endTimeCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
      endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
      endTimeCalendar.set(Calendar.MINUTE, minute);

      endTimeCalendar.clear(Calendar.MILLISECOND);
      endTimeCalendar.clear(Calendar.SECOND);
      startTimeCalendar.clear(Calendar.MILLISECOND);
      startTimeCalendar.clear(Calendar.SECOND);

      if (endTimeCalendar.before(startTimeCalendar)) {
        Toast.makeText(ScheduleActivity.this, "End time should be less than start time", Toast.LENGTH_LONG).show();
        return;
      }
      endTimeText.setText(TIME_FORMAT_12.format(endTimeCalendar.getTime()));
    }
  };
  private Disposable disposable;

  @OnClick(R.id.submit_button)
  void submit() {
    if (startTimeText.getText() == null || startTimeText.getText().equals("") ||
        endTimeText.getText() == null || endTimeText.getText().equals("")) {
      Toast.makeText(this, "Select start and end time", Toast.LENGTH_LONG).show();
      return;
    }
    checkSlot(startTimeText.getText(), endTimeText.getText());
  }

  private void checkSlot(CharSequence text, CharSequence text1) {
    Date start1, end1;
    try {
      start1 = TIME_FORMAT_12.parse(text.toString());
      end1 = TIME_FORMAT_12.parse(text1.toString());
      if (start1.compareTo(end1) > 0) {
        Toast.makeText(this, "Start time should be before end time", Toast.LENGTH_SHORT).show();
        return;
      }
    } catch (ParseException e) {
      Log.e(TAG, "Unable to parse the date", e);
      Toast.makeText(this, "Unable to parse the date", Toast.LENGTH_SHORT).show();
      return;
    }
    meetingsService.getSlot(meetingDate.getText().toString())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<Slot>>() {
          @Override
          public void onSubscribe(Disposable d) {
            disposable = d;
          }

          @Override
          public void onSuccess(List<Slot> slots) {
            boolean available = true;
            for (Slot slot : slots) {
              try {
                Date start2 = TIME_FORMAT_24.parse(slot.getStartTime());
                Date end2 = TIME_FORMAT_24.parse(slot.getEndTime());

                if (start1.before(end2) && start2.before(end1)) {
                  available = false;
                  break;
                }
              } catch (ParseException e) {
                Log.e(TAG, "Unable to parse the date", e);
                Toast.makeText(ScheduleActivity.this, "Unable to parse the date", Toast.LENGTH_SHORT).show();
                return;
              }
            }
            if (available) {
              Toast.makeText(ScheduleActivity.this, "Slot available", Toast.LENGTH_LONG).show();
            } else {
              Toast.makeText(ScheduleActivity.this, "Slot not available", Toast.LENGTH_LONG).show();
            }
          }

          @Override
          public void onError(Throwable e) {
            Toast.makeText(ScheduleActivity.this, "Error checking slot", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error checking slot", e);
          }
        });
  }

  @OnClick(R.id.toolbar_back)
  void goBack() {
    onBackPressed();
  }

  @OnClick(R.id.start_time)
  void setStartTime() {
    TimePickerDialog timePickerDialog = new TimePickerDialog(this, startTimeListener,
        startTimeCalendar.get(Calendar.HOUR_OF_DAY), startTimeCalendar.get(Calendar.MINUTE), false);
    timePickerDialog.show();
  }

  @OnClick(R.id.end_time)
  void setEndTime() {
    TimePickerDialog timePickerDialog = new TimePickerDialog(this, endTimeListener,
        endTimeCalendar.get(Calendar.HOUR_OF_DAY), endTimeCalendar.get(Calendar.MINUTE), false);
    timePickerDialog.show();
  }

  @OnClick(R.id.meeting_date)
  void chooseDate() {
    DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    datePickerDialog.show();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_schedule);
    String date = getIntent().getStringExtra(EXTRA_MEETING_DATE);
    if (date != null) {
      meetingDate.setText(date);
      String[] s = date.split("/");
      calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s[0]));
      calendar.set(Calendar.MONTH, Integer.parseInt(s[1]) - 1);
      calendar.set(Calendar.YEAR, Integer.parseInt(s[2]));
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (disposable != null && !disposable.isDisposed()) disposable.dispose();
  }
}
