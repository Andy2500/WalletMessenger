package ru.ravens.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class DefaultClasssAndDateAndID implements Serializable
{
    private DefaultClass defaultClass = new DefaultClass();
    private int id;

    public void setDate(String date) {
        Date = date;
    }

    private String Date;

    public DefaultClasssAndDateAndID(int newID)
    {
        this.id = newID;
    }
    public DefaultClasssAndDateAndID() {}

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }


    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
