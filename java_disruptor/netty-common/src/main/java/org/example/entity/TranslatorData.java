package org.example.entity;

import java.io.Serializable;

public class TranslatorData implements Serializable {


    private String id;
    private String name;
    private String message;

    public String getId() {
        return id;
    }

    public TranslatorData setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TranslatorData setName(String name) {
        this.name = name;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TranslatorData setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "TranslatorData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
