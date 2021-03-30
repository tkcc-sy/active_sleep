package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSleepResetStatusResponse{

	@JsonProperty("sleep_reset_timing")
	private int sleepResetTiming;

	@JsonProperty("sleep_reset_remains")
	private int sleepResetRemains;

	@JsonProperty("sleep_reset_datetime")
	private String sleepResetDatetime;

	public void setSleepResetTiming(int sleepResetTiming){
		this.sleepResetTiming = sleepResetTiming;
	}

	public int getSleepResetTiming(){
		return sleepResetTiming;
	}

	public void setSleepResetRemains(int sleepResetRemains){
		this.sleepResetRemains = sleepResetRemains;
	}

	public int getSleepResetRemains(){
		return sleepResetRemains;
	}

	public void setSleepResetDatetime(String sleepResetDatetime){
		this.sleepResetDatetime = sleepResetDatetime;
	}

	public String getSleepResetDatetime(){
		return sleepResetDatetime;
	}
}