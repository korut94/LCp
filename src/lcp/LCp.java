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
    private ReferenceTable tableGroup;
    
    public String compatta( String pr )
    {
        int index = 0;
        int lenghtPr = pr.length();
        
        String temp = new String();
        
        while( index < lenghtPr )
        {
            char c = pr.charAt( index );
            
            //Trovato un gruppo di proposizioni
            if( c == '(' )
            {
                //La funzione salvera il gruppo appena trovato nella
                //ReferenceTable, insieme a tutti i sottogruppi presenti. 
                //Inoltre ci restituisce l'indice che ha usato per identificarla
                //e la posizione della stringa raggiunto dal gruppo
                CompactInfo info = saveGroup( index + 1, pr );
                
                //La nuova stringa avra' l'indice della tabella per
                //rappresentare il gruppo di proposizioni, semplificando 
                //notevolmente il controllo di quale regola applicare
                temp += info.indexTable;
                //Essendo che pr non viene toccata devo saltare alla posizione
                //successiva delle parentesi tonde
                index = info.indexEndGroup;
            }
            
            else temp += c;
            
            //Seleziono il prossimo carattere delle stringa
            index++;
        }
        
        return temp;
    }
    
    
    
    private CompactInfo saveGroup( int start, String pr )
    {
        String compactPr = new String();
        
        int index = start;
        
        while( pr.charAt( index ) != ')' )
        {
            char c = pr.charAt( index );
            
            //Accumulo ogni carattere che incontro 
            if( c != '(' ) compactPr += c;
            //Nel caso trovassi un sottogruppo
            else
            {
                CompactInfo infoSubGroup = saveGroup( index + 1, pr );
                
                compactPr += infoSubGroup.indexTable;
                index = infoSubGroup.indexEndGroup;
            }
            
            index++;
        }
       
        char ref = tableGroup.insertReference( compactPr );
        
        return new CompactInfo( ref, index );
    }
    
    
    
    public static void main( String[] args ) 
    {
        LCp manager = new LCp();

        manager.tableGroup = new ReferenceTable( 20 );
        
        //Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        String sx = "(A&(BVC))&(C->(E&D))";
        String dx = "B&A";
        
        sx = manager.compatta( sx );
        
        manager.tableGroup.printAllReference();
        
        System.out.println( sx );
    }
}
