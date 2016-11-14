package almeida.rochapaulo.demo.exceptions;

public class EntityAlreadyExists extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EntityAlreadyExists(String message) {
		super(message);
	}
	
}
