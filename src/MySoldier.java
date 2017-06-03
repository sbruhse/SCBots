import bwapi.Position;
import bwapi.Unit;

public class MySoldier 
{
	EnemyUnit target = null;
	Unit myUnit;
	
	public MySoldier(Unit unit)
	{
		myUnit = unit;
	}
	
	public void attack(EnemyUnit enemy)
	{
//		System.out.println("Attacke beginn!");
		if (target == null ||
				enemy.getWeight() > target.getWeight() || target.unit.isVisible() && !target.unit.exists())
		{
			target = enemy;
		}
//		System.out.println("Target ist nun: " + target);
		if (target != null)
		{
			Position positionToAttack = target.getPosition();
//			myUnit.attack(positionToAttack);
//			if((myUnit.getOrderTargetPosition().getX() != positionToAttack.getX()) && (myUnit.getOrderTargetPosition().getY() != positionToAttack.getY()))
			if(myUnit.getOrderTargetPosition().getDistance(positionToAttack) > 20)
			{
				System.out.println("Attacke!");
				if (myUnit.canAttack()) myUnit.attack(positionToAttack);
				else myUnit.move(positionToAttack);
			}
			
		}
		
	}
	
}
