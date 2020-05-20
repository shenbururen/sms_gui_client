package cn.sanenen.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.connect.manager.EndpointConnector;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.EndpointManager;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.Random;

/**
 * 发送服务类 2
 * @author sun
 * 2019年4月23日 下午2:25:34
 */
public class ConvertService {
	
	public static void sendSms(String mobile, String content, String id) {
		
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		msg.setDestterminalId(mobile);
		msg.setMsgContent(content);
		msg.setRegisteredDelivery((short) 1);
		EndpointEntity endpointEntity = EndpointManager.INS.getEndpointEntity(id);
		EndpointConnector<EndpointEntity> connector = endpointEntity.getSingletonConnector();
		while (true) {
			msg.setAttachment(System.currentTimeMillis());
			ChannelFuture write = connector.asynwrite(msg);
			if (write!=null) {
				break;
			}else {
				ThreadUtil.sleep(10);
			}
		}
	}

	public static String getMobile() {
		StringBuilder mobile = new StringBuilder();
		// var reg = /^(1[3|5|7|8][0-9]{9})|(1[4][7|5][0-9]{8})$/;
//		int arr[] = {
//				// 1390105,1342600,1350100,1360100,1370100,1380000
//				135, 136, 137, 138, 139, 147, 150, 151, 152, 154, 157, 158, 159, 182, 183, 184, 187, 188, 172, 178,
//				1703, 1705, 1706, 1340, 1341, 1342, 1343, 1344, 1345, 1346, 1347, 1348, 130, 131, 132, 155, 156, 185,
//				186, 145, 175, 176, 1707, 1708, 1709, 133, 153, 173, 177, 180, 181, 189, 1700, 1701, 1349 };// 定义一个数组
				
		ArrayList<String> arrl = CollUtil.newArrayList("1590", "1852", "1899");
		String startNum = RandomUtil.randomEle(arrl);
		Random random = new Random();
		mobile.append(startNum);
		for (int i = 0; i < 11 - startNum.length(); i++) {
			mobile.append(random.nextInt(9));
		}
		return mobile.toString();
	}

	public static String getValidataCode() {
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			code.append(new Random().nextInt(9));
		}
		return code.toString();
	}
}
