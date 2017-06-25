package ru.ravens.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class DefaultClass implements Serializable {

    private Boolean operationOutput;
    private String token;



    public DefaultClass() {
    }

    public DefaultClass(Boolean operationOutput, String token) {
        this.operationOutput = operationOutput;
        this.token = token;
    }

    public Boolean getOperationOutput() {
        return operationOutput;
    }

    public void setOperationOutput(Boolean operationOutput) {
        this.operationOutput = operationOutput;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}