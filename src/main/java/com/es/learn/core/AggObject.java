package com.es.learn.core;

public class AggObject {
    private String key;
    private long doc_count;
    private double value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getDoc_count() {
        return doc_count;
    }

    public void setDoc_count(long doc_count) {
        this.doc_count = doc_count;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
