/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lcputility;

/**
 *
 * @author amantova
 */
public class ReferenceLine 
{
    private int index;
    private String groupPr;
    
    
    
    public ReferenceLine( int c, String s )
    {
        index = c;
        groupPr = s;
    }
    
    
    
    public int getIndex()
    {
        return index;
    }
    
    
    
    public String getPredicate()
    {
        return groupPr;
    }
    
    
    
    public void setReference( char c, String s )
    {
        index = c;
        groupPr = s;
    }
}
