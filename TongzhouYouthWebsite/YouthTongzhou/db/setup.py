import sqlite3

def setup():
	conn=sqlite3.connect("data.db")
	cursor=conn.cursor()
	cursor.execute("create table user (\
		`id` integer primary key autoincrement not null,\
		`password` varchar(30) not null,\
		`type` varchar(5) not null,\
		`phone` varchar(50) not null,\
		`realname` varchar(255),\
		`email` text,\
		`wechat` text,\
		`age` integer,\
		`netpoint` integer,\
		`address` text,\
		`rank` text,\
		`specialty` text,\
		`attendance` integer,\
		`activity_duration` integer,\
		`volunteer_duration` integer,\
		`contactor` varchar(50),\
		`scope` text,\
		`photo` text,\
		`description` text)")
	#####################################################################
	#phone <-unique username   
	#password <- ''"";(), not available
	#type <- W worker, Y youth, C company
	#
	#
	#
	#
	#
	#
	#
	####################################################################
	conn.commit()
	conn.close()
	
def create_user(phone,utype,password):
	if len(phone)>20 or len(password)>20 or len(utype)>1:
		return 1 #length overflow
	banlist=['\'','"','(',')',';',',','/','\\','$','*',':','?']
	for char in banlist:
		if (char in phone) or (char in utype) or (char in password):
			return 2 #illegal character
	conn=sqlite3.connect("data.db")
	cursor=conn.cursor()
	cursor.execute("select * from user where `phone`=(?)",(phone,))
	ret=cursor.fetchall()
	if len(ret)>0:
		conn.close()
		return 3 #phone duplicated
	if utype=="W":
		cursor.execute("insert into user(`phone`,`password`,`type`)\
			values(?,?,'W')",(phone,password))
		conn.commit()
		conn.close()
		return 0
	elif utype=="Y":
		cursor.execute("insert into user(`phone`,`password`,`type`)\
			values(?,?,'Y')",(phone,password))
		conn.commit()
		conn.close()
		return 0
	elif utype=="C":
		cursor.execute("insert into user(`phone`,`password`,`type`)\
			values(?,?,'C')",(phone,password))
		conn.commit()
		conn.close()
		return 0
	else:
		conn.close()
		return 4 #illegal utype

def check_user(phone,utype):
	if len(phone)>20 or len(utype)>1:
		return (1) #length overflow
	banlist=['\'','"','(',')',';',',','/','\\','$','*',':','?']
	for char in banlist:
		if (char in phone) or (char in utype):
			return (2) #illegal character
	conn=sqlite3.connect("data.db")
	cursor=conn.cursor()
	cursor.execute("select password from user where `phone`=(?) and `type`=(?)",(phone,utype,))
	ret=cursor.fetchall()
	if len(ret)==0:
		conn.close()
		return (5) #phone not exist
	if len(ret)>1:
		conn.close()
		return (3) #phone duplicated
	password=ret[0][0]
	conn.close()
	return (0,password)

def view_user(phone):
	if len(phone)>20:
		return (1) #length overflow
	banlist=['\'','"','(',')',';',',','/','\\','$','*',':','?']
	for char in banlist:
		if char in phone:
			return (2) #illegal character
	conn=sqlite3.connect("data.db")
	cursor=conn.cursor()
	cursor.execute("select * from user where `phone`=(?)",(phone,))
	ret=cursor.fetchall()
	if len(ret)==0:
		conn.close()
		return (5) #phone not exist
	if len(ret)>1:
		conn.close()
		return (3) #phone duplicated
	entry=ret[0]
	utype=entry[2]
	if utype=="W":
		realname=entry[4]
		email=entry[5]
		wechat=entry[6]
		netpoint=entry[8]
		address=entry[9]
		rank=entry[10]
		specialty=entry[11]
		if specialty is None:
			specialties=[]
		else:
			specialties=specialty.split("$$")
		r=[phone,realname,email,wechat,netpoint,address,rank,specialties]
		conn.close()
		return (0,r)
	elif utype=="Y":
		realname=entry[4]
		email=entry[5]
		wechat=entry[6]
		age=entry[7]
		specialty=entry[11]
		attendance=entry[12]
		activity_duration=entry[13]
		volunteer_duration=entry[14]
		if specialty is None:
			specialties=[]
		else:
			specialties=specialty.split("$$")
		r=[phone,realname,email,wechat,age,specialties,attendance,
		activity_duration,volunteer_duration]
		conn.close()
		return (0,r)
	elif utype=="C":
		pass
	else:
		conn.close()
		return (4) #invalid utype

