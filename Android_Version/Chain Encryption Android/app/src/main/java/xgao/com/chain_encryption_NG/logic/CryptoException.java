package xgao.com.chain_encryption_NG.logic;

public class CryptoException extends Exception {
	 
	private static final long serialVersionUID = 1L;

	public CryptoException() {
    }
	
	public CryptoException(String message) {
		super(message);
	}
 
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}