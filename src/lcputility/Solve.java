package lcputility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author Mantovani Andrea
 * 
 * @version 1.2.2
 * - Implementata la gestione del sequente vuoto sia a destra che a sinistra
 * 
 * - DA AGGIUNGERE NELLE VERSIONI SUCCESSIVE:
 * 	 + Controllore sintattico del sequente
 * 	 + Struttura per contenere l'albero di derivazione, con annesse regole utilizzate, per poterlo stampare a video in un formato leggibile
 * 	 + Il metodo threeLeaf deve restituire l'albero di derivazione
 *   + Smaltire il numero di sequenti compatti singoli inseriti nella ReferenceTable:
 *     / Riducento all'essenziale il numero di parentesi tonde dentro ad un sequente prima di compattarlo
 *     / Scompattare il sequente compatto una volta inserito nell'albero ed eliminando parentesi superflue
 * 
 * @version 1.2.1 
 * - Introdotta la classe legOperand per introdurre la forza del legame tra operatori e operandi, evitando che l'utente lo svolga a mano attraverso le parentesi tonde 
 * - Risolta la mancata decomprensione del sequente compatto singolo nel caso fosse stato estratto un'altro sequente compatto singolo
 * 
 * - DA AGGIUNGERE NELLE VERSIONI SUCCESSIVE:
 * 	 + Controllore sintattico del sequente
 * 	 + Gestione del sequente vuoto
 * 	 + Struttura per contenere l'albero di derivazione, con annesse regole utilizzate, per poterlo stampare a video in un formato leggibile 
 * 	  
 * @version 1.1.0
 * - Tolto un qualsiasi limite alla grandezza dell'albero
 * 
 * @version 1.0.0
 * - La derivazione di sequenti semplici viene svolta senza errori
*/

public class Solve implements Runnable
{
	//Tabella la quale ogni entry e' il sequente di un ramo. Alla fine conterra'
    //tutte le foglie dell'albero.
    private final ArrayList<Predicate> sequents;
	
    private boolean reader;

    //L'indice sara equivalente al numero del thread che lavora al sequente
    private int indexSequent;

    private final legOperand operandPriority;
    
    //Negato del sequente da in input
    private final Predicate negSequent;
    
    private final ReferenceTable tableGroup;

    private Tree alberoDerivato;

    public Solve( String sx, String dx )
    {
        tableGroup = new ReferenceTable( 20 );
        sequents = new ArrayList<Predicate>();
        operandPriority = new legOperand();

        //La chiamata dal main ha indice 0
        indexSequent = 0;
        
        sx = operandPriority.addPriority( sx );
        dx = operandPriority.addPriority( dx );
        
        System.out.println( sx );
        System.out.println( dx ); 
        
        //Negato del sequente
        String negative = "-(";
        
        if( sx.isEmpty() ) negative += "(%)>";
        else negative += '(' + sx + ")>";
        
        if( dx.isEmpty() ) negative += "(@))";
        else negative += '(' + dx + "))";
        
        negative = compatta( negative ) + ",@";

        //Per evitare errori di overflow dell'array e correttezza nei confronti
        //della logica, una lista non deve essere mai vuota. Il % indica il
        //vero mentre @ indica il falso.
        sx = ( sx.isEmpty() ) ? "%" : "%," + compatta( sx );
        dx = ( dx.isEmpty() ) ? "@" : compatta( dx ) + ",@";

        //Se non ci fosse la virgola nella stringa sorgente sx lo split
        //ritornera esattamente sx. In quel caso la lunghezza dell'array
        //sarebbe 1 e quindi ogni riferimento all'ultimo elemento coinciderebbe
        //con l'unico presente.
        String[] elemPrSx = sx.split( "," );
        String[] elemPrDx = dx.split( "," );
        String[] elemNeSx = { "%" };
        String[] elemNeDx = negative.split( "," );
        //Crea le liste di sinitra e destra di stringhe a partire dall'array
        //ottenuto dallo split.
        sequents.add( new Predicate( Arrays.asList( elemPrSx ), Arrays.asList( elemPrDx ) ) );
        
        //Creo il sequente negato che verr� risolto dopo il sequente originale
        negSequent = new Predicate( Arrays.asList( elemNeSx ), Arrays.asList( elemNeDx ) );

        reader = true;
    }



    public String threeLeaf()
    {
        //Deriva l'albero utilizzando un thread per ramo
        run();

        //Numero di foglie raggiunto
        indexSequent++;

        int response = tautology( 0 );

        //Tutte le foglie sono assiomi
        if( response == indexSequent ) return "Il sequente e' una tautologia";
        else
        {
            int posNotAxiomSeq = response;

            int baseNegSequent = indexSequent;
            sequents.add( negSequent );

            //Derivo il sequente negato, indexSequent aumentera' in base al
            //numero di foglie che sono generate
            run();

            //Numero di foglie raggiunto
            indexSequent++;

            response = tautology( baseNegSequent );

            if( response == indexSequent ) return "Il sequente e' un paradosso";

            else
            {
            	System.out.println( "Sequente" );

                for( int i = 0; i < baseNegSequent; i++ ) sequents.get( i ).printPredicate();

                System.out.println( "Negato" );

                for( int i = baseNegSequent; i < indexSequent; i++ ) sequents.get( i ).printPredicate();
            	
                int posNotAxiomNeg = response;

                String result = "Il sequente non e' valido per: ";

                ArrayList<String> preSx = sequents.get( posNotAxiomSeq ).prSx;
                ArrayList<String> preDx = sequents.get( posNotAxiomSeq ).prDx;

                //Rimuovo il segno '%' dalla lista di sinista e il segno '@' dalla
                //lista di destra essendo annotazioni superflue da questo punto
                preSx.remove( 0 );
                preDx.remove( preDx.size() - 1 );

                result += atomicVariableSet( preSx, '1' );
                result += atomicVariableSet( preDx, '0' );

                result += '\n' + "Il sequente e' soddisfacibile per: ";

                preSx = sequents.get( posNotAxiomNeg ).prSx;
                preDx = sequents.get( posNotAxiomNeg ).prDx;

                //Rimuovo il segno '%' dalla lista di sinista e il segno '@' dalla
                //lista di destra essendo annotazioni superflue da questo punto
                preSx.remove( 0 );
                preDx.remove( preDx.size() - 1 );

                result += atomicVariableSet( preSx, '1' );
                result += atomicVariableSet( preDx, '0' );

                return result;
            }
        }
    }



    @Override
    public void run()
    {
        int index = 0;

        synchronized( this )
        {
            index = indexSequent;
            reader = false;

            //Sveglio i thread in attesa di andare in esecuzione
            notify();
        }
        
        derThree( sequents.get( index ).prSx, sequents.get( index ).prDx, index );
    }



    private boolean foundOperand( String s )
    {
        boolean found = false;
        char op;

        //Cerco nella stringa passata gli operatori
        for( int i = 0; i < s.length() && !found; i++ )
        {
            op = s.charAt( i );
            found = ( op == '&' || op == 'v' || op == '-' || op == '>' || op == '#' );
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


    /*
    Tautology restituisce la posizione di dove si e' fermato, se fosse uguale a
    indexSequent vuol dire che tutte le foglie sono assiomi, altrimenti e' stata
    trovata un foglia che non si chiude
    */
    private int tautology( int start )
    {
        //Lo start indica in quale posizione dell'array inizia l'elenco delle
        //foglie dell'albero preso in esame
        boolean foundNotAxiom = false;
        int indPre = start;

        //Cerca la prima foglia che non si chiude
        while( indPre < indexSequent && !foundNotAxiom )
        {
            if( !( isAxiomIdentity( sequents.get( indPre ).prSx, sequents.get( indPre ).prDx ) ) )
            {
                foundNotAxiom = true;
            }

            else indPre++;
        }

        return indPre;
    }



    private String atomicVariableSet( ArrayList<String> variable, char set )
    {
        String setting = new String();
        int cntVar = variable.size();

        //Il risultato e' la stringa composta dalla variabili atomiche
        //contenute in variable con affianco il segno uguale e il valore
        //rappresentato da set
        for( int i = 0; i < cntVar; i++ )
        {
            setting += variable.get( i ) + '=' + set + " ";
        }

        return setting;
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



    private synchronized void creaRamo( ArrayList<String> sx, ArrayList<String> dx, String s, int verso )
    {
        try{ while( reader ) wait(); }
        catch( Exception e ){}

        //Prenoto la lettura dell'array sequents
        reader = true;

        //Creo il ramo
        indexSequent++;
        sequents.add( new Predicate( sx, dx ) );

        //Ramo sinistro
        if( verso == 0 ) sequents.get( indexSequent ).prSx.add( s );
        //Ramo destro
        else if( verso == 1 ) sequents.get( indexSequent ).prDx.add( 0, s );
    }



    private void derThree( ArrayList<String> listSx, ArrayList<String> listDx, int idThread )
    {
        boolean isLeaf = false;

        //Pila dei rami che genera la derivazione
        Stack<Thread> threads = new Stack<Thread>();

        while( !isLeaf )
        {
            System.out.println( listSx.toString() + "|-" + listDx.toString() + " " + idThread );

            int lastElemSx = listSx.size() - 1;

            //Stringa di servizio
            String derElem = new String();

            //Presente un periodo compatto singolo nella lista di sinistra
            while( listSx.get( lastElemSx ).matches( "#.#$" ) || listSx.get( lastElemSx ).matches( "#..#$" ) )
            {
                //Decomprimo il periodo e lo inserisco al posto di
                //quello compatto
                listSx.add( tableGroup.getReference( listSx.remove( lastElemSx ) ) );
            }

            //Presente un periodo compatto singolo nella lista di destra
            while( listDx.get( 0 ).matches( "^#.#" ) || listDx.get( 0 ).matches( "^#..#" ) )
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
            	alberoDerivato.add( new Predicate( listSx, listDx ), "&-sx", idThread );
            	
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
            	alberoDerivato.add( new Predicate( listSx, listDx ), "V-dx", idThread );
            	
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

                //Creo il thread per il ramo destro
                Thread trDxE = new Thread( this );

                creaRamo( listSx, listDx, splitE[1], 1 );

                trDxE.start();

                //Puscio il thread nello stack per eseguire la join
                //successivamente
                threads.push( trDxE );

                //Inserisco l'operando A nella stringa di destra in modo
                //da creare il ramo sinistro
                listDx.add( 0, splitE[0] );
            }

            //Applicata la regola della v sinistra
            else if( listSx.get( lastElemSx ).contains( "v" ) )
            {
                //Rimuovo l'ultimo elemento della lista di sinistra
                derElem = listSx.remove( lastElemSx );
                //Faccio lo split per ottenere A e B dell'operatore v
                String[] splitV = derElem.split( "v" );

                //Creo il thread per il ramo destro
                Thread trDxV = new Thread( this );

                creaRamo( listSx, listDx, splitV[1], 0 );

                trDxV.start();

                //Puscio il thread nello stack per eseguire la join
                //successivamente
                threads.push( trDxV );

                //Puscio l'operatore A nella stringa di sinistra in modo
                //da creare il ramo sinistro
                listSx.add( splitV[0] );
            }

            //Applicata la regola del > sinstra
            else if( listSx.get( lastElemSx ).contains( ">" ) )
            {
                //Rimuovo l'ultimo elemento della stringa di sinistra
                derElem = listSx.remove( lastElemSx );
                //Faccio lo split per ottenere A e B dell'operatore >
                String[] splitImp = derElem.split( ">" );

                //Creo il thread per il ramo destro
                Thread trDxImp = new Thread( this );

                creaRamo( listSx, listDx, splitImp[1], 0 );

                trDxImp.start();

                //Puscio il thread nello stack per eseguire la join
                //successivamente
                threads.push( trDxImp );

                //Inserisco A nella stringa di destra per creare il ramo
                //di sinistra
                listDx.add( 0, splitImp[0] );
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
                if( pos == -1 && listDx.size() > 1 )
                {
                    pos = presentOperand( listDx, 1, listDx.size() );

                    //Nessun nuovo operando o periodo compatto trovato a destra
                    //e a sinistra. Siamo in presenza di una foglia senza assiomi
                    if( pos == -1 )
                    {
                    	//Applico la regola del compattamento a sinistra
                    	ruleCompact( listSx );
                    	//Applico la regola del compattamento a destra
                    	ruleCompact( listDx );
                    	
                        isLeaf = true;
                        //Attendi che termino gli altri rami
                        waitThread( threads );
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
                	//Applico la regola del compattamento a sinistra
                	ruleCompact( listSx );
                	
                	isLeaf = true;
                    //Attendi che termino gli altri rami
                    waitThread( threads );
                }
            }
        }
    }



    private void ruleCompact( ArrayList<String> list )
    {
    	//Posizione iniziale del pattern
    	int posPattern = 0;
    	//Il matcher iniziale e' sempre una posizione in avanti del pattern
    	int posMatcher = 1;
    	
    	//Scorro la lista mantenendo fisso il pattern
    	while( posPattern < list.size() )
    	{
    		//E scandendo ogni possibile matcher successivo 
    		while( posMatcher < list.size() )
        	{
    			//Se sono uguali elimino il matcher in modo da non fare controlli se il valore di posPattern sia idoneo o no
    			if( list.get( posMatcher ).equals( list.get( posPattern ) ) ) list.remove( posMatcher );
        		else posMatcher++;
        	}
    		
    		posPattern++;
    		posMatcher = posPattern + 1;
    	}
    }
    
    
    
    private void waitThread( Stack<Thread> headerThr )
    {
        while( !headerThr.isEmpty() )
        {
            Thread td = headerThr.pop();
            try{ td.join(); }
            catch( InterruptedException e ){}
        }
    }
    
}
