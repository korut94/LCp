/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcp;


import java.util.Arrays;
import java.util.ArrayList;
import lcputility.*;

/**
 *
 * @author amantova
 */
public class LCp implements Runnable
{   
    private int indexLeaf;
    private ReferenceTable tableGroup;
    private Solve solve;
    
    private String[][][] tableLeaf;
 
    
    
    public LCp()
    {
        tableGroup = new ReferenceTable( 20 );
        tableLeaf = new String[20][2][];
        
        indexLeaf = 0;
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
    
    
    
    public void run()
    {
        
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
            
            //Presente un periodo compatto singolo nella lista di sinistra
            if( listSx.get( lastElemSx ).matches( "#.#$" ) )
            {
                //Decomprimo il periodo e lo inserisco al posto di 
                //quello compatto
                listSx.add( tableGroup.getReference( listSx.remove( lastElemSx ) ) );
            }
            
            //Presente un periodo compatto singolo nella lista di destra
            if( listDx.get( 0 ).matches( "^#.#" ) )
            {
                //Decomprimo il periodo e lo inserisco al posto di 
                //quello compatto
                listDx.add( 0, tableGroup.getReference( listDx.remove( 0 ) ) );
            }
            
            //Le regole sono ordinate in ordine crescente al numero di
            //premesse che genera
            
            //Applicata regola & sinistra
            if( listSx.get( lastElemSx ).contains( "&" ) )
            {
                //Faccio il pop della stringa 
                derElem = listSx.remove( lastElemSx );
                //Splitto per ottenere A e B
                String[] splitE = derElem.split( "&" );
                //Puscio A e B separati
                listSx.add( splitE[0] );
                listSx.add( splitE[1] );
            }
            
            //Applicata la regola v destra
            else if( listDx.get( 0 ).contains( "v" )  )
            {
                //Faccio il push della stringa 
                derElem = listDx.remove( 0 );
                //Splitto per ottenere A e B
                String[] splitV = derElem.split( "v" );
                //Rinserisco A e B separati all'inizio della lista
                listDx.add( 0, splitV[0] );
                listDx.add( 1, splitV[1] );
                
                System.out.println( listDx.get( 0 ) + " " + listDx.get( 1 ) );
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
                //La porto nella lista di sinistra facendo il push
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
            
            //Applicata la regola della & destra
            else if( listDx.get( 0 ).contains( "&" ) )
            {
                //Faccio il pop della string della lista di destra
                derElem = listDx.remove( 0 );
                //Splitto l'operatore & per ottenere A e B
                String[] splitE = derElem.split( "&" );
                
                //Inserisco l'operando A nella stringa di destra in modo
                //da creare il ramo sinistro
                listDx.add( 0, splitE[0] );
                
                //Creo un thread figlio per elaborare il ramo sinistro
                /* solve( listSx, listDx ); */
                
                //Tolgo A per inserire B per creare il ramo destro
                listDx.remove( 0 );
                listDx.add( splitE[1] );
                
                //Creo un thread figlio per elaborare il ramo destro
                /* solve( listSx, listDx ); */
            }
            
            //Applicata la regola della v sinistra
            else if( listSx.get( lastElemSx ).contains( "v" ) )
            {
                //Rimuovo l'ultimo elemento della lista di sinistra
                derElem = listSx.remove( lastElemSx );
                //Faccio lo split per ottenere A e B dell'operatore v
                String[] splitV = derElem.split( "v" );
                
                //Puscio l'operatore A nella stringa di sinistra in modo
                //da creare il ramo sinistro
                listSx.add( splitV[0] );
                
                //Creo un thread figlio per elaborare il ramo sinistro
                /* solve( listSx, listDx ); */
                
                //Sostituisco l'operatore A con l'operatore B per creare 
                //il ramo destro
                listSx.remove( lastElemSx );
                listSx.add( splitV[1] );
                
                //Creo un thread figlio per elaborare il ramo destro
                /* solve( listSx, listDx ); */
            }
            
            //Applicata la regola del > sinstra
            else if( listSx.get( lastElemSx ).contains( ">" ) )
            {
                //Rimuovo l'ultimo elemento della stringa di sinistra
                derElem = listSx.remove( lastElemSx );
                //Faccio lo split per ottenere A e B dell'operatore >
                String[] splitImp = derElem.split( ">" );
                
                //Inserisco A nella stringa di destra per creare il ramo 
                //di sinistra
                listDx.add( 0, splitImp[0] );
                
                //Creo un thread figlio per elaborare il ramo sinisto
                /* solve( listSx, listDx ); */
                
                //Tolgo A dalla stringa di destra e puscio B su quella di 
                //sinista per creare il ramo destro
                listDx.remove( 0 );
                listSx.add( splitImp[1] );
                
                //Creo un thread figlio per elaborare il ramo destro
                /* solve( listSx, listDx ); */
            }
            
            //Non potendo applicare nessuna regola di derivazione possiamo 
            //essere in due casi: ci sono proposizioni compresse in altri punti
            //della lista oppure siamo arrivati ad una foglia senza assioma
            
            //Proposizioni compresse, usiamo la regola dello scambio e 
            //continuiamo
            int pos = presentOperand( listSx, 0, lastElemSx );
            //Nessun nuovo operando o periodo compatto trovato a sinistra
            if( pos == -1 && listDx.size() > 0 )
            {
                pos = presentOperand( listDx, 1, listDx.size() );
                
                //Nessun nuovo operando o periodo compatto trovato a destra
                //e a sinistra. Siamo in presenza di una foglia senza assiomi
                if( pos == -1 ) writeLeaf( listSx, listDx );
                
                //Altrimenti applico lo scambio a destra
                else
                {
                    //Copio la stringa in posizione pos della lista
                    derElem = listDx.get( pos );
                    //La sostituisco con quella in posizione 0
                    listDx.set( pos, listDx.get( 0 ) );
                    //La setto in posizione 0
                    listDx.set( 0, derElem );
                }
            }
            //Applicato la regola dello scambio a sinistra
            else if( pos != -1 )
            {
                //Copio la stringa in posizione pos della lista
                derElem = listSx.get( pos );
                //La sostituisco con quella in fondo
                listSx.set( pos, listSx.get( lastElemSx ) );
                //La setto alla fine della lista
                listSx.set( lastElemSx, derElem );
            }
            
            //Lista destra vuota
            else writeLeaf( listSx, listDx );
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
    
    
    
    private int presentOperand( ArrayList<String> list, int start, int end )
    {
        int pos = start;
        boolean present = false;
            
        //Controlliamo nella stringa
        while( pos < end && !present )
        {
            present = list.get( pos ).matches( "[&v->#]" );
            pos++;
        }
            
        if( present ) return pos;
        else return -1;
    }
    
    
    
    private void writeLeaf( ArrayList<String> sx, ArrayList<String> dx )
    {
        synchronized( this )
        {
            tableLeaf[ indexLeaf ][0] = sx.toArray( tableLeaf[ indexLeaf ][0] );
            tableLeaf[ indexLeaf ][1] = dx.toArray( tableLeaf[ indexLeaf ][1] );
            indexLeaf++;
        }
    }
    
    
    
    public static void main( String[] args ) 
    {
        LCp manager = new LCp();
        
        //Frase da analizzare, implementare in android due form che contengono
        //rispettivamente la parte sinistra e quella destra del sequente
        String sx = "(A>(BvC))";
        String dx = "(A&B)v(B&A),C";
        
        sx = manager.compatta( sx );
        dx = manager.compatta( dx );
        
        manager.solve = new Solve( sx, dx );
        
        System.out.println( "main" );
        System.out.println( sx );
        System.out.println( dx );
        manager.printAllReference();
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
