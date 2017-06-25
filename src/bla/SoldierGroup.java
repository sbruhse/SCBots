package bla;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;

public class SoldierGroup 
{
	private List<MySoldier> soldiers = new ArrayList<>();
	public static int maxGroupSize = 7;
	EnemyUnit target = null;
	private boolean grouping = true;
	
	public SoldierGroup()
	{}
	
	public SoldierGroup(MySoldier soldier)
	{
		addSoldier(soldier);
	}
	
	public boolean isInBase()
	{
		for(MySoldier s : soldiers)
		{
			if (!(s.myUnit.getDistance(FirstBot.getSelf().getStartLocation().toPosition())<=1000))
				return false;
		}
		return true;
	}
	
	public boolean isFull()
	{
		if(getSoldierCount() >= maxGroupSize)
			return true;
		else
			return false;
	}
	
	public boolean isEmpty()
	{
		if(getSoldierCount() <= 0)
			return true;
		else
			return false;
	}
	
	public void updateSoldierStatus()
	{
		for(MySoldier s:new ArrayList<>(soldiers))
		{
			if(!s.myUnit.exists())
        	{
        		soldiers.remove(s);
        	}
		}
	}
	
	public int getSoldierCount()
	{
		return soldiers.size();
	}
	
	public boolean addSoldier(MySoldier soldier)
	{
		if (!isFull() && soldier.group == null && soldier.myUnit.isCompleted())
		{
			soldier.group = this;
			soldiers.add(soldier);
			return true;
		}	
		
		return false;
	}
	
	public void attack(EnemyUnit enemy)
	{
		if (!getGrouping())
		{
			if (target == null ||
					enemy.getWeight() > target.getWeight() || target.unit.isVisible() && !target.unit.exists())
			{
				target = enemy;
			}
			
			if (target != null)
			{
				for(MySoldier s : soldiers)
				{
					Position positionToAttack = target.getPosition();
					if(s.myUnit.getOrderTargetPosition().getDistance(positionToAttack) > s.myUnit.getType().sightRange())
					{
						System.out.println("Attacke!");
						if (s.myUnit.canAttack()) s.myUnit.attack(positionToAttack);
						else s.myUnit.move(positionToAttack);
					}
				}
				
				
			}
		}
		
		
	}
	
	public void setGrouping(boolean status)
	{
		grouping = status;
	}
	
	public boolean getGrouping()
	{
		return grouping;
	}
}
