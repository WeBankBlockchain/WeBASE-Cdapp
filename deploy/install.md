# 一键部署

​	一键部署可以在 **同机** 快速搭建证书环境，方便用户快速体验证书管理平台。


## 前提条件

| 环境   | 版本                   |
| ------ | ---------------------- |
| Java   | JDK 8 至JDK 14 |
| MySQL | MySQL-5.6及以上 |
| Python | Python3.6及以上 |
| PyMySQL | |

### 检查环境

#### 平台要求

推荐使用CentOS 7.2+，Ubuntu 16.04及以上版本。

#### 检查Java

推荐JDK8-JDK13版本，使用OracleJDK[安装指引](#jdk)：

```
java -version
```

*注意：不要用`sudo`执行安装脚本*


#### 检查mysql

MySQL-5.6或以上版本：
```
mysql --version
```

- Mysql安装部署可参考[数据库部署](#mysql-install)

#### 检查Python
<span id="checkpy"></span>
使用Python3.6或以上版本：
```shell
python --version
# python3时
python3 --version
```

如已安装python3，也可通过`python3 --version`查看，在运行脚本时，使用`python3`命令即可

- Python3安装部署可参考[Python部署](#python3)

#### PyMySQL部署（Python3.6+）

Python3.6及以上版本，需安装`PyMySQL`依赖包

- CentOS

  ```
  sudo yum -y install python36-pip
  sudo pip3 install PyMySQL
  ```


- Ubuntu

  ```
  sudo apt-get install -y python3-pip
  sudo pip3 install PyMySQL
  ```

 CentOS或Ubuntu不支持pip命令的话，可以使用以下方式：

  ```
  git clone https://github.com/PyMySQL/PyMySQL
  cd PyMySQL/
  python3 setup.py install
  ```

### 检查服务器网络策略

网络策略检查：
- **开放证书管理平台端口**：检查cdapp-web管理平台页面的端口`webPort`(默认为5000)在服务器的网络安全组中是否设置为**开放**。如，云服务厂商如腾讯云，查看安全组设置，为cdapp-web开放5000端口。**若端口未开放，将导致浏览器无法访问证书服务页面**
- **开放H5查询页端口**：检查cdapp-h5查询页面的端口`h5Port`(默认为5001)在服务器的网络安全组中是否设置为**开放**。

## 获取部署脚本

获取部署安装包，咨询【mingzhenliu(刘明臻)】

解压安装包：
```shell
unzip cdapp-deploy.zip
```
进入目录：
```shell
cd cdapp-deploy
```

## 修改配置

① mysql数据库需提前安装，已安装直接配置即可，还未安装请参看[数据库部署](#mysql)；

② 修改配置文件（`vi common.properties`）：

```shell
# 数据库连接信息
mysql.ip=localhost
mysql.port=3306
mysql.user=dbUsername
mysql.password=dbPassword
mysql.database=cdappDb

# cdapp-web service port
web.port=5000
# cdapp-h5 service port
h5.port=5001

# cdapp-server service port
server.port=5002

# cdapp-server
# 网关ip端口
server.gatewayIpPort=gatewayIpPort
# 应用ID
server.appId=appIdTemp
# 应用Key
server.appKey=appKeyTemp
# 应用签名私钥用户
server.signUserId=signUserIdTemp
```


## 部署

* 执行installAll命令，部署服务将部署证书管理服务（cdapp-server）、证书管理前端（cdapp-web）、证书H5查询页（cdapp-h5）

**备注：** 
- 首次部署需要初始化数据库，重复部署时可以根据提示不重复操作
- 部署过程中出现报错时，可根据错误提示进行操作，或根据本文档中的[常见问题](#q&a)进行排查
- **不要用sudo执行脚本**，例如`sudo python3 deploy.py installAll`（sudo会导致无法获取当前用户的环境变量如JAVA_HOME）

```shell
# 部署并启动所有服务
python3 deploy.py installAll
```

部署完成后可以看到`deploy  has completed`的日志：

```shell
$ python3 deploy.py installAll
...
============================================================
==============      deploy  has completed     ==============
============================================================
```

* 服务部署后，需要对各服务进行启停操作，可以使用以下命令：

```shell
# 一键部署
部署并启动所有服务        python3 deploy.py installAll
停止一键部署的所有服务    python3 deploy.py stopAll
启动一键部署的所有服务    python3 deploy.py startAll
# 各子服务启停
启动cdapp-server:          python3 deploy.py startServer
停止cdapp-server:          python3 deploy.py stopServer
启动cdapp-web和cdapp-h5:          python3 deploy.py startWeb
停止cdapp-web和cdapp-h5:          python3 deploy.py stopWeb
```

#### 检查进程端口
通过`netstat`命令，检查各子系统进程的端口监听情况。

检查方法如下，若无输出，则代表进程端口监听异常，需要到该子系统的日志中[检查日志错误信息](#checklog)，并根据错误提示或本文档的[常见问题](#q&a)进行排查

- 检查cdapp-server端口(默认为5002)是否已监听
```
$ netstat -anlp | grep 5002    
```
输出如下
```
tcp6       0      0 :::5002                 :::*                    LISTEN      14049/java 
```

- 检查webase-web端口(默认为5000)在nginx是否已监听
```
$ netstat -anlp | grep 5000
```
输出如下
```
tcp        0      0 0.0.0.0:5000            0.0.0.0:*               LISTEN      3498/nginx: master  
```

<span id="checklog"></span>

#### 检查服务日志 

<span id="logpath"></span>
##### 各子服务的日志路径如下：

```
|-- cdapp-deploy # 一键部署目录
|--|-- log # 部署日志目录
|--|-- log-web # 管理平台日志目录
|--|-- cdapp-server # 管理服务目录
|--|--|-- log # 管理服务日志目录
```
*备注：当前节点日志路径为一键部署搭链的路径，使用已有链请在相关路径查看日志*

日志目录中包含`{XXX}.log`全量日志文件和`{XXX}-error.log`错误日志文件
 - *通过日志定位错误问题时，可以结合`.log`全量日志和`-error.log`错误日志两种日志信息进行排查。*

##### 检查服务日志有无错误信息

- 如果各个子服务的进程**已启用**且端口**已监听**，可直接访问下一章节[访问管理平台](#access)

- 如果上述检查步骤出现异常，如检查不到进程或端口监听，则需要按[日志路径](#logpath)进入**异常子服务**的日志目录，检查该服务的日志

- 如果检查步骤均无异常，但服务仍无法访问，可以分别检查部署日志、 证书管理服务日志进行排查：
  - 检查cdapp-deploy/log中的**部署日志**，是否在部署时出现错误
  - 检查cdapp-deploy/cdapp-server/log中的日志


<span id="access"></span>
## 访问

* 一键部署完成后，**打开浏览器（Chrome Safari或Firefox）访问**
```
# 证书管理平台
http://{deployIP}:{webPort}
示例：http://localhost:5000

# H5查询页
http://{deployIP}:{h5Port}
示例：http://localhost:5001
```

**备注：** 

- 部署服务器IP和管理平台服务端口需对应修改，网络策略需开通
  - 使用云服务厂商的服务器时，需要开通网络安全组的对应端口。如开放使用的5000端口
- 证书管理平台使用说明请查看[使用手册]()
  - 默认账号为`admin`，默认密码为`Test123`。


## 附录

<span id="jdk"></span>
### 1. Java环境部署

#### CentOS环境安装Java
<span id="centosjava"></span>

**注意：CentOS下OpenJDK无法正常工作，需要安装OracleJDK[下载链接](https://www.oracle.com/technetwork/java/javase/downloads/index.html)。**

```
# 创建新的文件夹，安装Java 8或以上的版本，推荐JDK8-JDK13版本，将下载的jdk放在software目录
# 从Oracle官网(https://www.oracle.com/java/technologies/downloads/#java8)选择Java 8或以上的版本下载，例如下载jdk-8u301-linux-x64.tar.gz
$ mkdir /software

# 解压jdk
$ tar -zxvf jdk-8u301-linux-x64.tar.gz

#修改解压后文件的文件名
$mv jdk1.8.0_301 jdk-8u301

# 配置Java环境，编辑/etc/profile文件
$ vim /etc/profile

# 打开以后将下面三句输入到文件里面并保存退出
export JAVA_HOME=/software/jdk-8u301  #这是一个文件目录，非文件
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

# 生效profile
$ source /etc/profile

# 查询Java版本，出现的版本是自己下载的版本，则安装成功。
java -version
```

#### Ubuntu环境安装Java
<span id="ubuntujava"></span>

```
  # 安装默认Java版本(Java 8或以上)
  sudo apt install -y default-jdk
  # 查询Java版本
  java -version
```

### 2. 数据库部署
<span id="mysql-install"></span>

#### ① CentOS安装MariaDB

此处以**CentOS 7(x86_64)**安装**MariaDB 10.2**为例。*MariaDB*数据库是 MySQL 的一个分支，主要由开源社区在维护，采用 GPL 授权许可。*MariaDB*完全兼容 MySQL，包括API和命令行。MariaDB 10.2版本对应Mysql 5.7。其他安装方式请参考[MySQL官网](https://dev.mysql.com/downloads/mysql/)。
- CentOS 7 默认MariaDB为5.5版本，安装10.2版本需要按下文进行10.2版本的配置。
- 若使用CentOS 8则直接使用`sudo yum install -y mariadb*`即可安装MariaDB 10.3，并跳到下文的 *启停* 章节即可。

使用`vi`或`vim`创建新文件`/etc/yum.repos.d/mariadb.repo`，并写入下文的文件内容（参考[MariaDB中科大镜像源修改](http://mirrors.ustc.edu.cn/help/mariadb.html)进行配置）

- 创建repo文件
```Bash
sudo vi /etc/yum.repos.d/mariadb.repo
```

- 文件内容，此处使用的是中科大镜像源
```Bash
# MariaDB 10.2 CentOS repository list - created 2021-07-12 07:37 UTC
# http://downloads.mariadb.org/mariadb/repositories/
[mariadb]
name = MariaDB
baseurl = https://mirrors.ustc.edu.cn/mariadb/yum/10.2/centos7-amd64
gpgkey=https://mirrors.ustc.edu.cn/mariadb/yum/RPM-GPG-KEY-MariaDB
gpgcheck=1
```

- 更新yum源缓存数据
```
yum clean all
yum makecache all
```

- 安装`MariaDB 10.2`
- 如果已存在使用`sudo yum install -y mariadb*`命令安装的MariaDB，其版本默认为5.5版本，对应Mysql版本为5.5。新版本MariaDB无法兼容升级，需要先**卸载旧版本**的MariaDB，卸载前需要**备份**数据库内容，卸载命令可参考`yum remove mariadb`
```
sudo yum install MariaDB-server MariaDB-client -y
```

若安装时遇到错误`“Failed to connect to 2001:da8:d800:95::110: Network is unreachable”`，将源地址中的 `mirrors.ustc.edu.cn` 替换为 `ipv4.mirrors.ustc.edu.cn` 以强制使用 IPv4：
```
sudo sed -i 's#//mirrors.ustc.edu.cn#//ipv4.mirrors.ustc.edu.cn#g' /etc/yum.repos.d/mariadb
```
详情参考[MariaDB官网安装](https://downloads.mariadb.org/mariadb/repositories/#mirror=digitalocean-nyc)。
- 启停

```shell
启动：sudo systemctl start mariadb.service
停止：sudo systemctl stop  mariadb.service
```

- 设置开机启动

```
sudo systemctl enable mariadb.service
```

- 初始化

```shell
执行以下命令：
sudo mysql_secure_installation
以下根据提示输入：
Enter current password for root (enter for none):<–初次运行直接回车
Set root password? [Y/n] <– 是否设置root用户密码，输入y并回车或直接回车
New password: <– 设置root用户的密码
Re-enter new password: <– 再输入一次你设置的密码
Remove anonymous users? [Y/n] <– 是否删除匿名用户，回车
Disallow root login remotely? [Y/n] <–是否禁止root远程登录，回车
Remove test database and access to it? [Y/n] <– 是否删除test数据库，回车
Reload privilege tables now? [Y/n] <– 是否重新加载权限表，回车
```

#### ② 授权访问和添加用户

- 使用root用户登录，密码为初始化设置的密码

```
mysql -uroot -p -h localhost -P 3306
```

- 授权root用户远程访问

```sql
mysql > GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
mysql > flush PRIVILEGES;
```

- 创建test用户并授权本地访问

```sql
mysql > GRANT ALL PRIVILEGES ON *.* TO 'test'@localhost IDENTIFIED BY '123456' WITH GRANT OPTION;
mysql > flush PRIVILEGES;
```

**安全温馨提示：**

- 例子中给出的数据库密码（123456）仅为样例，强烈建议设置成复杂密码
- 例子中root用户的远程授权设置会使数据库在所有网络上都可以访问，请按具体的网络拓扑和权限控制情况，设置网络和权限帐号

#### ③ 测试连接和创建数据库

- 登录数据库

```shell
mysql -utest -p123456 -h localhost -P 3306
```

- 创建数据库

```sql
mysql > create database webasenodemanager;
```

#### ④ Ubuntu安装mysql数据库

- 以root用户执行命令

```
apt-get install software-properties-common
sudo add-apt-repository 'deb http://archive.ubuntu.com/ubuntu trusty universe'
sudo apt-get update
sudo apt install mysql-server-5.6
sudo apt install mysql-client-5.6
```
- 执行mysql --version命令，若显示如下则安装成功
```
mysql  Ver 14.14 Distrib 5.6.16, for debian-linux-gnu (x86_64) using  EditLine wrapper
```

<span id="python3"></span>
### 3. Python部署

python版本要求使用python3.x, 推荐使用python3.6及以上版本

- CentOS

  ```
  sudo yum install -y python36
  sudo yum install -y python36-pip
  ```

- Ubuntu

  ```
  // 添加仓库，回车继续
  sudo add-apt-repository ppa:deadsnakes/ppa
  // 安装python 3.6
  sudo apt-get install -y python3.6
  sudo apt-get install -y python3-pip
  ```

<span id="q&a"></span>
## 常见问题

### 1. Python命令出错

- SyntaxError报错
```
  File "deploy.py", line 62
    print helpMsg
                ^
SyntaxError: Missing parentheses in call to "print". Did you mean print(helpMsg)?
```

- 找不到fallback关键字
```
File "/home/ubuntu/cdapp-deploy/comm/utils.py", line 127, in getCommProperties
    value = cf.get('common', paramsKey,fallback=None)
TypeError: get() got an unexpected keyword argument 'fallback'
```

答：检查[Python版本](#checkpy)，推荐使用python3.6及以上版本


### 2. 使用Python3时找不到pymysql

```
Traceback (most recent call last):
...
ImportError: No module named 'pymysql'
```

答：需要安装PyMySQL，安装请参看 [pymysql](#pymysql-python3-5)

### 3. 部署时某个组件失败，重新部署提示端口被占用问题

答：因为有个别组件是启动成功的，需先执行“python deploy.py stopAll”将其停止，再执行“python deploy.py installAll”部署全部。

### 4. 管理平台启动时Nginx报错

```
...
==============      Web      start...  ==============
Traceback (most recent call last):
...
Exception: execute cmd  error ,cmd : sudo /usr/local/nginx/sbin/nginx -c /data/app/cdapp-deploy/comm/nginx.conf, status is 256 ,output is nginx: [emerg] open() "/etc/nginx/mime.types" failed (2: No such file or directory) in /data/app/cdapp-deploy/comm/nginx.conf:13
```

答：缺少/etc/nginx/mime.types文件，建议重装nginx。

### 5. 部署时数据库访问报错

```
...
checking database connection
Traceback (most recent call last):
  File "/data/temp/cdapp-deploy/comm/mysql.py", line 21, in dbConnect
    conn = mdb.connect(host=mysql_ip, port=mysql_port, user=mysql_user, passwd=mysql_password, charset='utf8')
  File "/usr/lib64/python2.7/site-packages/MySQLdb/__init__.py", line 81, in Connect
    return Connection(*args, **kwargs)
  File "/usr/lib64/python2.7/site-packages/MySQLdb/connections.py", line 193, in __init__
    super(Connection, self).__init__(*args, **kwargs2)
OperationalError: (1045, "Access denied for user 'root'@'localhost' (using password: YES)")
```

答：确认数据库用户名和密码
