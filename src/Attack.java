import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import bwapi.Position;
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
				if (u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()) <= 900)
					weight = weight + 2;
				if (u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()) <= 650)
					weight = weight + 3;
				if (u.getType().canProduce())
					weight++;
				
				u.setWeight(weight);
//				System.out.println("Unit bewertet: " + u.getType() + " Weight: " + weight + "/Distanz: " + u.getPosition().getDistance(FirstBot.getSelf().getStartLocation().getPoint().toPosition()));
			}	
		}
		
//		Collections.reverse(vEnemyUnits);
		Collections.sort(vEnemyUnits);
		
		return vEnemyUnits;
	}
	
	
	
	
	public static void attack(List<EnemyUnit> enemyUnits, List<MySoldier> mySoldiers)
	{
//		System.out.println("Angriff! " + FirstBot.getGame().getFrameCount());
		
		Position vPosition = Position.Invalid;
		
		for( Unit vUnit : FirstBot.getSelf().getUnits())
		{
			if ( vUnit.getPlayer().getID() != FirstBot.getSelf().getID())
			{
				vPosition = vUnit.getPosition();
				break;
			}
		}
		if(vPosition == Position.Invalid)
		{
			for(BaseLocation vLocation : BWTA.getStartLocations())
			{
				if(!FirstBot.getGame().isExplored(vLocation.getTilePosition()))
				{
					vPosition = vLocation.getPosition();
					break;
				}
			}
		}
		if(vPosition == Position.Invalid)
		{
			for(BaseLocation vLocation : BWTA.getBaseLocations())
			{
				if(!FirstBot.getGame().isExplored(vLocation.getTilePosition()))
				{
					vPosition = vLocation.getPosition();
					break;
				}
			}
		}
	
//		System.out.println(mySoldiers);
		//todo: machen
		for(MySoldier soldier : mySoldiers)
		{
			
			if(enemyUnits.isEmpty() && vPosition != Position.Invalid)
			{
//				System.out.println("Warten auf: Erster Angriff!");
				if (mySoldiers.size() < 10)
				{
					return;
				}
				System.out.println("Erster Angriff!");
//				java.awt.Toolkit.getDefaultToolkit().beep();
				soldier.myUnit.attack(vPosition);
			}
			else
			{
//				System.out.println("Liste abarbeiten");
				Collections.reverse(enemyUnits);
				for(EnemyUnit u:enemyUnits)
				{
					if(u.unit.getType().isBuilding() || u.unit.isVisible())
					{
						soldier.attack(u);
						break;
					}
						
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
}
