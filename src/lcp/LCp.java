/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcp;


import lcputility.CompactInfo;
import lcputility.ReferenceTable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author amantova
 */
public class LCp 
{   
    private ReferenceTable tableGroup;
    private String[] patternGroup;
    
    
    
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
       
        String ref = tableGroup.insertReference( compactPr );
        
        return new CompactInfo( ref, index );
    }
    
    
    
    private void setPatternGroup()
    {
        //Setto l'espressioni regolari che identificano la regola da usare
        patternGroup[0] = ".&.";
        patternGroup[1] = ".V.";
        patternGroup[2] = "-.";
        patternGroup[3] = ".>.";
    }
    
    
    
    public static void main( String[] args ) 
    {
        LCp manager = new LCp();

        manager.tableGroup = new ReferenceTable( 20 );
        manager.patternGroup = new String[ 4 ];
        
        manager.setPatternGroup();
                
        //Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        
        String sx = "CVB,(A&(B>C))V(AVB),A";
        String dx = "B&A";
        
        sx = manager.compatta( sx );
        System.out.println( sx );
        
        Pattern pattern = Pattern.compile( "," );
        Matcher matcher = pattern.matcher( sx );
        
        while( matcher.find() ) 
        {
            System.out.println( matcher.group() + " " + matcher.start() + " " + matcher.end() );
        }
        
        for( int i = 0; i < 4; i++ )
        {
            pattern = Pattern.compile( manager.patternGroup[i] );
            matcher = pattern.matcher( sx );
            
            while( matcher.find() )
            {
                switch( i )
                {
                    case 0: System.out.println( "Usare la regola della &" );
                            break;
                    case 1: System.out.println( "Usare la regola della V" );
                            break;
                    case 2: System.out.println( "Usare la regola della -" );
                            break;
                    case 3: System.out.println( "Usare la regola della >" );
                            break;
                }
            }
        }
        
        manager.tableGroup.printAllReference();
    }
}
