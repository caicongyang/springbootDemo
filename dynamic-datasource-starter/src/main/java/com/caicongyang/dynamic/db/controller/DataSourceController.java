package com.caicongyang.dynamic.db.controller;

import com.caicongyang.dynamic.db.config.DynamicDataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataSourceController {


    @Autowired
    private DynamicDataSourceManager dynamicDataSourceManager;


    @PostMapping("/update-datasource")
    public String updateDataSource() {
        dynamicDataSourceManager.reloadDataSource();
        return "DataSource updated!";
    }
}
