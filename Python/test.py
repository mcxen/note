from pynput.keyboard import Key, Controller
import time
keyboard = Controller()
# 需要安装pynput 
# pip3 install pynput

a=input('内容：')
b=eval(input('次数：'))
print ("收到，光标移动")
time.sleep(2)
for i in range(3):
    print (r'距离运行还有%d秒！'%(3-i))
    time.sleep(1)
for i in range(b):
    keyboard.type(a)
    keyboard.press(Key.enter)
    keyboard.release(Key.enter)
    time.sleep(0.1)