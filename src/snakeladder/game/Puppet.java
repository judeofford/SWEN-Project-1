package snakeladder.game;

import ch.aplu.jgamegrid.*;
import java.awt.Point;
import java.lang.Math;

public class Puppet extends Actor
{
  private GamePane gamePane;
  private NavigationPane navigationPane;
  private int cellIndex = 0;
  private int nbSteps;
  private Connection currentCon = null;
  private int y;
  private int dy;
  private boolean isAuto;
  private String puppetName;
  private PlayerStats playerStats;
  private boolean rolledLow;
  private int stepsTaken;

  Puppet(GamePane gp, NavigationPane np, String puppetImage)
  {
    super(puppetImage);
    this.gamePane = gp;
    this.navigationPane = np;
    playerStats = new PlayerStats(np,this); //Initialize player stats
  }

  public boolean isAuto() {
    return isAuto;
  }

  public void setAuto(boolean auto) {
    isAuto = auto;
  }

  public String getPuppetName() {
    return puppetName;
  }

  public void setPuppetName(String puppetName) {
    this.puppetName = puppetName;
  }

  void go(int nbSteps)
  {
    if (cellIndex == 100)  // after game over
    {
      cellIndex = 0;
      setLocation(gamePane.startLocation);
    }
    this.nbSteps = nbSteps;
    setActEnabled(true);
  }

  void resetToStartingPoint() {
    cellIndex = 0;
    setLocation(gamePane.startLocation);
    setActEnabled(true);
  }

  int getCellIndex() {
    return cellIndex;
  }

  private void moveToNextCell()
  {
    int tens = cellIndex / 10;
    int ones = cellIndex - tens * 10;
    if (tens % 2 == 0)     // Cells starting left 01, 21, .. 81
    {
      if (ones == 0 && cellIndex > 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() + 1, getY()));
    }
    else     // Cells starting left 20, 40, .. 100
    {
      if (ones == 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() - 1, getY()));
    }
    cellIndex++;
  }

  public void act()
  {
    if ((cellIndex / 10) % 2 == 0)
    {
      if (isHorzMirror())
        setHorzMirror(false);
    }
    else
    {
      if (!isHorzMirror())
        setHorzMirror(true);
    }

    // Animation: Move on connection
    if (currentCon != null && rolledLow == false)
    // wont move a player who starts on a connection if they ended up there from a low roll
    {
      int x = gamePane.x(y, currentCon);
      setPixelLocation(new Point(x, y));
      y += dy;

      // Check end of connection
      if ((dy > 0 && (y - gamePane.toPoint(currentCon.locEnd).y) > 0)
        || (dy < 0 && (y - gamePane.toPoint(currentCon.locEnd).y) < 0))
      {
        gamePane.setSimulationPeriod(100);
        setActEnabled(false);
        setLocation(currentCon.locEnd);
        cellIndex = currentCon.cellEnd;
        setLocationOffset(new Point(0, 0));
        currentCon = null;
        navigationPane.prepareRoll(cellIndex);
      }
      return;
    }

    // Normal movement
    stepsTaken = 0;
    if (nbSteps > 0)
    {
      moveToNextCell();
      stepsTaken++;

      if (cellIndex == 100)  // Game over
      {
        setActEnabled(false);
        navigationPane.prepareRoll(cellIndex);
        return;
      }

      nbSteps--;
      if (nbSteps == 0)
      {
    	if (stepsTaken == navigationPane.diceCount) { //checks if the roll was the lowest possible
    		rolledLow = true;
    	}
    	else {
    		rolledLow = false;
    	}
        // Check if on connection start
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null && stepsTaken != 0) {
          if (rolledLow = true && currentCon.locEnd.y > currentCon.locStart.y) { //skips downward traversals on low rolls
        	  setActEnabled(false);
              navigationPane.prepareRoll(cellIndex);
          }
          else {
	          gamePane.setSimulationPeriod(50);
	          y = gamePane.toPoint(currentCon.locStart).y;
	          if (currentCon.locEnd.y > currentCon.locStart.y) {
	        	playerStats.incrementDownTraversal();
	            dy = gamePane.animationStep;
	          }
	          else {
	        	playerStats.incrementUpTraversal();
	            dy = -gamePane.animationStep;
	          }
	          
	          if (currentCon instanceof Snake)
	          {
	            navigationPane.showStatus("Digesting...");
	            navigationPane.playSound(GGSound.MMM);
	          }
	          else
	          {
	            navigationPane.showStatus("Climbing...");
	            navigationPane.playSound(GGSound.BOING);
	          }
          }
        }
        else
        {
          setActEnabled(false);
          navigationPane.prepareRoll(cellIndex);
        }
      }
    }
  }
  
  public PlayerStats getStats() {
	  return playerStats;
  }

}
