package com.abdulwd.meetings.data.remote;

import com.abdulwd.meetings.models.Slot;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MeetingsService {
  @GET("/api/schedule")
  Single<List<Slot>> getSlot(@Query("date") String date);
}
