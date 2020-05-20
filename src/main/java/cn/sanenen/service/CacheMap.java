package cn.sanenen.service;

import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMap {
    public static final Map<String, CmppSubmitRequestMessage> map = new ConcurrentHashMap<>(Integer.parseInt(Param.getSendCount()));
}
