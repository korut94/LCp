package lcp;

import lcputility.Solve;

public class LCp 
{
	public static void main( String[] args ) 
	{
		//Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        
        System.out.println();
        
        String sx = "-(A&B>((A>(A>B))>A))vB";
        String dx = "Av(A>B)>B&(B>A)";
        
        Solve solve = new Solve( sx, dx );
        
        solve.treeLeaf().stampa();
	}
}
