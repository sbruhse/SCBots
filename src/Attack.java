import java.io.StreamTokenizer;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;



public class Attack 
{
	public static List<EnemyUnit> think(List<EnemyUnit> enemyUnits, boolean overthink)
	{
		List<EnemyUnit> vEnemyUnits = enemyUnits;
		for(EnemyUnit u:vEnemyUnits)
		{
			if (overthink || u.getWeight() == -1)
			{
				int weight = 0;
				if (u.getType().canAttack())
					weight = 5;
				if (u.getType().isWorker())
					weight = 1;
				if (u.getType().isBuilding())
					weight = 3;
				if (u.getType() == UnitType.Terran_Medic)
					weight = 4;
				if (u.getType() == UnitType.Protoss_Archon)
					weight = 6;
				if (u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()) <= 900)
					weight = weight + 2;
				if (u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()) <= 650)
					weight = weight + 3;
				if (u.getType().canProduce())
					weight++;
				if ((u.getType() == UnitType.Terran_Bunker && u.unit.isLoaded()) || u.getType() == UnitType.Protoss_Photon_Cannon)
					weight = weight + 3;
				
				u.setWeight(weight);
			}	
		}
		Collections.sort(vEnemyUnits);
		
		return vEnemyUnits;
	}
	
	
	
	
	public static void attack(List<EnemyUnit> enemyUnits, List<SoldierGroup> mySoldiers, TilePosition enemyLocation, Map<TilePosition, Boolean> startLocations)
	{		
		TilePosition vPosition = enemyLocation;
		
		for( Unit vUnit : FirstBot.getSelf().getUnits())
		{
			if ( vUnit.getPlayer().getID() != FirstBot.getSelf().getID())
			{
				vPosition = vUnit.getTilePosition();
				break;
			}
		}
		if(vPosition == TilePosition.Invalid)
		{
			for(BaseLocation vLocation : BWTA.getStartLocations())
			{
				if(!FirstBot.getGame().isExplored(vLocation.getTilePosition()))
				{
					vPosition = vLocation.getTilePosition();
					break;
				}
			}
		}
		if(vPosition == TilePosition.Invalid)
		{
			for(BaseLocation vLocation : BWTA.getBaseLocations())
			{
				if(!FirstBot.getGame().isExplored(vLocation.getTilePosition()))
				{
					vPosition = vLocation.getTilePosition();
					break;
				}
			}
		}
	

		for(SoldierGroup soldierGroup : mySoldiers)
		{
			Collections.reverse(enemyUnits);
			for(EnemyUnit u:enemyUnits)
			{
				if (!soldierGroup.isFull())
					break;
				soldierGroup.setGrouping(false);
				break;
			}
			for(EnemyUnit u:enemyUnits)
			{
				if (soldierGroup.getGrouping())
					break;
				soldierGroup.attack(u);
				break;
			}
		}
	}
	
	public static TilePosition scout(Unit scout, Map<TilePosition, Boolean> startLocations)
	{
		System.out.println("Ich scoute!");
		TilePosition target = null;
		for(TilePosition s:FirstBot.getGame().getStartLocations())
		{
			if (!startLocations.get(s) && s != FirstBot.getSelf().getStartLocation())
			{
				target = s;
				break;
			}
		}
		
		if(scout.getDistance(target.toPosition()) <=  20)
		{
			startLocations.put(target, true);
		}
		scout.move(target.toPosition());
		
		return target;
	}

}
