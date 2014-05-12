/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcputility;

/**
 *
 * @author amantova
 */
public class CompactInfo 
{
    public String indexTable;
    public int indexEndGroup;
    
    
    public CompactInfo( String a, int b )
    {
        indexTable = a;
        indexEndGroup = b;
    }
    
    
    
    public void printIndexTable()
    {
        System.out.println( indexTable );
    }
}
