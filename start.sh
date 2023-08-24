#!/bin/bash

APP_MAIN=com.certapp.Application
CLASSPATH='conf/:apps/*:lib/*'
CURRENT_DIR=`pwd`/
LOG_DIR=${CURRENT_DIR}log
CONF_DIR=${CURRENT_DIR}conf
# JDK路径
#JAVA_HOME="/nemo/jdk1.8.0_141"
# AOMP会读取到host的ip
HOST_IP=[@HOSTIP]
# 字体
FONTS_DIR=${CONF_DIR}/fonts

SERVER_PORT=$(cat $CONF_DIR/application.yml | grep "server:" -A 3 | grep "port" | awk '{print $2}'| sed 's/\r//')
if [ ${SERVER_PORT}"" = "" ];then
    echo "$CONF_DIR/application.yml server port has not been configured"
    exit -1
fi

JMX_PORT=`expr $SERVER_PORT + 1`

if [ ${JAVA_HOME}"" = "" ];then
    echo "JAVA_HOME has not been configured"
    exit -1
fi

mkdir -p $LOG_DIR
mkdir -p ${CURRENT_DIR}files/qr
mkdir -p ${CURRENT_DIR}files/ten
mkdir -p ${CURRENT_DIR}files/three
mkdir -p ${CURRENT_DIR}files/create

startWaitTime=30
processPid=0
processStatus=0
checkProcess(){
    server_pid=`ps aux | grep java | grep $CURRENT_DIR | grep $APP_MAIN | awk '{print $2}'`
    if [ -n "$server_pid" ] && [ -n "$(echo $server_pid| sed -n "/^[0-9]\+$/p")" ]; then
        processPid=$server_pid
        processStatus=1
    else
        processPid=0
        processStatus=0
    fi
}

JAVA_OPTS=" -Dfile.encoding=UTF-8"
JAVA_OPTS+=" -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
JAVA_OPTS+=" -Djava.rmi.server.hostname=${HOST_IP}"
JAVA_OPTS+=" -Xmx1024m -Xms1024m -Xss512k -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"
JAVA_OPTS+=" -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:${LOG_DIR}/jvm.log"
JAVA_OPTS+=" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}/ -XX:ErrorFile=${LOG_DIR}/heap_error.log"

start(){
    checkProcess
    echo "==============================================================================================="
    if [ $processStatus == 1 ]; then
        echo "Server $APP_MAIN Port $SERVER_PORT is running PID($processPid)"
        echo "==============================================================================================="
    else
        echo -n "Starting Server $APP_MAIN Port $SERVER_PORT ..."
        nohup $JAVA_HOME/bin/java -Dpath=$CURRENT_DIR $JAVA_OPTS -cp $CLASSPATH $APP_MAIN >> $LOG_DIR/server.out 2>&1 &
        
        count=1
        result=0
        while [ $count -lt $startWaitTime ] ; do
           checkProcess
           if [ $processPid -ne 0 ]; then
               result=1
               break
           fi
           let count++
           echo -n "."
           sleep 1
       done
        
       if [ $result -ne 0 ]; then
           echo "PID($processPid) [Starting]. Please check through the log file (default path:./log/)."
           echo "==============================================================================================="
       else
           echo "[Failed]. Please check through the log file (default path:./log/)."
           echo "==============================================================================================="
       fi
    fi
}

start
