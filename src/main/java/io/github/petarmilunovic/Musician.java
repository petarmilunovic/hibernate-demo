package io.github.petarmilunovic;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "musicians")
public class Musician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musician_id")
    private int musicianId;
    @Column(name = "musician_name")
    private String name;
    @Column(name = "musician_type")
    private String type;
    @Column(name = "started_performing")
    private int startedPerforming;
    @Column(name = "stopped_performing")
    private int stoppedPerforming;
    @Column (name = "official_website")
    private String website;
    @OneToMany(mappedBy = "musician")
    @Cascade(CascadeType.ALL)
    private Set<Album> albums;

    public Musician(int musicianId, String name, String type, int startedPerforming, int stoppedPerforming, String website, Set<Album> albums) {
        this.musicianId = musicianId;
        this.name = name;
        this.type = type;
        this.startedPerforming = startedPerforming;
        this.stoppedPerforming = stoppedPerforming;
        this.website = website;
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "\n--- Musician ---" +
                "\nID: " + musicianId +
                "\nName: " + name +
                "\nType: " + type +
                "\nWebsite: " + website +
                "\nStarted performing: " + startedPerforming +
                "\nStopped performing: " + stoppedPerforming;
    }

    public int getMusicianId() {
        return musicianId;
    }

    public void setMusicianId(int musicianId) {
        this.musicianId = musicianId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartedPerforming() {
        return startedPerforming;
    }

    public void setStartedPerforming(int startedPerforming) {
        this.startedPerforming = startedPerforming;
    }

    public int getStoppedPerforming() {
        return stoppedPerforming;
    }

    public void setStoppedPerforming(int stoppedPerforming) {
        this.stoppedPerforming = stoppedPerforming;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

}
