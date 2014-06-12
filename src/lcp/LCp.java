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
        
        System.out.println();
        
        if( args.length != 2 ) System.out.println( "Numero di parametri non valido" );
        else
        {
            String sx = args[0];
            String dx = args[1];
        
            Solve solve = new Solve( sx, dx );
        
            System.out.println( solve.threeLeaf() );
        }
        
        System.out.println();
    }
}
