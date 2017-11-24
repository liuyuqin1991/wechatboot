package wechatboot

import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader

import javax.annotation.Resource
import java.security.MessageDigest

class MainPortController {

    @Resource
    private IntelligentRobotService intelligentRobotService;

    //输入自己的公众号token
    private static final TOKEN = "your wechat key";


    /**
     * 微信后台主入口，所有的通过微信后台服务器转接到系统的方法全部由此入口登录
     * @author liuyuqin
     * @since 2015年6月4日17:38:34
     * @return
     */
    public String mainPort() {
        //验证服务器地址有效性
        if (request.getMethod().equals("GET") || request.getMethod().equals("get")) {
            boolean result = checkSignature(params.signature, params.timestamp, params.nonce, TOKEN);
            if (result)
                render params.echostr;
        }
        //具体业务主入口
        else {
            try {
                String replyString = null;
                InputStream inputStream = request.getInputStream();
                // 创建saxReader对象
                SAXReader reader = new SAXReader();
                Document document = reader.read(inputStream);
                Element element = document.getRootElement();
                List<Element> list = element.elements();
                Map<String, Object> map = new HashMap<String, Object>();
                for (Element e : list) {
                    map.put(e.getName(), e.getText());
                }
                //接入各种接口
                if (map.get("MsgType") != null && map.get("MsgType").equals("text")) {
                    if(map.get("Content").toString().contains("today")){
                        replyString = intelligentRobotService.index(map);
                    }
                    else{
                        replyString = intelligentRobotService.index(map);
                    }
                }
                render replyString;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /* 检查请求是否合法
    * @param signature
    * @param timestamp
    * @param nonce
    * @param token
    * @return
    * @throws Exception
    */

    private boolean checkSignature(String signature, String timestamp, String nonce, String token) throws Exception {
        String[] tmpArr = new String[3];
        tmpArr[0] = token;
        tmpArr[1] = timestamp;
        tmpArr[2] = nonce;
        Arrays.sort(tmpArr);//对字符串数组 排序
        String tmpStr = getArrayStr(tmpArr);
        String sha1 = makeSHA1(tmpStr);
        if (signature.equals(sha1))
            return true;
        return false;
    }

    /**
     * 获得字符串数组的字符串形式
     * @param array
     * @return
     */
    private String getArrayStr(String[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * 对字符串进行SHA1加密
     * @param tmpStr
     * @return
     * @throws Exception
     */
    private String makeSHA1(String tmpStr) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");//获取MessageDigest实例
        md.update(tmpStr.getBytes());
        byte[] tmpArr = md.digest();
        return byteToHex(tmpArr);
    }

    /**
     * 将二进制数据转化为十六进制数据串
     * @param arr
     * @return
     */
    private String byteToHex(byte[] arr) {
        String hs = "";
        String tmp = "";
        for (int i = 0; i < arr.length; i++) {
            tmp = Integer.toHexString(arr[i] & 0xFF);
            if (tmp.length() < 2) {
                tmp = "0" + tmp;
            }
            hs = hs + tmp;
        }
        return hs;
    }
}