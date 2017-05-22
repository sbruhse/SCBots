import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;



public class TestBot1 extends DefaultBWListener {

    private Mirror mirror = new Mirror();
    Unit buildSupply;

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
    
    private Boolean aggro = false;
    private TilePosition enemyBase;
    private Boolean startOK = false;
    
    private Unit emergencySupply = null;

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        enemyBase = game.enemy().getStartLocation();

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        int i = 0;
        for(BaseLocation baseLocation : BWTA.getBaseLocations()){
        	System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
        	for(Position position : baseLocation.getRegion().getPolygon().getPoints()){
        		System.out.print(position + ", ");
        	}
        	System.out.println();
        }
        game.setLocalSpeed(10);

    }
    
    

    @Override
    public void onFrame() {
    	
    	//Count units
         int engCount = 0;
         int defCount = 0;
         int barCount = 0;
         int workerCount = 0;
         int cmdCount=0;
         int milCount=0;
         
        
        for (Unit myUnit : self.getUnits()) 
        {
        	if (myUnit.getType().isWorker())
        	{
        		workerCount++;
        		continue;
        	}
        	if (myUnit.getType() == UnitType.Terran_Barracks)
        	{
        		barCount++;
        		continue;
        	}
        	if (myUnit.getType() == UnitType.Terran_Engineering_Bay)
        	{
        		engCount++;
        		continue;
        	}
        	if (myUnit.getType() == UnitType.Terran_Missile_Turret)
        	{
        		defCount++;
        		continue;
        	}
        	if (myUnit.getType() == UnitType.Terran_Command_Center)
        	{
        		cmdCount++;
        		continue;
        	}
        	if(myUnit.getType() == UnitType.Terran_Marine)
        	{
        		milCount++;
        		continue;
        	}

        }
        
        if(milCount>9)attack();
        if(!startOK&&workerCount > 20) startOK = true;

        //iterate through my units
        for (Unit myUnit : self.getUnits()) 
        {
        	if(cmdCount==0&&myUnit.getType().isWorker())
        	{
        		buildStuff(myUnit, UnitType.Terran_Command_Center);
        		cmdCount++;
        		//continue;
        	}
//        	if((self.supplyTotal() <= self.supplyUsed()) && (self.minerals() >= 100) && myUnit.getType().isWorker())
//        	{
//        		buildStuff(myUnit, UnitType.Terran_Supply_Depot);
//        		System.out.println("Need supply");
//        		continue;
//        	}
        	
        	
        	if(myUnit.getType().isBuilding())
        	{
        		//if there's enough minerals, train an SCV
                if (myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50 && workerCount < 80) 
                {
                    myUnit.train(UnitType.Terran_SCV);
                }
                else if (myUnit.getType() == UnitType.Terran_Barracks && !myUnit.isBeingConstructed())
                {
                    myUnit.train(UnitType.Terran_Marine);
                }
        	}
        	
            // idling workers
            
            if(myUnit.getType().isWorker()&&myUnit.isIdle())
            {
            	// build supply
            	if((self.supplyTotal() - self.supplyUsed() <= 3) && (self.minerals() >= 100))
            	{
            		buildStuff(myUnit, UnitType.Buildings.Terran_Supply_Depot);
//            		continue;
            	}
            	else if(startOK)
            	{
            		// build barracks
                	if(barCount <5 && self.minerals() >= 150)
                	{
                		buildStuff(myUnit, UnitType.Buildings.Terran_Barracks);
                		barCount++;
//                		continue;
                	}
                	// build engineering bay
                	else if(engCount < 1 && self.minerals() >= 125)
                	{
                		buildStuff(myUnit, UnitType.Buildings.Terran_Engineering_Bay);
                		engCount++;
//                		continue;
                	}
                	// build missile launchers
                	else if(engCount > 0 && defCount < 5 && self.minerals() >= 75)
                	{
                		buildStuff(myUnit, UnitType.Buildings.Terran_Missile_Turret);
                		defCount++;
//                		continue;
                	}
            		//default: go mine
            	}
            	else
	        	{
	        		Unit closestMineral = null;
	
	                //find the closest mineral
	                for (Unit neutralUnit : game.neutral().getUnits()) 
	                {
	                    if (neutralUnit.getType().isMineralField())
	                    {
	                        if (closestMineral == null || myUnit.getDistance(neutralUnit) < myUnit.getDistance(closestMineral)) 
	                        {
	                            closestMineral = neutralUnit;
	                        }
	                    }
	                }
	                if (closestMineral != null) {
	                  myUnit.gather(closestMineral, false);
	              }
	        	}
            }

        }

    }
    public void buildStuff(Unit workerDude, UnitType buildThisBuilding)
    {
    	TilePosition buildTile = getBuildTile(workerDude, buildThisBuilding, workerDude.getTilePosition());
		if (buildTile != null) 
		{            			
			workerDude.build(buildThisBuilding,getBuildTile(workerDude, buildThisBuilding, workerDude.getTilePosition()));
   	    }
    }
    
    public static void main(String[] args) {
        new TestBot1().run();
    }
    
    public void attack()
    {
    	Position vPosition = Position.Invalid;
    	for(Unit vUnit : getGame().getAllUnits())
    	{
    		if(vUnit.getPlayer().getID() != self.getID())
    		{
    			vPosition = vUnit.getPosition();
    			break;
    		}
    	}	
    	if(vPosition==Position.Invalid)
    	{
    		for(BaseLocation vLocation:BWTA.getStartLocations())
    		{
    			if(!game.isExplored(vLocation.getTilePosition()))
    			{
    				vPosition = vLocation.getPosition();
    				break;
    			}
    		}
    	}
//    	int vCounter = 0;
//    	for(Unit vUnit:self.getUnits())
//    	{
//    		if(vUnit.isIdle() &&
//    				!vUnit.getType().isWorker()&&
//    				vUnit.canAttack())
//    		{
//    			vCounter++;
//    		}
//    			
//    	}
//    	if(vCounter<10)return;
    	for(Unit vUnit:self.getUnits())
    	{
    		if(vUnit.isIdle()&&
    				!vUnit.getType().isWorker()&&
    				vUnit.canAttack())
    		{
    			if(vPosition != Position.Invalid) vUnit.attack(vPosition);
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
 }