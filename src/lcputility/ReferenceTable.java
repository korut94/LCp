package lcputility;

/**
 *
 * @author amantova
 */
public class ReferenceTable 
{
    //Numero di righe da agguingere ad ogni chiamata di addRow
    private final int incrementRow;
    //Numero di righe della tabella
    private int size;
    //Tabella dei riferimenti tra singolo carattere e gruppo di proposizioni        
    private String[] tableRef;
    
    public ReferenceTable( int numRow )
    {
        tableRef = new String[ numRow ];
        incrementRow = 10;
        size = 0;
    }
    
    public String getReference( String index )
    {
        //Rimuoco i cancelletti che delimitano il numero
        index = index.substring( 1, index.length() - 1 );
        //Estrapolo l'intero contenuto
        int row = Integer.parseInt( index );
        
        //Restituisco la stringa referenziata
        return tableRef[ row ];
    }
    
    public String insertReference( String pr )
    {
        if( size == tableRef.length ) addRows();
        
        //Nel caso si richiedesse di inserire un predicato gia' inserito
        //e' inutile crearli una riga apposita, restituiamo l'indice 
        //corrispondente. 
        int ind = alreadyInsert( pr );
        
        if( ind != -1 ) return '#'+Integer.toString( ind )+'#';
        
        //Altrimenti aggiungo alla tabella il riferimento
        tableRef[ size ] = pr;
        
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
        for( int i = 0; i < size; i++ ) System.out.println( i + " " + tableRef[i] );
    }
    
    private int alreadyInsert( String pr )
    {
        boolean found = false;
        int i = 0;
        
        while( i < size && !found ) 
        {
            //Trovato e mantengo la posizione in cui l'ho trovato
            if( tableRef[i].equals( pr ) ) found = true;
            //Continuo a cercarlo
            else i++;
        }
        
        if( found ) return i;
        else return -1;
    }
    
    private void addRows()
    {
        String[] tempTable = new String[ size + incrementRow ];
        
        System.arraycopy( tableRef, 0, tempTable, 0, size );
        
        tableRef = tempTable;
    }
}
