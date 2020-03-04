package cn.sanenen.fx.service;

import cn.hutool.core.thread.ThreadUtil;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.connect.manager.EndpointConnector;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.session.cmpp.SessionStateManager;
import io.netty.channel.Channel;

import java.util.Random;

/**
 * 发送服务类 2
 *
 * @author sun
 * 2020年3月4日 17:26:34
 */
public class ConvertService {

    public static void sendSms(String mobile, String content, String id) {

        CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
        msg.setDestterminalId(mobile);
        msg.setMsgContent(content);
        msg.setRegisteredDelivery((short) 1);
        EndpointConnector<?> connector = EndpointManager.INS.getEndpointConnector(id);
        while (true) {
	        Channel channel = getChannel(connector);
            if (channel != null) {
                msg.setAttachment(System.currentTimeMillis());
	            channel.writeAndFlush(msg);
                break;
            } else {
                ThreadUtil.sleep(10);
            }
        }
    }

    private static Channel getChannel(EndpointConnector<?> connector) {
        Channel ch = connector.fetch();
        if (ch == null) {
            return null;
        }
        SessionStateManager ssm = (SessionStateManager) ch.pipeline().get("sessionStateManager");
        int waitingResp = ssm.getWaittingResp();
        //如果服务端响应慢导致 大量响应未匹配 停止发送
        int windowSize = connector.getEndpointEntity().getWriteLimit() / 2;
        if (waitingResp > (Math.max(windowSize, 320))) {
			return null;
        }
        if (ch.isActive() && ch.isWritable()) {
            return ch;
        }
        return null;
    }

    public static String getMobile() {
        StringBuilder mobile = new StringBuilder();
        int[] arr = {
                135, 136, 137, 138, 139, 147, 150, 151, 152, 154, 157, 158, 159, 182, 183, 184, 187, 188, 172, 178,
                1703, 1705, 1706, 1340, 1341, 1342, 1343, 1344, 1345, 1346, 1347, 1348, 130, 131, 132, 155, 156, 185,
                186, 145, 175, 176, 1707, 1708, 1709, 133, 153, 173, 177, 180, 181, 189, 1700, 1701, 1349};
        int len = arr.length;
        Random random = new Random();
        int arrIdx = random.nextInt(len - 1);
        int startNum = arr[arrIdx];
        mobile.append(startNum + "");
        for (int i = 0; i < 11 - (startNum + "").length(); i++) {
            mobile.append(random.nextInt(9) + "");
        }
        return mobile.toString();
    }
}
