package org.gujavasc.javaee.testing.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Talk implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column
    private String title;
    @Column

    private int slots;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Attendee> Attendees = new HashSet<Attendee>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        if (id != null) {
            return id.equals(((Talk) that).id);
        }
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getSlots() {
        return this.slots;
    }

    public void setSlots(final int slots) {
        this.slots = slots;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (title != null && !title.trim().isEmpty())
            result += "title: " + title;
        result += ", slots: " + slots;
        return result;
    }

    public Set<Attendee> getAttendees() {
        return this.Attendees;
    }

    public void setAttendees(final Set<Attendee> Attendees) {
        this.Attendees = Attendees;
    }
}