package pl.jsql.api.exceptions;

public class CryptographyException extends RuntimeException {

    public CryptographyException(){
        super();
    }

    public CryptographyException(String message){
        super(message);
    }

}
