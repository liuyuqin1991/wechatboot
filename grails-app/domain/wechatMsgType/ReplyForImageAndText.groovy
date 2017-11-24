package wechatMsgType
/**
 * Created by liuyuqin on 2016/11/16.
 */
class ReplyForImageAndText {

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
    //图文消息个数，限制为10条以内
    Integer articleCount;
}
