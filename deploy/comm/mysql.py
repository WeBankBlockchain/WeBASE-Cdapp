#!/usr/bin/env python3
# encoding: utf-8

from . import log as deployLog
import sys
import MySQLdb as mdb
from .utils import *
from urllib import parse
import re

log = deployLog.getLocalLogger()

def serverDbInit():
    # return whether to init tables
    whether_init = True
    # get properties
    mysql_ip = getCommProperties("mysql.ip")
    mysql_port = int(getCommProperties("mysql.port"))
    mysql_user = getCommProperties("mysql.user")
    mysql_password_raw = getCommProperties("mysql.password")
    mysql_password = parse.unquote_plus(mysql_password_raw)      
    mysql_database = getCommProperties("mysql.database")

    try:
        # connect
        conn = mdb.connect(host=mysql_ip, port=mysql_port, user=mysql_user, passwd=mysql_password, charset='utf8')
        conn.autocommit(1)
        cursor = conn.cursor()
        
        # check db
        result = cursor.execute('show databases like "%s"' %mysql_database)
        drop_db = 'DROP DATABASE IF EXISTS {}'.format(mysql_database)
        create_db = 'CREATE DATABASE IF NOT EXISTS {} default character set utf8'.format(mysql_database)
        if result == 1:
            info = "n"
            if sys.version_info.major == 2:
                info = raw_input("database {} already exists. Do you want drop and re-initialize it?[y/n]:".format(mysql_database))
            else:
                info = input("database {} already exists. Do you want drop and re-initialize it?[y/n]:".format(mysql_database))
            if info == "y" or info == "Y":
                log.info(drop_db)
                cursor.execute(drop_db)
                log.info(create_db)
                cursor.execute(create_db)
            # if not rebuild database, no need to re-init tables of database
            else:
                whether_init = False
        else:
            log.info(create_db)
            cursor.execute(create_db)
        cursor.close()
        conn.close()
        return whether_init
    except:
        import traceback
        log.info(" mysql except {}".format(traceback.format_exc()))
        traceback.print_exc()
        sys.exit(0)

def checkServerDbAuthorized():
    print ("check database user/password...")
    # get properties
    mysql_ip = getCommProperties("mysql.ip")
    mysql_port = int(getCommProperties("mysql.port"))
    mysql_user = getCommProperties("mysql.user")
    mysql_password_raw = getCommProperties("mysql.password")
    mysql_password = parse.unquote_plus(mysql_password_raw)
    try:
        # connect
        conn = mdb.connect(host=mysql_ip, port=mysql_port, user=mysql_user, passwd=mysql_password)
        conn.close()
        print("check finished sucessfully.")        
        log.info("check db user/password correct!")
    except:
        import traceback
        print("======[Error]mysql user/password error!======")
        log.info("mysql user/password error {}".format(traceback.format_exc()))
        traceback.print_exc()
        sys.exit(0)

def checkServerDbVersion():
    print ("check mysql version...")
    # get properties
    mysql_ip = getCommProperties("mysql.ip")
    mysql_port = int(getCommProperties("mysql.port"))
    mysql_user = getCommProperties("mysql.user")
    mysql_password_raw = getCommProperties("mysql.password")
    mysql_password = parse.unquote_plus(mysql_password_raw)
    try:
        # connect
        conn = mdb.connect(host=mysql_ip, port=mysql_port, user=mysql_user, passwd=mysql_password)
        # start check mysql version
        cursor = conn.cursor(cursor=mdb.cursors.DictCursor)
        log.info("checking mysql version")
        cursor.execute("select version();")
        # 5.6.xx-XX
        mysqlVersion = cursor.fetchall()[0].get("version()", "cannot_get_version")
        print("mysql version is [{}]".format(mysqlVersion))
        match = re.search("\d+(.\d+){0,2}", mysqlVersion)
        log.info("version match is:[{}]".format(match))
        # match不为空
        if match != "" and match != None:
            version = match.group()
            # 提取version第一位
            firstInt = int(version.split(".")[0])
            if firstInt == 5:
                # 提取version第二位
                secondInt = int(version.split(".")[1])
                # 5.6+
                if secondInt < 5:
                    sys.exit(0)
                elif secondInt == 5:
                    print("[WARN]server recommend mysql 5.6 or above")
        cursor.close()
        conn.close()
        print("check finished sucessfully.")        
        log.info("check db version correct!")
    except:
        import traceback
        print("======[Error]server require mysql 5.6 or above======")
        log.info("mysql require 5.6 or above error {}".format(traceback.format_exc()))
        traceback.print_exc()
        sys.exit(0)

# tool function:
def initTable(script_dir):
    print ("init database tables...")
    # get properties
    mysql_ip = getCommProperties("mysql.ip")
    mysql_port = int(getCommProperties("mysql.port"))
    mysql_user = getCommProperties("mysql.user")
    mysql_password_raw = getCommProperties("mysql.password")
    mysql_password = parse.unquote_plus(mysql_password_raw)    
    mysql_database = getCommProperties("mysql.database")
    
    # read .sql content
    create_sql_path = script_dir + "/table_create.sql"
    init_sql_path = script_dir + "/table_insert.sql"
    # create table
    create_sql_list=readSqlContent(create_sql_path,1)
    # init table data
    init_sql_list=readSqlContent(init_sql_path,2)

    try:
        # connect
        conn = mdb.connect(host=mysql_ip, port=mysql_port, user=mysql_user, passwd=mysql_password, database=mysql_database, charset='utf8')
        conn.autocommit(1)
        cursor = conn.cursor()

        log.info("start create tables...")
        for sql_item in create_sql_list:
            #log.info("create sql:{}".format(sql_item))
            cursor.execute(sql_item)

        log.info("start init default data of tables...")
        for sql_item in init_sql_list:
            #log.info("init sql:{}".format(sql_item))
            cursor.execute(sql_item)
        
        print ("==============  db script  init  success!  ==============")
        log.info("init tables success!")
        cursor.close()
        conn.close()
    except:
        import traceback
        print ("============== script init  fail! Please view log file (default path:./log/). ==============")
        log.info("init database tables error {}".format(traceback.format_exc()))
        traceback.print_exc()
        sys.exit(0)

# tool function:
# sql_type: 1-create, 2-insert
def readSqlContent(sql_path,sql_type):
    log.info("reading table sql file {}".format(sql_path))        
    with open(sql_path,encoding="utf-8",mode="r") as f:  
        data = f.read()
        lines = data.splitlines()
        sql_data = ''
	    # remove -- comment
        for line in lines:
            if len(line) == 0:
                continue
            elif line.startswith("--"):
                continue
            else:
                sql_data += line
        # create
        if sql_type == 1:
            final_sql_list = sql_data.split(';')[:-1]
            final_sql_list = [x.replace('\n', ' ') if '\n' in x else x for x in final_sql_list]
            return final_sql_list
        else:
            sql_list = sql_data.split(');')[:-1]
            final_sql_list = []
            for sql_splited in sql_list:
                sql_splited = sql_splited + ");"
                #log.info("after sql sql_splited:{}".format(sql_splited))
                final_sql_list.append(sql_splited)
            final_sql_list = [x.replace('\n', ' ') if '\n' in x else x for x in final_sql_list]
            return final_sql_list

if __name__ == '__main__':
    pass
