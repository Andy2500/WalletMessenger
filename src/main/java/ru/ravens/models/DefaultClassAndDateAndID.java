package ru.ravens.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement
public class DefaultClassAndDateAndID implements Serializable
{
    private DefaultClass defaultClass = new DefaultClass();
    private int id;
    private Date date;

    public DefaultClassAndDateAndID(int newID)
    {
        this.id = newID;
    }
    public DefaultClassAndDateAndID() {}


    public DefaultClass getDefaultClass() {
        return defaultClass;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
