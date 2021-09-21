
package com.caicongyang.client;

/**
 * 
 * 
 * 
 * @author pengrongxin
 * @version 1.0  2017-02-20 下午12:22:31
 * @since 1.0
 */
public class UploadException extends Exception {

	private static final long serialVersionUID = 6196940781950771285L;

	/**
	 * 
	 */
	public UploadException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UploadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UploadException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UploadException(Throwable cause) {
		super(cause);
	}

}
