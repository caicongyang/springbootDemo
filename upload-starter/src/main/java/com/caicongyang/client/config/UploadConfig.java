package com.caicongyang.client.config;

import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.constant.UploadConstant.UploadType;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * fastdfs的配置文件和ksYun的配置文件是一个文件,
 * ebay的配置由apicall维护，如果后面配置文件越来越多，该类需要拆分
 * Created by ZhouChenmin on 2017/5/2.
 */
public class UploadConfig {

    private static Logger logger = LoggerFactory.getLogger(UploadConfig.class);

    private static final String UPLOAD_SERVER_PATH = "/upload/UploadAction";
    //配置在Properties 对应的key

    // global key
    private static final String UPLOAD_TYPE_KEY = "spring.odfs.upload-type";
    /**
     * 正在使用的uploadType
     */
    private static UploadConstant.UploadType usingUploadType = UploadType.QN_YUN;
    // global uploadEnv 用于在bucket只有一个,但是需要来区分不同环境的情形
    private static final String UPLOAD_ENV_KEY = "spring.odfs.upload.env";
    // fastdfs
    private static final String UPLOAD_SERVER_ADDRESS_KEY = "spring.odfs.fastdfs.server-url";
    public static final String FASTDFS_UPLOAD_SERVER_DOMAIN = "spring.odfs.fastdfs.group-domain";
    public static final String FASTDFS_UPLOAD_PROTOCOL_PREFIX = "spring.odfs.fastdfs.protocol-prefix";
    // ksyun
    private static final String UPLOAD_BUCKET_NAME_KEY = "spring.odfs.ksyun.bucket-name";
    private static final String UPLOAD_FILE_AK_KEY = "spring.odfs.ksyun.ak";
    private static final String UPLOAD_FILE_SK_KEY = "spring.odfs.ksyun.sk";
    private static final String UPLOAD_END_POINT_KEY = "spring.odfs.ksyun.endpoint";
    private static final String UPLOAD_FILE_DOMAIN_KEY = "spring.odfs.ksyun.domain";
    // aliyun
    private static final String ALI_UPLOAD_BUCKET_NAME_KEY = "spring.odfs.aliyun.bucket-name";
    private static final String ALI_UPLOAD_FILE_AK_KEY = "spring.odfs.aliyun.ak";
    private static final String ALI_UPLOAD_FILE_SK_KEY = "spring.odfs.aliyun.sk";
    private static final String ALI_UPLOAD_END_POINT_KEY = "spring.odfs.aliyun.endpoint";
    private static final String ALI_UPLOAD_FILE_DOMAIN_KEY = "spring.odfs.aliyun.domain";
    // qnniu
    private static final String QN_UPLOAD_BUCKET_NAME_KEY = "spring.odfs.qnyun.bucket-name";
    private static final String QN_UPLOAD_FILE_AK_KEY = "spring.odfs.qnyun.ak";
    private static final String QN_UPLOAD_FILE_SK_KEY = "spring.odfs.qnyun.sk";
    private static final String QN_UPLOAD_END_POINT_KEY = "spring.odfs.qnyun.endpoint";
    private static final String QN_UPLOAD_FILE_DOMAIN_KEY = "spring.odfs.qnyun.domain";
    private static final String QN_UPLOAD_ZONE_KEY = "spring.odfs.qnyun.zone";
    // qcloud
    private static final String TX_YUN_UPLOAD_BUCKET_NAME_KEY = "spring.odfs.qcloud.bucket-name";
    private static final String TX_YUN_UPLOAD_FILE_AK_KEY = "spring.odfs.qcloud.ak";
    private static final String TX_YUN_UPLOAD_FILE_SK_KEY = "spring.odfs.qcloud.sk";
    private static final String TX_YUN_UPLOAD_END_POINT_KEY = "spring.odfs.qcloud.endpoint";
    private static final String TX_YUN_UPLOAD_FILE_DOMAIN_KEY = "spring.odfs.qcloud.domain";
    private static final String TX_YUN_UPLOAD_REGION = "spring.odfs.qcloud.region";
    // 微软云
    private static final String MS_YUN_UPLOAD_BUCKET_NAME_KEY = "spring.odfs.msyun.bucket-name";
    private static final String MS_YUN_UPLOAD_FILE_AK_KEY = "spring.odfs.msyun.ak";
    private static final String MS_YUN_UPLOAD_FILE_SK_KEY = "spring.odfs.msyun.sk";
    private static final String MS_YUN_UPLOAD_END_POINT_KEY = "spring.odfs.msyun.endpoint";
    private static final String MS_YUN_UPLOAD_FILE_DOMAIN_KEY = "spring.odfs.msyun.domain";
    // 亚马逊云
    private static final String AWS_YUN_UPLOAD_BUCKET_NAME_KEY = "spring.odfs.awsyun.bucket-name";
    private static final String AWS_YUN_UPLOAD_FILE_AK_KEY = "spring.odfs.awsyun.ak";
    private static final String AWS_YUN_UPLOAD_FILE_SK_KEY = "spring.odfs.awsyun.sk";
    private static final String AWS_YUN_UPLOAD_END_POINT_KEY = "spring.odfs.awsyun.endpoint";
    private static final String AWS_YUN_UPLOAD_FILE_DOMAIN_KEY = "spring.odfs.awsyun.domain";
    // 京东云
    private static final String JD_YUN_UPLOAD_BUCKET_NAME_KEY = "spring.odfs.jdyun.bucket-name";
    private static final String JD_YUN_UPLOAD_FILE_AK_KEY = "spring.odfs.jdyun.ak";
    private static final String JD_YUN_UPLOAD_FILE_SK_KEY = "spring.odfs.jdyun.sk";
    private static final String JD_YUN_UPLOAD_END_POINT_KEY = "spring.odfs.jdyun.endpoint";
    private static final String JD_YUN_UPLOAD_FILE_DOMAIN_KEY = "spring.odfs.jdyun.domain";
    private final static Properties properties = new Properties();
    /**
     * @see #UPLOAD_ENV_KEY
     */
    public static String UPLOAD_ENV = null;
    // fastdfs 配置
    public static String UPLOAD_SERVER_ADDRESS;
    // 金山云配置
    public static String BUCKET_NAME = null;
    public static String FILE_AK = null;
    public static String FILE_SK = null;
    public static String FILE_DOMAIN = null;
    public static String END_POINT = null;
    public static String REGION = null;
    public static String envPath = System.getProperty("global.config.path");

    private static boolean illegalConfigValue(String value){
        if (StringUtils.isBlank(value) || value.trim().startsWith("${")){
            return true;
        }else {
            return false;
        }
    }

    private static String modifyConfigValue(String fileDomain) {
        if (illegalConfigValue(fileDomain)){
            return "";
        }else {
            return fileDomain;
        }
    }

    /**
     * 读取odfs的配置文件
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperties(String key,String defaultValue){
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value;
    }

    public static UploadConstant.UploadType getUsingUploadType() {
        return usingUploadType;
    }

    /**
     *
     * @return
     */
    public static synchronized String getUploadProtocolPrefix(){
        String prefix = UploadConfig.getProperties(UploadConfig.FASTDFS_UPLOAD_PROTOCOL_PREFIX,UploadConstant.HTTPS_PROTOCOL);
        return prefix;
    }

    public static String getQnYunUploadZone(){
        String zone = UploadConfig.getProperties(UploadConfig.QN_UPLOAD_ZONE_KEY,"1");
        return zone;
    }

    public static void main(String[] args) {
        UploadConstant.UploadType type = UploadConstant.UploadType.valueOf("FAST_DFS");
        UploadConstant.UploadType uploadTypeEnum = UploadConstant.UploadType.getUploadType("ks_yun");
        UploadConstant.UploadType uploadTypeEnum2 = UploadConstant.UploadType.getUploadType("ksyun");
        UploadConstant.UploadType uploadTypeEnum3 = UploadConstant.UploadType.getUploadType("ksYun");
        System.out.println(type);
        System.out.println(uploadTypeEnum);
        System.out.println(uploadTypeEnum2);
        System.out.println(uploadTypeEnum3);
    }

    public static void init() {
        UploadConstant.UploadType uploadType = UploadConstant.UploadType.getUploadType(properties.getProperty(UPLOAD_TYPE_KEY));
        UPLOAD_ENV = properties.getProperty(UPLOAD_ENV_KEY);
        logger.info("UPLOAD_ENV --> {} " , UPLOAD_ENV);
        // 如果没有写uploadEnv那么置为null
        UPLOAD_ENV = modifyConfigValue(UPLOAD_ENV).trim();
        if (UploadConstant.UploadType.FAST_DFS == uploadType  ){
            // fastdfs 的
            UPLOAD_SERVER_ADDRESS = properties.getProperty(UPLOAD_SERVER_ADDRESS_KEY);
            if(illegalConfigValue(UPLOAD_SERVER_ADDRESS)){
                throw new RuntimeException("Can't found upload.server.address properties");
            }
            UPLOAD_SERVER_ADDRESS = UPLOAD_SERVER_ADDRESS.trim() + UPLOAD_SERVER_PATH;
            logger.info("upload server address {}" ,UPLOAD_SERVER_ADDRESS);

        }else if (UploadConstant.UploadType.KS_YUN == uploadType){
            BUCKET_NAME = properties.getProperty(UPLOAD_BUCKET_NAME_KEY);
            FILE_AK = properties.getProperty(UPLOAD_FILE_AK_KEY);
            FILE_SK = properties.getProperty(UPLOAD_FILE_SK_KEY);
            END_POINT = properties.getProperty(UPLOAD_END_POINT_KEY);
            FILE_DOMAIN = properties.getProperty(UPLOAD_FILE_DOMAIN_KEY);
            logger.info("BUCKET_NAME->{}" ,BUCKET_NAME);
            logger.info("FILE_AK->{}" ,FILE_AK);
            logger.info("FILE_SK->{}" ,FILE_SK);
            logger.info("END_POINT->{}" ,END_POINT);
            logger.info("FILE_DOMAIN->{}" ,FILE_DOMAIN);
            if (illegalConfigValue(BUCKET_NAME) || illegalConfigValue(FILE_AK)
                    || illegalConfigValue(FILE_SK) || illegalConfigValue(END_POINT)){
                throw new RuntimeException("config properties exist but invalid for existing empty value when init ksyun config");
            }
            FILE_DOMAIN = modifyConfigValue(FILE_DOMAIN).trim();
            BUCKET_NAME = BUCKET_NAME.trim();
            FILE_AK = FILE_AK.trim();
            FILE_SK = FILE_SK.trim();
            END_POINT = END_POINT.trim();
        }else if(UploadConstant.UploadType.ALI_YUN == uploadType){
            BUCKET_NAME = properties.getProperty(ALI_UPLOAD_BUCKET_NAME_KEY);
            FILE_AK = properties.getProperty(ALI_UPLOAD_FILE_AK_KEY);
            FILE_SK = properties.getProperty(ALI_UPLOAD_FILE_SK_KEY);
            END_POINT = properties.getProperty(ALI_UPLOAD_END_POINT_KEY);
            FILE_DOMAIN = properties.getProperty(ALI_UPLOAD_FILE_DOMAIN_KEY);
            logger.info("ALI_BUCKET_NAME->{}" ,BUCKET_NAME);
            logger.info("ALI_FILE_AK->{}" ,FILE_AK);
            logger.info("ALI_FILE_SK->{}" ,FILE_SK);
            logger.info("ALI_END_POINT->{}" ,END_POINT);
            logger.info("ALI_FILE_DOMAIN->{}" ,FILE_DOMAIN);
            if (illegalConfigValue(BUCKET_NAME) || illegalConfigValue(FILE_AK)
                    || illegalConfigValue(FILE_SK) || illegalConfigValue(END_POINT)){
                throw new RuntimeException("config properties exist but invalid for existing empty value when init aliyun config");
            }
            BUCKET_NAME = BUCKET_NAME.trim();
            FILE_AK = FILE_AK.trim();
            FILE_SK = FILE_SK.trim();
            END_POINT = END_POINT.trim();
            FILE_DOMAIN = modifyConfigValue(FILE_DOMAIN).trim();
        }else if(UploadConstant.UploadType.QN_YUN == uploadType){
            BUCKET_NAME = properties.getProperty(QN_UPLOAD_BUCKET_NAME_KEY);
            FILE_AK = properties.getProperty(QN_UPLOAD_FILE_AK_KEY);
            FILE_SK = properties.getProperty(QN_UPLOAD_FILE_SK_KEY);
            END_POINT = properties.getProperty(QN_UPLOAD_END_POINT_KEY);
            FILE_DOMAIN = properties.getProperty(QN_UPLOAD_FILE_DOMAIN_KEY);
            logger.info("QN_BUCKET_NAME->{}" ,BUCKET_NAME);
            logger.info("QN_FILE_AK->{}" ,FILE_AK);
            logger.info("QN_FILE_SK->{}" ,FILE_SK);
            logger.info("QN_END_POINT->{}" ,END_POINT);
            logger.info("QN_FILE_DOMAIN->{}" ,FILE_DOMAIN);
            if (illegalConfigValue(BUCKET_NAME) || illegalConfigValue(FILE_AK)
                    || illegalConfigValue(FILE_SK) || (illegalConfigValue(END_POINT) && illegalConfigValue(FILE_DOMAIN)) ){
                throw new RuntimeException("config properties exist but invalid for existing empty value when init qnyun config");
            }
            BUCKET_NAME = BUCKET_NAME.trim();
            FILE_AK = FILE_AK.trim();
            FILE_SK = FILE_SK.trim();
            END_POINT = END_POINT.trim();
            FILE_DOMAIN = modifyConfigValue(FILE_DOMAIN).trim();
        } else if(UploadConstant.UploadType.TX_YUN == uploadType){
            BUCKET_NAME = properties.getProperty(TX_YUN_UPLOAD_BUCKET_NAME_KEY);
            FILE_AK = properties.getProperty(TX_YUN_UPLOAD_FILE_AK_KEY);
            FILE_SK = properties.getProperty(TX_YUN_UPLOAD_FILE_SK_KEY);
            END_POINT = properties.getProperty(TX_YUN_UPLOAD_END_POINT_KEY);
            FILE_DOMAIN = properties.getProperty(TX_YUN_UPLOAD_FILE_DOMAIN_KEY);
            REGION = properties.getProperty(TX_YUN_UPLOAD_REGION);
            logger.info("TX_YUN_BUCKET_NAME->{}" , BUCKET_NAME);
            logger.info("TX_YUN_FILE_AK->{}" , FILE_AK);
            logger.info("TX_YUN_FILE_SK->{}" , FILE_SK);
            logger.info("TX_YUN_END_POINT->{}" , END_POINT);
            logger.info("TX_YUN_FILE_DOMAIN->{}" , FILE_DOMAIN);
            logger.info("TX_YUN_REGION->{}" , REGION);
            if (illegalConfigValue(BUCKET_NAME) || illegalConfigValue(FILE_AK) || illegalConfigValue(REGION)
                    || illegalConfigValue(FILE_SK) || (illegalConfigValue(END_POINT) && illegalConfigValue(FILE_DOMAIN)) ){
                throw new RuntimeException("config properties exist but invalid for existing empty value when init txyun config");
            }
            BUCKET_NAME = BUCKET_NAME.trim();
            FILE_AK = FILE_AK.trim();
            FILE_SK = FILE_SK.trim();
            END_POINT = END_POINT.trim();
            FILE_DOMAIN = modifyConfigValue(FILE_DOMAIN).trim();
            REGION = REGION.trim();
        }else if(UploadConstant.UploadType.MS_YUN == uploadType){
            BUCKET_NAME = properties.getProperty(MS_YUN_UPLOAD_BUCKET_NAME_KEY);
            FILE_AK = properties.getProperty(MS_YUN_UPLOAD_FILE_AK_KEY);
            FILE_SK = properties.getProperty(MS_YUN_UPLOAD_FILE_SK_KEY);
            END_POINT = properties.getProperty(MS_YUN_UPLOAD_END_POINT_KEY);
            FILE_DOMAIN = properties.getProperty(MS_YUN_UPLOAD_FILE_DOMAIN_KEY);
            logger.info("MS_YUN_BUCKET_NAME->{}" , BUCKET_NAME);
            logger.info("MS_YUN_FILE_AK->{}" , FILE_AK);
            logger.info("MS_YUN_FILE_SK->{}" , FILE_SK);
            logger.info("MS_YUN_END_POINT->{}" , END_POINT);
            logger.info("MS_YUN_FILE_DOMAIN->{}" , FILE_DOMAIN);
            if (illegalConfigValue(BUCKET_NAME) || illegalConfigValue(FILE_AK)
                    || illegalConfigValue(FILE_SK) || (illegalConfigValue(END_POINT) && illegalConfigValue(FILE_DOMAIN)) ){
                throw new RuntimeException("config properties exist but invalid for existing empty value when init msyun config");
            }
            BUCKET_NAME = BUCKET_NAME.trim();
            FILE_AK = FILE_AK.trim();
            FILE_SK = FILE_SK.trim();
            END_POINT = END_POINT.trim();
            FILE_DOMAIN = modifyConfigValue(FILE_DOMAIN).trim();
        }else if(UploadConstant.UploadType.AWS_YUN == uploadType){
            BUCKET_NAME = properties.getProperty(AWS_YUN_UPLOAD_BUCKET_NAME_KEY);
            FILE_AK = properties.getProperty(AWS_YUN_UPLOAD_FILE_AK_KEY);
            FILE_SK = properties.getProperty(AWS_YUN_UPLOAD_FILE_SK_KEY);
            END_POINT = properties.getProperty(AWS_YUN_UPLOAD_END_POINT_KEY);
            FILE_DOMAIN = properties.getProperty(AWS_YUN_UPLOAD_FILE_DOMAIN_KEY);
            logger.info("AWS_YUN_BUCKET_NAME->{}" , BUCKET_NAME);
            logger.info("AWS_YUN_FILE_AK->{}" , FILE_AK);
            logger.info("AWS_YUN_FILE_SK->{}" , FILE_SK);
            logger.info("AWS_YUN_END_POINT->{}" , END_POINT);
            logger.info("AWS_YUN_FILE_DOMAIN->{}" , FILE_DOMAIN);
            if (illegalConfigValue(BUCKET_NAME) || illegalConfigValue(FILE_AK)
                    || illegalConfigValue(FILE_SK) || (illegalConfigValue(END_POINT) && illegalConfigValue(FILE_DOMAIN)) ){
                throw new RuntimeException("config properties exist but invalid for existing empty value when init msyun config");
            }
            BUCKET_NAME = BUCKET_NAME.trim();
            FILE_AK = FILE_AK.trim();
            FILE_SK = FILE_SK.trim();
            END_POINT = END_POINT.trim();
            FILE_DOMAIN = modifyConfigValue(FILE_DOMAIN).trim();
        }else if(UploadConstant.UploadType.JD_YUN == uploadType){
            BUCKET_NAME = properties.getProperty(JD_YUN_UPLOAD_BUCKET_NAME_KEY);
            FILE_AK = properties.getProperty(JD_YUN_UPLOAD_FILE_AK_KEY);
            FILE_SK = properties.getProperty(JD_YUN_UPLOAD_FILE_SK_KEY);
            END_POINT = properties.getProperty(JD_YUN_UPLOAD_END_POINT_KEY);
            FILE_DOMAIN = properties.getProperty(JD_YUN_UPLOAD_FILE_DOMAIN_KEY);
            logger.info("JD_YUN_BUCKET_NAME->{}" , BUCKET_NAME);
            logger.info("JD_YUN_FILE_AK->{}" , FILE_AK);
            logger.info("JD_YUN_FILE_SK->{}" , FILE_SK);
            logger.info("JD_YUN_END_POINT->{}" , END_POINT);
            logger.info("JD_YUN_FILE_DOMAIN->{}" , FILE_DOMAIN);
            if (illegalConfigValue(BUCKET_NAME) || illegalConfigValue(FILE_AK)
                    || illegalConfigValue(FILE_SK) || (illegalConfigValue(END_POINT) && illegalConfigValue(FILE_DOMAIN)) ){
                throw new RuntimeException("config properties exist but invalid for existing empty value when init jdyun config");
            }
            BUCKET_NAME = BUCKET_NAME.trim();
            FILE_AK = FILE_AK.trim();
            FILE_SK = FILE_SK.trim();
            END_POINT = END_POINT.trim();
            FILE_DOMAIN = modifyConfigValue(FILE_DOMAIN).trim();
        }
        logger.info("odfs load config end");
    }

    public static Properties getProperties() {
        return properties;
    }
}
