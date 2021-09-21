
package com.caicongyang.client;

/**
 * 
 * 
 * 
 * @author ada
 * @version 1.0  2015-7-20 下午12:41:59
 * @since 1.0
 */
public class MetadataException extends Exception {

	private static final long serialVersionUID = -8877334445885796943L;

	/**
	 * 
	 */
	public MetadataException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MetadataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MetadataException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MetadataException(Throwable cause) {
		super(cause);
	}

}
