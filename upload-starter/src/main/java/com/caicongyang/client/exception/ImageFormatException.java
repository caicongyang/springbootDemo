package com.caicongyang.client.exception;

/**
 * 图片格式异常
 *	
 * @author 王明
 * @since 2015-05-04
 */
public class ImageFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8769735246088009986L;

	public ImageFormatException(String message) {
		super(message);
	}
	
	public ImageFormatException(String message, Throwable t) {
		super(message, t);
	}
	
	public ImageFormatException(Throwable t) {
        super(t);
    }
}
