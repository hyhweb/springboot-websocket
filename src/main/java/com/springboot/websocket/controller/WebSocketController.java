package com.springboot.websocket.controller;

import com.springboot.websocket.component.WebSocketServer;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Create by hyhweb on 2020/9/7 10:53
 */
@RestController
@RequestMapping("/api/ws")
public class WebSocketController {
    /**
     * 群发消息内容
     *
     * @param message
     * @return
     */
    @GetMapping("/sendAllMessage")
    public String sendAllMessage(@RequestParam(required = true) String message) throws IOException {
        WebSocketServer.broadCastInfo(message);
        return "ok";
    }
    /**
     * 指定会话ID发消息
     * @param message 消息内容
     * @param id 连接会话ID
     * @return
     */
    @GetMapping("/sendOneMessage")
    public String sendOneMessage(@RequestParam(required = true) String message,@RequestParam(required = true) String id) throws IOException{
        WebSocketServer.sendMessage(message, id);
        return "ok";
    }
}
