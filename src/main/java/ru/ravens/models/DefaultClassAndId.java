package ru.ravens.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class DefaultClassAndId implements Serializable
{
    private DefaultClass defaultClass = new DefaultClass();
    private int id;

    public DefaultClassAndId(int newID)
    {
        this.id = newID;
    }
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
