//选手1写入ai
#include "teamstyle18-my-1.h"
#include <vector>
#include <windows.h>
using namespace std;

inline int getDistance(Position p1,Position p2) { return abs(p1.x-p2.x)+abs(p1.y-p2.y); }
inline int getLength(Position p1) { return abs(p1.x)+abs(p1.y); }
inline Position Add(Position p1,Position p2) { return Position(p1.x+p2.x,p1.y+p2.y); }
inline Position Minus(Position p1,Position p2) { return Position(p1.x-p2.x,p1.y-p2.y); }  //可计算两点距离向量 注意p1p2顺序


#define maxU 350
#define maxMine 200
const int InfantryC=1,VehicleC=4,AircraftC=7,BuildingC=HACK_LAB;

int TeamId,cnts;	//阵营 总单位数
Unit each_unit[maxU];		//所有单位

#define maxX 120
#define maxY 120
inline int getUnit_id(int id) { return each_unit[id].unit_id; }
inline Position getPosition(int id) { return each_unit[id].position; }
inline int PostoValue(Position pos) { return pos.x*maxX+pos.y; }

#define maxC 50 //同时最大指令数
//地图
class Map
{
public:
	int head[maxX*maxY],nxt[maxU];
	int nodes;
	int uid[maxU]; //unit_id

	bool is_building[maxX][maxY];
	Map() { memset(this,0,sizeof(this)); }
	void init()
	{
		while(nodes)
		{
			int pos=PostoValue(getPosition(uid[nodes]));
			head[pos]=0;
			nodes--;
		}
	}
	void insert(int tid)		//注意id并非unit_id
	{
		int pos=each_unit[tid].position.x*maxY+each_unit[tid].position.y;
		nxt[++nodes]=head[pos];
		head[pos]=nodes;
		uid[nodes]=tid;

		if(each_unit[tid].type_name>=BuildingC)
			is_building[each_unit[tid].position.x][each_unit[tid].position.y]=1;
	}
	void find(Position pos,int *id0,int unit_s)		//获取此位置的所有单位的一些信息，unit_s为单位个数（id并非unit_id而是each_unit中的id
	{
		int p=head[pos.x*maxY+pos.y];
		for(unit_s=0;p;p=nxt[p],unit_s++)
			id0[unit_s]=uid[p];
	}
	bool checkBuilding(Position pos)
	{
		return is_building[pos.x][pos.y];
	}
	void Print()
	{
		cout<<"map: "<<nodes<<endl;
		for(int i=1;i<=nodes;i++)
			cout<<"   "<<uid[i]<<"   "<<each_unit[uid[i]].position.x<<' '<<each_unit[uid[i]].position.y<<"     "<<nxt[i]<<endl;
	}
};

Map ground;

//基地
class Base: public Unit
{
public:
	void operator = (const Unit x)
	{
		type_name = x.type_name;
		unit_type=x.unit_type;
		attack_mode=x.attack_mode;			// 攻击模式，例如可对空，可对坦克，可对步兵之类的
		attack_now=x.attack_now;					// 当前攻击
		defense_now=x.defense_now;				// 当前防御
		disable_since =x.disable_since;			// 被瘫痪的时间点，用于判断瘫痪时间
		flag =x.flag;					// 所属阵营
		hacked_point=x.hacked_point;				// 被黑的点数
		healing_rate =x.healing_rate;		// 治疗 / 维修速率	
		health_now=x.health_now;					// 当前生命值		
		is_disable=x.is_disable;		// 是否被瘫痪
		max_health_now=x.max_health_now;				// 当前HP上限
		max_speed_now=x.max_speed_now;				// 当前最大速度
		position=x.position;				// 单位位置
		shot_range_now=x.shot_range_now;				// 当前射程
		skill_last_release_time1=x.skill_last_release_time1;// 上次技能1释放时间
		skill_last_release_time2=x.skill_last_release_time2;// 上次技能2释放时间
		unit_id=x.unit_id;				// 单位id
	}
};

//各种建筑 必要的话可以用更小的每种建筑子类继承
class Building: public Unit
{
public:
	void operator = (const Unit &x);
};

void Building::operator = (const Unit &x)
{

	this->type_name = x.type_name;
	this->unit_type=x.unit_type;
	this->attack_mode=x.attack_mode;			// 攻击模式，例如可对空，可对坦克，可对步兵之类的
	this->attack_now=x.attack_now;					// 当前攻击
	this->defense_now=x.defense_now;				// 当前防御
	this->disable_since =x.disable_since;			// 被瘫痪的时间点，用于判断瘫痪时间
	this->flag =x.flag;					// 所属阵营
	this->hacked_point=x.hacked_point;				// 被黑的点数
	this->healing_rate =x.healing_rate;		// 治疗 / 维修速率	
	this->health_now=x.health_now;					// 当前生命值		
	this->is_disable=x.is_disable;		// 是否被瘫痪
	this->max_health_now=x.max_health_now;				// 当前HP上限
	this->max_speed_now=x.max_speed_now;				// 当前最大速度
	this->position=x.position;				// 单位位置
	this->shot_range_now=x.shot_range_now;				// 当前射程
	this->skill_last_release_time1=x.skill_last_release_time1;// 上次技能1释放时间
	this->skill_last_release_time2=x.skill_last_release_time2;// 上次技能2释放时间
	this->unit_id=x.unit_id;				// 单位id
}


//各兵种决策类
class Squad
{
public:
	int fighter[10],fighterS;
	bool death[10];
	//int aims;
	Squad() { memset(this,0,sizeof(this)); }
	void add(int x)
	{
		fighter[fighterS++]=x;
	}
	void update()
	{
		for(int i=0;i<fighterS;i++)
			if(death[i])
			{
				fighterS--;
				swap(fighter[i],fighter[fighterS]);
				death[i]=0;
			}
	}
	void UnitToPosition(int id,Position pos) //不同于unit_id
	{
		Unit &cur_unit=each_unit[id];
		Position cur_pos=cur_unit.position;
		Position dis=Minus(pos,cur_pos),way;
		int dx=(dis.x>0?1:-1),dy=(dis.y>0?1:-1);
		if(abs(dis.x)<=cur_unit.max_speed_now)
			Move(cur_unit.unit_id,pos);
		else
		{
			way=Position(dx*cur_unit.max_speed_now,0);
			while(ground.checkBuilding(Add(cur_pos,way))&&way.x)
				way.x-=dx,way.y+=dy;
			if(ground.checkBuilding(Add(cur_pos,way)))
				way.y-=dy;
			Move(cur_unit.unit_id,Add(cur_pos,way));
		}
		/*
		cout<<"#"<<id<<"   ";
		cur_pos.Print();
		pos.Print();
		*/
	}
	void toPosition(Position pos)	//仅适用于地面单位
	{
		for(int i=0;i<fighterS;i++)
			UnitToPosition(fighter[i],pos);
	}
	bool attackPosition(Position pos)	//
	{
		for(int i=0;i<fighterS;i++)
		{
			Unit &cur_unit=each_unit[fighter[i]];
			int cur_id=cur_unit.unit_id;
			Position cur_pos=cur_unit.position;
			Position dis=Minus(cur_pos,pos),way;
			if(getLength(dis)<=cur_unit.shot_range_now)
				return 1;//skill_1(unit_id,int target_id=-1,Position tpos1=none_position,Position tpos2=none_position) ;
			UnitToPosition(fighter[i],pos);
		}
		return 0;
	}
};

Squad squad[40];	//1~squadS
int squadS=0;
int belong[maxU];  //每个单位所属于的小队

class Army
{
public:
	int member[50];	//1~memberS
	int memberS;	

	Army() { memberS=0; }
	int add(int x,bool tflag)
	{
		member[++memberS]=x;
		if(tflag) return -1;
		if(!belong[x])
		{
			if(squad[squadS].fighterS>=5)
				squadS++;
			squad[squadS].add(x);
		}
		return memberS;
	}
	void Print() 
	{ 
		for(int tflag=0;tflag<2;tflag++)
		{
			cout<<tflag<<':'<<endl;
			for(int i=0;i<memberS;i++) 
			{ 
				cout<<' ';
				each_unit[member[i]].Print(); 
			}
			cout<<endl; 
		}
	}

	//void attackPosition(int squadId,Position pos);
	//void attackId(int squadId,Position pos,int unit_id);
};

class Meat: public Army
{
public:
	void meatAdd(int x,bool tflag);
};

class Hacker: public Army
{
};

class Superman: public Army
{
};

class Battle_tank: public Army
{
};

class Bolt_tank: public Army
{
};

class Nuke_tank: public Army
{
};

class Uav: public Army
{
};

class Eagle: public Army
{
};

enum CommandType
{
	PRODUCING,ATTACKING,DEFENDING,GOTO		//	ATTACK/CAPTURE
};

class Command
{
public:
	CommandType type; //0-produce  1-attack/capture  2-defend  3-to
	int id;	//	squad/building
	int aim;	//produce limit
	//attack 0-pos >0-id  <0-default
	Position position;
	Command(CommandType t=PRODUCING,int id=0,int aim=0,Position pos=0):type(t),id(id),aim(aim),position(pos) {}
	bool execute()
	{
		switch(type)
		{
		case PRODUCING:
			produce(id);
			break;
		case ATTACKING:
			if(squad[id].attackPosition(position))
			{
				;  // 进攻完成后改变目标
				return 1;
			}
			break;
		case DEFENDING:

			break;
		case GOTO:
			squad[id].toPosition(position);
			break;
		default:
			break;
		}
		return 0;
	}
};

class myAI  //总决策类
{
public:
	int cnt[2];  //0-己方Unit  1-敌方（中立）Unit     下同
	int max_unit_id;

	Base __base[2];  //0-己方  1-敌方
	int labs[2][15];
	Building teach_building[2][20],bank[2][20];   //labs中依enum次序为HACK_LAB等等
	int BuildingS[2],Teach_buildingS[2],BankS[2];      //建筑数量 教学楼数量 银行数量

	//各兵种信息及自主决策类(也许没用
	Meat meat[2];
	Hacker hacker[2];
	Superman superman[2];

	Battle_tank battle_tank[2];
	Bolt_tank bolt_tank[2];
	Nuke_tank nuke_tank[2];

	Uav uav[2];
	Eagle eagle[2];

	//指令集和内存栈
	Command my_command[maxC];
	bool command_is_valid[maxC];
	int empty_command_id[maxC],command_top;

	myAI()
	{
		memset(this,0,sizeof(this)); 
		for(int i=0;i<maxC;i++)
			empty_command_id[command_top++]=maxC-i-1;
	}
	void getInformation();
	void addCommand(Command c0)
	{
		int command_id=empty_command_id[--command_top];
		command_is_valid[command_id]=1;
		my_command[command_id]=c0;
	}
	void delCommand(int command_id)
	{
		command_is_valid[command_id]=0;
		empty_command_id[command_top++]=command_id;
	}
	void action();
	void process()
	{
		for(int i=0;i<maxC;i++)
			if(command_is_valid[i])
			{
				if(my_command[i].execute())
					delCommand(i);
			}
	}
private:
	void Print()
	{
		__base[0].Print();
		__base[1].Print();

		for(int i=0;i<2;i++)
		{
			meat[i].Print();
			hacker[i].Print();
			superman[i].Print();
			battle_tank[i].Print();
			bolt_tank[i].Print();
			nuke_tank[i].Print();
			uav[i].Print();
			eagle[i].Print();
		}
		cout<<TeamId<<' '<<cnts<<"  "<<cnt[0]<<' '<<cnt[1]<<endl;
	}
};

bool cmp_unit_id(const Unit &x,const Unit &y)
{
	return x.unit_id<y.unit_id;
}

bool cmpUnit(const Unit &x,const Unit &y)
{
	return (x.type_name>=BuildingC?-1:x.unit_id)
		<(y.type_name>=BuildingC?-1:y.unit_id);
}

void myAI::getInformation()		//获取并整合各种信息
{
	TeamId=getTeamId();
	int last_turn_cnt=cnts;
	int cnts0=getUnitSize();
	cnts=cnt[0]=cnt[1]=0;
	Unit *tu=getUnit();
	for(int i=0;i<cnts0;i++)
	{
		if(tu[i].position.x>=0&&tu[i].position.y>=0)
		{
			int t=(tu[i].flag!=TeamId);  //这里暂时将中立建筑考虑成需占领的敌方建筑
			cnt[t]++;
			each_unit[cnts++]=tu[i];
		}
	}

	sort(each_unit,each_unit+cnts,cmp_unit_id);
	__base[0]=each_unit[TeamId];
	__base[1]=each_unit[!TeamId];
	teach_building[0][0]=each_unit[2|TeamId];
	teach_building[1][0]=each_unit[2|!TeamId];
	bank[0][0]=each_unit[4|TeamId];
	bank[1][0]=each_unit[4|!TeamId];

	sort(each_unit+6,each_unit+cnts,cmpUnit);  //初始6建筑不可占领
	BuildingS[0]=Teach_buildingS[0]=BankS[0]=0;
	BuildingS[1]=Teach_buildingS[1]=BankS[1]=0;
	memset(labs,0,sizeof(labs));
	int k;
	for(k=6;k<cnts&&each_unit[k].type_name>=BuildingC;k++)
	{
		int tflag=(each_unit[k].flag<0?1:each_unit[k].flag^TeamId);
		BuildingS[tflag]++;
		switch(each_unit[k].type_name)
		{
		case TEACH_BUILDING:
			teach_building[tflag][Teach_buildingS[tflag]++]=each_unit[k];
			break;
		case BANK:
			bank[tflag][BankS[tflag]++]=each_unit[k];
			break;
		default:
			int &tl0=labs[tflag][each_unit[k].type_name-BuildingC];
			int &tl1=labs[!tflag][each_unit[k].type_name-BuildingC];
			if(!tl0)
				tl0=k;
			else
			{
				tl1=k;
				if(getDistance(__base[0].position,each_unit[tl1].position)<
					getDistance(__base[0].position,each_unit[tl0].position))
					swap(tl0,tl1);
			}
			break;
		}
	}

	while(each_unit[k].unit_id<=max_unit_id&&k<cnts)
		k++;
	for(;k<cnts;k++)
	{
		bool tflag=each_unit[k].flag^TeamId;
		switch(each_unit[k].type_name)
		{
		case MEAT:
			meat[tflag].meatAdd(k,tflag);
			break;
		case HACKER:
			hacker[tflag].add(k,tflag);
			break;
		case SUPERMAN:
			superman[tflag].add(k,tflag);
			break;
		case BATTLE_TANK:
			battle_tank[tflag].add(k,tflag);
			break;
		case BOLT_TANK:
			bolt_tank[tflag].add(k,tflag);
		case NUKE_TANK:
			nuke_tank[tflag].add(k,tflag);
		case UAV:
			uav[tflag].add(k,tflag);
		case EAGLE:
			eagle[tflag].add(k,tflag);
		}
	}
	max_unit_id=each_unit[cnts-1].unit_id;

	// 生成地图
	ground.init();
	for(int i=0;i<cnts;i++)
		ground.insert(i);

	//Print();
}

void myAI::action()
{
	//produce(__base[0].unit_id);
	if(command_top==maxC)
		addCommand(Command(PRODUCING,__base[0].unit_id,50));
}

myAI Ayanami;

void Meat::meatAdd(int x,bool tflag)
{
	if(tflag)
	{
		add(x,tflag);
		return ;
	}
	//cout<<x<<endl;
	if(memberS<3)		//优先占领附近、UAV_LAB
	{
		member[memberS++]=x;
		if(memberS==1)		//1st meat to teach_building
		{
			squad[++squadS].add(x);
			Ayanami.addCommand(Command(ATTACKING,squadS,0,Ayanami.teach_building[0][0].position));
		}
		else if(memberS==2)	//2nd meat to bank
		{
			squad[++squadS].add(x);
			Ayanami.addCommand(Command(ATTACKING,squadS,0,Ayanami.bank[0][0].position));
		}
		else
		{
			squad[++squadS].add(x);
			Ayanami.addCommand(Command(ATTACKING,squadS,0,getPosition(Ayanami.labs[0][UAV_LAB-BuildingC])));
		}
	}
	else
	{
		int squad_id=add(x,0);
		Ayanami.addCommand(Command(ATTACKING,squad_id,-1,Position(-1,-1)));
	}
}

void player_main(void)
{
	Ayanami.getInformation();
	//ground.Print();
	for(int i=3;i<=4;i++)
	{
		cout<<squad[i].fighterS<<':';
		for(int j=0;j<squad[i].fighterS;j++)
			cout<<squad[i].fighter[j]<<' ';
		cout<<"     ";
		getPosition(squad[i].fighter[0]).Print();
		cout<<endl;
	}
	if(each_unit[Ayanami.labs[0][UAV_LAB-BuildingC]].flag==TeamId)
		cout<<"succeed!"<<endl;
	Ayanami.action();
	Ayanami.process();
	//system("pause");
}