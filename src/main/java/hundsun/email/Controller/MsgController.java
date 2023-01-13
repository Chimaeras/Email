package hundsun.email.Controller;

import hundsun.email.Server.MsgServerImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Joker
 */
@RestController
@RequestMapping("/Msg")
public class MsgController {

    @Resource
    private MsgServerImpl msgServer;


    @RequestMapping(value = "/receiveMsg", method = RequestMethod.POST)
    public String receiveMsg(HttpServletRequest request) throws IOException {
//        MultipartHttpServletRequest params=((MultipartHttpServletRequest) request);

        Map<String,String[]> params= request.getParameterMap();
        System.out.println(params);
        Enumeration<String> strings =request.getParameterNames();
        while(strings.hasMoreElements()){
            System.out.println(Arrays.toString(params.get(strings.nextElement())));
        }
        return msgServer.receiveMsg();
    }
}
