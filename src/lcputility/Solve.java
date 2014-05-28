/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lcputility;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author andrea
 */
public class Solve implements Runnable
{
    private ArrayList<String> predicateDx;
    private ArrayList<String> predicateSx;
    
    private int indexLeaf;
    
    private ReferenceTable tableGroup;
    
    //1° parametro indice della foglia dell'albero
    //2° parametro sequente sinistro o destro
    //3° parametro indice delle proposizione che compongono il sequente
    //specificato
    private String[][][] tableLeaf;
    
    
    
    public Solve( String sx, String dx )
    {
        tableGroup = new ReferenceTable( 20 );
        tableLeaf = new String[20][2][];
        
        indexLeaf = 0;
        
        sx = compatta( sx );
        dx = compatta( dx );
        
        //Se non ci fosse la virgola nella stringa sorgente sx lo split
        //ritornera esattamente sx. In quel caso la lunghezza dell'array 
        //sarebbe 1 e quindi ogni riferimento all'ultimo elemento coinciderebbe
        //con l'unico presente.
        String[] elemPrSx = sx.split( "," );
        String[] elemPrDx = dx.split( "," );
        
        //Crea le liste di sinitra e destra di stringhe a partire dall'array 
        //ottenuto dallo split
        predicateSx = new ArrayList<String>();
        predicateSx.addAll( Arrays.asList( elemPrSx ) );
        
        predicateDx = new ArrayList<String>();
        predicateDx.addAll( Arrays.asList( elemPrDx ) );
        
        printAllReference();
        
        derThree( predicateSx, predicateDx );
    }
    
    
    
    public String[][][] threeLeaf()
    {
        return tableLeaf;
    }
    
    
    
    public void run()
    {
    }
    
    
    
    private boolean foundOperand( String s )
    {
        boolean found = false;
        char op;
        
        //Cerco nella stringa passata gli operatori
        for( int i = 0; i < s.length() && !found; i++ )
        {
            op = s.charAt( i );
            
            found = ( op == '&' || op == 'v' || op == '-' ||
                      op == '>' || op == '#' );
        }
            
        return found;
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
            
        //Controlliamo nella lista se una proposizione contiene altri operatori
        while( pos < end && !( present = foundOperand( list.get( pos ) ) ) ) 
        {
            pos++;
        }
            
        if( present ) return pos;
        else return -1;
    }
    
    
    
    private String compatta( String pr )
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
    
    
    
    private void derThree( ArrayList<String> listSx, ArrayList<String> listDx )
    {
        boolean isLeaf = false;
        
        while( !isLeaf )
        {
            //Per evitare errori di overflow dell'array e correttezza nei confronti
            //della logica, una lista non deve essere mai vuota. Il % indica il 
            //vero mentre @ indica il falso
            if( listSx.isEmpty() ) listSx.add( "%" );
            else if( listSx.size() == 1 && listSx.get( 0 ).equals( "" ) )
            {
                listSx.set( 0, "%" );
            }
        
            if( listDx.isEmpty() ) listDx.add( "@" );
            else if( listDx.size() == 1 && listDx.get( 0 ).equals( "" ) ) 
            {
                listDx.set( 0, "@" );
            }
            
            
            System.out.println( listSx.toString() + "|-" + listDx.toString() );
            
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
            
            else
            {
                //Proposizioni compresse, usiamo la regola dello scambio e 
                //continuiamo
                int pos = presentOperand( listSx, 0, lastElemSx );
                
                //Nessun nuovo operando o periodo compatto trovato a sinistra
                if( pos == -1 && listDx.size() > 0 )
                {
                    pos = presentOperand( listDx, 1, listDx.size() );
                    
                    //Nessun nuovo operando o periodo compatto trovato a destra
                    //e a sinistra. Siamo in presenza di una foglia senza assiomi
                    if( pos == -1 )
                    {
                        if( !isAxiomIdentity( listSx, listDx ) )
                            writeLeaf( listSx, listDx );
                        
                        isLeaf = true;
                    }
                
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
                else 
                {
                    if( !isAxiomIdentity( listSx, listDx ) ) 
                        writeLeaf( listSx, listDx );
                    
                    isLeaf = true;
                }
            }
        }
    }
    
    
    
    public void printAllReference()
    {
        tableGroup.printAllReference();
    }
    
    
    
    private synchronized void writeLeaf( ArrayList<String> sx, ArrayList<String> dx )
    {
        tableLeaf[ indexLeaf ][0] = new String[ sx.size() ];
        tableLeaf[ indexLeaf ][1] = new String[ dx.size() ];
        
        tableLeaf[ indexLeaf ][0] = sx.toArray( tableLeaf[ indexLeaf ][0] );
        tableLeaf[ indexLeaf ][1] = dx.toArray( tableLeaf[ indexLeaf ][1] );
        
        indexLeaf++;
    }
}
