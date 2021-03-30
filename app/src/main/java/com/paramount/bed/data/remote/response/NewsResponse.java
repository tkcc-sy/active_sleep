package com.paramount.bed.data.remote.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsResponse implements Parcelable, Comparable {
    public String title;
    public String content;
    public String created_date;
    public String url;
    public String key;
    public int priority;
    @JsonProperty("news_id")
    public int id;

    public NewsResponse() {
    }

    public NewsResponse(String title, String content, String created_date, String url, String key, int priority, int newsId) {
        this.title = title;
        this.content = content;
        this.created_date = created_date;
        this.url = url;
        this.key = key;
        this.priority = priority;
        this.id = newsId;
    }

    protected NewsResponse(Parcel in) {
        title = in.readString();
        content = in.readString();
        created_date = in.readString();
        url = in.readString();
        key = in.readString();
        priority = in.readInt();
        id = in.readInt();
    }

    public static final Creator<NewsResponse> CREATOR = new Creator<NewsResponse>() {
        @Override
        public NewsResponse createFromParcel(Parcel in) {
            return new NewsResponse(in);
        }

        @Override
        public NewsResponse[] newArray(int size) {
            return new NewsResponse[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated_date() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(created_date);
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(created_date);
        dest.writeString(url);
        dest.writeString(key);
        dest.writeInt(priority);
        dest.writeInt(id);
    }

    private static final NewsResponse newsResponse = new NewsResponse();
    public static NewsResponse getInstance(){return newsResponse;}

    @Override
    public int compareTo(Object o) {
        int compareage = ((NewsResponse)o).getPriority();
        return this.priority-compareage;
    }

    public String getKeyTag(){
        String tag = "";

        if(key.equalsIgnoreCase("NEWS_BIRTH_DATE")){
            tag = "UI000504C006";
        }else if(key.equalsIgnoreCase("NEWS_MAINTENANCE")){
            tag = "UI000504C007";
        }else if(key.equalsIgnoreCase("NEWS_MALFUNCTION")){
            tag = "UI000504C008";
        }else if(key.equalsIgnoreCase("NEWS_REGULAR")){
            tag = "UI000504C009";
        }

        return tag;
    }
}
