import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;



public class Attack 
{
	
	public static void attack(List<Unit> enemyBuildings)
	{
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
		int vCounter = 0;
		for(Unit vUnit : FirstBot.getSelf().getUnits())
		{
			if(vUnit.isIdle() && !vUnit.getType().isWorker() && vUnit.canAttack() && FirstBot.getSelf().getStartLocation().getDistance(vUnit.getTilePosition()) <= 10)
			{
				++vCounter;
			}
		}
		
		if (vCounter < 10)
		{
			return;
		}
		
		for(Unit vUnit : FirstBot.getSelf().getUnits())
		{
		
			if(vUnit.isIdle() && !vUnit.getType().isWorker() && vUnit.canAttack())
			{
				if(enemyBuildings.isEmpty() && vPosition != Position.Invalid)
				{
					vUnit.attack(vPosition);
					return;
				}
				vUnit.attack(enemyBuildings.get(0));
				
			}
		}
	}
}
