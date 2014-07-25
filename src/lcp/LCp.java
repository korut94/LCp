package lcp;

import lcputility.Solve;

public class LCp 
{
	public static void main( String[] args ) 
	{
		//Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        
        System.out.println();
        
        String sx = "-(AvC>C&B)>-(B>A)";
        String dx = "";
        
        Solve solve = new Solve( sx, dx );
        
        System.out.println( solve.threeLeaf() );
	}
}
