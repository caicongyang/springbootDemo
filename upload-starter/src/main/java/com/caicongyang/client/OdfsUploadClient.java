package com.caicongyang.client;

import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import com.caicongyang.client.handler.AliyunUploadHandler;
import com.caicongyang.client.handler.AwsyunUploadHandler;
import com.caicongyang.client.handler.FastDfsUploadHandler;
import com.caicongyang.client.handler.JdyunUploadHandler;
import com.caicongyang.client.handler.KsyunUploadHandler;
import com.caicongyang.client.handler.MsyunUploadHandler;
import com.caicongyang.client.handler.QnyunUploadHandler;
import com.caicongyang.client.handler.TxyunUploadHandler;
import com.caicongyang.client.handler.UploadHandler;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pengrongxin on 2017/2/28.
 */
public class OdfsUploadClient {

    private static final Logger log = LoggerFactory.getLogger(OdfsUploadClient.class);


    private static volatile OdfsUploadClient instance;

    private UploadHandler uploadHandler;

    private OdfsUploadClient(UploadConstant.UploadType uploadType){
        uploadType = uploadType == null ? UploadConstant.UploadType.FAST_DFS : uploadType;
        setUploadHandler(uploadType);
    }

    private void setUploadHandler(UploadConstant.UploadType uploadType) {
        UploadConfig.init();
        if (UploadConstant.UploadType.FAST_DFS == uploadType  ){
            uploadHandler = new FastDfsUploadHandler();
        }else if (UploadConstant.UploadType.KS_YUN == uploadType){
            uploadHandler = new KsyunUploadHandler();
        }else if (UploadConstant.UploadType.ALI_YUN == uploadType){
            uploadHandler = new AliyunUploadHandler();
        }else if (UploadConstant.UploadType.QN_YUN == uploadType){
            uploadHandler = new QnyunUploadHandler();
        }else if (UploadConstant.UploadType.TX_YUN == uploadType){
            uploadHandler = new TxyunUploadHandler();
        }else if (UploadConstant.UploadType.MS_YUN == uploadType){
            uploadHandler = new MsyunUploadHandler();
        }else if (UploadConstant.UploadType.AWS_YUN == uploadType){
            uploadHandler = new AwsyunUploadHandler();
        }else if (UploadConstant.UploadType.JD_YUN == uploadType){
            uploadHandler = new JdyunUploadHandler();
        }else {
            throw new IllegalArgumentException("upload type must in " + Arrays.toString(UploadConstant.UploadType.values()));
        }
    }
    /**
     * use {@link #getInstanceFromConfig()}
     * @return
     */
    @Deprecated
    public static OdfsUploadClient getInstance(){
        return getInstance(UploadConstant.UploadType.FAST_DFS);
    }

    /**
     * use {@link #getInstanceFromConfig()}
     * @return
     */
    @Deprecated
    public static OdfsUploadClient getInstance(UploadConstant.UploadType uploadType){
        if(instance == null){
            synchronized (OdfsUploadClient.class){
                if(instance == null){
                    UploadConfig.init();
                    instance = new OdfsUploadClient(uploadType);
                }
            }
        }
        return instance;
    }


    /**
     * 如果配置文件没有配置走那种类型的上传，默认走金山云
     * @return
     */
    public synchronized static OdfsUploadClient getInstanceFromConfig(){

        return getInstance(UploadConfig.getUsingUploadType());
    }



    /**
     * 根据文件流上传，需要指定文件名，文件的长度 contentLength
     * @param fileName 文件名,需要后缀
     * @param poolName 上传的pool Name
     * @param io 文件IO流
     * @return
     * @throws UploadException
     */
    public UploadResult upload(final String fileName,String poolName,final InputStream io) throws UploadException {
        return uploadHandler.upload(fileName, poolName, io, new UploadImage());
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
    public UploadResult upload(final String fileName,String poolName,final InputStream io, UploadImage uploadImage) throws UploadException {
        return uploadHandler.upload(fileName,poolName,io,uploadImage);
    }


    /**
     * 文件上传，需要指定文件名称
     * @param uploadName 文件的上传名称，需要有后缀
     * @param poolName poolName
     * @param file 文件
     * @return
     * @throws UploadException
     */
    public UploadResult upload(String uploadName, String poolName ,File file ) throws UploadException {
        return uploadHandler.upload(uploadName, poolName, file, new UploadImage());
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
        return uploadHandler.upload(uploadName, poolName, file,uploadImage);
    }


    /**
     * byte[] 数组上传
     * @return
     * @throws UploadException
     */
    public UploadResult upload(String uploadName, String poolName ,byte[] content) throws UploadException{
        return uploadHandler.upload(uploadName, poolName, content, new UploadImage());
    }


    public UploadResult upload(String uploadName, String poolName, byte[] content, UploadImage uploadImage) throws UploadException {
        return uploadHandler.upload(uploadName, poolName, content, uploadImage);
    }




    /**
     * 根据文件名上传
     * @param file 文件的绝对路径
     * @param poolName poolName
     * @return
     * @throws UploadException
     */
    public UploadResult upload(String file, String poolName) throws UploadException {
        return uploadHandler.upload(new File(file), poolName, new UploadImage());
    }

    /**
     * 上传
     * @param file file
     * @param poolName poolName
     * @return
     * @throws UploadException
     */
    public UploadResult upload(File file, String poolName) throws UploadException {
        return uploadHandler.upload(file, poolName, new UploadImage());
    }


    /**
     * 根据文件名上传
     * @param file 文件的绝对路径
     * @param poolName poolName
     * @param uploadImage 文件是否需要加水印，是否生成缩略图的配置信息，是否裁剪等
     * @return
     * @throws UploadException
     */
    public UploadResult upload(String file, String poolName, UploadImage uploadImage) throws UploadException {
        return uploadHandler.upload(new File(file), poolName, uploadImage);
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
    public UploadResult batchUpload(List<String> fileList, String poolName, UploadImage uploadImage) throws UploadException {
        return uploadHandler.batchUpload(fileList,poolName,uploadImage);
    }

    /**
     * 上传图片
     * @param fileUrl 图片的外网地址
     * @param poolName 上传的pool 名称
     * @return
     * @throws UploadException
     */
    public UploadResult uploadByUrl(String fileUrl, String poolName) throws UploadException {
        return uploadHandler.uploadByUrl(fileUrl,poolName);
    }

    /**
     * 批量上传图片
     * @param fileUrlList 多个图片的外网地址
     * @param poolName 上传的pool 名称
     * @return
     * @throws UploadException
     */
    /*public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName) throws UploadException {
        return uploadHandler.batchUploadByUrl(fileUrlList,poolName);
    }*/

    /**
     * 批量上传图片
     * @param fileUrlList 多个图片的外网地址
     * @param poolName 上传的pool 名称
     * @param uploadImage 批量上传时，多个图片共享一个是否加水印，是否生成缩略图，是否裁剪
     *                    即，多张图片一视同仁
     * @return
     * @throws UploadException
     */
    /*public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName, UploadImage uploadImage) throws UploadException {
        return uploadHandler.batchUploadByUrl(fileUrlList,poolName,uploadImage);
    }*/



    /**
     * 根据file 对象上传。
     * @param file 文件对象
     * @param poolName pool 名称
     * @param uploadImage 文件是否需要加水印，是否生成缩略图的配置信息，是否裁剪等
     * @return
     * @throws UploadException
     */
    public UploadResult upload(File file,String poolName, UploadImage uploadImage) throws UploadException {
        return uploadHandler.upload(file,poolName,uploadImage);
    }


    /**
     * 删除图片，可以删除多个
     * @param  fileName 是需要删除文件的访问路径的集合
     * @param poolName
     * @return
     * @throws UploadException
     */
    public UploadResult delete(String fileName, String poolName ) throws UploadException {
        return uploadHandler.delete(fileName, poolName);
    }



    /**
     * 删除图片，可以删除多个
     * @param fileList 是需要删除文件的访问路径的集合
     * @param poolName
     * @return
     * @throws UploadException
     */
    public UploadResult delete(List<String> fileList, String poolName,boolean deleteSeriesImage) throws UploadException {
        return uploadHandler.delete(fileList,poolName,deleteSeriesImage);
    }

    /**
     * 下载文件，fileUrl可以是 带上域名的
     *      (fastdfs 格式) http://***.com/G00/M00/00/08/wKgUGFjlvA2AJ_OTAAALJz5HLjc628.jpg
     *      (金山云 格式  )http://swift.ks3-cn-shanghai.ksyun.com/wahaha/1509526356564_59.83518338011109_044b4032-f422-40be-8f56-5cfed808b66e.jpg
     * 也可以是 不带上域名的
     *      (fastdfs 格式)/G00/M00/00/08/wKgUGFjlvA2AJ_OTAAALJz5HLjc628.jpg
     *      (金山云 格式 )/product/1509526356564_59.83518338011109_044b4032-f422-40be-8f56-5cfed808b66e.jpg
     * @param url
     * @return  流
     */
    public InputStream download(String url){

        InputStream inputStream = uploadHandler.download(url);
        return inputStream;

    }


    /**
     * 下载文件，fileUrl可以是 带上域名的
     *      http://***.com/G00/M00/00/08/wKgUGFjlvA2AJ_OTAAALJz5HLjc628.jpg
     * 也可以是 不带上域名的
     *      /G00/M00/00/08/wKgUGFjlvA2AJ_OTAAALJz5HLjc628.jpg
     * @param url
     * @param storeDirectory 返回的文件保存的目录
     * @return  文件
     */
    public File download(String url,String storeDirectory){

        File file = uploadHandler.download(url,storeDirectory);
        return file;

    }

}
