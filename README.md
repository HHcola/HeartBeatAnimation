# HeartBeatAnimation
 
android 心跳动画实现，使用方便简单。 

![Circle animation](https://github.com/HHcola/HeartBeatAnimation/blob/master/dots.gif "Circle animation")

 
使用方法：

        HeartBeatAnimation
                .with(view)
                .scaleFrom(1.0f)
                .scaleTo(1.3f)
                .in(100)
                .after(1200)
                .start();
