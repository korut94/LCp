package lcputility;

@SuppressWarnings( "unused" )
public class Derivate 
{
	private Predicate sequent;
	private String rule;
	
	/**
	 * @param a : sequente da derivare
	 * @param b : regola da utilizzare o assioma usato in caso di una foglia
	 */
	public Derivate( Predicate a, String b )
	{
		sequent = a;
		rule = b;
	}
}
