package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.models.Page;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageUnitTest {

    @Test
    public void test_create_page() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("test1");
        objects.add("test2");
        objects.add("test3");
        objects.add("test4");
        objects.add("test5");
        Page p = new Page(objects, 1);

        assertEquals(5, p.count);
    }

    @Test
    public void test_current_page() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("test1");
        objects.add("test2");
        objects.add("test3");
        objects.add("test4");
        objects.add("test5");
        Page p = new Page(objects, 1);
        p.setCurrent(true);

        assertTrue(p.isCurrent);
    }

    @Test
    public void test_paginate() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("test1");
        objects.add("test2");
        objects.add("test3");
        objects.add("test4");
        objects.add("test5");
        objects.add("test6");
        ArrayList<Page> pages = Page.Paginate(objects, 2);

        assertEquals(3, pages.size() - 1);

    }

    @Test
    public void test_paginate_empty() {
        ArrayList<Object> objects = new ArrayList<>();
        ArrayList<Page> pages = Page.Paginate(objects, 2);

        assertEquals(0, pages.size());
    }

    @Test
    public void test_paginate_single() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("test1");
        ArrayList<Page> pages = Page.Paginate(objects, 2);

        assertEquals(1, pages.size());
    }
}
