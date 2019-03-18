#coding=utf-8

from flask_login import UserMixin
from flaskext.mysql import MySQL
from re import search

mysql = MySQL()

class Validator():
    def __init__(self, form={'username':'','password':''}):
        self._usr = form['username']
        self._pwd = form['password']

    def validate(self):
        cursor = mysql.get_db().cursor()
        cursor.execute("SELECT * FROM `user` WHERE `username` = '" + self._usr + "'")
        data = cursor.fetchone()
        if data is None:
            flash('用户名不存在！')
            return False

        db_username = data[0]
        db_password = data[1]
        db_auth = data[2]
        db_regtime = data[3]
        return self._pwd == db_password



class LoginForm():
    def __init__(self, form):
        self._form = {'username': form.get('username'), 'password': form.get('password')}
        self._validator = Validator(form=self._form)

    # validate if username and password match
    def validate(self):
        return self._validator.validate()

    def get(self, key):
        return self._form[key]
