package org.example.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Method;
import java.util.Set;
import org.reflections.Reflections;
/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/1 19:38
 */
//服务端业务处理类
public class InvokeHandler extends ChannelInboundHandlerAdapter {
    //得到某个接口下某个实现类的名字
    private String getImplClassName(ClassInfo classInfo)throws Exception{
        //服务方接口和实现类所在的包路径
        String interfacePath = "org.example.rpc.producer";
        int lastDot = classInfo.getClassName().lastIndexOf(".");
        //接口名称
        String interfaceName = classInfo.getClassName().substring(lastDot);
        //接口字节码对象
        Class superClass = Class.forName(interfacePath+interfaceName);
        //反射得到某个接口下的所有实现类
        Reflections reflections = new Reflections(interfacePath);
        Set<Class> implClassSet = reflections.getSubTypesOf(superClass);
        if(implClassSet.size()==0){
            System.out.println("未找到实现类");
            return null;
        }else if(implClassSet.size()>1){
            /**
             * 思考一下，Spring是如何处理多个实现类的
             */
            return null;
        }else{
            //把集合转数组
            Class[] classes = implClassSet.toArray(new Class[0]);
            return classes[0].getName();//得到实现类名字
        }
    }

    /**
     * 读取客户端发来的数据并通过反射调用实现类方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClassInfo classInfo = (ClassInfo) msg;
        Object clazz = Class.forName(getImplClassName(classInfo)).getDeclaredConstructor().newInstance();
        Method method = clazz.getClass().getMethod(classInfo.getMethodName(),classInfo.getTypes());
        //通过反射调用实现类方法
        Object result = method.invoke(clazz, classInfo.getObjects());
        ctx.writeAndFlush(result);
    }
}
