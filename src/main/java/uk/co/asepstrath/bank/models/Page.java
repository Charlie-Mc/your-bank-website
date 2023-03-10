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

    public static ArrayList<Page> Paginate(ArrayList<Object> objects, int pageSize) {
        ArrayList<Page> pages = new ArrayList<>();
        int count = 0;
        for (Object a : objects) {
            count++;
            if (count % pageSize == 0) {
                Page p = new Page(new ArrayList<>(objects.subList(count - pageSize, count)), count / pageSize);
                pages.add(p);
            }
            // If we're at the end of the list, add the remaining objects to a new page
            if (count == objects.size()) {
                Page p = new Page(new ArrayList<>(objects.subList(count - (count % pageSize), count)), count / pageSize + 1);
                pages.add(p);
            }
        }
        return pages;
    }

    @Override
    public String toString() {
        return "Page{" + "objects=" + objects.toString() + ", number=" + number + "}";
    }
}
