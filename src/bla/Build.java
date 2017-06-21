package bla;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class Build  
{
	public static void build(UnitType unittype, int workerCount)
	{
		int maxDistanceToBase = 800;
		for (Unit myUnit : FirstBot.getSelf().getUnits()) {
			if (myUnit.getType() == UnitType.Terran_SCV && (workerCount >= FirstBot.maxWorker || myUnit.isIdle()) && myUnit.getDistance(FirstBot.getSelf().getStartLocation().toPosition()) < maxDistanceToBase) 
			{
				//bla.TilePosition buildTile = getBuildTile(myUnit, unittype, bla.FirstBot.getSelf().getStartLocation());
				TilePosition buildTile = getBuildTile(myUnit, unittype, myUnit.getTilePosition());
				if (buildTile != null) 
				{
					myUnit.build(unittype, buildTile);
					break;
				}
			}
		}
	}
	
    public static TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
    	TilePosition ret = null;
    	int maxDist = 2;
    	int stopDist = 40;

    	// Refinery, Assimilator, Extractor
    	if (buildingType.isRefinery()) {
    		for (Unit n : FirstBot.getGame().neutral().getUnits()) {
    			if ((n.getType() == UnitType.Resource_Vespene_Geyser) &&
    					( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
    					( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
    					) return n.getTilePosition();
    		}
    	}

    	while ((maxDist < stopDist) && (ret == null)) {
    		for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
    			for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
    				if (FirstBot.getGame().canBuildHere(new TilePosition(i,j), buildingType, builder, false)) {
    					// units that are blocking the tile
    					boolean unitsInWay = false;
    					for (Unit u : FirstBot.getGame().getAllUnits()) {
    						if (u.getID() == builder.getID()) continue;
    						if ((Math.abs(u.getTilePosition().getX()-i) < 3) && (Math.abs(u.getTilePosition().getY()-j) < 3)) unitsInWay = true;
    					}
    					if (!unitsInWay) {
    						return new TilePosition(i, j);
    					}
    					
    				}
    			}
    		}
    		maxDist += 2;
    	}

    	if (ret == null) FirstBot.getGame().printf("Unable to find suitable build position for "+buildingType.toString());
    	return ret;
    }
    
    
} 
