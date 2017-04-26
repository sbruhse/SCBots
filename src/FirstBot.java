import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import bwapi.*;
import bwta.BWTA;
import bwta.Region;

public class FirstBot extends DefaultBWListener
{
	public volatile Map<UnitType, Boolean> building = new HashMap<>();
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

        getGame().setLocalSpeed(10);

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
    public void onUnitDiscover(Unit unit)
    {
    	if(unit.getPlayer() != getSelf())
    	{
    		for (Unit vCurrentUnit : getSelf().getUnits())
	        {
    			if (vCurrentUnit.getType() == UnitType.Terran_Marine)
    			{
    				vCurrentUnit.attack(unit);
    			}
	        }
    	}
    }



    @Override
    public void onFrame() {
    	try
    	{
    		int vWorkerCount = 0;
 	        int vBarracksCount = 0;
 	        int vAccademyCount = 0;
 	        int vEngineeringBayCount = 0;
 	        int vMedicCount = 0;
 	        //iterate through my units, count workers
 	        for (Unit vCurrentUnit : getSelf().getUnits())
 	        {
 	        	if (vCurrentUnit.getType().isWorker())
 	        	{
 	        		++vWorkerCount;
 	        	}
 	        	if (vCurrentUnit.getType() == UnitType.Terran_Barracks)
 	        	{
 	        		++vBarracksCount;
 	        	}
 	        	if (vCurrentUnit.getType() == UnitType.Terran_Academy)
 	        	{
 	        		++vAccademyCount;
 	        	}
 	        	if (vCurrentUnit.getType() == UnitType.Terran_Engineering_Bay)
 	        	{
 	        		++vEngineeringBayCount;
 	        	}
 	        	if (vCurrentUnit.getType() == UnitType.Terran_Medic)
 	        	{
 	        		++vMedicCount;
 	        	}
 	        }
 	        
 	        Attack.attack();

	    	Build build;
	    	//Editing Buildorder:
	        //Supply
	    	if ((self.supplyTotal() - self.supplyUsed() < 3) && (self.minerals() >= 100)) {
	    		//iterate over units to find a worker
	    		for (Unit myUnit : self.getUnits()) {
	    			if (myUnit.getType() == UnitType.Terran_SCV) {
	    				//get a nice place to build a supply depot
	    				TilePosition buildTile =
	    					getBuildTile(myUnit, UnitType.Terran_Supply_Depot, self.getStartLocation());
	    				//and, if found, send the worker to build it (and leave others alone - break;)
	    				if (buildTile != null) {
	    					myUnit.build(UnitType.Terran_Supply_Depot, buildTile);
	    					break;
	    				}
	    			}
	    		}
	    	}

//	    	if (vAccademyCount < 1 && self.minerals() >= UnitType.Terran_Academy.mineralPrice()) {
//	    		//iterate over units to find a worker
//	    		for (Unit myUnit : self.getUnits()) {
//	    			if (myUnit.getType() == UnitType.Terran_SCV) {
//	    				//get a nice place to build a supply depot
//	    				TilePosition buildTile =
//	    					getBuildTile(myUnit, UnitType.Terran_Academy, self.getStartLocation());
//	    				//and, if found, send the worker to build it (and leave others alone - break;)
//	    				if (buildTile != null) {
//	    					myUnit.build(UnitType.Terran_Academy, buildTile);
//	    					break;
//	    				}
//	    			}
//	    		}
//	    	}
//
//	    	if (vAccademyCount >= 1 && vBarracksCount > 2 && self.minerals() >= UnitType.Terran_Engineering_Bay.mineralPrice()) {
//	    		//iterate over units to find a worker
//	    		for (Unit myUnit : self.getUnits()) {
//	    			if (myUnit.getType() == UnitType.Terran_SCV) {
//	    				//get a nice place to build a supply depot
//	    				TilePosition buildTile =
//	    					getBuildTile(myUnit, UnitType.Terran_Engineering_Bay, self.getStartLocation());
//	    				//and, if found, send the worker to build it (and leave others alone - break;)
//	    				if (buildTile != null) {
//	    					myUnit.build(UnitType.Terran_Engineering_Bay, buildTile);
//	    					break;
//	    				}
//	    			}
//	    		}
//	    	}
//
//	    	if (vEngineeringBayCount >= 1 && self.minerals() >= UnitType.Terran_Missile_Turret.mineralPrice()) {
//	    		//iterate over units to find a worker
//	    		for (Unit myUnit : self.getUnits()) {
//	    			if (myUnit.getType() == UnitType.Terran_SCV) {
//	    				//get a nice place to build a supply depot
//	    				TilePosition buildTile =
//	    					getBuildTile(myUnit, UnitType.Terran_Missile_Turret, self.getStartLocation());
//	    				//and, if found, send the worker to build it (and leave others alone - break;)
//	    				if (buildTile != null) {
//	    					myUnit.build(UnitType.Terran_Missile_Turret, buildTile);
//	    					break;
//	    				}
//	    			}
//	    		}
//	    	}

	    	if (vBarracksCount < 3 && self.minerals() >= UnitType.Terran_Barracks.mineralPrice()) {
	    		//iterate over units to find a worker
	    		for (Unit myUnit : self.getUnits()) {
	    			if (myUnit.getType() == UnitType.Terran_SCV) {
	    				//get a nice place to build a supply depot
	    				TilePosition buildTile =
	    					getBuildTile(myUnit, UnitType.Terran_Barracks, self.getStartLocation());
	    				//and, if found, send the worker to build it (and leave others alone - break;)
	    				if (buildTile != null) {
	    					myUnit.build(UnitType.Terran_Barracks, buildTile);
	    					break;
	    				}
	    			}
	    		}
	    	}





	        //iterate through my units,
	        for (Unit vCurrentUnit : getSelf().getUnits())
	        {
	        	//if there's enough minerals, train a Worker if there are less than 50
	        	if ( vWorkerCount < 30 &&
	               	 vCurrentUnit.getType() == getSelf().getRace().getCenter() &&
	               	 !vCurrentUnit.isTraining() &&
	               	 getSelf().minerals() >= 50 ) {
	                   vCurrentUnit.train(getSelf().getRace().getWorker());
	            }

	        	if ( vCurrentUnit.getType() == UnitType.Buildings.Terran_Barracks &&
		        		 vCurrentUnit.isCompleted() &&
		               	 !vCurrentUnit.isTraining() &&
		               	 getSelf().minerals() >= 50 &&
		               	 vAccademyCount < 1
	        			)
	        	{
	                   vCurrentUnit.train(UnitType.Terran_Marine);
	            }
	        	if ( vCurrentUnit.getType() == UnitType.Buildings.Terran_Barracks &&
		        		 vCurrentUnit.isCompleted() &&
		               	 !vCurrentUnit.isTraining() &&
		               	 getSelf().minerals() >= 50 &&
		               	 vAccademyCount > 1 &&
		               	 vMedicCount < 5
	        			)
	        	{
	                   vCurrentUnit.train(UnitType.Terran_Medic);
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

    public TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
    	TilePosition ret = null;
    	int maxDist = 3;
    	int stopDist = 40;

    	// Refinery, Assimilator, Extractor
    	if (buildingType.isRefinery()) {
    		for (Unit n : game.neutral().getUnits()) {
    			if ((n.getType() == UnitType.Resource_Vespene_Geyser) &&
    					( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
    					( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
    					) return n.getTilePosition();
    		}
    	}

    	while ((maxDist < stopDist) && (ret == null)) {
    		for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
    			for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
    				if (game.canBuildHere(new TilePosition(i,j), buildingType, builder, false)) {
    					// units that are blocking the tile
    					boolean unitsInWay = false;
    					for (Unit u : game.getAllUnits()) {
    						if (u.getID() == builder.getID()) continue;
    						if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
    					}
    					if (!unitsInWay) {
    						return new TilePosition(i, j);
    					}
    					// creep for Zerg
    					if (buildingType.requiresCreep()) {
    						boolean creepMissing = false;
    						for (int k=i; k<=i+buildingType.tileWidth(); k++) {
    							for (int l=j; l<=j+buildingType.tileHeight(); l++) {
    								if (!game.hasCreep(k, l)) creepMissing = true;
    								break;
    							}
    						}
    						if (creepMissing) continue;
    					}
    				}
    			}
    		}
    		maxDist += 2;
    	}

    	if (ret == null) game.printf("Unable to find suitable build position for "+buildingType.toString());
    	return ret;
    }


    public static void main(String[] args) {
        new FirstBot().run();
    }
}
