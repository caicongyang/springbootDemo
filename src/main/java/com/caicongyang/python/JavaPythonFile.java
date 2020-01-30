package com.caicongyang.python;

import org.python.util.PythonInterpreter;

public class JavaPythonFile {
    public static void main(String[] args) {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("/Users/caicongyang/PycharmProjects/financial-engineering/program/python/com/caicongyang/financial/engineering/stock_select_strategy/JoinQuantUtil.py");
    }
}
