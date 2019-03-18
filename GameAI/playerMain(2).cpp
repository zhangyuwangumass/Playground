//选手1写入ai
#include "teamstyle18-my-1.h"
#include <cmath>


#define addPosition(a, dx, dy) Position(a.x + dx, a.y + dy)
 
struct dist
{
	int x;
	int y;
	int sgnx;
	int sgny;
};

int sgn(int x)
{
	if(x > 0)
		return 1;
	else if(x < 0)
		return -1;
	else
		return 0;
}

int find(int unit_id)
{
	Unit* each_unit = getUnit();
	for(int i = 0; i<getUnitSize(); i++)
	{
		if(each_unit[i].unit_id == unit_id)
		{
			return i;
		}
	}
}

dist calculate(Position a, Position b)
{
	dist dis;
	dis.sgnx = sgn(a.x - b.x);
	dis.sgny = sgn(a.y - b.y);
	dis.x = abs(a.x - b.x);
	dis.y = abs(a.y - b.y);
	return dis;

}

bool ifInBuilding(Position a)
{
	Unit* each_unit = getUnit();
	for(int i = 0; i<getUnitSize(); i++)
	{
		if((each_unit[i].unit_type == BUILDING || each_unit[i].unit_type == BASE) && each_unit[i].position.x == a.x && each_unit[i].position.y == a.y)
		{
			return 1;
		}
	}
	return 0;
}

void cicleAttack(int unit_id, int target_id)
{
	Unit* each_unit = getUnit();
	int my = find(unit_id);
	int target = find(target_id);

	dist dis;
	dis = calculate(each_unit[my].position, each_unit[target].position);

	Position way;
	if(dis.sgnx > 0 && dis.sgny <= 0)
		way=Position(-1,-1);
	if(dis.sgnx >= 0 && dis.sgny > 0)
		way=Position(1,-1);
	if(dis.sgnx <= 0 && dis.sgny < 0)
		way=Position(-1,1);
	if(dis.sgnx < 0 && dis.sgny >= 0)
		way=Position(1,1);
	Move(unit_id,addPosition(each_unit[unit_id].position, way.x, way.y));
}

//移动到可以发动占领技能的位置
void moveAndCapture(int unit_id, int target_id)
{
	Unit* each_unit = getUnit();

	int my = find(unit_id);
	int target = find(target_id);

	dist dis;
	dis = calculate(each_unit[my].position, each_unit[target].position);

	if(dis.x + dis.y > each_unit[my].max_speed_now + 1)
	{
		if(dis.x >= each_unit[my].max_speed_now)
		{
			if( !ifInBuilding(Position(each_unit[my].position.x - dis.sgnx*each_unit[my].max_speed_now, each_unit[my].position.y)))
				Move(unit_id, Position(each_unit[my].position.x - dis.sgnx*each_unit[my].max_speed_now, each_unit[my].position.y));
			else
				Move(unit_id, Position(each_unit[my].position.x - dis.sgnx*(each_unit[my].max_speed_now - 1), each_unit[my].position.y - dis.sgny));
		}
		else
		{
			if(!ifInBuilding(Position(each_unit[target].position.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x))))
				Move(unit_id, Position(each_unit[target].position.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x)));
			else
			{
				if(dis.sgnx != 0)
					Move(unit_id, Position(each_unit[target].position.x + dis.sgnx, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x + 1)));
				else
					Move(unit_id, Position(each_unit[target].position.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - 1)));
			}
		}
	}
	else
	{
		switch(dis.sgnx)
		{
		case 0:
			Move(unit_id, Position(each_unit[target].position.x, each_unit[target].position.y + dis.sgny));
			break;
		default:
			Move(unit_id, Position(each_unit[target].position.x + dis.sgnx, each_unit[target].position.y));
			break;
		}
	}
}//moveAndCapture结束

//移动到主基地，并且圆周移动
void moveAndAttackBase(int unit_id, int target_id)
{
	Unit* each_unit = getUnit();

	int my = find(unit_id);
	int target = find(target_id);

	dist dis;
	dis = calculate(each_unit[my].position, each_unit[target].position);

	if(dis.x + dis.y > each_unit[my].shot_range_now)
	{
		if(dis.x >= each_unit[my].max_speed_now)
		{
			if(each_unit[my].unit_type != AIRCRAFT)
			{
				if( !ifInBuilding(Position(each_unit[my].position.x - dis.sgnx*each_unit[my].max_speed_now, each_unit[my].position.y)))
					Move(unit_id, Position(each_unit[my].position.x - dis.sgnx*each_unit[my].max_speed_now, each_unit[my].position.y));
				else
					Move(unit_id, Position(each_unit[my].position.x - dis.sgnx*(each_unit[my].max_speed_now - 1), each_unit[my].position.y - dis.sgny));
			}
			else
			{
				Move(unit_id, Position(each_unit[my].position.x - dis.sgnx*each_unit[my].max_speed_now, each_unit[my].position.y));
			}
		}
		else
		{
			if(each_unit[my].unit_type != AIRCRAFT)
			{
				if(!ifInBuilding(Position(each_unit[target].position.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x))))
					Move(unit_id, Position(each_unit[target].position.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x)));
				else
				{
					if(dis.sgnx != 0)
						Move(unit_id, Position(each_unit[target].position.x + dis.sgnx, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x + 1)));
					else
						Move(unit_id, Position(each_unit[target].position.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - 1)));
				}
			}
			else
			{
				Move(unit_id, Position(each_unit[target].position.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x)));
			}
		}
	}
	else
	{
		cicleAttack(unit_id, target_id);
	}

}//moveAndAttackBase函数结束

//移动到某一确定位置(要求移动到的位置符合不在建筑物的要求)
void moveToPosition(int unit_id, Position a)
{
	Unit* each_unit = getUnit();
	int my = find(unit_id);

	dist dis;
	dis = calculate(each_unit[my].position, a);

	if(dis.x + dis.y > each_unit[my].max_speed_now)
	{
		if(dis.x >= each_unit[my].max_speed_now)
		{
			if( !ifInBuilding(Position(each_unit[my].position.x - dis.sgnx*each_unit[my].max_speed_now, each_unit[my].position.y)))
				Move(unit_id, Position(each_unit[my].position.x - dis.sgnx*each_unit[my].max_speed_now, each_unit[my].position.y));
			else
				Move(unit_id, Position(each_unit[my].position.x - dis.sgnx*(each_unit[my].max_speed_now - 1), each_unit[my].position.y - dis.sgny));
		}
		else
		{
			if(!ifInBuilding(Position(a.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x))))
				Move(unit_id, Position(a.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x)));
			else
			{
				if(dis.sgnx != 0)
					Move(unit_id, Position(a.x + dis.sgnx, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - dis.x + 1)));
				else
					Move(unit_id, Position(a.x, each_unit[my].position.y - dis.sgny*(each_unit[my].max_speed_now - 1)));
			}
		}
	}
	else
	{
		Move(unit_id, Position(a.x, a.y));
	}
}

void player_main(void)
{
	Unit* each_unit = getUnit();




}

