
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class Build  
{
	public static void build(UnitType unittype, int workerCount)
	{
		for (Unit myUnit : FirstBot.getSelf().getUnits()) {
			if (myUnit.getType() == UnitType.Terran_SCV && (workerCount >= FirstBot.maxWorker || myUnit.isIdle())) 
			{
				TilePosition buildTile = getBuildTile(myUnit, unittype, FirstBot.getSelf().getStartLocation());
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
    	int maxDist = 3;
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
    						if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
    					}
//    					if (!unitsInWay) {
    						return new TilePosition(i, j);
//    					}
    					// creep for Zerg
//    					if (buildingType.requiresCreep()) {
//    						boolean creepMissing = false;
//    						for (int k=i; k<=i+buildingType.tileWidth(); k++) {
//    							for (int l=j; l<=j+buildingType.tileHeight(); l++) {
//    								if (!game.hasCreep(k, l)) creepMissing = true;
//    								break;
//    							}
//    						}
//    						if (creepMissing) continue;
//    					}
    				}
    			}
    		}
    		maxDist += 2;
    	}

    	if (ret == null) FirstBot.getGame().printf("Unable to find suitable build position for "+buildingType.toString());
    	return ret;
    }
    
    
} 
