#!/bin/bash

this_dir=$(cd `dirname $0`; pwd)
cd $this_dir
java -Dfile.encoding=UTF-8 -Duser.timezone=GMT+08 -XX:+UseG1GC -jar $this_dir/lib/sms_gui_client-1.0.jar