package lcputility;

import java.util.ArrayList;

public class Tree 
{
	//Nodo utilizzati in questo istante dai thread
	private ArrayList<Nodo> workingNode; 
	
	private Nodo radice;
	
	@SuppressWarnings( "unused" )
	private class Nodo
	{
		private Derivate info;
		
		private Nodo treeDX;
		private Nodo treeSX;
	
		/**
		 * @param a : contenuto del nodo, sequente piu' regola
		 * @param b : sequente derivato
		 * @param c : eventuale biforcazione del sequente in due rami
		 */
		public Nodo( Derivate a, Nodo b, Nodo c )
		{
			info = a;
			treeSX = b;
			treeDX = c;
		}
	}
	
	/**
	 * Istanzia un albero binario vuoto
	 */
	public Tree()
	{
		radice = null;
		workingNode = new ArrayList<Nodo>();
	}
	
	/**
	 * @param seq : sequente da inserire nel campo info del nodo
	 * @param rule : regola applicata da inserire nel campo info del nodo
	 * @param idWorker : id del thread che lavora a quel ramo dell'albero
	 * @return Aggiunge un nodo all'albero contenente il sequente con la regola da applicare
	 */
	public void add( Predicate seq, String rule, int idWorker )
	{
		Derivate der = new Derivate( seq, rule );
		Nodo p = new Nodo( der, null, null );
		
		if( radice == null ) 
		{
			radice = p;
			workingNode.add( radice );
		}
		
		else
		{
			Nodo currentPoint = workingNode.get( idWorker );
			
			if( currentPoint.treeSX == null ) currentPoint.treeSX = p;
			else currentPoint.treeDX = p;
			
			workingNode.set( idWorker, p );
		}
	}
	
	/**
	 * @param idSxWorker : id thread che lavorera sul nuovo ramo sinistro dell'albero
	 * @param idDxWorker : id thread che lavorera sul nuovo ramo destro dell'albero 
	 * @return Crea un nuovo lavoratore sul nodo puntato da quello in corso in cui, quello nuovo procedera a destra e quello attuale a sinistra dell'albero
	 */
	public void branchSplit( int idSxWorker, int idDxWorker )
	{
		workingNode.add( idDxWorker, workingNode.get( idSxWorker ) );
	}
}
