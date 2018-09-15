package com.example.carlosjof.prjreport.model;


public class UploadImage {
    private String Description;
    private String URLImage;
    private String Location;

    public UploadImage() {

    }

    public UploadImage(String description, String urlImage, String location) {
        if (description.trim().equals("")) {
            description = "No Description";
            location = "No Location";
        }

        Description = description;
        URLImage = urlImage;
        Location = location;
    }

    public void setURLImage(String urlImage) {
        URLImage = urlImage;
    }

    public String getURLImage() {
        return URLImage;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLocation() {
        return Location;
    }
}
