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
    
    
    
    public char insertReference( String pr )
    {
        //Creo l'indice del gruppo di predicati sommando al carattere 
        //'a' ( base ) la posizione raggiunta nella tabella
        char ind = ( char ) ( 'a' + size );
        
        if( size == tableRef.length ) addRow();
        
        //Aggingo alla tabella il riferimento
        tableRef[ size ] = new ReferenceLine( ind, pr );
        //Incremento il contatore di righe
        size++;
        
        //Restituisco l'indice che e' stato asssiocato a pr nella 
        //ReferenceTable
        return ind;
    }
    
    
    
    public void printAllReference()
    {
        for( int i = 0; i < size; i++ ) 
            System.out.println( tableRef[i].getIndex() + 
                                " " + tableRef[i].getPredicate() );
    }
    
    
    
    private void addRow()
    {
        ReferenceLine[] tempTable = new ReferenceLine[ 2*size ];
        
        System.arraycopy( tableRef, 0, tempTable, 0, size );
        
        tableRef = tempTable;
    }
}
