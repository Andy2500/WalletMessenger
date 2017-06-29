package ru.ravens.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@XmlRootElement
public class DefaultClassWrapper implements Serializable
{
    DefaultClass defaultClass;

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }


    public DefaultClassWrapper(DefaultClass defaultClass1)
    {
        defaultClass = defaultClass1;
    }
}
