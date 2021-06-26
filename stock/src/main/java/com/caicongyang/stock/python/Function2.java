package com.caicongyang.stock.python;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.util.Properties;

public class Function2 {

    static String pythonFile = "/Users/caicongyang/PycharmProjects/financial-engineering/program/python/com/caicongyang/financial/engineering/stock_select_strategy/JoinQuantUtil.py";

//    public static void main(String[] args) {
//
//        Properties props = new Properties();
//        Properties preprops = System.getProperties();
//        PythonInterpreter.initialize(preprops, props, new String[0]);
//        PythonInterpreter interpreter = new PythonInterpreter();
//
//
//        PySystemState sys = Py.getSystemState();
//        sys.path.add("/Users/caicongyang/PycharmProjects/financial-engineering/program/python/com/caicongyang/financial/engineering/stock_select_strategy");
//        sys.path.add("/Users/caicongyang/PycharmProjects/financial-engineering");
//        sys.path.add("/usr/local/Cellar/python/3.7.6_1/Frameworks/Python.framework/Versions/3.7/lib/python37.zip");
//        sys.path.add("/usr/local/Cellar/python/3.7.6_1/Frameworks/Python.framework/Versions/3.7/lib/python3.7");
//        sys.path.add("/usr/local/Cellar/python/3.7.6_1/Frameworks/Python.framework/Versions/3.7/lib/python3.7/lib-dynload");
//        sys.path.add("/usr/local/lib/python3.7/site-packages");
//
//
//
//        interpreter.execfile(pythonFile);
//
//
//        // 第一个参数为期望获得的函数（变量）的名字，第二个参数为期望返回的对象类型
//        PyFunction pyFunction = interpreter.get("get_stock_concept", PyFunction.class);
//        String stock_code = "603797.XSHG";
//
//        //调用函数，如果函数需要参数，在Java中必须先将参数转化为对应的“Python类型”
//        PyObject pyobj = pyFunction.__call__(new PyString(stock_code));
//        System.out.println("the anwser is: " + pyobj);
//    }


}
