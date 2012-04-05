# -*- coding:utf-8 -*-
"""
Created on 2011-8-17

@author: Administrator
"""
import socket,time
from PyQt4.QtCore import *
from PyQt4.QtGui import *


class Worker(QThread):
    def __init__(self,parent=None):
        QThread.__init__(self,parent)
        self.exiting = False
        self.str = "send str start" 
        self.curtime = None
    def __del__(self):
        self.exiting = True
        self.wait()
    def render(self,str):
        self.str = str
        
        self.start()
    def run(self):
        #线程执行
        print "thread"
        self.curtime = time.time()
        while(True):
            if time.time()- self.curtime > 10:
                self.curtime = time.time()
                print "send.." + str(self.curtime)
                tmp = str(self.curtime)+"\n\r"
                sendData.send(tmp)
                #sendData.send('weixin\n')

WORDLength = 1024

sRecvCMD  = socket.socket(socket.AF_INET,socket.SOCK_STREAM)            #接受指令套接字
sSendData = socket.socket(socket.AF_INET,socket.SOCK_STREAM)             #发送传感器数据套接字

host = socket.gethostname()
portRecvCMD  = 9999            #接受指令端口
portSendData = 10000        #发送传感器数据端口

#接受按钮指令
sRecvCMD.bind((host,9999))
sSendData.bind((host,portSendData))
sRecvCMD.listen(10000)              #最大监听数量：可以供页面反复重启
sSendData.listen(10000)            

while True: 
    recvCMD,ipRecv  = sRecvCMD.accept()
    sendData,ipSend = sSendData.accept()
    print '接收IP地址:'+ str(ipRecv) +' 客户端连接'
    print '接收数据，指令状态机显示',recvCMD.recv(WORDLength)
 
    print '接收IP地址:'+str(ipSend)+' 客户端连接'
    sendData.send('tianwei\n')
    print '接收数据，发送数据状态机显示',sendData.recv(WORDLength)

    thread = Worker()
    thread.render("tianwei")

    flag = True
    cnt = 0
    while flag == True: 
            strCMD = recvCMD.recv(WORDLength)
            print strCMD
            if "-EOF-Activity" in  strCMD:
                print "此次连接断开"
                flag = False            #退出标志
            if len(strCMD) == 0:
                cnt += 1
            if cnt > 3:
                print "程序重启"
                flag = False
            
             
                
                

            
    



        

        
        
    