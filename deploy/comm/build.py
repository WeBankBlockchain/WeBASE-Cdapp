#!/usr/bin/python3
# encoding: utf-8

import sys
import os
import time
from .utils import *
from .mysql import *

baseDir = getBaseDir()
currentDir = getCurrentBaseDir()
# check init node server's tb_front 
initDbEnable = False
serverWaitTime = 5

def do():
    print ("==============        starting  deploy        ==============")
    installServer()
    installWeb()
    print ("============================================================")
    print ("==============      deploy  has completed     ==============")
    print ("============================================================")
    return

def start():
    startServer()
    startWeb()
    return

def end():
    stopWeb()
    stopServer()
    return

def changeWebConfig():
    # get properties
    deploy_ip = "127.0.0.1"
    web_port = getCommProperties("web.port")
    h5_port = getCommProperties("h5.port")
    server_port = getCommProperties("server.port")
    pid_file = currentDir + "/nginx-web.pid"

    # init configure file
    web_conf_dir = currentDir + "/comm"
    if not os.path.exists(web_conf_dir + "/temp.conf"):
        doCmd('cp -f {}/nginx.conf {}/temp.conf'.format(web_conf_dir, web_conf_dir))
    else:
        doCmd('cp -f {}/temp.conf {}/nginx.conf'.format(web_conf_dir, web_conf_dir))

    # change web config
    web_dir = currentDir + "/cdapp-web"
    h5_dir = currentDir + "/cdapp-h5"
    web_log_dir = currentDir + "/log-web"
    doCmd('mkdir -p {}'.format(web_log_dir))
    doCmd('sed -i "s/127.0.0.1/{}/g" {}/comm/nginx.conf'.format(deploy_ip, currentDir))
    doCmd('sed -i "s/5000/{}/g" {}/comm/nginx.conf'.format(web_port, currentDir))
    doCmd('sed -i "s/5001/{}/g" {}/comm/nginx.conf'.format(h5_port, currentDir))
    doCmd('sed -i "s/server 127.0.0.1:5002/server {}:{}/g" {}/comm/nginx.conf'.format(deploy_ip, server_port, currentDir))
    doCmd('sed -i "s:log_path:{}:g" {}/comm/nginx.conf'.format(web_log_dir, currentDir))
    doCmd('sed -i "s:pid_file:{}:g" {}/comm/nginx.conf'.format(pid_file, currentDir))
    doCmd('sed -i "s:web_page_url:{}:g" {}/comm/nginx.conf'.format(web_dir, currentDir))
    doCmd('sed -i "s:h5_page_url:{}:g" {}/comm/nginx.conf'.format(h5_dir, currentDir))

    # change the path of mime.types, which is in the path of nginx configuration by default.
    res = doCmd("which nginx")
    if res["status"] == 0:
        res2 = doCmd("sudo " + res["output"] + " -t ")
        if res2["status"] == 0:
           oneLineOutput = res2["output"].split('\n')[0]; 
           print("onelineOutput: %s" %(oneLineOutput));
           startIndex = oneLineOutput.index("/");
           endIndex = oneLineOutput.rindex("/");

           nginxConfPath = oneLineOutput[startIndex:endIndex];
           print("Defualt nginx config path: %s" %(nginxConfPath)); 
           doCmd('sed -i "s/include .*\/mime.types/include {}\/mime.types/g" {}/comm/nginx.conf'.format(nginxConfPath.replace("/", "\/"), currentDir))
        else:
            print ("==============   Web start fail when checking the path of nginx configuration fail. Please view log file (default path:./log-web/). ==============")
            sys.exit(0)
    else:
        print ("==============    Web start fail when getting nginx. Please view log file (default path:./log-web/). ==============")
        sys.exit(0)

    return

def installWeb():
    print ("============================================================")
    print ("==============      Installing Web     ==============")
    os.chdir(currentDir)
    changeWebConfig()
    startWeb()

def startWeb():
    print ("==============      Starting Web       ==============")
    pid_file = currentDir + "/nginx-web.pid"
    if os.path.exists(pid_file):
        info = "n"
        if sys.version_info.major == 2:
            info = raw_input("Web Process already exists. Kill process to force restart?[y/n]:")
        else:
            info = input("Web Process already exists. Kill process to force restart?[y/n]:")
        if info == "y" or info == "Y":
            fin = open(pid_file, 'r')
            pid = fin.read()
            cmd = "sudo kill -QUIT {}".format(pid)
            os.system(cmd)
            doCmdIgnoreException("sudo rm -rf " + pid_file)
        else:
            sys.exit(0)
    web_log_dir = currentDir + "/log-web"
    doCmd('mkdir -p {}'.format(web_log_dir))
    nginx_config_dir = currentDir + "/comm/nginx.conf"
    res = doCmd("which nginx")
    if res["status"] == 0:
        res2 = doCmd("sudo " + res["output"] + " -c " + nginx_config_dir)
        if res2["status"] != 0:
            print ("==============    Web start fail. Please view log file (default path:./log-web/). ==============")
            sys.exit(0)
    else:
        print ("==============    Web start fail. Please view log file (default path:./log-web/). ==============")
        sys.exit(0)
    print ("==============      Web Started        ==============")
    return

def stopWeb():
    pid_file = currentDir + "/nginx-web.pid"
    if os.path.exists(pid_file):
        fin = open(pid_file, 'r')
        pid = fin.read()
        cmd = "sudo kill -QUIT {}".format(pid)
        os.system(cmd)
        doCmdIgnoreException("sudo rm -rf " + pid_file)
        print ("=======      Web     stop  success!  =======")
    else:
        print ("=======      Web     is not running! =======")
    return

def changeServerConfig():
    # get properties
    server_port = getCommProperties("server.port")
    mysql_ip = getCommProperties("mysql.ip")
    mysql_port = getCommProperties("mysql.port")
    mysql_user = getCommProperties("mysql.user")
    mysql_password = getCommProperties("mysql.password")
    mysql_database = getCommProperties("mysql.database")
    front_address = getCommProperties("server.gatewayIpPort")
    app_id = getCommProperties("server.appId")
    app_key = getCommProperties("server.appKey")
    sign_user_id = getCommProperties("server.signUserId")

    # init file
    server_dir = currentDir + "/cdapp-server"
    script_dir = server_dir + "/script"
    front_dir = server_dir + "/cert"
    conf_dir = server_dir + "/conf"
    # if not os.path.exists(script_dir + "/temp.sh"):
    #     doCmd('cp -f {}/dbInit.sh {}/temp.sh'.format(script_dir, script_dir))
    # else:
    #     doCmd('cp -f {}/temp.sh {}/dbInit.sh'.format(script_dir, script_dir))
    if not os.path.exists(conf_dir + "/temp.yml"):
        doCmd('cp -f {}/application.yml {}/temp.yml'.format(conf_dir, conf_dir))
    else:
        doCmd('cp -f {}/temp.yml {}/application.yml'.format(conf_dir, conf_dir))
    if not os.path.exists(front_dir + "/temp.properties"):
        doCmd('cp -f {}/front.properties {}/temp.properties'.format(front_dir, front_dir))
    else:
        doCmd('cp -f {}/temp.properties {}/front.properties'.format(front_dir, front_dir))

    # change script config
    # doCmd('sed -i "s/defaultAccount/{}/g" {}/dbInit.sh'.format(mysql_user, script_dir))
    # doCmd('sed -i "s/defaultPassword/{}/g" {}/dbInit.sh'.format(mysql_password, script_dir))
    # doCmd('sed -i "s/cdappDb/{}/g" {}/dbInit.sh'.format(mysql_database, script_dir))

    # change server config
    doCmd('sed -i "s/5002/{}/g" {}/application.yml'.format(server_port, conf_dir))
    doCmd('sed -i "s/127.0.0.1/{}/g" {}/application.yml'.format(mysql_ip, conf_dir))
    doCmd('sed -i "s/3306/{}/g" {}/application.yml'.format(mysql_port, conf_dir))
    doCmd('sed -i "s/defaultAccount/{}/g" {}/application.yml'.format(mysql_user, conf_dir))
    doCmd('sed -i "s/defaultPassword/{}/g" {}/application.yml'.format(mysql_password, conf_dir))
    doCmd('sed -i "s/cdappDb/{}/g" {}/application.yml'.format(mysql_database, conf_dir))
    doCmd('sed -i "s/appIdTemp/{}/g" {}/application.yml'.format(app_id, conf_dir))
    doCmd('sed -i "s/appKeyTemp/{}/g" {}/application.yml'.format(app_key, conf_dir))
    doCmd('sed -i "s/signUserIdTemp/{}/g" {}/application.yml'.format(sign_user_id, conf_dir))
    doCmd('sed -i "s/gatewayIpPort/{}/g" {}/front.properties'.format(front_address, front_dir))

    return

def installServer():
    print ("============================================================")
    print ("============== Installing server ==============")
    os.chdir(currentDir)
    changeServerConfig()
    
    # connect server's db and create database
    # if no re-create db, no need to init tables in db
    whether_init = serverDbInit()
    server_dir = currentDir + "/cdapp-server"
    script_dir = server_dir + "/script/table"
    if len(sys.argv) == 3 and sys.argv[2] == "travis":
        print ("Travis CI do not initialize database")
    elif whether_init == True:
        initTable(script_dir)
        global initDbEnable
        initDbEnable = True
        log.info(" installServer initDbEnable {}".format(initDbEnable))
        
    startServer()
    return        

def startServer():
    print ("==============  Starting server  ==============")
    os.chdir(currentDir)
    managerPort = getCommProperties("server.port")
    server_dir = currentDir + "/cdapp-server"
    if not checkPathExists(server_dir):
        sys.exit(0)
    os.chdir(server_dir)
    
    doCmdIgnoreException("source /etc/profile")
    doCmdIgnoreException("chmod u+x *.sh")
    doCmdIgnoreException("dos2unix *.sh")
    result = doCmd("bash start.sh")
    if result["status"] == 0:
        if_started = 'is running' in result["output"]
        if if_started:
            pid = get_str_btw(result["output"], "(", ")")
            print ("server Port {} is running PID({})".format(managerPort,pid))
            sys.exit(0)
        if_success = 'Starting' in result["output"]
        if if_success:
            timeTemp = 0
            while timeTemp < serverWaitTime :
                print("=", end='')
                sys.stdout.flush()
                time.sleep(1)
                timeTemp = timeTemp + 1
            print ("========= Server starting. Please check through the log file (default path:./cdapp-server/log/). ==============")
        else:
            print ("============== Server start fail. Please check through the log file (default path:./cdapp-server/log/). ==============")
            sys.exit(0)
    else:
        print ("============== Server start fail. Please check through the log file (default path:./cdapp-server/log/). ==============")
        sys.exit(0)
    print ("==============  Server  Started  ==============")
    return

def stopServer():
    server_dir = currentDir + "/cdapp-server"
    if not checkPathExists(server_dir):
        return
    os.chdir(server_dir)
    
    doCmdIgnoreException("source /etc/profile")
    doCmdIgnoreException("chmod u+x *.sh")
    doCmdIgnoreException("dos2unix *.sh")
    result = doCmd("bash stop.sh")
    if result["status"] == 0:
        if_success = 'Success' in result["output"]
        if if_success:
            print ("=======      Server  stop  success!  =======")
        else:
            print ("=======      Server is not running!  =======")
    else:
        print ("======= Server stop   fail. Please view log file (default path:./log/).    =======")
    return
