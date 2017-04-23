
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class Build implements Runnable 
{
	UnitType building;
	Unit bobTheBuilder = null;
	
	public Build(UnitType building)
	{
		this.building = building;
		System.out.println("Starte Bau von " + building);
	}
	
	public void run() 	
	{
		System.out.println("Bin im run!");
        for (Unit vCurrentUnit : FirstBot.getSelf().getUnits()) 
        { 
        	if (vCurrentUnit.getType().isWorker() && !vCurrentUnit.isConstructing() && !vCurrentUnit.isCarryingGas() && !vCurrentUnit.isCarryingMinerals()) 
        	{
        		bobTheBuilder = vCurrentUnit;
        		break;
        	}
        }
        System.out.println("Bauarbeiter ausgewählt: " + bobTheBuilder);
        
        
		if(bobTheBuilder != null && building.gasPrice() <= FirstBot.getSelf().gas() && building.mineralPrice() <= FirstBot.getSelf().minerals())
		{
			TilePosition buildingPos = null;
			
			buildingPos = getBuildTile(bobTheBuilder, building, bobTheBuilder.getTilePosition());
			if (bobTheBuilder.canBuild(building, buildingPos))
			{
				bobTheBuilder.build(building,buildingPos);
			}
			else
			{
				System.out.println("Kann dort nicht bauen!");
			}
			
			
		}
	}
	
	public TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
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
						for (int n = 0; n<=100;n++)
						{
							for (Unit u : FirstBot.getGame().getAllUnits()) {
								if (u.getID() == builder.getID()) continue;
								if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
							}
							if (!unitsInWay) {
								return new TilePosition(i, j);
							}
						}
						
						if (!unitsInWay) {
							return new TilePosition(i, j);
						}
						// creep for Zerg
						if (buildingType.requiresCreep()) {
							boolean creepMissing = false;
							for (int k=i; k<=i+buildingType.tileWidth(); k++) {
								for (int l=j; l<=j+buildingType.tileHeight(); l++) {
									if (!FirstBot.getGame().hasCreep(k, l)) creepMissing = true;
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

		if (ret == null) FirstBot.getGame().printf("Unable to find suitable build position for "+buildingType.toString());
		return ret;
	}
} 
