package codechicken.lib.vec;

@SuppressWarnings("serial")
public class IrreversibleTransformationException extends RuntimeException
{
    @SuppressWarnings("rawtypes")
	public ITransformation t;

    public IrreversibleTransformationException(@SuppressWarnings("rawtypes") ITransformation t)
    {
        this.t = t;
    }

    @Override
    public String getMessage()
    {
        return "The following transformation is irreversible:\n" + t;
    }
}
