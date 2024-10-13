package com.example.Marqeusina;

public class cmodel
{
    //String name,email;
    String username, comment, purl, ratings;


    public cmodel() {
    }

    public cmodel(String username, String comment, String purl, String ratings) {
        this.username = username;
        this.comment = comment;
        this.purl = purl;
        this.ratings = ratings;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPurl() {
        return purl;
    }
    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getRatings() {
        return ratings;
    }
    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

}
