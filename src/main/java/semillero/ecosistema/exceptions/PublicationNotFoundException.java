package semillero.ecosistema.exceptions;

public class PublicationNotFoundException extends RuntimeException{
    public PublicationNotFoundException(String message) {
        super(message);
    }
}