package wechatMsgType

class ReplyForText {

    static constraints = {
    }

    //发送至 的微信号
    String toUserName;
    //来自于 的微信号
    String fromUserName;
    //回复 创建时间
    BigInteger createTime;
    //消息类别：文字，图文，视频，音频，地理位置
    String msgType;
    //内容
    String content;
    //消息Id
    BigInteger msgId;
}
