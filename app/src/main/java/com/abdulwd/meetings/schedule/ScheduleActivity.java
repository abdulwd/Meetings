package com.abdulwd.meetings.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.abdulwd.meetings.R;
import com.abdulwd.meetings.base.BaseActivity;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

import static com.abdulwd.meetings.utils.DateTimeUtils.NETWORK_DATE_FORMAT;
import static com.abdulwd.meetings.utils.DateTimeUtils.TIME_FORMAT_12;

public class ScheduleActivity extends BaseActivity {

  public static final String EXTRA_MEETING_DATE = "meetingDate";
  @BindView(R.id.meeting_date)
  TextView meetingDate;
  @BindView(R.id.start_time)
  TextView startTimeText;
  @BindView(R.id.end_time)
  TextView endTimeText;
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
    if (startTimeText.getText() == null || startTimeText.getText().equals("")) {
      Toast.makeText(this, "Select start time", Toast.LENGTH_LONG).show();
      return;
    }
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
}
