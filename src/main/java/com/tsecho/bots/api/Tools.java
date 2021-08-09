package com.tsecho.bots.api;

import com.tsecho.bots.model.bill.ClientService;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Tools {

    public static String getSalutations() {
        return getSalutations(new Date());
    }

    public static String getSalutations(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if ((hour >= 22 && hour <= 24) || (hour >= 0 && hour <= 4)) {
            return "Доброй ночи, ";
        } else if (hour >= 5 && hour <= 10) {
            return "Доброе утро, ";
        } else if (hour >= 11 && hour <= 17) {
            return "Добрый день, ";
        } else {
            return "Добрый вечер, ";
        }
    }

    public static InputFile createImage(List<ClientService> list) throws IOException {

        Graphics2D g2d = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB).createGraphics();
        int firstColWidth = g2d.getFontMetrics().stringWidth(" Договор ");
        int secondColWidth = g2d.getFontMetrics().stringWidth(" Услуга ");
        int thirdColumnWidth = g2d.getFontMetrics().stringWidth(" Заблокирована ");
        int charHeight = g2d.getFontMetrics().getHeight();
        Font f = g2d.getFont();

        int width = firstColWidth+secondColWidth+thirdColumnWidth+4;
        int height = charHeight*2*(list.size()+1);

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
         g2d = bufferedImage.createGraphics();
        // fill all the image with white
        g2d.setColor(Color.white);
        g2d.fillRoundRect(0, 0, width, height,10,10);
        g2d.setColor(Color.black);
        g2d.drawRoundRect(0,0,width,height,10,10);
        g2d.drawLine(firstColWidth+1,0,firstColWidth+1,height); // 1 col
        g2d.drawLine(firstColWidth+secondColWidth+1,0,firstColWidth+secondColWidth+1,height); // 2 & 3 col
        g2d.drawString("Договор", 4, 22);
        g2d.drawString("Услуга", firstColWidth+5, 22);
        g2d.drawString("Статус", firstColWidth+secondColWidth+6, 22);
        int i = 1;
        for (ClientService cs : list){
            g2d.drawLine(0,charHeight*2*i,width,charHeight*2*i); // row

            i +=1;
            g2d.drawString(cs.getNumber(), 4, charHeight*2*i-12);
            g2d.drawString(cs.getId().toString(), firstColWidth+5, charHeight*2*i-12);
            if (cs.getBlocked() == 0) {
                g2d.setColor(Color.green);
            } else {
                g2d.setColor(Color.red);
            }
            g2d.drawString(statusToString(cs.getBlocked()), firstColWidth+secondColWidth+6, charHeight*2*i-12);
            g2d.setColor(Color.black);
        }

        // Disposes of this graphics context and releases any system resources that it is using.
        g2d.dispose();
        File file = new File("query.png");
        ImageIO.write(bufferedImage, "png", file);

        return new InputFile(file);
    }

    private static String statusToString(int status) {
        String result;
        switch (status) { //Текущее состояние блокировки:
            case 0: // 0-уч. запись активна,
                result = "Активна";
                break;
            case 1, 4: // 1-заблокирована по балансу, 4-по балансу(активная блокировка),
                result = "Заблокирована по балансу";
                break;
            case 2:  // 2-пользователем,
                result = "Отключена пользователем";
                break;
            default: // 3-администратором, 10-уч. запись отключена
                result = "Отключена оператором";
        }
        return result;
    }

}
