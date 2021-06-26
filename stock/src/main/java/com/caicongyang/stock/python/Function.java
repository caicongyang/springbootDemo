package com.caicongyang.stock.python;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.util.Properties;

public class Function {

    static String pythonFile = "/Users/caicongyang/PycharmProjects/financial-engineering/program/python/com/caicongyang/financial/engineering/stock_select_strategy/xuangu002.py";

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
//    //['/Users/caicongyang/PycharmProjects/financial-engineering/program/python/com/caicongyang/financial/engineering/stock_select_strategy', '/Users/caicongyang/PycharmProjects/financial-engineering', '/usr/local/Cellar/python/3.7.6_1/Frameworks/Python.framework/Versions/3.7/lib/python37.zip', '/usr/local/Cellar/python/3.7.6_1/Frameworks/Python.framework/Versions/3.7/lib/python3.7', '/usr/local/Cellar/python/3.7.6_1/Frameworks/Python.framework/Versions/3.7/lib/python3.7/lib-dynload', '/usr/local/lib/python3.7/site-packages']
//
//
//
//        //['/Users/caicongyang/PycharmProjects/financial-engineering/program/python/com/caicongyang/financial/engineering/stock_select_strategy', '/Users/caicongyang/PycharmProjects/financial-engineering', '/usr/local/Cellar/python/3.7.3/Frameworks/Python.framework/Versions/3.7/lib/python37.zip', '/usr/local/Cellar/python/3.7.3/Frameworks/Python.framework/Versions/3.7/lib/python3.7', '/usr/local/Cellar/python/3.7.3/Frameworks/Python.framework/Versions/3.7/lib/python3.7/lib-dynload', '/Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages', '/Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages/setuptools-40.8.0-py3.7.egg', '/Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages/pip-19.0.3-py3.7.egg']
////        interpreter.exec("import sys");
////        interpreter.exec("sys.path.append('/Users/caicongyang/PycharmProjects/financial-engineering/program/python/com/caicongyang/financial/engineering/stock_select_strategy')");
////        interpreter.exec("sys.path.append('/Users/caicongyang/PycharmProjects/financial-engineering')");
////        interpreter.exec("sys.path.append('/usr/local/Cellar/python/3.7.3/Frameworks/Python.framework/Versions/3.7/lib/python37.zip')");
////        interpreter.exec("sys.path.append('/usr/local/Cellar/python/3.7.3/Frameworks/Python.framework/Versions/3.7/lib/python3.7')");
////        interpreter.exec("sys.path.append('/usr/local/Cellar/python/3.7.3/Frameworks/Python.framework/Versions/3.7/lib/python3.7/lib-dynload')");
////        interpreter.exec("sys.path.append('/Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages')");
////        interpreter.exec("sys.path.append('/Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages/setuptools-40.8.0-py3.7.egg')");
////        interpreter.exec("sys.path.append('/Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages/pip-19.0.3-py3.7.egg')");
//
//        ///Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages/setuptools-40.8.0-py3.7.egg', '/Users/caicongyang/PycharmProjects/financial-engineering/venv/lib/python3.7/site-packages/pip-19.0.3-py3.7.egg'
//
//        interpreter.execfile(pythonFile);
//
//
//        // 第一个参数为期望获得的函数（变量）的名字，第二个参数为期望返回的对象类型
//        PyFunction pyFunction = interpreter.get("getStockPrice", PyFunction.class);
//        String stock_code = "603797.XSHG";
//        String  trading_day = "2019-01-02";
//
//        //调用函数，如果函数需要参数，在Java中必须先将参数转化为对应的“Python类型”
//        PyObject pyobj = pyFunction.__call__(new PyString(stock_code), new PyString(trading_day));
//        System.out.println("the anwser is: " + pyobj);
//    }


}
