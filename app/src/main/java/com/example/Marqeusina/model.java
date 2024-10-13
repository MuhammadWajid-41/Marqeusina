package com.example.Marqeusina;

public class model
{
    //String name,email;
    String name, email, ratings, purl;


    public model() {
    }

    public model(String name, String ratings, String purl, String email) {
        this.name = name;
        this.ratings = ratings;
        this.purl = purl;
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRatings() {
        return ratings;
    }
    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getPurl() {
        return purl;
    }
    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
