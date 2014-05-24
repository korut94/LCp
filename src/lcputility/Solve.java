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
    private ArrayList<String> listDx;
    private ArrayList<String> listSx;
    //1° parametro indice della foglia dell'albero
    //2° parametro sequente sinistro o destro
    //3° parametro indice delle proposizione che compongono il sequente
    //specificato
    private String[][][] tableLeaf;
    
    
    
    public Solve( String sx, String dx )
    {
        //Se non ci fosse la virgola nella stringa sorgente sx lo split
        //ritornera esattamente sx. In quel caso la lunghezza dell'array 
        //sarebbe 1 e quindi ogni riferimento all'ultimo elemento coinciderebbe
        //con l'unico presente.
        String[] elemPrSx = sx.split( "," );
        String[] elemPrDx = dx.split( "," );
        
        //Crea le liste di sinitra e destra di stringhe a partire dall'array 
        //ottenuto dallo split
        listSx = new ArrayList<String>();
        listSx.addAll( Arrays.asList( elemPrSx ) );
        
        listDx = new ArrayList<String>();
        listDx.addAll( Arrays.asList( elemPrDx ) );
        
        tableLeaf = new String[20][2][];
    }
    
    
    
    public String[][][] threeLeaf()
    {
        return tableLeaf;
    }
    
    
    
    public void run()
    {
        
    }
    
}
