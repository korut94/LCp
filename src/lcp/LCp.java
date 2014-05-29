/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcp;

import lcputility.Solve;

/**
 *
 * @author amantova
 */
public class LCp
{   
    public static void main( String[] args ) 
    {
        //Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        String sx = "(-(C&(A&C)))&(A&B)";
        String dx = "A>B";
        
        Solve solve = new Solve( sx, dx );
        
        solve.threeLeaf();
    }
}
