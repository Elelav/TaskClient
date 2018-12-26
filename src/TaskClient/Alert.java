/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TaskClient;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Nikita
 */
public class Alert {
    List<Date> alarmTimeList = new ArrayList<>();
    AlertJFrame ajf = new AlertJFrame();   


    
    class AlarmSound {        
        Clip clip;       
        
        void initSound() throws MalformedURLException,LineUnavailableException,
                UnsupportedAudioFileException,IOException, FileNotFoundException {
            File file = new File("a.wav");
            clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(file.toURI().toURL());
            clip.open(ais);
        }
        void play(){
            clip.start();
        }
    }     
     
    void alarm(String name, String description)
            throws MalformedURLException, LineUnavailableException, 
            UnsupportedAudioFileException, IOException, FileNotFoundException{       
        AlarmSound sound = new AlarmSound();        
        ajf.setName("Alarm");
        ajf.setTextName(name);
        ajf.setTextDesc(description);                     
        try{
            sound.initSound();
            sound.play();
        }catch(FileNotFoundException e){
            System.err.println("Sound file didn`t exist");
        };                
        ajf.setVisible(true);
    }

    void waitMinute(Date date) throws InterruptedException{
        Thread.sleep(60000);
    }

    /**
     * Метод добавления MouseListener на кнопку {@link AlertJFrame#button1}
     * @param ml - MouseListener
     * @see AlertJFrame#addButtonListener(java.awt.event.MouseListener) 
     */
    public void addButtonListener(MouseListener ml){
        ajf.addButtonListener(ml);
    }
    
    /**
     * Метод добавляет MouseListener на кнопку {@link AlertJFrame#button2}
     * @param ml
     * @see AlertJFrame#addButton2Listener(java.awt.event.MouseListener) 
     */
    public void addButton2Listener(MouseListener ml){
        ajf.addButton2Listener(ml);
    }

    /**
     * Метод скрытия окна {@link AlertJFrame}
     */
    public void disposeFrame(){
        ajf.dispose();
    }
    
}
