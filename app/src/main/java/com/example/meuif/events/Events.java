package com.example.meuif.events;

import com.google.firebase.Timestamp;

import java.util.List;

public class Events {
    private String type;
    private String description;
    private String title;
    private Timestamp started;
    private Timestamp ended;
    private List<Timestamp> Modifications;
    private List<String> colors;
    private String whoCreated;
    private String discipline;
    private String location;

    // Constructor
    public Events(String type, String description, String title, Timestamp started, Timestamp ended,
                   List<Timestamp> Modifications, List<String> colors, String whoCreated,
                   String discipline, String location) {
        this.type = type;
        this.description = description;
        this.title = title;
        this.started = started;
        this.ended = ended;
        this.Modifications = Modifications;
        this.colors = colors;
        this.whoCreated = whoCreated;
        this.discipline = discipline;
        this.location = location;
    }

    // Setter and Getter methods for 'type'
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Setter and Getter methods for 'description'
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Setter and Getter methods for 'title'
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Setter and Getter methods for 'started'
    public Timestamp getStarted() {
        return started;
    }

    public void setStarted(Timestamp started) {
        this.started = started;
    }

    // Setter and Getter methods for 'ended'
    public Timestamp getEnded() {
        return ended;
    }

    public void setEnded(Timestamp ended) {
        this.ended = ended;
    }

    // Setter and Getter methods for 'Modifications'
    public List<Timestamp> getModifications() {
        return Modifications;
    }

    public void setModifications(List<Timestamp> modifications) {
        Modifications = modifications;
    }

    // Setter and Getter methods for 'colors'
    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    // Setter and Getter methods for 'whoCreated'
    public String getWhoCreated() {
        return whoCreated;
    }

    public void setWhoCreated(String whoCreated) {
        this.whoCreated = whoCreated;
    }

    // Setter and Getter methods for 'discipline'
    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    // Setter and Getter methods for 'location'
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
