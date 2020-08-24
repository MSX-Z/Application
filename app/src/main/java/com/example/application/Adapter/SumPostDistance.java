package com.example.application.Adapter;

import com.example.application.Retrieving_Data.Post;

public class SumPostDistance implements Comparable{
    private Post post;
    private double distance;
    private boolean isShrink = true;

    public SumPostDistance(){};

    public SumPostDistance(Post post,double distance){
        this.post = post;
        this.distance = distance;
    }
    //      GET     //
    public Post getPost() {
        return post;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isShrink() {
        return isShrink;
    }
    //      SET     //
    public void setPost(Post post) {
        this.post = post;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }

    @Override
    public int compareTo(Object o) {
        int compareage = (int)((SumPostDistance)o).distance;
        return ((int)this.distance - compareage);
    }

    @Override
    public String toString() {
        return "SumPostDistance{" +
                "post=" + post.toString() +
                ", distance=" + distance +
                '}';
    }
}
