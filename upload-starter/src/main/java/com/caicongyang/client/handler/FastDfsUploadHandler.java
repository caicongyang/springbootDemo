package com.caicongyang.client.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caicongyang.client.AccessLogger;
import com.caicongyang.client.UploadException;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.domain.ItemResult;
import com.caicongyang.client.domain.SerialResult;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import com.caicongyang.client.domain.ImageItem;
import com.caicongyang.client.util.DESPlus;
import com.caicongyang.client.util.ImageConfigUtil;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * fastdfs 存储上传核心类
 * Created by ZhouChenmin on 2017/5/2.
 */
public class FastDfsUploadHandler extends UploadHandler{

    private  static Logger log = LoggerFactory.getLogger(FastDfsUploadHandler.class);

    private static final String PARAM_REQUEST = "upload_request";
    private static final String PARAM_KEY = "creator_id";

    // TODO DEFAULT_CREATER服务端要求是Integer
    private static final String DEFAULT_CREATER = "110";
    public static  final String DEFAULT_RESOURCE_TYPE = "58";
    public static  final String DEFAULT_PICTURE_TYPE = "58";
    public static  final String DEFAULT_RESOURCE_ID = "0";
    public static  final String DEFAULT_MC_SITE_ID = "0";
    public static  final String DEFAULT_WATERMARK_SEQUENCE = "before";

    public static final String SERIES_MAJOR = "1";
    public static final String SERIES_NOT_MAJOR = "0";
    public static final String FILE_BACK_UP = "1";
    
    private static String hostIp = ImageConfigUtil.getIp();
    private static final String DEFAULT_NAMESPACE = "default";
    private static final String DEFAULT_POOL_NAME = "default";
    private static final String NAMESPACE = "namespace";
    private static final String DEFAULT_CHART_SET = "UTF-8";


    /**
     * groupName对应域名，比如
     * G00 对应 d1.ccoop.com.cn
     * G01 对应 d2.ccoop.com.cn
     */
    private static Map<String,String> group2domainMap = new HashMap<>();


    /**
     * 根据文件流上传，需要指定文件名，文件的长度 contentLength
     * @param fileName 文件名,需要后缀
     * @param poolName 上传的pool Name
     * @param io 文件IO流
     * @return
     * @throws com.odianyun.architecture.odfs.upload.client.UploadException
     */
    @Override
    public UploadResult upload(final String fileName,String poolName,final InputStream io) throws UploadException {
        return upload(fileName,poolName,io,new UploadImage());
    }


    /**
     * 根据文件流上传，需要指定文件名，文件的长度 contentLength
     * @param fileName 文件名,需要后缀
     * @param poolName 上传的pool Name
     * @param io 文件IO流
     * @param uploadImage 文件是否需要加水印，是否生成缩略图的配置信息，是否裁剪等
     * @return
     * @throws UploadException
     */
    @Override
    public UploadResult upload(final String fileName,String poolName,final InputStream io, UploadImage uploadImage) throws UploadException {

        log.info("use fastdfs upload file {} ",fileName);

        byte[]b = new byte[1024];
        int length = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        Part filePart = null;
        try {
            while ((length = io.read(b)) != -1){
                //dataOutputStream.write(b,0,length);
                byteArrayOutputStream.write(b,0,length);
            }
            filePart = new FilePart(fileName,new ByteArrayPartSource(fileName,byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            if (byteArrayOutputStream!= null){
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dataOutputStream!= null){
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (io != null){
                try {
                    io.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        List<Part> parts = new ArrayList<>(1);
        parts.add(filePart);
        return upload(parts,poolName,uploadImage);
    }


    /**
     * 文件上传，需要指定文件名称
     * @param uploadName 文件的上传名称，需要有后缀
     * @param poolName poolName
     * @param file 文件
     * @return
     * @throws UploadException
     */
    @Override
    public UploadResult upload(String uploadName, String poolName ,File file ) throws UploadException {

        return upload(uploadName,poolName,file,new UploadImage());
    }



    /**
     * 文件上传，需要指定文件名称
     * @param uploadName 文件的上传名称，需要有后缀
     * @param poolName poolName
     * @param file 文件
     * @param uploadImage 文件是否需要加水印，是否生成缩略图的配置信息，是否裁剪等
     * @return
     * @throws UploadException
     */
    public UploadResult upload(String uploadName, String poolName  ,File file, UploadImage uploadImage) throws UploadException {
        FilePart filePart = null;
        List<Part> parts = new ArrayList<Part>(1);
        try {
            filePart = new FilePart(uploadName,file);
        } catch (FileNotFoundException e) {
            log.error("file not found ",e);
        }
        if (filePart != null){
            parts.add(filePart);
        }
        return upload(parts, poolName, uploadImage);
    }


    /**
     * byte[] 数组上传
     * @return
     * @throws UploadException
     */
    @Override
    public UploadResult upload(String uploadName, String poolName ,byte[] content) throws UploadException{
        return upload(uploadName,poolName,content,new UploadImage());
    }


    @Override
    public UploadResult upload(String uploadName, String poolName  ,byte[] content, UploadImage uploadImage) throws UploadException {
        FilePart filePart = new FilePart(uploadName,new ByteArrayPartSource(uploadName,content));
        List<Part> parts = new ArrayList<>(1);
        parts.add(filePart);
        return upload(parts, poolName, uploadImage);
    }



    /**
     * 根据文件名上传
     * @param file 文件的绝对路径
     * @param poolName poolName
     * @return
     * @throws UploadException
     */
    public UploadResult upload(String file, String poolName) throws UploadException {
        return upload(new File(file), poolName, new UploadImage());
    }


    /**
     * 根据文件名上传
     * @param file 文件的绝对路径
     * @param poolName poolName
     * @param uploadImage 文件是否需要加水印，是否生成缩略图的配置信息，是否裁剪等
     * @return
     * @throws UploadException
     */
    @Override
    public UploadResult upload(String file, String poolName, UploadImage uploadImage) throws UploadException {
        return upload(new File(file), poolName, uploadImage);
    }


    /**
     * 批量上传图片
     * @param fileList 多个图片的绝对路径
     * @param poolName 上传的pool 名称
     * @param uploadImage 批量上传时，多个图片共享一个是否加水印，是否生成缩略图，是否裁剪
     *                    即，多张图片一视同仁
     * @return
     * @throws UploadException
     */
    @Override
    public UploadResult batchUpload(List<String> fileList, String poolName, UploadImage uploadImage) throws UploadException {
        if(fileList == null || fileList.size()<=0){
            throw new UploadException("Must provider one file for upload to fastdfs");
        }
        List<File> files = new ArrayList<>(1);
        for(String file:fileList){
            files.add(new File(file));
        }
        return uploadMulitFile(files, poolName, uploadImage);
    }


    /**
     * 根据file 对象上传。
     * @param file 文件对象
     * @param poolName pool 名称
     * @param uploadImage 文件是否需要加水印，是否生成缩略图的配置信息，是否裁剪等
     * @return
     * @throws UploadException
     */
    public UploadResult upload(File file,String poolName, UploadImage uploadImage) throws UploadException {
        if (file == null || file.isDirectory()) {
            throw new IllegalArgumentException("Illegal argument: " + file.getAbsolutePath());
        }
        List<File> files = new ArrayList<>(1);
        files.add(file);
        return uploadMulitFile(files, poolName, uploadImage);

    }

    /**
     * 直接文件路径
     * @param fileUrl
     * @param poolName
     * @return
     * @throws UploadException
     */
    @Override
    public UploadResult uploadByUrl(String fileUrl, String poolName) throws UploadException {

        InputStream inputStream = null;
        try {
            URL url = new URL(fileUrl);
            URLConnection urlConnection = url.openConnection();
            // 设置超时时间，避免一直阻塞
            urlConnection.setConnectTimeout(3*1000);
            urlConnection.setReadTimeout(10*1000);
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("can not open url: " + fileUrl , e);
        }

        return upload(getFileName(fileUrl),poolName,inputStream);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName)throws UploadException {
        return null;
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName, UploadImage uploadImage) throws UploadException{
        return null;
    }

    /**
     * 上传多个文件
     * @param fileList 文件对象
     * @param poolName pool 名称
     * @param uploadImage 文件是否需要加水印，是否生成缩略图的配置信息，是否裁剪等
     * @return
     * @throws UploadException
     */
    public UploadResult uploadMulitFile(List<File> fileList,String poolName, UploadImage uploadImage) throws UploadException {
        if (fileList == null || fileList.size()<=0) {
            throw new UploadException("Must provider one file for upload to fastdfs");
        }
        List<Part> parts = new ArrayList<>(fileList.size());
        for(File file:fileList){
            Part filePart;
            try {
                filePart = new FilePart(file.getName(), file);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File " + file.getAbsolutePath() + " not found", e);
            }
            parts.add(filePart);
        }
        return upload(parts,poolName,uploadImage);

    }

    private UploadResult upload(List<Part> filePartList, String poolName, UploadImage uploadImage) throws UploadException {

        if(filePartList == null || filePartList.size()<=0){
            throw new UploadException("Must provider one file for upload to fastdfs");
        }
        if(filePartList !=null && filePartList.size()>0) {
            JSONObject finalJson = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (Part filePart : filePartList) {

                JSONObject ups = new JSONObject();
                ups.put("creator_id", DEFAULT_CREATER);
                ups.put("namespace", NAMESPACE==null?DEFAULT_NAMESPACE:NAMESPACE);
                ups.put("pool_name", StringUtils.isEmpty(poolName) ? DEFAULT_POOL_NAME : poolName);
                ups.put("host_ip", hostIp);
                ups.put("action", UploadConstant.ActionType.UPLOAD.getOp());
                ups.put("name", filePart.getName());
                ups.put("backup", FILE_BACK_UP);

                ups.put("mc_site_id", DEFAULT_MC_SITE_ID);
                ups.put("resource_type", DEFAULT_RESOURCE_TYPE);
                ups.put("resource_id",DEFAULT_RESOURCE_ID);
                ups.put("pic_type", DEFAULT_PICTURE_TYPE);

                //added

                /*if (uploadImage.getImgWmIndex() > 0) {
                    ups.put("img_wm", uploadImage.getImgWmIndex());
                    ups.put("watermark_sequence", DEFAULT_WATERMARK_SEQUENCE);
                }

                if ( uploadImage.getWateImgUrl().length() > 0) {
                    ups.put("watermark_url", uploadImage.getWateImgUrl());
                    ups.put("watermark_gravity", uploadImage.getWaterMarkGravity().getValue());
                    //覆盖程度
                    ups.put("watermark_dissolve", uploadImage.getWaterMarkDissolve());
                    ups.put("watermark_sequence", DEFAULT_WATERMARK_SEQUENCE);
                }*/

                if (uploadImage.getImgWmIndex() > 0 || StringUtils.isNotBlank(uploadImage.getWateImgUrl() )){

                    ups.put("watermark_sequence", DEFAULT_WATERMARK_SEQUENCE);
                    ups.put("watermark_gravity", uploadImage.getWaterMarkGravity().getValue());
                    //水印透明程度
                    ups.put("watermark_dissolve", uploadImage.getWaterMarkDissolve());
                    ups.put("watermark_style",uploadImage.getWaterMarkStyle());

                    if (uploadImage.getImgWmIndex() > 0) {
                        ups.put("img_wm", uploadImage.getImgWmIndex());
                    }else if (StringUtils.isNotBlank(uploadImage.getWateImgUrl())){
                        ups.put("watermark_url", uploadImage.getWateImgUrl());
                    }
                }


                //无损压缩参数
                ups.put("strip", uploadImage.isStrip());


                //处理套图
                if (uploadImage.getImgSeries() != null && uploadImage.getImgSeries().size() > 0) {
                    JSONArray imageSeries = new JSONArray();
                    boolean crateMainSeries = false;
                    for (ImageItem series : uploadImage.getImgSeries()) {
                        if (!crateMainSeries) {
                            JSONObject mp = new JSONObject();
                            mp.put("img_wm", series.getImgWmIndex());
                            mp.put("is_major",SERIES_MAJOR);
                            imageSeries.add(mp);
                            crateMainSeries = true;
                        }
                        JSONObject job1 = new JSONObject();
                        job1.put("img_wm", series.getImgWmIndex());
                        job1.put("is_major", SERIES_NOT_MAJOR);
                        if (series.isNeedCrop()) {
                            job1.put("img_crop", series.getWidth() + "#" + series.getHeight());
                            job1.put("img_X", series.getImgX());
                            job1.put("img_Y", series.getImgY());
                        } else {
                            job1.put("img_scale", series.getWidth() + "#" + series.getHeight());
                        }
                        //job1.put("img_crop", series.getWidth()+"#"+series.getHeight());
                        imageSeries.add(job1);
                    }
                    ups.put("img_series", imageSeries);
                }

                jsonArray.add(ups);
            }
            finalJson.put("items", jsonArray);

            String resp = null;
            try {
                DESPlus jsonKey = new DESPlus(DEFAULT_CREATER);

                DESPlus defaultKey = new DESPlus();

                String p = finalJson.toString();
                p = URLEncoder.encode(p, DEFAULT_CHART_SET);

                Part[] parts = new Part[filePartList.size()+2];
                parts[0] = new StringPart(PARAM_REQUEST, jsonKey.encrypt(p));
                parts[1] = new StringPart(PARAM_KEY, defaultKey.encrypt(DEFAULT_CREATER));
                for (int i=0;i<filePartList.size();i++) {
                    parts[i+2] = filePartList.get(i);
                }
                String url = ImageConfigUtil.getPostUrl();
                resp = post(UploadConfig.UPLOAD_SERVER_ADDRESS, parts,null,true);
            } catch (Exception e) {
                throw new UploadException(e);
            }

            if (resp == null) {
                throw new UploadException("fail to upload: " + resp);
            }
            log.debug("fast dfs return {}" , resp);
            UploadResult result;
            try {
                result = JSON.parseObject(resp, UploadResult.class);
            } catch (Exception e) {
                log.error("parse result {}  failed:{}" , resp , e.getMessage());
                throw new UploadException(e);
            }
            for (ItemResult itemResult : result.getResultDetail()){
                if (itemResult != null ){

                    itemResult.setStore_url(getStorePathInFastDfs(itemResult.getUrl()));

                    if( itemResult.getDetails()!= null ){

                        for (int i = 0; i<itemResult.getDetails().size();i++){

                            SerialResult serialResult = itemResult.getDetails().get(i);
                            if (serialResult.isIs_major()){
                                itemResult.getDetails().remove(i);
                                i--;
                            }
                        }
                    }

                }
            }

            return result;
        }

        return null;

    }



    /**
     * 删除图片，可以删除多个
     * @param  fileName 是需要删除文件的访问路径的集合
     * @param poolName
     * @return
     * @throws UploadException
     */
    public UploadResult delete(String fileName, String poolName ) throws UploadException {
        return delete(Arrays.asList(fileName),poolName,true);
    }



    /**
     * 删除图片，可以删除多个
     * @param fileList 是需要删除文件的访问路径的集合
     * @param poolName
     * @return
     * @throws UploadException
     */
    @Override
    public UploadResult delete(List<String> fileList, String poolName,boolean deleteSeriesImage) throws UploadException {

        if(fileList == null || fileList.size()<=0){
            throw new UploadException("Must provider one file for upload to fastdfs");
        }

        if(fileList !=null && fileList.size()>0) {
            JSONObject finalJson = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (String fileUrl : fileList) {

                JSONObject ups = new JSONObject();
                ups.put("creator_id", DEFAULT_CREATER );
                ups.put("namespace", NAMESPACE == null ? DEFAULT_NAMESPACE : NAMESPACE);
                ups.put("pool_name", poolName);
                ups.put("action", UploadConstant.ActionType.DELETE.getOp());
                ups.put("target_url",fileUrl);
                ups.put("is_del_series",deleteSeriesImage);

                jsonArray.add(ups);
            }
            finalJson.put("items", jsonArray);

            String resp = null;
            try {
                DESPlus jsonKey = new DESPlus(DEFAULT_CREATER);

                DESPlus defaultKey = new DESPlus();

                String p = finalJson.toString();
                String joString = URLEncoder.encode(p, DEFAULT_CHART_SET);


                NameValuePair[] parts = new NameValuePair[] {
                        new NameValuePair(PARAM_REQUEST,
                                jsonKey.encrypt(joString)),
                        new NameValuePair(PARAM_KEY,
                                defaultKey.encrypt(DEFAULT_CREATER)) };
                resp = post(UploadConfig.UPLOAD_SERVER_ADDRESS,null,parts,false);
            } catch (Exception e) {
                throw new UploadException(e);
            }

            if (resp == null) {
                throw new UploadException("fail to upload: " + resp);
            }
            AccessLogger.log("resp: " + resp);
            UploadResult result;
            try {
                result = JSON.parseObject(resp, UploadResult.class);
            } catch (Exception e) {
                log.error("parse result {} failed: {}" ,resp , e.getMessage());
                throw new UploadException(e);
            }

            return result;
        }

        return null;

    }



    private String post(String url, Part[] parts,NameValuePair[] content,boolean isMultipart) throws HttpException, UnsupportedEncodingException {
        // 提交url
        HttpClient client = new HttpClient();
        // 设置相关参数
        client.getHttpConnectionManager().getParams().setConnectionTimeout(300 * 1000);
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Connection", "close");
        if(isMultipart && parts !=null) {
            method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
        }else if(content!=null){
            method.addParameters(content);
        }
        try {
            int statusCode = client.executeMethod(method);
            if (HttpStatus.SC_OK == statusCode) {// sc_ok=200
                InputStream resStream = method.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(resStream));
                StringBuffer resBuffer = new StringBuffer();
                String resTemp = "";
                while ((resTemp = br.readLine()) != null) {
                    resBuffer.append(resTemp);
                }

                return resBuffer.toString();
            } else {
                throw new HttpException("http error: with status code " + statusCode + " when post  url  " + url);
            }
        } catch (IOException e) {
            throw new HttpException("http error when post " + url , e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }


    @Override
    public InputStream download(String url) {

        try {
            if (StringUtils.isBlank(url)){
                throw new IllegalArgumentException("download url can not be null");
            }
            String fullUrl = getFullUrlIfNeed(url);
            URL httpUrl = new URL(fullUrl);

            HttpURLConnection urlConnection = (HttpURLConnection)httpUrl.openConnection();
            return urlConnection.getInputStream();

        }catch (Exception e){
            log.error("download file inputStream exception {}" , url ,e);
            throw new RuntimeException("download file inputStream exception :"+url ,e);
        }

    }

    /*@Override
    public File download(String url, String storeDirectory) {

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        File file = null;
        try {

            File fileDir = new File(storeDirectory);
            if (!fileDir.isDirectory()){
                throw new IllegalArgumentException( storeDirectory +" is not directory ");
            }
            if (!fileDir.exists()){
                fileDir.mkdirs();
            }

            file = new File(storeDirectory + "/" + getFileName(url));

            if ( !file.exists()) {

                inputStream = download(url);

                fileOutputStream = new FileOutputStream(file);

                byte[] b = new byte[1024];
                int length = 0;
                while ( (length = inputStream.read(b)) != -1){
                    fileOutputStream.write(b,0,length);
                }

            }

        }catch (Exception e){
            log.error("download file exception {}" , url ,e);
            throw new RuntimeException("download file exception :"+url ,e);
        }finally {

            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(fileOutputStream);


        }

        return file;
    }*/

    /**
     * 完整的url转成 存储在 fastdfs 上的路径
     * @param url
     * @return
     */
    public static String getStorePathInFastDfs(String url) {
        if (StringUtils.isBlank(url)){
            return url;
        }
        try {

            String groupName = getGroupName(url);
            String domainName  = getUploadDomain(groupName,false);
            String storePath = "";
            if (StringUtils.isBlank(domainName) || !url.contains(domainName)){
                // 如果要是没有找到domainName ，那么就不要替换了
                return url;
            }
            if (url.startsWith(UploadConstant.HTTP_PROTOCOL)){
                storePath = url.replaceAll(UploadConstant.HTTP_PROTOCOL,"");
                storePath = storePath.replaceAll(domainName,"");
            }
            if (url.startsWith(UploadConstant.HTTPS_PROTOCOL)){
                storePath = url.replaceAll(UploadConstant.HTTPS_PROTOCOL,"");
                storePath = storePath.replaceAll(domainName,"");
            }
            return storePath;

        }catch (Exception e){
            log.error("getStorePathInFastDfs error" ,e);
        }
        return url;


    }

    /**
     * 给文件的路径，获取到文件的完整url
     * 比如 /G00/M00/00/08/wKgUGFjlvA2AJ_OTAAALJz5HLjc628.jpg
     * 截取到 G00,获取到 domain，返回完整的url比如
     *   http://dev.odfs.download.odianyun.local/G00/M00/00/08/wKgUGFjlvA2AJ_OTAAALJz5HLjc628.jpg
     * @param url
     * @return
     */
    public static String getFullUrlIfNeed(String url) {

        if (url.startsWith(UploadConstant.HTTP_PROTOCOL) || url.startsWith(UploadConstant.HTTPS_PROTOCOL)){
            // 完整的地址
            return url;
        }
        String groupName = getGroupName(url);
        String domain = getUploadDomain(groupName,true);
        url = url.startsWith("/") ? url : "/"+url;
        return UploadConfig.getUploadProtocolPrefix() + domain + url;
    }

    public static String getGroupName(String url){

        if (StringUtils.isBlank(url)){
            return url;
        }
        String groupName = url ;
        if (groupName.startsWith(UploadConstant.HTTP_PROTOCOL) || groupName.startsWith(UploadConstant.HTTPS_PROTOCOL)){
            // 完整的地址
            groupName = groupName.replaceAll(UploadConstant.HTTP_PROTOCOL ,"");
            groupName = groupName.replaceAll(UploadConstant.HTTPS_PROTOCOL ,"");
            groupName = groupName.substring(groupName.indexOf("/")+1);
        }

        if (groupName.startsWith("/")){
            groupName = groupName.substring(1);
        }
        groupName = groupName.substring(0,groupName.indexOf("/"));
        return groupName;

    }



    /**
     *
     * @param groupName   G00
     * @return
     */
    public static synchronized String getUploadDomain(String groupName, boolean throwExceptionIfNull){

        if (StringUtils.isBlank(groupName)){
            return groupName;
        }

        if (group2domainMap == null || group2domainMap.isEmpty()){

            String domain = UploadConfig.getProperties(UploadConfig.FASTDFS_UPLOAD_SERVER_DOMAIN,"");
            if (StringUtils.isBlank(domain) && throwExceptionIfNull){
                throw new RuntimeException("can not get server config");
            }
            domain =domain.trim();
            //G00#d1.ccoop.com.cn;G01#d2.ccoop.com.cn
            for (String group2domain : domain.split(";")){

                if (StringUtils.isNotEmpty(group2domain)){
                    group2domainMap.put(group2domain.split("#")[0],group2domain.split("#")[1]);
                }

            }

        }
        if ( (group2domainMap == null || group2domainMap.isEmpty() ) && throwExceptionIfNull){

            throw new RuntimeException("can not get server config");
        }
        String domain = group2domainMap.get(groupName);

        if (StringUtils.isEmpty(domain) && throwExceptionIfNull){

            throw new RuntimeException("can not get server config");
        }

        return domain;

    }





}
