import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bwapi.*;
import bwta.BWTA;

public class FirstBot extends DefaultBWListener
{
	public static final int maxWorker = 30;
	public List<EnemyUnit> enemyUnits = new ArrayList<>();
	public Unit scout;
	public Map<TilePosition, Boolean> startLocations = new HashMap<>();
	public TilePosition enemyLocation = TilePosition.Invalid;
	public List<SoldierGroup> soldierGroups = new ArrayList<>();

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
		if (soldierGroups.isEmpty())
			soldierGroups.add(new SoldierGroup());

		if(!(unit.getPlayer().getType() == PlayerType.Neutral ||  unit.getPlayer().getType() == PlayerType.None)
				&& unit.getPlayer().getID() == self.getID() && !unit.getType().isBuilding() && !unit.getType().isWorker())
		{
			System.out.println("Neuer Soldat: " + unit.getType());
			MySoldier soldier = new MySoldier(unit);
			boolean foundGroup = false;
			for(SoldierGroup s: soldierGroups)
			{
				if (s.isInBase())
				{
					if (s.addSoldier(soldier))
					{
						foundGroup = true;
						break;
					}
				}
			}
			if(!foundGroup)
				soldierGroups.add(new SoldierGroup(soldier));
		}
    }
    
    @Override
    public void onEnd(boolean b) {
    	System.out.println("Ende!");
    	System.out.println(enemyUnits);
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = getGame().self();

        if (!getGame().isMultiplayer())
        {
            // L�sst es zu das der User Eingaben vornehmen kann
            getGame().enableFlag(1);
            // Eingabe von Cheats f�r schnelleres Bauen, viele Mineralien etc.
            // Mache cheats werden von bwapi nicht erkannt, z.B "food for thought" usw.
//        	getGame().sendText("show me the money"); // 10000 Mineralien 10000 Gas
//        	getGame().sendText("operation cwal"); // schnelleres Bauen
//        	getGame().sendText("black sheep wall"); // ganze karte sehen
//        	getGame().sendText("power overwhelming"); // einheiten und geb�ude unverwundbar

            getGame().setLocalSpeed(15);
        }
        for(TilePosition l:FirstBot.getGame().getStartLocations())
        {
        	 startLocations.put(l, false);
        }
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
    }

        
    @Override
    public void onUnitDiscover(Unit unit)
    {
    	if (!(unit.getPlayer().getType() == PlayerType.Neutral ||  unit.getPlayer().getType() == PlayerType.None) 
    			&& unit.getPlayer().getID() != self.getID())
    	{
    		System.out.println("Neuer Feind: " + unit.getType());
    		enemyUnits.add(new EnemyUnit(unit));
    	}
    	
    }
    
    @Override
    public void onUnitDestroy(Unit unit) {
    	enemyUnits.remove(unit);
    }

    int vorherig = 0;
    @Override
    public void onFrame() {
    	if (getGame().getFrameCount() % 500 == 0)
    	{
    		System.out.println("Wachstum: " + Integer.toString(getSelf().gatheredMinerals() - vorherig));
        	vorherig = getSelf().gatheredMinerals();
    	}
    	
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
 	        
 	        for(EnemyUnit b : new ArrayList<>(enemyUnits))
 	        {
				if((b.unit.isVisible() && !b.unit.exists()))
				{
					enemyUnits.remove(b);
				}
			}
 	        
 	        //Update SoldierGroups
 	        if (!soldierGroups.isEmpty())
 	        {
 	        	for(SoldierGroup sg : new ArrayList<>(soldierGroups))
 	 	        {
 	        		
 	 	        	System.out.println("soldierGroupSize: " + sg.getSoldierCount());
 	 	        	sg.updateSoldierStatus();
 	 	        	if(sg.isEmpty())
 	 	        		soldierGroups.remove(sg);
 	 	        }
 	        }


 	        
 	       
 	        //Worker-Scouting:
			if(enemyUnits.isEmpty())
				scout = null;
 	        if (scout == null)
 	        {
 	        	for (Unit myUnit : FirstBot.getSelf().getUnits()) 
 	        	{
 	        		if (myUnit.getType() == UnitType.Terran_SCV) 
 	        		{
 	        			scout = myUnit;
 	        		}

 	        		break;
 	        	}
 	        }
 	        if(enemyUnits.isEmpty())
 	        {
 	        	enemyLocation = Attack.scout(scout, startLocations);
 	        }
 	        else
 	        {
 	        	scout = null;
 	        }
 	        
 	        //Angriff:
 	        if (getGame().getFrameCount() % 20 == 0)
 	        {
				enemyUnits = Attack.think(enemyUnits, true);
 	        	Attack.attack(enemyUnits, soldierGroups, enemyLocation, startLocations);
 	        }
 	        //Bauen: 	        
	    	if ((self.supplyTotal() - self.supplyUsed() <= 5) && (self.minerals() >= 100)) 
	    	{
	    		Build.build(UnitType.Terran_Supply_Depot, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}
	    	
	    	if (myBuildingsCounter.getOrDefault(UnitType.Terran_Refinery,0) < 1 && myBuildingsCounter.getOrDefault(UnitType.Terran_Barracks,0) > 2 && self.minerals() >= UnitType.Terran_Refinery.mineralPrice())
	    	{
	    		Build.build(UnitType.Terran_Refinery, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}
	    	
	    	if (myBuildingsCounter.getOrDefault(UnitType.Terran_Academy,0) < 1 && self.minerals() >= UnitType.Terran_Academy.mineralPrice() && self.gas() >= UnitType.Terran_Academy.gasPrice())
	    	{
	    		Build.build(UnitType.Terran_Academy, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}
	    	
	    	if ((myBuildingsCounter.getOrDefault(UnitType.Terran_Barracks,0) < 3 && self.minerals() >= UnitType.Terran_Barracks.mineralPrice())||self.minerals() >= 350) 
	    	{
	    		Build.build(UnitType.Terran_Barracks, myPeopleCounter.getOrDefault(UnitType.Terran_SCV,0));
	    	}


	        //iterate through my units,
	        for (Unit vCurrentUnit : getSelf().getUnits())
	        {
	        
	        	//Gebäude reparieren
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
	               	 getSelf().minerals() >= 50
	               	 ){
	                   vCurrentUnit.train(getSelf().getRace().getWorker());
	            }
	        	
	        	if ( vCurrentUnit.getType() == UnitType.Terran_Barracks &&
		        		 vCurrentUnit.isCompleted() &&
		               	 !vCurrentUnit.isTraining() &&
		               	 myPeopleCounter.getOrDefault(UnitType.Terran_Medic,0) < 3 &&
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
	            if (vCurrentUnit.getType().isWorker() && vCurrentUnit.isIdle() && !vCurrentUnit.isSelected() && ( scout == null || vCurrentUnit.getID() != scout.getID()))
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
	        if (!getGame().isMultiplayer())
	        {
		        ausgabe();
	        }

    	}
    	catch( Exception vException )
    	{
       		vException.printStackTrace();
       	}
    	
    	

    }




    private void ausgabe() {
		for (Unit vCurrentUnit : getSelf().getUnits())
		{
			if(vCurrentUnit.getTarget() != null)
			{
				getGame().drawLineMap(vCurrentUnit.getPosition(), vCurrentUnit.getTargetPosition(), Color.Red);
			}
			if(vCurrentUnit.getOrderTarget() != null)
			{
				getGame().drawLineMap(vCurrentUnit.getPosition(), vCurrentUnit.getOrderTargetPosition(), Color.Yellow);
			}

		}
		for(EnemyUnit u : enemyUnits)
		{
			getGame().drawTextMap(u.getPosition(), "Weight:" + u.getWeight() + "\nUnitID" + u.unit.getID() + "\nType:" +u.getType());
		}
		
	}

	public static void main(String[] args) {
        new FirstBot().run();
    }
}
