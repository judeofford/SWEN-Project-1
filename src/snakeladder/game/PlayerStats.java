package snakeladder.game;

import ch.aplu.jgamegrid.*;

import java.util.ArrayList;

public class PlayerStats
{
	private Puppet puppet;
	
	private ArrayList<String> rollsList = new ArrayList<String>();
	
	private int upTraversal = 0;
	private int downTraversal = 0;
	
    PlayerStats(NavigationPane np, Puppet p)
	{
	  this.puppet = p;
	}
    
    public void addRoll(String rolls) {
    	rollsList.add(rolls);
    }
    
    public void incrementUpTraversal() {
    	upTraversal++;
    }
    
    public void incrementDownTraversal() {
    	downTraversal++;
    }
    
    public void printStats() {
        System.out.print(puppet.getPuppetName()+" rolled: ");
        for(int i=0; i<rollsList.size()-1;i++) {
        	System.out.print(rollsList.get(i)+", ");
        }
        System.out.println(rollsList.get(rollsList.size()-1));
    	
        System.out.print(puppet.getPuppetName()+" traversed: "+ "up-"+upTraversal+", down-"+downTraversal+"\n");
    	
    }
}
