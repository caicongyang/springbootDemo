package com.caicongyang.client.exception;

/**
 * 上传参数异常
 *	
 * @author pengrongxin
 * @since 2017-02-04
 */
public class RequestFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3578925803835761454L;

	public RequestFormatException(String message) {
		super(message);
	}
	
	public RequestFormatException(String message, Throwable t) {
		super(message, t);
	}
	
	public RequestFormatException(Throwable t) {
        super(t);
    }
}
