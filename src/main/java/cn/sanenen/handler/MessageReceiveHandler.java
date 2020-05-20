package cn.sanenen.handler;

import cn.hutool.log.Log;
import cn.sanenen.service.AtomicUtil;
import com.zx.sms.codec.cmpp.msg.*;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class MessageReceiveHandler extends AbstractBusinessHandler {
    private static final Log log = Log.get();

    @Override
    public String name() {
        return "MessageReceiveHandler-smsBiz";
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // System.out.println(msg);
        if (msg instanceof CmppDeliverRequestMessage) {
            CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) msg;
            if (e.isReport()) {
                AtomicUtil.reportCount.incrementAndGet();
//                CmppReportRequestMessage report = e.getReportRequestMessage();
//                CmppSubmitRequestMessage request = CacheMap.map.remove(report.getMsgId().toString());
//                if (request != null){
//                    long sendTime = (long) request.getAttachment();
//                    log.warn("report delay:{}", System.currentTimeMillis() - sendTime);
//                }
            } else {
                log.error(e.toString());
            }
            CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
            responseMessage.setMsgId(e.getMsgId());
            responseMessage.setResult(0);
            ctx.channel().writeAndFlush(responseMessage);
            // cnt.incrementAndGet();

        } else if (msg instanceof CmppDeliverResponseMessage) {
//			CmppDeliverResponseMessage e = (CmppDeliverResponseMessage) msg;

        } else if (msg instanceof CmppSubmitRequestMessage) {
            CmppSubmitRequestMessage e = (CmppSubmitRequestMessage) msg;
            CmppSubmitResponseMessage resp = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());
            // resp.setResult(RandomUtils.nextInt()%1000 <10 ? 8 : 0);
            resp.setResult(0);
            ctx.channel().writeAndFlush(resp);
        } else if (msg instanceof CmppSubmitResponseMessage) {
            CmppSubmitResponseMessage e = (CmppSubmitResponseMessage) msg;
            CmppSubmitRequestMessage request = (CmppSubmitRequestMessage) e.getRequest();
            long sendTime = (long) request.getAttachment();
            long l = System.currentTimeMillis();
//            log.warn("submitRequest delay:{}", l - sendTime);
//            request.setAttachment(l);
//            CacheMap.map.put(e.getMsgId().toString(), request);
            long result = e.getResult();
            if (result == 0) {
                AtomicUtil.sucCount.incrementAndGet();
            } else {
                AtomicUtil.failCount.incrementAndGet();
            }
            AtomicUtil.reponseCount.incrementAndGet();
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public MessageReceiveHandler clone() throws CloneNotSupportedException {
        MessageReceiveHandler ret = (MessageReceiveHandler) super.clone();
        return ret;
    }

}
