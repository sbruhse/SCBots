import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;



public class Attack 
{
	
	
	public static void attack(List<EnemyUnit> enemyBuildings)
	{
		Position vPosition = Position.Invalid;
		List<Unit> attackTroops = new ArrayList<>();
		
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
		int vCounter = 0;
	
		for(Unit vUnit : FirstBot.getSelf().getUnits())
		{
			if(!vUnit.getType().isWorker() && vUnit.canAttack())
			{
				attackTroops.add(vUnit);
				if (vUnit.isIdle() && FirstBot.getSelf().getStartLocation().getDistance(vUnit.getTilePosition()) <= 10) 
					++vCounter;	
			}
		}
		
		for(Unit vUnit : attackTroops)
		{
			if(enemyBuildings.isEmpty() && vPosition != Position.Invalid)
			{
				if (vCounter < 10)
				{
					return;
				}
				vUnit.attack(vPosition);
			}
			else
			{
				
				if (!(vUnit.getTarget() != null && vUnit.getTarget().getDistance(enemyBuildings.get(0).getPosition()) <= 10))
				{
					vUnit.attack(enemyBuildings.get(0).getPosition());
				}
				
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
