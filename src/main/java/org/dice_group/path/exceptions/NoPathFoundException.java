package org.dice_group.path.exceptions;

/**
 * Could not find a path, end goal could not be reached
 *
 */
public class NoPathFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4779561434135125132L;

	public NoPathFoundException() {
		super();
	}
	
	public NoPathFoundException(String message) {
		super(message);
	}

	public NoPathFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoPathFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoPathFoundException(Throwable cause) {
		super(cause);
	}
	
	
}
