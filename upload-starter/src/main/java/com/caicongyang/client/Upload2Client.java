package com.caicongyang.client;

import com.alibaba.fastjson.JSONObject;
import com.caicongyang.client.util.DESPlus;
import com.caicongyang.client.util.ImageConfigUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangfengxian
 * 
 */
public class Upload2Client {
	
	private static final Logger log = LoggerFactory.getLogger(Upload2Client.class);
	
	private static final String PARAM_REQUEST = "upload_request";
	
	private static final String PARAM_KEY = "creator_id";
	
	private static String hostIp = ImageConfigUtil.getIp();
	
	public static void main(String[] args) {
		HashMap<String, String> arg1 = new HashMap<String, String>(); 
		// 2.Upload file
		int uploadLoop = 1;
		for (int j = 0; j < uploadLoop; j++) {
			for (int i = 1; i <= 1; i++) {
				
				arg1.put("name", "JMeter");
				arg1.put("creatorId", "wangming");
				arg1.put("localFileName", "C:\\aaa.jpg");
				arg1.put("poolName", "test");
				arg1.put("fileOrImage", "img");

				// 上传主文件
				System.out.println("\n\n");
				String masterFile = Upload2File("C:\\aaa.jpg", arg1, "wangming");
				System.out.println("Master File upload: " + masterFile);

				// 上传从文件
				System.out.println("\n\n");
				String slaveFile = Upload2SlaveFile(masterFile, "C:\\aaa.jpg",
						"_abcdefghijklmnopqrstuvwxyz0123456789", arg1);
				System.out.println("Slave File upload: " + slaveFile);

				// 上传文件，带有访问信息
				System.out.println("\n\n");
				String metaFile = Upload2MetaFal("C:\\aaa.jpg", masterFile, "_abcdefghijklmnopqrstuvwxyz0123456789", arg1);
				System.out.println("metaFile File upload: " + metaFile);

				// 上传文件，存在文件相对路径
				System.out.println("\n\n");
				String metaMPath = Upload2MetaFam("C:\\aaa.jpg", masterFile, "_abcdefghijklmnopqrstuvwxyz0123456789", arg1);
				System.out.println("metaFile File upload: " + metaMPath);

				// 更新文件的meta信息
				System.out.println("\n\n");
				String aa = Upload2MetaUpdateMeta(arg1, metaFile);
				if (aa == null || aa.isEmpty() || aa.length() < 1)
					System.out.println("meta Data Info update: " + metaFile);
				else
					System.out.println("meta Data Info update ERROR: " + aa);

				// 删除文件的Meta信息
				System.out.println("\n\n");
				String zz = Upload2MetaDelMeta(arg1, metaMPath);
				System.out.println("meta Data Info delete: " + zz);
			}
		}
	}
	
	public static String post(String url, Part[] parts) throws HttpException {
		// 提交url
		HttpClient client = new HttpClient();
		// 设置相关参数
		client.getHttpConnectionManager().getParams().setConnectionTimeout(300 * 1000);
		
		PostMethod method = new PostMethod(url);
		method.setRequestHeader("Connection", "close");
		method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
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
				throw new HttpException("http error: with status code " + statusCode);
			}
		} catch (IOException e) {
			throw new HttpException("http error: " + e.getMessage(), e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}
	
	public static String upload(String file, JSONObject param, String creator) throws UploadException {
		return upload(new File(file), param, creator);
	}
	
	public static String upload(File file, JSONObject param, String creator) throws UploadException {
		if (file == null || file.isDirectory()) {
			throw new IllegalArgumentException("Illegal argument: " + file.getAbsolutePath());
		}
		Part filePart;
		try {
			filePart = new FilePart(file.getName(), file);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File " + file.getAbsolutePath() + " not found", e);
		}
		
		String resp = null;
		try {
			DESPlus jsonKey = new DESPlus(creator);

			DESPlus defaultKey = new DESPlus();

			String p = param.toString();
			p = URLEncoder.encode(p, "UTF-8");
			Part[] parts = new Part[] {
				new StringPart(PARAM_REQUEST, jsonKey.encrypt(p)),
				new StringPart(PARAM_KEY, defaultKey.encrypt(creator)),
				filePart};
			
			AccessLogger.log("request: upload file: " + file.getAbsolutePath() + ", param: " + param.toString() + ", creator: " + creator);
			String url = ImageConfigUtil.getPostUrl();
			resp = post(url, parts);
		} catch (Exception e) {
			throw new UploadException(e);
		}
		
		if (resp == null) {
			throw new UploadException("fail to upload: " + resp);
		}
		AccessLogger.log("resp: " + resp);
		JSONObject rjo = JSONObject.parseObject(resp);

		String result = rjo.getString("result");
		String URL = rjo.getString("URL");
		String META = rjo.getString("result_meta");

		if (result.equalsIgnoreCase("success")) {
			if (!META.equalsIgnoreCase("success")) {
				System.out.println("文件上传更新Meta失败：" + META);
			}
			return URL;
		} else {
			System.out.println("文件上传到FDFS：" + result);
			System.out.println("文件上传更新Meta：" + META);
			return null;
		}
	}

	public static String metawrite(JSONObject param, String creator) throws MetadataException {
		try {
			DESPlus defaultKey = new DESPlus();
			DESPlus jsonKey = new DESPlus(creator);
			
			String p = param.toString();
			p = URLEncoder.encode(p, "UTF-8");
//			NameValuePair[] postData = new NameValuePair[] {
//				new NameValuePair("upload_request", jsonKey.encrypt(p)), 
//				new NameValuePair("creator_id", defaultKey.encrypt(creator))};
//			method.setQueryString(postData);
			Part[] parts = new Part[] {
				new StringPart(PARAM_REQUEST, jsonKey.encrypt(p)),
				new StringPart(PARAM_KEY, defaultKey.encrypt(creator))};

			AccessLogger.log("request: param: " + param.toString() + ", creator: " + creator);
			String url = ImageConfigUtil.getPostUrl();
			String resp = post(url, parts);
			if (resp == null) {
				return null;
			}

			AccessLogger.log("resp: " + resp);
			JSONObject rjo = JSONObject.parseObject(resp);

			String result = rjo.getString("result");
			String URL = rjo.getString("URL");
			String META = rjo.getString("result_meta");

			if (result.equalsIgnoreCase("success")) {
				if (!META.equalsIgnoreCase("success")) {
					System.out.println("文件上传更新Meta失败：" + META);
				}
				return URL;
			} else {
				System.out.println("文件上传到FDFS：" + result);
				System.out.println("文件上传更新Meta：" + META);
				return null;
			}
		} catch (Exception e) {
			throw new MetadataException(e);
		}
	}
	
//	public static String httpPost(String fileToUpload, String creatorid,
//			String json) throws HttpException {
//
//		File file = null;
//		if (fileToUpload != null && fileToUpload.isEmpty() == false
//				&& fileToUpload.length() > 0)
//			file = new File(fileToUpload);
//
//		if (file != null && file.isFile() && file.exists()) {
//			return upload(file, JSONObject.parseObject(json), creatorid);
//		}
//		
//		return metawrite(JSONObject.parseObject(json), creatorid);
//	}

	// 上传单一文件（主文件）
	public static String Upload2File(String file, HashMap<String, String> param, String creator) {

		String creatorId = param.get("creatorId");
		String poolName = param.get("poolName");
		String fileOrImage = param.get("fileOrImage");
		String json = "";

		JSONObject jo = new JSONObject();

		// 1.Form json
		try {
			JSONObject jo1 = new JSONObject();
			
			jo1.put("creator_id", creatorId);
			jo1.put("pool", poolName);
			jo1.put("host_ip", hostIp);
			jo1.put("action", "upload");
			jo1.put("name", file);

			// jo1.put("master_file_URL", "");
			jo1.put("file_or_image", fileOrImage);// 取值可以是 img, file, test, https,
												// default,增加新的类型需要编码支持(小于10行代码)
			jo.put("item", jo1);
			json = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			return upload(file, jo, creatorId);
		} catch (UploadException e) {
			log.error("upload error: " + e.getMessage(), e);
			return null;
		}
	}

	// 上传从文件
	public static String Upload2SlaveFile(String masterFileURL,
			String slaveFilePath, String slaveFileSuffix,
			HashMap<String, String> arg0) {
		if (masterFileURL == null || masterFileURL.isEmpty()
				|| masterFileURL.length() < 1) {
			System.out.println("masterFileURL is null");
			System.out.println("OR slaveFilePath is null");
			System.out.println("exit Upload2SlaveFile");
			return null;
		}
		JSONObject jo = new JSONObject();
		String creatorId = arg0.get("creatorId");
		String poolName = arg0.get("poolName");
		String fileOrImage = arg0.get("fileOrImage");
		// 1.Form json
		try {
			JSONObject jo1 = new JSONObject();

			jo1.put("creator_id", creatorId);
			jo1.put("pool", poolName);
			jo1.put("host_ip", hostIp);
			jo1.put("action", "upload");
			jo1.put("name", slaveFilePath);

			jo1.put("master_file_URL", masterFileURL);
			jo1.put("slave_file_suffix", slaveFileSuffix); // 从文件相对于主文件的后缀名
			jo1.put("file_or_image", fileOrImage);// 取值可以是 img, file, test, https,
												// default,增加新的类型需要编码支持(小于10行代码)

			jo.put("item", jo1);
			// System.out.println(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			return upload(slaveFilePath, jo, creatorId);
		} catch (UploadException e) {
			log.error("upload error: " + e.getMessage(), e);
			return null;
		}
	}

	// 上传文件，指定文件访问信息
	public static String Upload2MetaFal(String name, String masterFileURL, String slaveFileSuffix, HashMap<String, String> arg0) {
		JSONObject jo = new JSONObject();

		String creatorId = arg0.get("creatorId");
		String poolName = arg0.get("poolName");
		String fileOrImage = arg0.get("fileOrImage");
		try {
			JSONObject jo1 = new JSONObject();

			jo1.put("creator_id", creatorId);
			jo1.put("pool", poolName);
			jo1.put("host_ip", hostIp);
			jo1.put("action", "upload");
			jo1.put("name", name);

			jo1.put("master_file_URL", masterFileURL);
			jo1.put("slave_file_suffix", slaveFileSuffix);
			jo1.put("file_or_image", fileOrImage); // 取值可以是 img, file, test, https,
												// default,增加新的类型需要编码支持(小于10行代码)

			// 当文件存在访问权限信息时,
			JSONObject job1 = new JSONObject();
			job1.put("file_access_level", arg0.get("file_access_level")); // 0表示对外禁止访问，其他表示无限制访问
			job1.put("file_access_level_code", arg0.get("file_access_level_code")); // 文件访问，需要通过提取码
			job1.put("file_access_level_crypt", arg0.get("file_access_level_crypt")); // 文件存放前已经加密过，文件取出前要解密（防止内鬼）
			// jb.add(job1);
			jo1.put("FAL", job1);

			// ja.put(jo1);
			jo.put("item", jo1);
			// System.out.println(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			return upload(name, jo, creatorId);
		} catch (UploadException e) {
			log.error("upload error: " + e.getMessage(), e);
			return null;
		}
	}

	// 上传文件并指定文件的访问路径：相对文件
	/**
	 * @param name  the file name to upload
	 * @param masterFileURL  specific the master file url.
	 * @param suffix  specific the file suffix name when upload slave file. param <code>masterFileURL</code> specified.
	 * @param param  param
	 * @return
	 */
	public static String Upload2MetaFam(String name, String masterFileURL, String suffix, HashMap<String, String> param) {
		JSONObject jo = new JSONObject();

		String creatorId = param.get("creatorId");
		String poolName = param.get("poolName");
		String fileOrImage = param.get("fileOrImage");
		try {
			JSONObject jo1 = new JSONObject();

			jo1.put("creator_id", creatorId);
			jo1.put("pool", poolName);
			jo1.put("host_ip", hostIp);
			jo1.put("action", "upload");
			jo1.put("name", name);

			jo1.put("master_file_URL", masterFileURL);
			if (! StringUtils.isBlank(masterFileURL)) {
				if (StringUtils.isBlank(suffix)) {
					throw new IllegalArgumentException("param slave_file_suffix not specific");
				}
			}
			jo1.put("slave_file_suffix", suffix);
			jo1.put("file_or_image", fileOrImage);// 取值可以是 img, file, test, https,
												// default,增加新的类型需要编码支持(小于10行代码)

			// 当文件存在相对路径等的限制时
			JSONObject job2 = new JSONObject();
			job2.put("file_access_map_path", param.get("file_access_map_path"));
			job2.put("file_access_map_suffix", param.get("file_access_map_suffix"));
			jo1.put("FAM", job2);

			// ja.put(jo1);
			jo.put("item", jo1);
			// System.out.println(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			return upload(name, jo, creatorId);
		} catch (UploadException e) {
			log.error("upload error: " + e.getMessage(), e);
			return null;
		}
	}

	// 更新服务器上已存在文件的Meta信息
	// 更新文件的文件提取码、加密信息
	public static String Upload2MetaUpdateMeta(HashMap<String, String> arg0,
			String masterFile) {
		JSONObject jo = new JSONObject();

		try {
			JSONObject jo1 = new JSONObject();

			jo1.put("creator_id", "gavin");
			jo1.put("pool", "product-images");
			jo1.put("host_ip", hostIp); // 本地服务器IP，用于在服务器端记录上传信息
			jo1.put("action", "UpdateMeta");
			jo1.put("name", null);

			jo1.put("master_file_URL", masterFile);

			JSONObject job1 = new JSONObject();
			job1.put("file_access_level", "0"); // 0表示对外禁止访问，其他表示无限制访问
			job1.put("file_access_code", "123456789"); // 文件访问，需要通过提取码
			job1.put("file_access_crypt", "0zxc123zzz"); // 文件存放前已经加密过，文件取出前要解密（防止内鬼）

			jo1.put("FAL", job1);

			jo.put("item", jo1);
			// System.out.println(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			return metawrite(jo, "wangming");
//			return httpPost(null, "wangming", jo.toString());
		} catch (MetadataException e) {
			log.error("metadata error: " + e.getMessage(), e);
			return null;
		}
	}

	public static String Upload2MetaDelMeta(HashMap<String, String> arg0,
			String slaveFile) {
		JSONObject jo = new JSONObject();

		try {
			JSONObject jo1 = new JSONObject();

			jo1.put("creator_id", "pengrongxin");
			jo1.put("pool", "product-images");
			jo1.put("host_ip", "192.168.117.78"); // 本地服务器IP，用于在服务器端记录上传信息
			jo1.put("action", "DelMeta");
			jo1.put("name", null);

			jo1.put("master_file_URL", slaveFile);

			JSONObject job1 = new JSONObject();
			job1.put("file_access_level", "0"); // 0表示对外禁止访问，其他表示无限制访问
			job1.put("file_access_code", "123456789"); // 文件访问，需要通过提取码
			job1.put("file_access_crypt", "0zxc123zzz"); // 文件存放前已经加密过，文件取出前要解密（防止内鬼）

			jo1.put("FAL", job1);

			jo.put("item", jo1);
			// System.out.println(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			return metawrite(jo, "wangming");
//			return httpPost(null, "wangming", jo.toString());
		} catch (MetadataException e) {
			log.error("metadata error: " + e.getMessage(), e);
			return null;
		}
	}
}