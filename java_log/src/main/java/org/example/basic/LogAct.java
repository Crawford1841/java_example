package org.example.basic;

import org.slf4j.Logger;

@FunctionalInterface
public interface LogAct {

    void apply(Logger logger);
}
