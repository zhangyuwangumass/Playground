#coding=utf-8
import sys
reload(sys) # Python2.5 初始化后删除了 sys.setdefaultencoding 方法，我们需要重新载入
sys.setdefaultencoding('utf-8')
import os
from json import load
from flask import Flask, request, render_template, session, redirect, url_for, make_response, flash
from flask_login import login_user,logout_user,login_required,LoginManager,current_user
import consts as cst
from models import mysql, LoginForm



#load configs
configs = load(open(cst.USCONFIGS,'r'))
net_cfs = configs[cst.NET_CONFIGS]
HOST = net_cfs[cst.HOST]
PORT = net_cfs[cst.PORT]
DEBUG = net_cfs[cst.DEBUG]
THREADED = net_cfs[cst.THREADED]

app = Flask(__name__	)
app.config['SECRET_KEY'] = os.urandom(24)
app.config['MYSQL_DATABASE_USER'] = 'guest'
app.config['MYSQL_DATABASE_PASSWORD'] = 'default-password'
app.config['MYSQL_DATABASE_DB'] = 'login'
app.config['MYSQL_DATABASE_HOST'] = 'localhost'

mysql.init_app(app)
#set up LoginManager
login_manager=LoginManager()
login_manager.session_protection='strong'
login_manager.login_view='login'
login_manager.login_message='请登录'
login_manager.init_app(app)

@login_manager.user_loader
def load_user(userid):
	return User.query.get(int(userid))

@app.route('/')
def main():
	return render_template('login.html')

@app.route('/<page>')
def login_page(page):
	return render_template(page)

@app.route('/login/<quest>', methods=['GET', 'POST'])
def login(quest):
	print request.form
	remember = (request.form.get('remember') == 'on')
	form = LoginForm(request.form)

	if request.method == 'POST' and form.validate():
		resp = make_response(redirect(url_for('upload_page', quest=quest)))
		if remember:
			resp.set_cookie('username', form.get('username'))
		#flash('登录成功！')
		return resp
	return redirect(url_for('main'))

@app.route('/logout',methods=['GET'])
@login_required
def logout():
    logout_user()
    return redirect(url_for('login'))

@app.route('/<quest>.html')
def page(quest):
	return render_template(quest + '.html')

@app.route('/upload/<quest>')
def upload_page(quest):
	#return redirect(url_for('page', quest=quest))
	#return render_template(quest + '.html')
	return render_template('/user/user.html')

if __name__=='__main__':
    app.run(host=HOST,port=PORT, debug=DEBUG, threaded=THREADED)
