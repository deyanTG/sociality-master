package sociality.server.exceptions;

public  class BaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 190471791826699381L;
	private Error.Code code;

	public BaseException(Error.Code code) {
		this.code = code;
	}

	public BaseException(Error.Code code, String message) {
		super(message);
		this.code = code;
	}

	public BaseException(Error.Code code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public BaseException(Error.Code code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public Error.Code getCode() {
		return code;
	}

	public void setCode(Error.Code code) {
		this.code = code;
	}
}
