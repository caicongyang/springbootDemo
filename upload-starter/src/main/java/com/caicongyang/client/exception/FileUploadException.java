package com.caicongyang.client.exception;

/**
 * 上传文件异常
 *	
 * @author pengrongxin
 * @since 2017-02-04
 */
public class FileUploadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6791410624782109743L;

	public FileUploadException(String message) {
		super(message);
	}
	
	public FileUploadException(String message, Throwable t) {
		super(message, t);
	}
	
	public FileUploadException(Throwable t) {
        super(t);
    }
}
