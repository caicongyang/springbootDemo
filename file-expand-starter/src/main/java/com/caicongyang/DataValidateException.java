package com.caicongyang;

/**
 * @author bo.wu
 *
 */
public class DataValidateException extends RuntimeException {
	private static final long serialVersionUID = -7386873986824780322L;
	private String name;
	private Object[] extraParams;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public DataValidateException(DataValidateException e) {
	    this(e.getOriginalMessage(), e.getName(), e.getExtraParams());
	}

	public DataValidateException(String message, String name, Object... extraParams) {
		super(message);
		this.extraParams = extraParams;
		this.name = name;
	}
	
	@Override
	public String getMessage() {
		return DataValidateMessages.get(super.getMessage(), getParams());
	}

	private Object[] getParams() {
		if (extraParams == null || extraParams.length == 0) {
			return new Object[] {name};
			
		} else {
			Object[] params = new Object[1 + extraParams.length];
			params[0] = name;
			System.arraycopy(extraParams, 0, params, 1, extraParams.length);
			return params;
		}
	}
    
	protected String getOriginalMessage() {
	    return super.getMessage();
	}
    protected Object[] getExtraParams() {
        return extraParams;
    }
}
