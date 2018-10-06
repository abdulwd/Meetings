package com.abdulwd.meetings.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeUtils {
  public static final SimpleDateFormat NETWORK_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
  public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
  public static final SimpleDateFormat TIME_FORMAT_12 = new SimpleDateFormat("hh:mm a", Locale.US);
  public static final SimpleDateFormat TIME_FORMAT_24 = new SimpleDateFormat("hh:mm", Locale.US);
}
