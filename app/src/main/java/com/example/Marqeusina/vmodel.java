package com.example.Marqeusina;

public class vmodel
{
    //String name,email;
    String name, Date, guests_coming, purl, package_name, email, event_time, event_type, package_price, token_payed;


    public vmodel() {
    }

    public vmodel(String name, String Date, String guests_coming, String purl, String package_name, String email, String event_time, String event_type, String package_price, String token_payed) {
        this.name = name;
        this.Date = Date;
        this.guests_coming = guests_coming;
        this.purl = purl;
        this.package_name = package_name;
        this.email = email;

        this.event_time = event_time;
        this.event_type = event_type;
        this.package_price = package_price;
        this.token_payed = token_payed;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return Date;
    }
    public void setDate(String ratings) {
        this.Date = Date;
    }

    public String getGuests_coming() {
        return guests_coming;
    }
    public void setGuests_coming(String guests_coming) {
        this.guests_coming = guests_coming;
    }

    public String getPurl() {
        return purl;
    }
    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getPackage_name() {
        return package_name;
    }
    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getPackage_price() {
        return package_price;
    }

    public void setPackage_price(String package_price) {
        this.package_price = package_price;
    }

    public String getToken_payed() {
        return token_payed;
    }

    public void setToken_payed(String token_payed) {
        this.token_payed = token_payed;
    }
}


