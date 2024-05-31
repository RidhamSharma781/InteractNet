package com.androiod.social_media_app.Model;

import java.util.ArrayList;

public class Story {
    private String storyBy;
    private String storyAt;
    ArrayList<UserStories> stories;

    public Story() {
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public String getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(String storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStories> getList() {
        return stories;
    }

    public void setList(ArrayList<UserStories> list) {
        this.stories = list;
    }
}
