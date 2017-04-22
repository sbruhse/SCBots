import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bwapi.*;
import bwta.BWTA;
import bwta.Region;

public class FirstBot extends DefaultBWListener {

	Unit buildingSupply = null;
	boolean buildingRefinery = false;
	int workerOnRefinery = 0;
    Unit refinery = null;
    List<Building> buildorder = new ArrayList<Building>();
    boolean buildingBarrack = false;
	
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
        
        if (unit.getType().isBuilding() && unit.getBuildType() == getSelf().getRace().getRefinery())
    	{
    		System.out.println("Neue Refinery!");
    		refinery = unit;
    	}
        if (unit.getType().isBuilding() && unit.getBuildType() == getSelf().getRace().getSupplyProvider())
    	{
    		System.out.println("Neuer Supply!");
    		buildingSupply = unit;
    	}
        
    }
    
    @Override
    public void onUnitMorph(Unit unit) {
    	if (unit.getType().isBuilding() && unit.getBuildType() == getSelf().getRace().getRefinery())
    	{
    		System.out.println("Neue Refinery!");
    		refinery = unit;
    	}
        if (unit.getType().isBuilding() && unit.getBuildType() == getSelf().getRace().getSupplyProvider())
    	{
    		System.out.println("Neuer Supply!");
    		buildingSupply = unit;
    	}
    }
    
    @Override
    public void onUnitComplete(Unit unit) {
    	if (unit.getType().isBuilding() && unit.getBuildType() == UnitType.Buildings.Terran_Barracks)
        {
        	buildingBarrack = false;
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
        
        if(self.getRace() == Race.Terran)
        {
//	        buildorder.add(new Building(UnitType.Buildings.Terran_Barracks));
//	        buildorder.add(new Building(UnitType.Buildings.Terran_Barracks));
//	        buildorder.add(new Building(UnitType.Buildings.Terran_Barracks));
//	        buildorder.add(new Building(UnitType.Buildings.Terran_Barracks));
//	        buildorder.add(new Building(UnitType.Buildings.Terran_Barracks));
//	    	buildorder.add(new Building(UnitType.Buildings.Terran_Factory));
//	    	buildorder.add(new Building(UnitType.Buildings.Terran_Engineering_Bay));
	    	
	    	System.out.println("Buildorder erstellt!");
        }
        
    }
    
    @Override
    public void onEnd(boolean b) {
    	System.exit(0);
    }
    

    @Override
    public void onFrame() {
    	
    	try {
        	
        	getGame().drawTextScreen(10, 10, "" + getSelf().getName() + " - " + getSelf().getRace() + " vs. " + getEnemyRace() + " " + getEnemyName() + " Frame " + getGame().getFrameCount() +"(" + getGame().elapsedTime() + " s)");
                        
	        int vWorkerCount = 0;
	        //iterate through my units, count workers
	        for (Unit vCurrentUnit : getSelf().getUnits()) {
	        	if (vCurrentUnit.getType().isWorker()) {
	        		++vWorkerCount;
	        	}
	        	
	        }
	        
	        //iterate through my units,
	        for (Unit vCurrentUnit : getSelf().getUnits()) {
	        	
	        	if ( vCurrentUnit.getType().isBuilding() && buildingSupply != null && buildingSupply.isCompleted() )
	        	{
	        		buildingSupply = null;
	        	}
	        	
	        	
	        	if ( vCurrentUnit.getType() == UnitType.Buildings.Terran_Barracks &&
	        		 vCurrentUnit.isCompleted() &&	
	               	 !vCurrentUnit.isTraining() &&
	               	 getSelf().minerals() >= 50 
	               	  
        			) 
	        	{
	                   vCurrentUnit.train(UnitType.Terran_Marine);
	            }
	
	            //if there's enough minerals, train a Worker if there are less than 50
	        	if ( vWorkerCount < 50 && 
	               	 vCurrentUnit.getType() == getSelf().getRace().getCenter() &&
	               	 !vCurrentUnit.isTraining() &&
	               	 getSelf().minerals() >= 50 ) 
	        	{
	                   vCurrentUnit.train(getSelf().getRace().getWorker());
	            }
	        	
	        	// if a building is training units and is not selected add another unit to train to its queue
	        	if (  !vCurrentUnit.isSelected() &&
	        		  vCurrentUnit.getType().isBuilding() &&
	        		  vCurrentUnit.canTrain() &&
	        		  vCurrentUnit.getType() != getSelf().getRace().getCenter() &&
	                  vCurrentUnit.isTraining() &&
	                  vCurrentUnit.getTrainingQueue().size() == 1 ) {
	                    vCurrentUnit.train(vCurrentUnit.getTrainingQueue().get(0));
	                }
	        	
	        	if (getGame().elapsedTime() > 160)
	        	{
	        		for(Building b : buildorder)
	        		{
	        			if(b.getBuilded() == false && b.getGasPrice() <= self.gas() && b.getMineralPrice() <= self.minerals())
	        			{
	        				b.builded = !vCurrentUnit.build(b.getType(), getBuildTile(vCurrentUnit, b.getType(), vCurrentUnit.getTilePosition()));
	        			}
	        		}
	        	}
	        	
	        	if (getSelf().minerals() >= UnitType.Buildings.Terran_Barracks.mineralPrice() && buildingBarrack == false)
	        	{
	        		vCurrentUnit.build(UnitType.Buildings.Terran_Barracks, getBuildTile(vCurrentUnit, UnitType.Buildings.Terran_Barracks, vCurrentUnit.getTilePosition()));
	        		buildingBarrack = true;
	        	}
	        	
	        	
	        	
	            //if it's a worker and it's idle, send it to the closest mineral patch
	            if (vCurrentUnit.getType().isWorker() && vCurrentUnit.isIdle() && !vCurrentUnit.isSelected() && vCurrentUnit.isCompleted())
	            {
	                Unit closestMineral = null;
	                
	                
	                
	                
					//Wenn kein Supply vorhanden, baue.
	                if ((getSelf().supplyTotal() - getSelf().supplyUsed()) <= 2 && buildingSupply == null)
	                {
	                	vCurrentUnit.build(getSelf().getRace().getSupplyProvider(),getBuildTile(vCurrentUnit, getSelf().getRace().getSupplyProvider(), vCurrentUnit.getTilePosition()));
	                }
	                else if(getSelf().gas() == 0 && getGame().elapsedTime() >= 250 && getSelf().minerals() >= getSelf().getRace().getRefinery().mineralPrice() && refinery == null && buildingRefinery == false)
	                {
	                	buildingRefinery = vCurrentUnit.build(getSelf().getRace().getRefinery(),getBuildTile(vCurrentUnit, getSelf().getRace().getRefinery(), vCurrentUnit.getTilePosition()));
	                }	
	                else
	                {
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
		
		               
		                if (refinery != null && refinery.isCompleted() && workerOnRefinery < 3)
		                {
		                	
		                	vCurrentUnit.gather(refinery, false);
		                	++workerOnRefinery;
		                	System.out.println("Another unit works on the refinery! Total: " + workerOnRefinery);
		                } 
		                else if (closestMineral != null)
		                {
		                    vCurrentUnit.gather(closestMineral, false);
		                }
	                }
  
	            }
	        }
        	
//        	drawBWAPIandBWTARegions();
	    	 
        } catch( Exception vException ) {
       		vException.printStackTrace();
       	}
    }

    Race mEnemyRace = Race.None;
	private Race getEnemyRace() {
		if( mEnemyRace != Race.Protoss && mEnemyRace != Race.Zerg && mEnemyRace != Race.Terran ){
			mEnemyRace = findEnemyRace();
		}
		return mEnemyRace;
	}

	String mEnemyName = "Nobody";
	private String getEnemyName() {
		for( Player vPlayer : getGame().getPlayers() ){
			if( vPlayer.isEnemy(self) && !vPlayer.isNeutral() ){
				mEnemyName = vPlayer.getName();
			}
		}
		return mEnemyName;
	}

	private Race findEnemyRace() {
		for( Player vPlayer : getGame().getPlayers() ){
			if( !vPlayer.isEnemy(self) || vPlayer.isNeutral() ){
				continue;
			}
			Race vEnemyRace = vPlayer.getRace();

			if( vEnemyRace != Race.Protoss && vEnemyRace != Race.Zerg && vEnemyRace != Race.Terran ){
				for( Unit vUnit : getGame().getAllUnits() ){
					if( vUnit.getPlayer().getID() == vPlayer.getID() ){
						return vUnit.getType().getRace();
					}
				}
			} else {
				return vEnemyRace;
			}
		}
		
		return Race.Unknown;
	}

	// Draws the BWAPI and BWTA Regions to show their difference
	// Needs Extreme Resources 
	private void drawBWAPIandBWTARegions() {
		
		getGame().drawTextScreen(10, 20, "" + getGame().mapWidth() + "x" + getGame().mapHeight());
		 
		// BWAPI
		Color[] vColors = { Color.Black, Color.Blue, Color.Brown, Color.Cyan, Color.Green, Color.Grey, Color.Orange, Color.Purple, Color.Red, Color.Teal, Color.White, Color.Yellow };
		int currentColor = 0;
		Map<Integer, Color> vRegionColor = new HashMap<>();
		for( int vX = 0; vX < getGame().mapWidth(); ++vX ){
			for( int vY = 0; vY < getGame().mapHeight(); ++vY ){
				int vRegionID = getGame().getRegionAt(vX*32, vY*32).getID();
				if( vRegionColor.containsKey(vRegionID)){
					getGame().drawBoxMap(vX*32, vY*32, vX*32+32, vY*32+32, vRegionColor.get(vRegionID));
				} else {
					vRegionColor.put(vRegionID, vColors[++currentColor%vColors.length]);
					getGame().drawBoxMap(vX*32, vY*32, vX*32+32, vY*32+32, vRegionColor.get(vRegionID));
				}
			}
		}
		
		// BWTA
		for( Region vRegion : BWTA.getRegions() ){
			Position vBefore = vRegion.getPolygon().getPoints().get(vRegion.getPolygon().getPoints().size()-1);
			for( Position vPoint : vRegion.getPolygon().getPoints() ){
				getGame().drawLineMap(vBefore, vPoint, Color.Yellow);
				vBefore = vPoint;
			}
		}
		
	}
	
	// Returns a suitable TilePosition to build a given building type near
	// specified TilePosition aroundTile, or null if not found. (builder parameter is our worker)
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
	
	class Building
	{
		private UnitType type;
		public Boolean builded;
		
		public Building(UnitType type)
		{
			setType(type);
			setBuilded(false);
		}
		
		public UnitType getType() 
		{
			return type;
		}
		private void setType(UnitType type) 
		{
			this.type = type;
		}
		public Boolean getBuilded() 
		{
			return builded;
		}
		public void setBuilded(Boolean builded) 
		{
			this.builded = builded;
		}
		
		public int getMineralPrice()
		{
			return getType().mineralPrice();
		}
		
		public int getGasPrice(){
			return getType().gasPrice();
		}
		
		public UnitType build(){
			setBuilded(false);
			return getType();
		}
		
	}
	
	class TaskController
	{
		public void doBasicTasks()
		{
			
		}
	}


    public static void main(String[] args) {
        new FirstExamples().run();
    }
}
