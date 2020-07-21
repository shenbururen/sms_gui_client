package cn.sanenen.fx.handler;

import cn.sanenen.fx.controller.ConnectController;
import cn.sanenen.fx.controller.SendController;
import cn.sanenen.fx.service.AtomicUtil;
import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import com.zx.sms.session.cmpp.SessionState;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Sharable
@Component
public class MessageReceiveHandler extends AbstractBusinessHandler {
    @Resource
    private ConnectController connectController;
    @Resource
    private SendController sendController;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == SessionState.Connect) {
            connectController.getConnect().setDisable(true);
            sendController.getSend().setDisable(false);
            sendController.setCanSend(true);
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        connectController.getConnect().setDisable(false);
        sendController.getSend().setDisable(true);
        sendController.setCanSend(false);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        connectController.getConnect().setDisable(false);
        sendController.getSend().setDisable(true);
        sendController.setCanSend(false);
    }

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
            responseMessage.setMsgId(e.getMsgId());
            responseMessage.setResult(0);
            ctx.channel().writeAndFlush(responseMessage);
            AtomicUtil.reportCount.incrementAndGet();
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
            long attachment = (long) request.getAttachment();
            delayCount(System.currentTimeMillis() - attachment);
            long result = e.getResult();
            AtomicUtil.responseCount.incrementAndGet();
            if (result == 0) {
                AtomicUtil.sucCount.incrementAndGet();
            } else {
                AtomicUtil.failCount.incrementAndGet();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void delayCount(long delay) {
        if (delay < 1000) {
            return;
        }
        if (delay < 3000) {
            AtomicUtil._1sCount.incrementAndGet();
        } else if (delay < 10000) {
            AtomicUtil._3sCount.incrementAndGet();
        } else if (delay < 30000) {
            AtomicUtil._10sCount.incrementAndGet();
        } else {
            AtomicUtil._30sCount.incrementAndGet();
        }
    }

    @Override
    public MessageReceiveHandler clone() throws CloneNotSupportedException {
        MessageReceiveHandler ret = (MessageReceiveHandler) super.clone();
        return ret;
    }

}
