#!/bin/bash

this_dir=$(cd `dirname $0`; pwd)
tpid=`ps -ef|grep $this_dir|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]
then
	kill -15 $tpid
fi
echo $tpid
sleep 5s
cd $this_dir
nohup java -Xms256m -Xmx2048m -Dfile.encoding=UTF-8 -Duser.timezone=GMT+08 -XX:+UseG1GC -jar $this_dir/lib/sms_gui_client-1.0.jar >> /dev/null 2>&1 &