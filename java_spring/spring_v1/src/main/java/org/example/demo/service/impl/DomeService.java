package org.example.demo.service.impl;

import org.example.demo.service.IDemoService;
import org.example.mvcframework.annotation.Service;

@Service
public class DomeService implements IDemoService {

    @Override
    public String get(String name) {
        return "My name is "+name;
    }
}
