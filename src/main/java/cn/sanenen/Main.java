package cn.sanenen;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.log.Log;
import cn.sanenen.handler.MessageReceiveHandler;
import cn.sanenen.service.AtomicUtil;
import cn.sanenen.service.ConvertService;
import cn.sanenen.service.Param;
import com.zx.sms.connect.manager.EndpointConnector;
import com.zx.sms.connect.manager.EndpointEntity.SupportLongMessage;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;
import com.zx.sms.handler.api.BusinessHandlerInterface;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    private static final Log log = Log.get();
    private static final EndpointManager manager = EndpointManager.INS;
    private static long sendLastNum;
    private static long responseLastNum;
    private static boolean isSend = false;
    private static boolean sendComplete = false;

    private static long count = 0;
    private static long sendCount = 0;
    private static long responseCount = 0;
    
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        CronUtil.setMatchSecond(true);
        CronUtil.schedule("0/1 * * * * ? ", (Runnable) () -> {
            if (!isSend || sendComplete) {
                return;
            }
            count++;
            long nowcnt = AtomicUtil.sendCount.get();
            long send = nowcnt - sendLastNum;
            sendCount += send;
            sendLastNum = nowcnt;
            long nowcnt2 = AtomicUtil.reponseCount.get();
            long reponse = nowcnt2 - responseLastNum;
            responseCount += reponse;
            responseLastNum = nowcnt2;
            log.info("发送速度:{}/s,响应速度:{}/s", send, reponse);
        });
        CronUtil.schedule("0/5 * * * * ? ", (Runnable) () -> {
            EndpointConnector<?> connector = manager.getEndpointConnector(Param.getUsername());
            if (connector == null || connector.fetch() == null) {
                log.info("无连接");
                manager.close();
                connect();
            } else {
                if (isSend) {
                    return;
                }
                log.info("==============发送开始==============");
                send();
            }
        });
        CronUtil.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            manager.close();
            CronUtil.stop();
            System.out.println("停止。。");
        }));
    }

    private static void connect() {
        CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
        client.setId(Param.getUsername());
        client.setHost(Param.getIp());
        client.setPort(Integer.parseInt(Param.getPort()));
        client.setUserName(Param.getUsername());
        client.setPassword(Param.getPassword());
        client.setServiceId(Param.getServiceid());
        client.setSpCode(Param.getSpcode());
        client.setMaxChannels(Short.parseShort(Param.getMaxConnect()));
        client.setVersion((short) 0x20);
        client.setWriteLimit(Integer.parseInt(Param.getSpeed()));
        client.setGroupName("test");
        client.setChartset(StandardCharsets.UTF_8);
        client.setRetryWaitTimeSec((short) 30);
        client.setUseSSL(false);
        client.setMaxRetryCnt((short) 0);
        client.setReSendFailMsg(false);
        client.setSupportLongmsg(SupportLongMessage.BOTH);
        List<BusinessHandlerInterface> clienthandlers = new ArrayList<>();
        clienthandlers.add(new MessageReceiveHandler());
        // clienthandlers.add( new SessionConnectedHandler());
        client.setBusinessHandlerSet(clienthandlers);
        manager.addEndpointEntity(client);
        for (int i = 0; i < client.getMaxChannels(); i++) {
            manager.openEndpoint(client);
        }
    }

    private static void send() {
        isSend = true;
        String contStr = Param.getContent();
        String mobile = Param.getMobile();
        String id = Param.getUsername();
        boolean randomMobile = Param.getRandomMobile();
        try {
            int manyCount = Integer.parseInt(Param.getSendCount());
            new Thread(() -> {
                long start = System.currentTimeMillis();

                for (int i = 0; i < manyCount; i++) {
                    if (randomMobile) {// 随机手机号
                        ConvertService.sendSms(ConvertService.getMobile(), contStr, id);
                        AtomicUtil.sendCount.incrementAndGet();
                    } else {
                        String[] destMobile = mobile.split("[,，]");
                        for (String tmpmobile : destMobile) {
                            if (StrUtil.isNotBlank(tmpmobile)) {
                                ConvertService.sendSms(tmpmobile, contStr, id);
                                AtomicUtil.sendCount.incrementAndGet();
                            }
                        }
                    }
                }
                long hs = (System.currentTimeMillis() - start) / 1000;
                sendComplete = true;
                count = (count == 0 ? 1 : count);
                log.info("发送完成:平均发送速度：{}/s，平均响应速度：{}/s", sendCount / count, responseCount / count);
                log.info("总时间计算:发送速度：{}/s 开始时间：{}，结束时间：{}"
                        , AtomicUtil.sendCount.get() / (hs == 0 ? 1 : hs)
                        , DateUtil.formatDateTime(new Date(start))
                        , DateUtil.now()
                );
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
