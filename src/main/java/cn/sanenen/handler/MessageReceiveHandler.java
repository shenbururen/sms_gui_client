package cn.sanenen.handler;

import cn.hutool.log.Log;
import cn.sanenen.service.AtomicUtil;
import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
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
            CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
            responseMessage.setResult(0);
            ctx.channel().writeAndFlush(responseMessage);
            log.info("发送：{},响应：{},提交失败：{},提交成功：{},状态：{}"
                    , AtomicUtil.sendCount.get()
                    , AtomicUtil.reponseCount.get()
                    , AtomicUtil.failCount.get()
                    , AtomicUtil.sucCount.get()
                    , AtomicUtil.reportCount.incrementAndGet()
            );
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
            long result = e.getResult();
            long suc = 0;
            long fail = 0;
            if (result == 0) {
                suc = AtomicUtil.sucCount.incrementAndGet();
            } else {
                fail = AtomicUtil.failCount.incrementAndGet();
            }
            log.info("发送：{},响应：{},提交失败：{},提交成功：{},状态：{}"
                    , AtomicUtil.sendCount.get()
                    , AtomicUtil.reponseCount.incrementAndGet()
                    , fail
                    , suc
                    , AtomicUtil.reportCount.get()
            );
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
