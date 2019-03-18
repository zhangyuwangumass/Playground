#coding=utf-8
import sys
reload(sys) # Python2.5 初始化后删除了 sys.setdefaultencoding 方法，我们需要重新载入
sys.setdefaultencoding('utf-8')
from json import load
from flask import Flask
from flask import request
from flask import render_template
#from search import search_by_index
import consts as cst
import ctypes

#load configs
configs = load(open(cst.PSCONFIGS,'r'))
net_cfs = configs[cst.NET_CONFIGS]
HOST = net_cfs[cst.HOST]
PORT = net_cfs[cst.PORT]
DEBUG = net_cfs[cst.DEBUG]
THREADED = net_cfs[cst.THREADED]

app = Flask(__name__)

@app.route('/')
def main():
	return render_template('main.html')

@app.route('/login', methods=['GET','POST'])
def login():
	print "I received Message"
	if request.method == 'POST':
		print "I received POST"
		return render_template('login.html')
	else:
		print 'I received quest!' + request.args['quest']
		return render_template(request.args['quest'] + '.html')

if __name__=='__main__':
    app.run(host=HOST,port=PORT, debug=DEBUG, threaded=THREADED)
