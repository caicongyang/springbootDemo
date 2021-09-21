package com.caicongyang.client.exception;

/**
 * 删除异常
 *	
 * @author 王明
 * @since 2015-05-04
 */
public class FileDeleteException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7222960825694866063L;

	public FileDeleteException(String message) {
		super(message);
	}
	
	public FileDeleteException(String message, Throwable t) {
		super(message, t);
	}
	
	public FileDeleteException(Throwable t) {
        super(t);
    }
}
