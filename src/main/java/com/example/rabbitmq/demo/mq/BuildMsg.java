package com.example.rabbitmq.demo.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;

import java.io.File;

/**
 *
 *description 
 *
 *version 0.1
 *createDate 2019/8/15 17:48
 *updateDate 2019/8/15 17:48
 *@author wangshaoqi
 */
@Slf4j
public class BuildMsg {
    private AmqpTemplate amqpTemplate;
    private String queue;
    private String msgPrefix;

    public BuildMsg( AmqpTemplate amqpTemplate,String queue,String msgPrefix) {
        this.amqpTemplate = amqpTemplate;
        this.queue=queue;
        this.msgPrefix=msgPrefix;
    }

    private int num;
    public  void getAllFiles(File file,String f){
        if (!file.isDirectory()){
            num++;
            String absolutePath = file.getAbsolutePath();
            String msg;
            if(msgPrefix==null) {
                msg = f + ":" + absolutePath;
            }else {
                msg = msgPrefix + ":" + absolutePath;
            }
            amqpTemplate.convertAndSend(queue,msg);
            log.info("发送第{}条文件路径信息：{}",num,msg);
        }
        File[] files = file.listFiles();
        if (files==null){
            return;
        }
        for (File file1 : files) {
            String fileName;
            if (f==null) {
                fileName = file1.getName();
            }else {
                fileName=f;
            }
            getAllFiles(file1,fileName);
        }
    }
}
