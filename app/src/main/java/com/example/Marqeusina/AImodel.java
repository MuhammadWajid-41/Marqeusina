package com.example.Marqeusina;

public class AImodel
{
    //String name,email;
    String musername, musermail, ratings, mpurl;


    public AImodel() {
    }

    public AImodel(String musername, String ratings, String mpurl, String musermail) {
        this.musername = musername;
        this.ratings = ratings;
        this.mpurl = mpurl;
        this.musermail = musermail;
    }

    public String getMusername() {
        return musername;
    }
    public void setMusername(String musername) {
        this.musername = musername;
    }

    public String getRatings() {
        return ratings;
    }
    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getMpurl() {
        return mpurl;
    }
    public void setMpurl(String mpurl) {
        this.mpurl = mpurl;
    }

    public String getMusermail() {
        return musermail;
    }
    public void setMusermail(String musermail) {
        this.musermail = musermail;
    }
}
