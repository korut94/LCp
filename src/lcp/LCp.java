/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcp;


import lcputility.CompactInfo;
import lcputility.ReferenceTable;

/**
 *
 * @author amantova
 */
public class LCp 
{   
    public String compatta( String pr )
    {
        int index = 0;
        CompactInfo info = new CompactInfo( 'b', 3 );
        
        info.printIndexTable();
        
        return pr;
    }
    
    
    
    public static void main( String[] args ) 
    {
        LCp manager = new LCp();
        
        //Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        String sx = "A&B";
        String dx = "B&A";
        
        sx = manager.compatta( sx );
        
        System.out.println( sx );
    }
}
