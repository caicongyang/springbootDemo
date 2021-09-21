package com.caicongyang.client.constant;

/**
 * Created by ZhouChenmin on 2017/3/31.
 */
public interface UploadConstant {


    public enum ActionType{

        UPLOAD("upload"),DELETE("delete");
        String op ;
        ActionType(String op){
            this.op = op ;
        }

        public String getOp(){
            return op;
        }
    }


    public enum UploadType {

        FAST_DFS("使用fast_dfs作文件存储容器"),
        KS_YUN("使用金山云作文件存储容器"),
        E_BAY("ebay文件上传"),
        HW_YUN("华为云作为文件存储容器"),
        ALI_YUN("阿里云作为文件存储容器"),
        QN_YUN("七牛云作为文件存储容器"),
        TX_YUN("腾讯云作为文件存储容器"),
        MS_YUN("微软云作为文件存储容器"),
        AWS_YUN("亚马逊云作为文件存储容器"),
        JD_YUN("京东云作为文件存储器");

        UploadType(String desc){

        }

        public static UploadType getUploadType(String uploadTypeFromConf) {

            try {
                return UploadConstant.UploadType.valueOf(uploadTypeFromConf);
            }catch (Exception e){
                // 忽略
            }

            if ("fastdfs".equalsIgnoreCase(uploadTypeFromConf) || "fast_dfs".equalsIgnoreCase(uploadTypeFromConf)){

                return FAST_DFS;
            }else if ("ksyun".equalsIgnoreCase(uploadTypeFromConf) || "ks_yun".equalsIgnoreCase(uploadTypeFromConf)){

                return KS_YUN;
            }else if ("ebay".equalsIgnoreCase(uploadTypeFromConf) || "e_bay".equalsIgnoreCase(uploadTypeFromConf)){

                return E_BAY;
            }else if ("hwyun".equalsIgnoreCase(uploadTypeFromConf) || "hw_yun".equalsIgnoreCase(uploadTypeFromConf)){

                return HW_YUN;
            }else if ("aliyun".equalsIgnoreCase(uploadTypeFromConf) || "ali_yun".equalsIgnoreCase(uploadTypeFromConf)){

                return ALI_YUN;
            }else if ("qiniuyun".equalsIgnoreCase(uploadTypeFromConf)
                    || "qiniu_yun".equalsIgnoreCase(uploadTypeFromConf)
                    || "qn_yun".equalsIgnoreCase(uploadTypeFromConf)
                    || "qnyun".equalsIgnoreCase(uploadTypeFromConf)){

                return QN_YUN;
            } else if ("txyun".equalsIgnoreCase(uploadTypeFromConf)
                    || "tx_yun".equalsIgnoreCase(uploadTypeFromConf)){

                return TX_YUN;
            } else if ("msyun".equalsIgnoreCase(uploadTypeFromConf)
                    || "ms_yun".equalsIgnoreCase(uploadTypeFromConf)){

                return MS_YUN;
            } else if ("awsyun".equalsIgnoreCase(uploadTypeFromConf)
                    || "aws_yun".equalsIgnoreCase(uploadTypeFromConf)){

                return AWS_YUN;
            }

            return null;

        }
    }

    /**
     * 文件上传入参类型
     * 文件/流/byte数组
     */
    public enum FileUploadDataType{
        FILE,INPUT_STREAM,CLOSEABLE_INPUT_STREAM;
    }

    /**
     * 水印方位的枚举
     * // NorthWest
     // North
     // NorthEast
     // West
     // Center
     // East
     // SouthWest
     // South
     // SouthEast
     * Created by ZhouChenmin on 2017/3/31.
     */

    public enum  WaterMarkGravity {

        NORTH_WEST("NorthWest"),
        NORTH("North"),
        NORTH_EAST("NorthEast"),
        WEST("West"),
        CENTER("Center"),
        EAST("East"),
        SOUTH_WEST("SouthWest"),
        SOUTH("South"),
        SOUTH_EAST("SouthEast");

        private String value ;

        WaterMarkGravity(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum  WaterMarkStyle {

        NORMAL("没有任何特效"),
        TILE("平铺，即如果水印图比较小，那么上传图片的水印会重复出现，类似window背景的平铺"),
        STRETCH("拉伸,水印比较小，那么拉伸水印以盖住全背景");

        private String desc ;

        WaterMarkStyle(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    public static final String HTTPS_PROTOCOL = "https://";
    public static final String HTTP_PROTOCOL = "http://";


    // URL 拼接风格   bucketName/endpoint/{key}  斜杆拼接
    String USE_SLASH = Boolean.toString(true);
    // URL 拼接风格   bucketName.endpoint/{key}  点号拼接
    String USE_POINT = Boolean.toString(false);
    // URL 拼接风格   endpoint/{key}  仅用一个斜杆
    String ONLY_SLASH = "onlySlash";

}
