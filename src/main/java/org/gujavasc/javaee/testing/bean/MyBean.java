package org.gujavasc.javaee.testing.bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class MyBean {
    private boolean alive;

    public boolean isAlive() {
        return alive;
    }

    @PostConstruct
    public void init() {
        alive = true;
    }

}