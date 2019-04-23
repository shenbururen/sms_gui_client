package cn.sanenen.service;

import java.util.Random;

import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.common.util.MsgId;
import com.zx.sms.connect.manager.EndpointConnector;
import com.zx.sms.connect.manager.EndpointManager;

import cn.hutool.core.thread.ThreadUtil;
import io.netty.channel.ChannelFuture;

/**
 * 发送服务类
 * @author sun
 * 2019年4月23日 下午2:25:34
 */
public class ConvertService {
	
	public static void sendSms(String mobile, String content, String id) {
		
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		msg.setDestterminalId(mobile);
		msg.setLinkID("0000");
		msg.setMsgContent(content);
		msg.setRegisteredDelivery((short) 1);
		msg.setMsgid(new MsgId());
		msg.setServiceId("10086");
		msg.setSrcId("10086");
		msg.setMsgsrc("927165");
		EndpointConnector<?> connector = EndpointManager.INS.getEndpointConnector(id);
		while (true) {
			ChannelFuture write = connector.asynwrite(msg);
			if (write!=null) {
				break;
			}else {
				ThreadUtil.sleep(10);
			}
		};
	}

	public static String getMobile() {
		StringBuffer mobile = new StringBuffer();
		// var reg = /^(1[3|5|7|8][0-9]{9})|(1[4][7|5][0-9]{8})$/;
		int arr[] = {
				// 1390105,1342600,1350100,1360100,1370100,1380000
				135, 136, 137, 138, 139, 147, 150, 151, 152, 154, 157, 158, 159, 182, 183, 184, 187, 188, 172, 178,
				1703, 1705, 1706, 1340, 1341, 1342, 1343, 1344, 1345, 1346, 1347, 1348, 130, 131, 132, 155, 156, 185,
				186, 145, 175, 176, 1707, 1708, 1709, 133, 153, 173, 177, 180, 181, 189, 1700, 1701, 1349 };// 定义一个数组
																											// 1、获取数组长度
		int len = arr.length;// 获取数组长度给变量len
		// 2、根据数组长度，使用Random随机数组的索引值
		Random random = new Random();// 创建随机对象
		int arrIdx = random.nextInt(len - 1);// 随机数组索引，nextInt(len-1)表示随机整数[0,(len-1)]之间的值
		// 3、根据随机索引获取数组值
		int startNum = arr[arrIdx];// 获取数组值
		mobile.append(startNum + "");
		for (int i = 0; i < 11 - (startNum + "").length(); i++) {
			mobile.append(random.nextInt(9) + "");
		}
		return mobile.toString();
	}

	public static String getValidataCode() {
		StringBuffer code = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			code.append(new Random().nextInt(9));
		}
		return code.toString();
	}
}
