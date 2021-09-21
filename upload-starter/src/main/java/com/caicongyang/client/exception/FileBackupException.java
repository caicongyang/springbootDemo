package com.caicongyang.client.exception;

/**
 * 文件备份异常
 *	
 * @author 王明
 * @since 2015-05-04
 */
public class FileBackupException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8858308181112069688L;

	public FileBackupException(String message) {
		super(message);
	}
	
	public FileBackupException(String message, Throwable t) {
		super(message, t);
	}
	
	public FileBackupException(Throwable t) {
        super(t);
    }
}
