/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcp;


import java.util.Arrays;
import java.util.ArrayList;
import lcputility.CompactInfo;
import lcputility.ReferenceTable;

/**
 *
 * @author amantova
 */
public class LCp 
{   
    private ReferenceTable tableGroup;
    
    
    
    public LCp()
    {
        tableGroup = new ReferenceTable( 20 );
    }
    
    
    
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
    
    
    
    public void printAllReference()
    {
        tableGroup.printAllReference();
    }
    
    
    
    public void solve( ArrayList<String> listSx, ArrayList<String> listDx )
    {
        System.out.println( "solve" );
        
        if( isAxiomIdentity( listSx, listDx ) )
        {
            System.out.println( "Assioma identita" );
        }
        
        else
        {
            int lastElemSx = listSx.size() - 1;
            
            //Stringa di servizio
            String derElem = new String();
            
            //Le regole sono ordinate in ordine crescente al numero di
            //premesse che genera
            
            //Applicata regola & sinistra
            if( listSx.get( lastElemSx ).contains( "&" ) )
            {
                //Faccio il pop della stringa 
                derElem = listSx.remove( lastElemSx );
                //La modifico applicando la regola della &
                derElem = derElem.replace( "&", "," ) ;
                //La rinserisco nella lista
                listSx.add( derElem );
                
                System.out.println( listSx.get( lastElemSx ) );
            }
            
            //Applicata la regola v destra
            else if( listDx.get( 0 ).contains( "v" )  )
            {
                //Faccio il push della stringa 
                derElem = listDx.remove( 0 );
                //La modifico applicando la regola della v
                derElem = derElem.replace( "v", "," );
                //La rinserisco nella lista
                listDx.add( 0, derElem );
                
                System.out.println( listDx.get( 0 ) );
            }
            
            //Applicata la regola del - sinistra
            else if( listSx.get( lastElemSx ).contains( "-" ) )
            {
                //Faccio il pop della stringa dalla lista di sinistra
                derElem = listSx.remove( lastElemSx );
                //Tolgo il segno di negato
                derElem = derElem.substring( 1 );
                //La porto nella lista di destra mettendola all'inizio
                listDx.add( 0, derElem );
                
                System.out.println( listDx.get( 0 ) );
            }
            
            //Applicata la regola del - destra
            else if( listDx.get( 0 ).contains( "-" ) )
            {
                //Faccio la pop della stringa dalla lista di destra
                derElem = listDx.remove( 0 );
                //Tolgo il segno di negato
                derElem = derElem.substring( 1 );
                //La port nella lista di sinistra facendo il push
                listSx.add( derElem );
                
                System.out.println( listSx.get( lastElemSx + 1 ) );
            }
            
            //Applicata la regola del > destra
            else if( listDx.get( 0 ).contains( ">" ) )
            {
                //Faccio il pop della string dalla lista di destra
                derElem = listDx.remove( 0 );
                //Faccio lo split della stringa per ottenere i parametri A e B
                //dell'implica
                String[] splitImpl = derElem.split( ">" );
                
                //Il parametro A lo pusciamo nella lista di sinistra
                listSx.add( splitImpl[0] );
                //Il parametro B lo inseriamo all'inizio della lista di destra
                listDx.add( 0, splitImpl[1] );
                
                System.out.println( listSx.get( lastElemSx + 1 ) + " " + listDx.get( 0 ) );
            }
        }
    }
    
    
    
    private boolean isAxiomIdentity( ArrayList<String> arraySx, ArrayList<String> arrayDx )
    {
        boolean foundMatch = false;
        
        for( int i = 0; i < arraySx.size() && !foundMatch; i++ )
        {
            for( int j = 0; j < arrayDx.size() && !foundMatch; j++ )
            {
                foundMatch = ( arraySx.get(i).equals( arrayDx.get(j) ) );
            }
        }
        
        return foundMatch;
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
    
    
    
    public static void main( String[] args ) 
    {
        LCp manager = new LCp();
        
        //Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        String sx = "((AvB)>(C&D))v(B>C)";
        String dx = "A>B";
        
        sx = manager.compatta( sx );
        dx = manager.compatta( dx );
        
        System.out.println( "main" );
        System.out.println( sx );
        System.out.println( dx );
        System.out.println( "-----------" );
        
        //Se non ci fosse la virgola nella stringa sorgente sx lo split
        //ritornera esattamente sx. In quel caso la lunghezza dell'array 
        //sarebbe 1 e quindi ogni riferimento all'ultimo elemento coinciderebbe
        //con l'unico presente.
        String[] elemPrSx = sx.split( "," );
        String[] elemPrDx = dx.split( "," );
        
        //Crea le liste di sinitra e destra di stringhe a partire dall'array 
        //ottenuto dallo split
        ArrayList<String> listSx = new ArrayList<String>();
        listSx.addAll( Arrays.asList( elemPrSx ) );
        
        ArrayList<String> listDx = new ArrayList<String>();
        listDx.addAll( Arrays.asList( elemPrDx ) );
        
        manager.solve( listSx, listDx );
    }
}
