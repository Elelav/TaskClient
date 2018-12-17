/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TaskClient;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
 *
 * import java.util.Date;
 * @author Nikita
 */
public class ClientHandler extends SimpleChannelInboundHandler <ByteBuf> {
    private final Charset charset = Charset.forName("UTF-8");
    boolean isAlerted = false;
    ClientFrame cf = new ClientFrame();
    Alert alarm = new Alert();
    
    public ClientHandler(){       
        cf.setVisible(true);
    }
     
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ctx.writeAndFlush(Unpooled.copiedBuffer("Connected", CharsetUtil.UTF_8)); 
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception{
        
        class LaterMouseListener implements MouseListener{      
            public void mouseClicked(MouseEvent e) {        
            }
            public void mousePressed(MouseEvent e) {
                ctx.writeAndFlush(Unpooled.copiedBuffer("Later", CharsetUtil.UTF_8));
                ctx.writeAndFlush(Unpooled.copiedBuffer("Ready", CharsetUtil.UTF_8));
                alarm.disposeFrame();
            }
            public void mouseReleased(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        }
         class CheckedMouseListener implements MouseListener{      
            public void mouseClicked(MouseEvent e) {        
            }
            public void mousePressed(MouseEvent e) {
                ctx.writeAndFlush(Unpooled.copiedBuffer("Ready", CharsetUtil.UTF_8));
                alarm.disposeFrame();
            }
            public void mouseReleased(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        }
            
            int length = msg.readInt();
            String name = msg.readCharSequence(length, charset).toString();
            length = msg.readInt();
            String description = msg.readCharSequence(length, charset).toString(); 
            cf.updateList(name, description);            
            alarm.addButtonListener(new LaterMouseListener());
            alarm.addButton2Listener(new CheckedMouseListener());
            try{          
                alarm.alarm(name,description);
                System.out.println("ALARM");              
            } catch(LineUnavailableException e){
                System.err.println("LineUnavailableException");
            } catch(UnsupportedAudioFileException e){
                System.err.println("UnsupportedAudioFileExc9eption"); //пока так
            } catch(MalformedURLException e){
                System.err.println("MalformedURLException");
            } catch(IOException ex){
                System.err.println("IOException");
            }

            
            
            
        
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }    
    
}
