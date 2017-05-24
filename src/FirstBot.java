import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import bwapi.*;
import bwta.BWTA;

public class FirstBot extends DefaultBWListener
{
	public static final int maxWorker = 30;
	public List<EnemyUnit> enemyBuildings = new ArrayList<>();

	private Mirror mirror = new Mirror();

    static private Game game;
    static public Game getGame(){
    	synchronized (game) {
    		return game;
		}
    }

    static private Player self;
    static public Player getSelf(){
    	synchronized (self) {
    		return self;
		}
    }

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }
    

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
    }

    
    
    @Override
    public void onUnitComplete(Unit unit) {
    	
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = getGame().self();

        if (!getGame().isMultiplayer())
        {
            // Lässt es zu das der User Eingaben vornehmen kann
            getGame().enableFlag(1);
            // Eingabe von Cheats für schnelleres Bauen, viele Mineralien etc.
            // Mache cheats werden von bwapi nicht erkannt, z.B "food for thought" usw.
//        	getGame().sendText("show me the money"); // 10000 Mineralien 10000 Gas
//        	getGame().sendText("operation cwal"); // schnelleres Bauen
//        	getGame().sendText("black sheep wall"); // ganze karte sehen
//        	getGame().sendText("power overwhelming"); // einheiten und gebäude unverwundbar

            getGame().setLocalSpeed(13);
        }

        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
    }

        
    @Override
    public void onUnitDiscover(Unit unit)
    {
    	if (unit.getType().isBuilding() 
    			&& !(unit.getPlayer().getType() == PlayerType.Neutral ||  unit.getPlayer().getType() == PlayerType.None) 
    			&& unit.getPlayer().getID() != self.getID())
    	{
    		System.out.println("Neues Feindgebäude: " + unit.getType());
    		enemyBuildings.add(new EnemyUnit(unit));
    	}
    }
    
    @Override
    public void onUnitDestroy(Unit unit) {

    }

    @Override
    public void onFrame() {
    	try
    	{
    		Map<UnitType, Integer> myBuildingsCounter = new HashMap<>();
    		Map<UnitType, Integer> myPeopleCounter = new HashMap<>();
    		
 	        //Zählen:
 	        for (Unit vCurrentUnit : getSelf().getUnits())
 	        {
 	        	if (vCurrentUnit.getType().isBuilding() && vCurrentUnit.getPlayer().getID() == getSelf().getID())
 	        	{
 	        		if (myBuildingsCounter.get(vCurrentUnit.getType()) == null)
 	        		{
 	        			myBuildingsCounter.put(vCurrentUnit.getType(), 0);
 	        		}
 	        		int counter  = myBuildingsCounter.get(vCurrentUnit.getType());
 	        		myBuildingsCounter.put(vCurrentUnit.getType(), ++counter);
 	        	}
 	        	if (vCurrentUnit.getPlayer().getID() == getSelf().getID() && !vCurrentUnit.getType().isBuilding())
 	        	{
 	        		if (myPeopleCounter.get(vCurrentUnit.getType()) == null)
 	        		{
 	        			myPeopleCounter.put(vCurrentUnit.getType(), 0);
 	        		}
 	        		int counter  = myPeopleCounter.get(vCurrentUnit.getType());
 	        		myPeopleCounter.put(vCurrentUnit.getType(), ++counter);
 	        	}
 	        }
 	        
 	        System.out.println("Feindgebäude: ");
 	        for(EnemyUnit b : enemyBuildings)
 	        {
 	        	System.out.print(b + ", " + b.getType() + "; ");
 	        	if(b.myUnit.isVisible() && !b.myUnit.exists())
 	        	{
 	        		enemyBuildings.remove(b);
 	        		System.out.println("Feindgebäude " + b + " existiert nicht mehr");
 	        	}
 	        }
 	        System.out.println("");
 	        
 	        if (getGame().getFrameCount() % 10 == 0)
 	        {
 	        	Attack.attack(enemyBuildings);
 	        }
        	
 	        //Bauen:
	    	if ((self.supplyTotal() - self.supplyUsed() <= 4) && (self.minerals() >= 100)) 
	    	{
	    		Build.build(UnitType.Terran_Supply_Depot, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}

	    	if (myBuildingsCounter.getOrDefault(UnitType.Terran_Barracks,0) < 3 && self.minerals() >= UnitType.Terran_Barracks.mineralPrice()) 
	    	{
	    		Build.build(UnitType.Terran_Barracks, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}
	    	
	    	if (myBuildingsCounter.getOrDefault(UnitType.Terran_Refinery,0) < 1 && myBuildingsCounter.getOrDefault(UnitType.Terran_Barracks,0) > 1 && self.minerals() >= UnitType.Terran_Refinery.mineralPrice())
	    	{
	    		Build.build(UnitType.Terran_Refinery, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}
	    	
	    	if (myBuildingsCounter.getOrDefault(UnitType.Terran_Academy,0) < 1 && self.minerals() >= UnitType.Terran_Academy.mineralPrice() && self.gas() >= UnitType.Terran_Academy.gasPrice())
	    	{
	    		Build.build(UnitType.Terran_Academy, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}
	    	
//	    	if (myBuildingsCounter.getOrDefault(UnitType.Terran_Factory,0) < 2 && self.minerals() >= UnitType.Terran_Factory.mineralPrice() && self.gas() >= UnitType.Terran_Factory.gasPrice())
//	    	{
//	    		Build.build(UnitType.Terran_Factory, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
//	    	}



	        //iterate through my units,
	        for (Unit vCurrentUnit : getSelf().getUnits())
	        {
	        	
	        	if (vCurrentUnit.getType().isBuilding() && vCurrentUnit.getInitialHitPoints() > vCurrentUnit.getHitPoints())
	        	{
	        		for (Unit myUnit : FirstBot.getSelf().getUnits()) 
	        		{
	        			if (myUnit.getType() == UnitType.Terran_SCV && (myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0) >= FirstBot.maxWorker || myUnit.isIdle()))
	        			{
	        				myUnit.repair(vCurrentUnit);
	        			}
	        		}
	        	}
	        	
	        	//Einheiten bauen:
	        	if ( myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0) < maxWorker &&
	               	 vCurrentUnit.getType() == getSelf().getRace().getCenter() &&
	               	 !vCurrentUnit.isTraining() &&
	               	 getSelf().minerals() >= 50 ) {
	                   vCurrentUnit.train(getSelf().getRace().getWorker());
	            }
	        	
	        	if ( vCurrentUnit.getType() == UnitType.Terran_Barracks &&
		        		 vCurrentUnit.isCompleted() &&
		               	 !vCurrentUnit.isTraining() &&
		               	 myPeopleCounter.getOrDefault(UnitType.Terran_Medic,0) < 4 &&
		               	 getSelf().minerals() >= UnitType.Terran_Medic.mineralPrice() &&
		               	 getSelf().gas() >= UnitType.Terran_Medic.gasPrice() 
	        			)
	        	{
	                   vCurrentUnit.train(UnitType.Terran_Medic);
	            }

	        	if ( vCurrentUnit.getType() == UnitType.Buildings.Terran_Barracks &&
		        		 vCurrentUnit.isCompleted() &&
		               	 !vCurrentUnit.isTraining() &&
		               	 getSelf().minerals() >= 50 
	        			)
	        	{
	                   vCurrentUnit.train(UnitType.Terran_Marine);
	            }
	        	
	        	
	        	

	        	//if it's a worker and it's idle, send it to the closest mineral patch
	            if (vCurrentUnit.getType().isWorker() && vCurrentUnit.isIdle() && !vCurrentUnit.isSelected())
	            {
	                Unit closestMineral = null;

	                //find the closest mineral
	                for (Unit neutralUnit : getGame().neutral().getUnits())
	                {
	                    if (neutralUnit.getType().isMineralField())
	                    {
	                        if (closestMineral == null || vCurrentUnit.getDistance(neutralUnit) < vCurrentUnit.getDistance(closestMineral))
	                        {
	                            closestMineral = neutralUnit;
	                        }
	                    }
	                }

	                //if a mineral patch was found, send the worker to gather it
	                if (closestMineral != null)
	                {
	                    vCurrentUnit.gather(closestMineral, false);
	                }
	            }
	        }
    	}
    	catch( Exception vException )
    	{
       		vException.printStackTrace();
       	}

    }




    public static void main(String[] args) {
        new FirstBot().run();
    }
}
