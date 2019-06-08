package com.training.spring.bigcorp.model;

import java.util.Objects;
import java.util.UUID;

public class Captor {
    /**
     * Captor id
     */
    private String id;

    /**
     * Captor name
     */
    private String name;

    /**
     * Captor power source
     */
    private PowerSource powerSource;

    /**
     * Captor site
     */
    private Site site;

    @Deprecated
    public Captor() {
        // Use for serializer or deserializer
    }

    /**
     * Constructor to use with required property
     * @param name
     * @param powerSource
     * @param site
     */
    public Captor(String name, PowerSource powerSource, Site site) {
        this.name = name;
        this.powerSource = powerSource;
        this.site = site;
        this.id = UUID.randomUUID().toString();
    }

    public PowerSource getPowerSource() {return powerSource;}

    public void setPowerSource(PowerSource powerSource) {this.powerSource = powerSource;}

    public Site getSite() {return site;}

    public void setSite(Site site) {this.site = site;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Captor site = (Captor) o;
        return Objects.equals(name, site.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Captor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}