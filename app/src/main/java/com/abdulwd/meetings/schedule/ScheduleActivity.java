package com.abdulwd.meetings.schedule;

import android.os.Bundle;
import android.widget.TextView;

import com.abdulwd.meetings.R;
import com.abdulwd.meetings.base.BaseActivity;

import butterknife.BindView;

public class ScheduleActivity extends BaseActivity {

  public static final String EXTRA_MEETING_DATE = "meetingDate";

  @BindView(R.id.meeting_date)
  TextView meetingDate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_schedule);
    String date = getIntent().getStringExtra(EXTRA_MEETING_DATE);
    if (date != null) {
      meetingDate.setText(date);
    }
  }
}
