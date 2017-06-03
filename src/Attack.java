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
				if (u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()) <= 900)
					weight = weight + 2;
				if (u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()) <= 650)
					weight = weight + 3;
				if (u.getType().canProduce())
					weight++;
				if (u.getType() == UnitType.Terran_Bunker || u.getType() == UnitType.Protoss_Photon_Cannon)
					weight = weight + 3;
				
				u.setWeight(weight);
//				System.out.println("Unit bewertet: " + u.getType() + " Weight: " + weight + "/Distanz: " + u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()));
			}	
		}
		
//		Collections.reverse(vEnemyUnits);
		Collections.sort(vEnemyUnits);
		
		return vEnemyUnits;
	}
	
	
	
	
	public static void attack(List<EnemyUnit> enemyUnits, List<MySoldier> mySoldiers, TilePosition enemyLocation, Map<TilePosition, Boolean> startLocations)
	{
//		System.out.println("Angriff! " + FirstBot.getGame().getFrameCount());
		
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
	

		for(MySoldier soldier : mySoldiers)
		{
			
			if(enemyUnits.isEmpty() /*&& vPosition == TilePosition.Invalid*/)
			{
				
				
//				System.out.println("Warten auf: Erster Angriff!");
				
				if (!startLocations.containsKey(mySoldiers.get(0).myUnit.getOrderTargetPosition().toTilePosition()))
				{
					Attack.scout(mySoldiers.get(0).myUnit, startLocations);
				}
				
				System.out.println("Erster Angriff!");
//				java.awt.Toolkit.getDefaultToolkit().beep();
//				soldier.myUnit.attack(vPosition.toPosition());
			}
			else
			{
//				System.out.println("Liste abarbeiten");
				Collections.reverse(enemyUnits);
				int unitsInBase = 0;
				for (MySoldier s:mySoldiers)
				{
					if(s.myUnit.getDistance(FirstBot.getSelf().getStartLocation().toPosition()) <= 1000)
					{
						unitsInBase++;
					}
				}
				for(EnemyUnit u:enemyUnits)
				{
					if (unitsInBase <= 14 || u.getWeight() >= 8)
						break;
//					if(u.getType().isBuilding() || u.unit.isVisible())
//					{
						soldier.attack(u);
						break;
//					}
						
				}
				
//				System.out.println(enemyUnits);
//				for(EnemyUnit enemy : enemyUnits)
//				{
//					soldier.attack(enemy);
//				}
			}
		}
		
		/*for(Unit vUnit : FirstBot.getSelf().getUnits())
		{
		
			if(vUnit.isIdle() && !vUnit.getType().isWorker() && vUnit.canAttack())
			{
				if(enemyBuildings.isEmpty() && vPosition != Position.Invalid)
				{
					vUnit.attack(vPosition);
				}
				else
				{
					vUnit.attack(enemyBuildings.get(0));
				}
			}
			else if (vUnit.getType() == UnitType.Terran_Medic)
			{
				if(enemyBuildings.isEmpty() && vPosition != Position.Invalid)
				{
					vUnit.move(vPosition);
				}
				else
				{
					vUnit.move(vPosition);
				}
			}
		}*/
	}
	
	public static TilePosition scout(Unit scout, Map<TilePosition, Boolean> startLocations)
	{
		System.out.println("Ich scoute!");
		TilePosition target = null;
		for(TilePosition s:FirstBot.getGame().getStartLocations())
		{
			if (!startLocations.get(s))
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
