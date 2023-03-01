package uk.co.asepstrath.bank.models;

import java.util.ArrayList;

public class Page {
    public ArrayList<Object> objects;
    public int number;
    public int count;
    public boolean isCurrent;

    public Page(ArrayList<Object> objects, int number) {
        this.objects = objects;
        this.number = number;
        this.count = objects.size();
    }

    public Page(int number) {
        this(new ArrayList<>(), number);
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object> objects) {
        this.objects = objects;
        this.count = objects.size();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean getCurrent() {
        return this.isCurrent;
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    @Override
    public String toString() {
        return "Page{" + "objects=" + objects.toString() + ", number=" + number + "}";
    }
}
