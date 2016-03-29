package com.quemb.qmbform.sample.model;

import java.io.Serializable;

/**
 * Created by alvinzeng on 3/29/16.
 */
public class MockContent implements Serializable {

    public MockContent(String title) {
        this.title = title;
    }

    public String title;

    @Override
    public String toString() {
        return title;
    }
}
