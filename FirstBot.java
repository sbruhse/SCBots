import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import bwapi.*;
import bwta.BWTA;
import bwta.Region;

public class FirstBot extends DefaultBWListener 
{
	Map<UnitType, Boolean> building = new HashMap<>();
	Unit bobTheBuilder = null;
//	Vector<Building> buildorder = new Vector<>();
	
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
    	if (unit.getType() == UnitType.Buildings.Terran_Supply_Depot)
        {
        	building.put(UnitType.Buildings.Terran_Supply_Depot, false);
        }
        else if (unit.getType() == UnitType.Buildings.Terran_Refinery)
        {
        	building.put(UnitType.Buildings.Buildings.Terran_Refinery, false);
        }
        else if (unit.getType() == UnitType.Buildings.Terran_Barracks)
        {
        	building.put(UnitType.Buildings.Buildings.Terran_Barracks, false);
        }
    }
    
    @Override
    public void onStart() {
        game = mirror.getGame();
        self = getGame().self();

        // Lässt es zu das der User Eingaben vornehmen kann
        getGame().enableFlag(1);
        // Eingabe von Cheats für schnelleres Bauen, viele Mineralien etc.
        // Mache cheats werden von bwapi nicht erkannt, z.B "food for thought" usw.
//    	getGame().sendText("show me the money"); // 10000 Mineralien 10000 Gas
//    	getGame().sendText("operation cwal"); // schnelleres Bauen
//    	getGame().sendText("black sheep wall"); // ganze karte sehen
//    	getGame().sendText("power overwhelming"); // einheiten und gebäude unverwundbar
    	
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        //Buildings:
        building.put(UnitType.Buildings.Terran_Supply_Depot, false);
		building.put(UnitType.Buildings.Terran_Refinery, false);
		building.put(UnitType.Buildings.Terran_Barracks, false);
		
		
        
    }
    
    @Override
    public void onFrame() {
    	try
    	{
    		
	    	Build build;
	    	//Editing Buildorder:
	        //Supply
	        if (building.get(UnitType.Buildings.Terran_Supply_Depot) == false &&(FirstBot.getSelf().supplyTotal() - FirstBot.getSelf().supplyUsed()) <= 3 && building.get(UnitType.Buildings.Terran_Supply_Depot) == false)
	        {
	        	build = new Build(UnitType.Buildings.Terran_Supply_Depot);
	        	build.run();
	//        	building.put(UnitType.Buildings.Terran_Supply_Depot, true);
	        	System.out.println("Added supply to buildorder");
	        }
	        //Refinery
	        if( building.get(UnitType.Buildings.Terran_Refinery) == false && FirstBot.getSelf().gas() == 0 && FirstBot.getGame().elapsedTime() >= 250 && FirstBot.getSelf().minerals() >= FirstBot.getSelf().getRace().getRefinery().mineralPrice() )
	        {
	        	build = new Build(UnitType.Buildings.Terran_Refinery);
	        	build.run();
	//    		building.put(UnitType.Buildings.Terran_Refinery, true);
	        	System.out.println("Added refinery to buildorder");
	        }
	        //Barracks
	        if( building.get(UnitType.Buildings.Terran_Barracks) == false && FirstBot.getSelf().minerals() >= UnitType.Buildings.Terran_Barracks.mineralPrice())
	        {
	        	build = new Build(UnitType.Buildings.Terran_Barracks);
	        	build.run();
	//        	building.put(UnitType.Buildings.Terran_Barracks, true);
	        	System.out.println("Added Barrack to buildorder");
	        }
	        
	        int vWorkerCount = 0;
	        //iterate through my units, count workers
	        for (Unit vCurrentUnit : getSelf().getUnits()) 
	        {
	        	if (vCurrentUnit.getType().isWorker()) {
	        		++vWorkerCount;
	        	}
	        }
	        
	        //iterate through my units,
	        for (Unit vCurrentUnit : getSelf().getUnits()) 
	        {
	        	//if there's enough minerals, train a Worker if there are less than 50
	        	if ( vWorkerCount < 50 && 
	               	 vCurrentUnit.getType() == getSelf().getRace().getCenter() &&
	               	 !vCurrentUnit.isTraining() &&
	               	 getSelf().minerals() >= 50 ) {
	                   vCurrentUnit.train(getSelf().getRace().getWorker());
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