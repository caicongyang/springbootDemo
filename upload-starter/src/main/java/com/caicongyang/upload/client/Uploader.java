package com.caicongyang.upload.client;

import java.io.IOException;

public interface Uploader {


    public String upload(byte[] file, String folder, String key) throws IOException;

}
