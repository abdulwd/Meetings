package com.abdulwd.meetings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Slot {

  @SerializedName("start_time")
  @Expose
  private String startTime;
  @SerializedName("end_time")
  @Expose
  private String endTime;
  @SerializedName("description")
  @Expose
  private String description;
  @SerializedName("participants")
  @Expose
  private List<String> participants;

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getParticipants() {
    return participants;
  }

  public void setParticipants(List<String> participants) {
    this.participants = participants;
  }
}