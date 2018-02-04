package sociality.server.exceptions;

public class ItemNotFoundException extends BaseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4129722337140977676L;

	public ItemNotFoundException(Class<?> clazz, Long id) {
        super(Error.Code.ITEM_NOT_FOUND, "No " + clazz.getSimpleName() + " entity with id " + id + " exists!");
    }
}
