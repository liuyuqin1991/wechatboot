package wechatboot

import org.grails.web.json.JSONObject
import wechatMsgType.ImageAndTextDetail
import wechatMsgType.ReplyForImageAndText
import wechatMsgType.ReplyForText
import wechatMsgType.ReplyForUrl

/**
 * Created by liuyuqin on 2016/11/18.
 */
class IntelligentRobotService {

    //图灵机器人开发key
    private static final APIKEY = "your tuling key";

    def index(Map map){
        boolean listFlag = false;
        boolean urlFlag = false;
        boolean trainFlag = false;
        boolean planeFlag = false;
        boolean newsFlag = false;
        boolean eatFlag = false;
        StringBuffer replyString = new StringBuffer("<xml>\n");
        //打通图灵机器人
        String massageInfo = URLEncoder.encode(map.get("Content"), "utf-8");
        String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + massageInfo;
        URL getUrl = new URL(getURL);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.connect();

        // 取得输入流，并使用Reader读取
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        //第一层JSON解析对象
        JSONObject dataJson = new JSONObject(sb.toString());
        //第二层JSON解析对象
        JSONObject listJson
        Iterator it = dataJson.keys();
        //遍历最外层JSON
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            String value = (String) dataJson.get(key);
            //当获取到的key是list，即返回结果为新闻，列车，航班，菜谱
            if (key.equals("list")) {
                listFlag = true;
                String a = value.substring(1, value.length() - 1);
                listJson = new JSONObject(value.substring(1, value.length() - 1));
                Iterator listIt = listJson.keys();
                //遍历list中的JSON
                while (it.hasNext()) {
                    String listKey = String.valueOf(listIt.next());
                    //当获取的list中的key是trainnum时，即返回结果为列车
                    if (listKey.equals("trainnum")) {
                        trainFlag = true;
                        break;
                    }
                    if (listKey.equals("flight")) {
                        planeFlag = true;
                        break;
                    }
                    if (listKey.equals("article")) {
                        newsFlag = true;
                        break;
                    }
                    if (listKey.equals("name")) {
                        eatFlag = true;
                        break;
                    }
                }
            }
            //当获取到的key是url，即返回结果为链接时
            if (key.equals("url")) {
                urlFlag = true;
            }
        }
        ReplyForImageAndText replyForImageAndText = new ReplyForImageAndText();
        replyForImageAndText.toUserName = map.get("FromUserName");
        replyForImageAndText.fromUserName = map.get("ToUserName");
        replyForImageAndText.msgType = map.get("MsgType");
        replyForImageAndText.articleCount = 1;
        //如果返回结果为列车
        if (listFlag && trainFlag) {
            ImageAndTextDetail imageAndTextDetail = new ImageAndTextDetail();
            imageAndTextDetail.description = "车次：" + listJson.get("trainnum") + "; 起始站：" +
                    listJson.get("start") + "; 终点站：" + listJson.get("terminal") + "; 开车时间：" +
                    listJson.get("starttime") + "; 到达时间：" + listJson.get("endtime");
            imageAndTextDetail.picUrl = listJson.get("icon").toString();
            imageAndTextDetail.url = listJson.get("detailurl").toString();
            replyString.append("<ToUserName><![CDATA[" + replyForImageAndText.toUserName + "]]></ToUserName>\n");
            replyString.append("<FromUserName><![CDATA[" + replyForImageAndText.fromUserName + "]]></FromUserName>\n");
            replyString.append("<CreateTime>" + replyForImageAndText.createTime + "</CreateTime>\n");
            replyString.append("<MsgType><![CDATA[news]]></MsgType>\n");
            replyString.append("<ArticleCount>" + replyForImageAndText.articleCount + "</ArticleCount>\n");
            replyString.append("<Articles>\n");
            replyString.append("<item>\n");
            replyString.append("<Title><![CDATA[" + dataJson.get("text") + "]]></Title>\n");
            replyString.append("<Description><![CDATA[" + imageAndTextDetail.description + "]]></Description>\n");
            replyString.append("<PicUrl><![CDATA[" + imageAndTextDetail.picUrl + "]]></PicUrl>\n");
            replyString.append("<Url><![CDATA[" + imageAndTextDetail.url + "]]></Url>\n");
            replyString.append("</item>\n");
            replyString.append("</Articles>\n");
            replyString.append("</xml>");
        }
        //如果返回结果为航班
        else if (listFlag && planeFlag) {
            ImageAndTextDetail imageAndTextDetail = new ImageAndTextDetail();
            imageAndTextDetail.description = "航班次：" + listJson.get("flight") + "; 航班路线：" +
                    listJson.get("route") + "; 起飞时间：" +
                    listJson.get("starttime") + "; 到达时间：" + listJson.get("endtime") + "; 航班状态：" +
                    listJson.get("state");
            imageAndTextDetail.picUrl = listJson.get("icon").toString();
            imageAndTextDetail.url = listJson.get("detailurl").toString();
            replyString.append("<ToUserName><![CDATA[" + replyForImageAndText.toUserName + "]]></ToUserName>\n");
            replyString.append("<FromUserName><![CDATA[" + replyForImageAndText.fromUserName + "]]></FromUserName>\n");
            replyString.append("<CreateTime>" + replyForImageAndText.createTime + "</CreateTime>\n");
            replyString.append("<MsgType><![CDATA[news]]></MsgType>\n");
            replyString.append("<ArticleCount>" + replyForImageAndText.articleCount + "</ArticleCount>\n");
            replyString.append("<Articles>\n");
            replyString.append("<item>\n");
            replyString.append("<Title><![CDATA[" + dataJson.get("text") + "]]></Title>\n");
            replyString.append("<Description><![CDATA[" + imageAndTextDetail.description + "]]></Description>\n");
            replyString.append("<PicUrl><![CDATA[" + imageAndTextDetail.picUrl + "]]></PicUrl>\n");
            replyString.append("<Url><![CDATA[" + imageAndTextDetail.url + "]]></Url>\n");
            replyString.append("</item>\n");
            replyString.append("</Articles>\n");
            replyString.append("</xml>");
        }
        //如果返回结果为新闻
        else if (listFlag && newsFlag) {
            ImageAndTextDetail imageAndTextDetail = new ImageAndTextDetail();
            imageAndTextDetail.description = "标题：" + listJson.get("article") + "; 来源：" +
                    listJson.get("source");
            imageAndTextDetail.picUrl = listJson.get("icon").toString();
            imageAndTextDetail.url = listJson.get("detailurl").toString();
            replyString.append("<ToUserName><![CDATA[" + replyForImageAndText.toUserName + "]]></ToUserName>\n");
            replyString.append("<FromUserName><![CDATA[" + replyForImageAndText.fromUserName + "]]></FromUserName>\n");
            replyString.append("<CreateTime>" + replyForImageAndText.createTime + "</CreateTime>\n");
            replyString.append("<MsgType><![CDATA[news]]></MsgType>\n");
            replyString.append("<ArticleCount>" + replyForImageAndText.articleCount + "</ArticleCount>\n");
            replyString.append("<Articles>\n");
            replyString.append("<item>\n");
            replyString.append("<Title><![CDATA[" + dataJson.get("text") + "]]></Title>\n");
            replyString.append("<Description><![CDATA[" + imageAndTextDetail.description + "]]></Description>\n");
            replyString.append("<PicUrl><![CDATA[" + imageAndTextDetail.picUrl + "]]></PicUrl>\n");
            replyString.append("<Url><![CDATA[" + imageAndTextDetail.url + "]]></Url>\n");
            replyString.append("</item>\n");
            replyString.append("</Articles>\n");
            replyString.append("</xml>");
        }
        //如果返回结果为菜谱
        else if (listFlag && eatFlag) {
            ImageAndTextDetail imageAndTextDetail = new ImageAndTextDetail();
            imageAndTextDetail.description = "菜名：" + listJson.get("name") + "; 详情：" +
                    listJson.get("info");
            imageAndTextDetail.picUrl = listJson.get("icon").toString();
            imageAndTextDetail.url = listJson.get("detailurl").toString();
            replyString.append("<ToUserName><![CDATA[" + replyForImageAndText.toUserName + "]]></ToUserName>\n");
            replyString.append("<FromUserName><![CDATA[" + replyForImageAndText.fromUserName + "]]></FromUserName>\n");
            replyString.append("<CreateTime>" + replyForImageAndText.createTime + "</CreateTime>\n");
            replyString.append("<MsgType><![CDATA[news]]></MsgType>\n");
            replyString.append("<ArticleCount>" + replyForImageAndText.articleCount + "</ArticleCount>\n");
            replyString.append("<Articles>\n");
            replyString.append("<item>\n");
            replyString.append("<Title><![CDATA[" + dataJson.get("text") + "]]></Title>\n");
            replyString.append("<Description><![CDATA[" + imageAndTextDetail.description + "]]></Description>\n");
            replyString.append("<PicUrl><![CDATA[" + imageAndTextDetail.picUrl + "]]></PicUrl>\n");
            replyString.append("<Url><![CDATA[" + imageAndTextDetail.url + "]]></Url>\n");
            replyString.append("</item>\n");
            replyString.append("</Articles>\n");
            replyString.append("</xml>");
        }
        //如果返回结果为链接
        else if (urlFlag) {
            ReplyForUrl replyForUrl = new ReplyForUrl();
            replyForUrl.toUserName = map.get("FromUserName");
            replyForUrl.fromUserName = map.get("ToUserName");
            replyForUrl.msgType = map.get("MsgType");
            replyForUrl.msgId = BigInteger.valueOf(Long.valueOf(map.get("MsgId")));
            replyForUrl.contentUrl = dataJson.get("text") + ",点击查询";
            replyString.append("<ToUserName><![CDATA[" + replyForUrl.toUserName + "]]></ToUserName>\n");
            replyString.append("<FromUserName><![CDATA[" + replyForUrl.fromUserName + "]]></FromUserName>\n");
            replyString.append("<CreateTime>" + replyForUrl.createTime + "</CreateTime>\n");
            replyString.append("<MsgType><![CDATA[" + replyForUrl.msgType + "]]></MsgType>\n");
            replyString.append("<Content><![CDATA[<a href = '" + dataJson.get("url") + "'>" + replyForUrl.contentUrl + "</a>]]></Content>\n");
            replyString.append("</xml>");
        } else {
            ReplyForText replyForText = new ReplyForText();
            replyForText.toUserName = map.get("FromUserName");
            replyForText.fromUserName = map.get("ToUserName");
            replyForText.msgType = map.get("MsgType");
            replyForText.msgId = BigInteger.valueOf(Long.valueOf(map.get("MsgId")));
            replyForText.createTime = 15529131405;
            replyForText.content = dataJson.get("text");
            replyString.append("<ToUserName><![CDATA[" + replyForText.toUserName + "]]></ToUserName>\n");
            replyString.append("<FromUserName><![CDATA[" + replyForText.fromUserName + "]]></FromUserName>\n");
            replyString.append("<CreateTime>" + replyForText.createTime + "</CreateTime>\n");
            replyString.append("<MsgType><![CDATA[" + replyForText.msgType + "]]></MsgType>\n");
            replyString.append("<Content><![CDATA[" + replyForText.content + "]]></Content>\n");
            replyString.append("</xml>");
        }
        return replyString.toString();
    }
}
