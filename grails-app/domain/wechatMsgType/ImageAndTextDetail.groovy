package wechatMsgType

class ImageAndTextDetail {

    static constraints = {
    }

    //图文消息标题  
    String title;
    //图文消息描述  
    String description;
    //图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200  
    String picUrl;
    //点击图文消息跳转链接  
    String url;

}