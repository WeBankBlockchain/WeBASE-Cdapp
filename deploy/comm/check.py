#!/usr/bin/env python3
# encoding: utf-8

from . import log as deployLog
import os
import sys
from .utils import *
from .mysql import *
import re
#import psutil

log = deployLog.getLocalLogger()
checkDependent = ["dos2unix"]
# memery(B) and cpu(core counts logical)
# mem=psutil.virtual_memory()
# cpuCore=psutil.cpu_count()

def do():
    print ("==============      checking envrionment      ==============")
    installRequirements()
    checkNginx()
    checkJava()
    checkWebPort()
    checkH5Port()
    checkServerPort()
    checkServerDbConnect()
    checkServerDbAuthorized()
    checkServerDbVersion()
    print ("==============      envrionment available     ==============")
    print ("============================================================")

def checkPort():
    print ("==============      checking    port          ==============")
    checkWebPort()
    checkH5Port()
    checkServerPort()
    print ("==============        port    available       ==============")

def installRequirements():
   for require in checkDependent:
      print ("check {}...".format(require))
      hasInstall = hasInstallServer(require)
      if not hasInstall:
        installByYum(require)
      print ("check finished sucessfully.")

def checkNginx():
    print ("check nginx...")
    require = "nginx"
    hasInstall = hasInstallServer(require)
    if not hasInstall:
        installByYum(require)
    print ("check finished sucessfully.")

def checkJava():
    print ("check java...")
    res_check = doCmdIgnoreException("java -version")
    if res_check["status"] != 0:
        print ("  error! java has not been installed or configured!")
        sys.exit(0)
    res_home = doCmd("echo $JAVA_HOME")
    if res_home["output"].strip() == "":
        print ("  error! JAVA_HOME has not been configured!")
        sys.exit(0)
    print ("check finished sucessfully.")
    return
    
def checkWebPort():
    print ("check Web port...")
    deploy_ip = "127.0.0.1"
    web_port = getCommProperties("web.port")
    res_web = net_if_used(deploy_ip,web_port)
    if res_web:
        sys.exit(0)
    print ("check finished sucessfully.")
    return

def checkH5Port():
    print ("check H5 port...")
    deploy_ip = "127.0.0.1"
    web_port = getCommProperties("h5.port")
    res_web = net_if_used(deploy_ip,web_port)
    if res_web:
        sys.exit(0)
    print ("check finished sucessfully.")
    return

def checkServerPort():
    print ("check Server port...")
    deploy_ip = "127.0.0.1"
    server_port = getCommProperties("server.port")
    res_server = net_if_used(deploy_ip,server_port)
    if res_server:
        sys.exit(0)
    print ("check finished sucessfully.")
    return

def isBlank (str):
    return not (str and str.strip())

def checkServerDbConnect():
    print ("check database connection...")
    mysql_ip = getCommProperties("mysql.ip")
    mysql_port = getCommProperties("mysql.port")
    ifLink = do_telnet(mysql_ip,mysql_port)
    if not ifLink:
        print ('Server database ip:{} port:{} is disconnected, please confirm.'.format(mysql_ip, mysql_port))
        sys.exit(0)
    print ("check finished sucessfully.")
    return

def hasInstallServer(server):
    result = doCmdIgnoreException("which {}".format(server))
    if result["status"] == 0:
        return True
    else:
        return False

def installByYum(server):
    if isCentos():
        result = doCmdIgnoreException("sudo yum -y install {}".format(server))
        if result["status"] != 0:
            os.system("sudo yum -y install epel-release")
            os.system("sudo yum -y install python-pip")
            os.system("pip install requests")
            result = doCmd("sudo yum -y install {}".format(server))
    elif isSuse():
        os.system("sudo zypper install -y {}".format(server))
    elif isUbuntu():
        os.system("sudo apt-get install -y {}".format(server))
    else:
        print ("============================================================")
        print ('current system platform is not in target list(centos/redhat, ubuntu, suse')
        print ('===== please install dependency of [{}] on your own ====='.format(server))
        info = "n"
        if sys.version_info.major == 2:
            info = raw_input("Please check whether dependency of [{}] already installed, yes or not?[y/n]:".format(server))
        else:
            info = input("Please check whether dependency of [{}] already installed, yes or not?[y/n]:".format(server))
        if info == "y" or info == "Y":
            return
        else:
            raise Exception("error, not support this platform, only support centos/redhat, suse, ubuntu.")
    return

def checkMemAndCpu():
    print ("check host free memory and cpu core...")
    # result format: {'status': 0, 'output': '151.895'}
    # get free memory(M)
    memFree=doCmd("awk '($1 == \"MemFree:\"){print $2/1024}' /proc/meminfo 2>&1")
    # get cpu core num
    # cpuCore=doCmd("cat /proc/cpuinfo | grep processor | wc -l 2>&1")
    if (int(memFree.get("status")) != 0):
        raise Exception('Get memory or cpu core fail memFree:{}'.format(memFree))
    memFreeStr=memFree.get("output").split(".", 1)[0]
    memFreeInt=int(memFreeStr)

    existed_chain = getCommProperties("if.exist.fisco")
    # if existed chain, only need memory for webase
    if (existed_chain == 'yes'):
        if (memFreeInt <= 2047):
            print ('[WARN]Free memory {}(M) may be NOT ENOUGH for webase'.format(memFreeInt))
            print ("[WARN]Recommend webase with 2G memory at least. ")
        else:
            print ('check finished sucessfully.')
            return        
    # else not existed chain
    fisco_count_str = getCommProperties("node.counts")
    fisco_count = 2
    if (fisco_count_str != 'nodeCounts'):
        fisco_count = int(fisco_count_str)
    # check 2 nodes, 4 nodes, more nodes memory free rate/cpu require
    flag=False
    if (fisco_count <= 2):
        if (memFreeInt <= 2047):
            flag=True
    if (fisco_count >= 4):
        if (memFreeInt <= 4095):
            flag=True
    if (flag):
        print ('[WARN]Free memory {}(M) may be NOT ENOUGH for node count [{}] and webase'.format(memFreeInt, fisco_count))
        print ("[WARN]Recommend webase with 2G memory at least, and one node equipped with one core of CPU and 1G memory(linear increase with node count). ")
    else:
        print ('check finished sucessfully.')
        return

if __name__ == '__main__':
    pass
