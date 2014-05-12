/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcputility;

/**
 *
 * @author amantova
 */
public class ReferenceTable 
{
    //Numero di righe della tabella
    private int size;
    //Tabella dei riferimenti tra singolo carattere e gruppo di proposizioni        
    private ReferenceLine[] tableRef;
    
    
    
    public ReferenceTable( int numRow )
    {
        tableRef = new ReferenceLine[ numRow ];
        size = 0;
    }
    
    
    
    public String insertReference( String pr )
    {
        if( size == tableRef.length ) addRow();
        
        //Nel caso si richiedesse di inserire un predicato gia' inserito
        //e' inutile crearli una riga apposita, restituiamo l'indice 
        //corrispondente. 
        int ind = alreadyInsert( pr );
        
        if( ind != -1 ) return '#'+Integer.toString( ind )+'#';
        
        //Altrimenti aggiungo alla tabella il riferimento
        tableRef[ size ] = new ReferenceLine( size, pr );
        
        String index = '#'+Integer.toString( size )+'#';
        
        //Incremento il contatore di righe
        size++;
        
        //Restituisco l'indice che e' stato asssiocato a pr nella 
        //ReferenceTable sotto forma di stringa contrassegnata dai caratteri
        //cancelletto
        return index;
    }
    
    
    
    public void printAllReference()
    {
        for( int i = 0; i < size; i++ ) 
            System.out.println( tableRef[i].getIndex() + 
                                " " + 
                                tableRef[i].getPredicate() 
                              );
    }
    
    
    
    private int alreadyInsert( String pr )
    {
        boolean found = false;
        int i = 0;
        
        while( i < size && !found ) 
        {
            //Trovato e mantengo la posizione in cui l'ho trovato
            if( tableRef[i].getPredicate().equals( pr ) ) found = true;
            //Continuo a cercarlo
            else i++;
        }
        
        if( found ) return tableRef[i].getIndex();
        else return -1;
    }
    
    
    
    private void addRow()
    {
        ReferenceLine[] tempTable = new ReferenceLine[ 2*size ];
        
        System.arraycopy( tableRef, 0, tempTable, 0, size );
        
        tableRef = tempTable;
    }
}
