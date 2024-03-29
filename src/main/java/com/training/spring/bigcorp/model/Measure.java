package com.training.spring.bigcorp.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.Instant;

@Entity
@Table(name = "MEASURE")
public class Measure {

    /**
     * id
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * instant de la mesure
     */
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    @NotNull
    @Past
    private Instant instant;

    /**
     * Valeur de la mesure (W)
     */
    @Column(name= "VALUE_IN_WATT")
    @NotNull
    private Integer valueInWatt;

    /**
     * Capteur qui a fait la mesure
     */
    @ManyToOne
    private Captor captor;

    /**
     * Version
     */
    @Version
    private int version;

    @Deprecated
    public Measure(){

    }

    public Measure(Instant instant, Integer valueInWatt, Captor captor) {
        this.instant = instant;
        this.valueInWatt = valueInWatt;
        this.captor = captor;
    }

    @Override
    public String toString(){
        return "Measure : (Instant : "+instant
                +", Value : "+valueInWatt+"(W)"
                +", Capteur : "+captor+")";
    }

    @Override
    public boolean equals(Object o){
        if(o.getClass().equals(this.getClass())){
            Measure m = (Measure) o;
            return this.getInstant().equals(m.getInstant())
                    && this.getValueInWatt() == m.getValueInWatt()
                    && this.getCaptor().equals(m.getCaptor());
        }else{
            return false;
        }
    }

    @Override
    public int hashCode(){
        return getInstant().hashCode()
                + getCaptor().hashCode()
                + getValueInWatt().hashCode();
    }

    /*
     * Getters and Setters
     */
    public long getId() {return id; }

    public void setId(long id) {this.id = id; }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Integer getValueInWatt() {
        return valueInWatt;
    }

    public void setValueInWatt(Integer valueInWatt) {
        this.valueInWatt = valueInWatt;
    }

    public Captor getCaptor() {
        return captor;
    }

    public void setCaptor(Captor captor) {
        this.captor = captor;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
