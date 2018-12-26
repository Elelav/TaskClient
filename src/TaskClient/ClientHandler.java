/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TaskClient;

import java.io.IOException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingWorker;
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
    
    /**
     * Конструктор создающий новый объект. Устанавливает видимость окна ClientFrame в положение true
     * @see ClientHandler#ClientHandler()
     */
    public ClientHandler(){       
        cf.setVisible(true);        
    }
     
    /**
     * Переопределяемый метод вызывается про активации канала.
     * Оповещает сервер о подключении и отправляет серверу команды на добавление или удаление задач.
     * @param ctx экземпляр интерфейса ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){        
        
        class CreateButtonTask extends SwingWorker<Void, Void> {
        
        boolean nameCheck(){
            if(cf.getTextName().length()<=15){
                return true;
            } else{
                cf.nameError();
                return false;
            }
        }
        boolean dateCheck(){
            Pattern pattern = Pattern.compile("\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\ \\d\\d\\:\\d\\d");      
            Matcher m = pattern.matcher(cf.getTextDate());
            if(m.find()){
                return true;
            }else{
                cf.dateError();
                return false;
            }
        }
        
        @Override
        public Void doInBackground() {           
 
            if(nameCheck()){
                StringBuffer sb = new StringBuffer();
                sb.append("New,");
                sb.append(cf.getTextName()).append(",");
                sb.append(cf.getTextDesc()).append(",");
                sb.append(cf.getTextDate()).append(",");
                sb.append(cf.getTextContacts());
                cf.message = sb.toString();
            }
            ctx.writeAndFlush(Unpooled.copiedBuffer(cf.constructMessage(), CharsetUtil.UTF_8));
            cf.clearFrame();          
            return null;
             
        }
        @Override
        public void done() {
            cf.setEnabled(true);           
        }
    }
        class CreateMouseListener implements MouseListener{
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
            public void mouseClicked(MouseEvent e) {        
            }
            public void mousePressed(MouseEvent e) {
                cf.setEnabled(false);
                CreateButtonTask task = new CreateButtonTask();
                task.execute();
            }
            public void mouseReleased(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        }       
        
        class ExitButtonMouseListener implements MouseListener{
            public void mouseClicked(MouseEvent e) {        
            }
            public void mousePressed(MouseEvent e) {
                ctx.writeAndFlush(Unpooled.copiedBuffer("Done", CharsetUtil.UTF_8));
                System.exit(0);
            }
            public void mouseReleased(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        } 
        
        class DeleteMouseListener implements MouseListener{
            public void mouseClicked(MouseEvent e) {        
            }
            public void mousePressed(MouseEvent e) {
               String s = cf.deleteTask();
               System.out.println("Sending to delete from server: "+s);
               ctx.writeAndFlush(Unpooled.copiedBuffer("Delete,"+s, CharsetUtil.UTF_8));
            }
            public void mouseReleased(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        }
        
        
        cf.addButton1Listener(new ExitButtonMouseListener());
        cf.addCreateListener(new CreateMouseListener());
        cf.addDeleteListener(new DeleteMouseListener());
        ctx.writeAndFlush(Unpooled.copiedBuffer("Connected", CharsetUtil.UTF_8));       
    }
    
    /**
     * Переопределенный метод вызывается при получении нового сообщения.
     * Открывает окно оповещателя {@link AlertJFrame#AlertJFrame()}
     * @param ctx экземпляр интерфейса ChannelHandlerContext
     * @param msg полученное сообщение
     * @throws Exception
     */
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

    /**
     * Метод отлавливает выброшенные исключения
     * @param ctx экземпляр интерфейса ChannelHandlerContext
     * @param cause пойманное исключение
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }    
    
}
