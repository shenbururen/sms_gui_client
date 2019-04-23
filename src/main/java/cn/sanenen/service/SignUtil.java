package cn.sanenen.service;

/**
 * 签名工具类
 * @author sun
 * 2019年2月13日 下午3:47:48
 */
public class SignUtil {

	 private final static int SMS_LEN = 70;// 普通短信长度
     private final static int LONG_SMS_LEN = 67;// 汉字短信长短信分条长度
     private final static int BYTE_LEN = 140;// 纯字节短信长度
     private final static int BYTE_LONG = 134;// 纯字节短信长短信分条长度

     public static int spliteMsg(String msg) {
        int messageLen = msg.length();
        int charLen = msg.getBytes().length;
        int count = 1;
        if (messageLen != charLen) {
            // 说明包含中文字符
            if (messageLen > SMS_LEN) {
                count = messageLen / LONG_SMS_LEN;
                if (messageLen % LONG_SMS_LEN > 0)
                    count = count + 1;
            }
        } else {
            // 说明不包含中文字符
            if (charLen > BYTE_LEN) {
                count = charLen / BYTE_LONG;
                if (charLen % BYTE_LONG > 0)
                    count = count + 1;
            }
        }
        return count;
    }
    
}
