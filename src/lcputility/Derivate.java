package lcputility;

public class Derivate 
{
	private Predicate sequent;
	private String rule;
	
	/**
	 * @param a : sequente da derivare
	 * @param b : regola da utilizzare o assioma usato in caso di una foglia
	 */
	public Derivate( Predicate a )
	{
		sequent = new Predicate( a.prSx, a.prDx );
	}
	
	
	/**
	 * @return Stampa a video la regola affiancata dal sequente
	 */
	public void print()
	{
		System.out.print( rule + " " );
		sequent.printPredicate();
	}
	
	
	/**
	 * @param ruleUse : setta come regola utilizzata per derivare il sequente il contenuto di <i>ruleUse</i>
	 */
	public void setRule( String ruleUse )
	{
		rule = ruleUse;
	}
}
