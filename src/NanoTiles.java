import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

/**@author ndriqa*/

public class NanoTiles extends JFrame implements ActionListener {
    private String filePath = "No File Chosen Yet";
    JLabel label = new JLabel();
    JLabel done = new JLabel();
    BufferedImage generatedTiles;

    public NanoTiles() {
        this.getContentPane().setLayout(new FlowLayout());

        JButton chooseFileBtn = new JButton("Choose File");
        JButton generateTilesBtn = new JButton("Generate Tiles");
        add(label);
        label.setText(filePath);

        generateTilesBtn.addActionListener(this);
        chooseFileBtn.addActionListener(this);
        generateTilesBtn.setActionCommand("Generate");
        chooseFileBtn.setActionCommand("ChooseFile");

        add(chooseFileBtn);
        add(generateTilesBtn);
        add(done);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("Generate")) {
            if (filePath.equals("error") || filePath.equals("No File Chosen Yet")){
                popup("No valid file selected");
                done.setText("");
            } else {
                if (filePath.endsWith("png")){
                    boolean success = generateTiles();
                    if (success){
                        File outputfile = new File("tileSet_" + Math.abs(new Random().nextInt()) + ".png");
                        try {
                            ImageIO.write(generatedTiles, "png", outputfile);
                            done.setText("Generated Tiles Image Saved");
                        } catch (IOException e) {
                            popup("Sorry, couldn't save file");
                            e.printStackTrace();
                        }

                    } else {
                        popup("Error Generating Tiles");
                    }
                } else {
                    popup("Invalid Image Format");
                }
            }
        } else if (action.equals("ChooseFile")){
            JFileChooser fileChooser = new JFileChooser();
            int i = fileChooser.showOpenDialog(this);
            if (i == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                filePath = file.getPath();
            } else {
                filePath = "error";
            }
            label.setText(filePath);
        }
    }

    private boolean generateTiles(){
        boolean temp = true;
            try {
                BufferedImage nanoTiles = ImageIO.read(new File(filePath));

                int x = nanoTiles.getWidth();
                int y = nanoTiles.getHeight();
                int mode = BufferedImage.TYPE_INT_ARGB;

                int one = 0;
                int two = 1;
                int three = 2;

                BufferedImage a = nanoTiles.getSubimage(0, 0, x/3, y);
                BufferedImage b = nanoTiles.getSubimage(x/3, 0, x/3, y);
                BufferedImage c = nanoTiles.getSubimage(2*x/3, 0, x/3, y);
                BufferedImage v = vertical(a, b);
                BufferedImage h = horizontal(a, b);
                BufferedImage o = emptyImage(a);

                generatedTiles = new BufferedImage(x*4, y*4, mode);

                phaseOne(one, generatedTiles, a, b, v, h);
                phaseTwo(two, generatedTiles, a, b, c, v, h);
                phaseThree(three, generatedTiles, a, b, c, v, h, o);

                showImage(generatedTiles);
            } catch (Exception e){
                temp = false;
                e.printStackTrace();
            }
        return temp;
    }

    private void phaseOne(int phase, BufferedImage dest, BufferedImage a, BufferedImage b, BufferedImage v, BufferedImage h){
        BufferedImage[][] temp = new BufferedImage[][]{
                {tl(a), tr(a), tl(a), tr(h), tl(h), tr(h), tl(h), tr(a)},
                {bl(v), br(v), bl(v), br(b), bl(b), br(b), bl(b), br(v)},
                {tl(v), tr(v), tl(v), tr(b), tl(b), tr(b), tl(b), tr(v)},
                {bl(v), br(v), bl(v), br(b), bl(b), br(b), bl(b), br(v)},
                {tl(v), tr(v), tl(v), tr(b), tl(b), tr(b), tl(b), tr(v)},
                {bl(a), br(a), bl(a), br(h), bl(h), br(h), bl(h), br(a)},
                {tl(a), tr(a), tl(a), tr(h), tl(h), tr(h), tl(h), tr(a)},
                {bl(a), br(a), bl(a), br(h), bl(h), br(h), bl(h), br(a)}};


        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                BufferedImage x = temp[i][j];
                for (int k = 0; k < x.getHeight(); k++) {
                    for (int l = 0; l < x.getWidth(); l++) {
                        dest.setRGB(phase*dest.getWidth()/3 + j*x.getHeight() + l, i*x.getWidth() + k, x.getRGB(k, l));
                    }
                }
            }
        }
    }
    private void phaseTwo(int phase, BufferedImage dest, BufferedImage a, BufferedImage b, BufferedImage c, BufferedImage v, BufferedImage h){
        BufferedImage[][] temp = new BufferedImage[][]{
                {tl(c), tr(b), tl(h), tr(h), tl(h), tr(h), tl(b), tr(c)},
                {bl(b), br(b), bl(b), br(c), bl(c), br(b), bl(b), br(b)},
                {tl(v), tr(b), tl(b), tr(c), tl(c), tr(b), tl(b), tr(v)},
                {bl(v), br(c), bl(c), br(c), bl(c), br(c), bl(c), br(v)},
                {tl(v), tr(c), tl(c), tr(c), tl(c), tr(c), tl(c), tr(v)},
                {bl(v), br(b), bl(b), br(c), bl(c), br(b), bl(b), br(v)},
                {tl(b), tr(b), tl(b), tr(c), tl(c), tr(b), tl(b), tr(b)},
                {bl(c), br(b), bl(h), br(h), bl(h), br(h), bl(b), br(c)}};


        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                BufferedImage x = temp[i][j];
                for (int k = 0; k < x.getHeight(); k++) {
                    for (int l = 0; l < x.getWidth(); l++) {
                        dest.setRGB(phase*dest.getWidth()/3 + j*x.getHeight() + l, i*x.getWidth() + k, x.getRGB(k, l));
                    }
                }
            }
        }
    }
    private void phaseThree(int phase, BufferedImage dest, BufferedImage a, BufferedImage b, BufferedImage c, BufferedImage v, BufferedImage h, BufferedImage o){
        BufferedImage[][] temp = new BufferedImage[][]{
                {tl(a), tr(h), tl(b), tr(b), tl(h), tr(h), tl(h), tr(a)},
                {bl(v), br(c), bl(c), br(c), bl(c), br(c), bl(c), br(v)},
                {tl(v), tr(c), tl(b), tr(c), tl(o), tr(o), tl(c), tr(b)},
                {bl(v), br(c), bl(c), br(b), bl(o), br(o), bl(c), br(b)},
                {tl(b), tr(c), tl(c), tr(c), tl(c), tr(b), tl(c), tr(v)},
                {bl(b), br(c), bl(c), br(c), bl(b), br(c), bl(c), br(v)},
                {tl(v), tr(c), tl(c), tr(c), tl(c), tr(c), tl(c), tr(v)},
                {bl(a), br(h), bl(h), br(h), bl(b), br(b), bl(h), br(a)}};


        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                BufferedImage x = temp[i][j];
                for (int k = 0; k < x.getHeight(); k++) {
                    for (int l = 0; l < x.getWidth(); l++) {
                        dest.setRGB(phase*dest.getWidth()/3 + j*x.getHeight() + l, i*x.getWidth() + k, x.getRGB(k, l));
                    }
                }
            }
        }
    }

    private BufferedImage tl(BufferedImage x){
        BufferedImage temp = new BufferedImage(x.getWidth()/2, x.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < x.getWidth() / 2; i++) {
            for (int j = 0; j < x.getHeight() / 2; j++) {
                temp.setRGB(i, j, x.getRGB(i, j));
            }
        }
        return temp;
    }
    private BufferedImage tr(BufferedImage x){
        BufferedImage temp = new BufferedImage(x.getWidth()/2, x.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < x.getWidth() / 2; i++) {
            for (int j = x.getHeight() / 2; j < x.getHeight(); j++) {
                temp.setRGB(i, j - x.getHeight() / 2, x.getRGB(i, j));
            }
        }
        return temp;
    }
    private BufferedImage bl(BufferedImage x){
        BufferedImage temp = new BufferedImage(x.getWidth()/2, x.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
        for (int i = x.getWidth() / 2; i < x.getWidth(); i++) {
            for (int j = 0; j < x.getHeight() / 2; j++) {
                temp.setRGB(i - x.getWidth() / 2, j, x.getRGB(i, j));
            }
        }
        return temp;
    }
    private BufferedImage br(BufferedImage x){
        BufferedImage temp = new BufferedImage(x.getWidth()/2, x.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
        for (int i = x.getWidth() / 2; i < x.getWidth(); i++) {
            for (int j = x.getHeight() / 2; j < x.getHeight(); j++) {
                temp.setRGB(i - x.getWidth() / 2, j - x.getHeight() / 2, x.getRGB(i, j));
            }
        }
        return temp;
    }


    private BufferedImage horizontal(BufferedImage a, BufferedImage b){
        BufferedImage temp = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < b.getWidth(); i++) {
            for (int j = 0; j < b.getHeight(); j++) {
                temp.setRGB(i, j, b.getRGB(i, j));
            }
        }
        for (int i = 0; i < a.getWidth(); i++) {
            for (int j = a.getHeight()/4; j < 3*a.getHeight()/4; j++) {
                temp.setRGB(i, j, a.getRGB(i, j));
            }
        }
        return temp;
    }
    private BufferedImage vertical(BufferedImage a, BufferedImage b){
        BufferedImage temp = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < b.getWidth(); i++) {
            for (int j = 0; j < b.getHeight(); j++) {
                temp.setRGB(i, j, b.getRGB(i, j));
            }
        }
        for (int i = a.getWidth()/4; i < 3*a.getWidth()/4; i++) {
            for (int j = 0; j < a.getHeight(); j++) {
                temp.setRGB(i, j, a.getRGB(i, j));
            }
        }
        return temp;
    }

    private void showImage(BufferedImage image){
        JFrame test = new JFrame();
        test.getContentPane().add(new JLabel(new ImageIcon(image)));
        test.pack();
        test.setVisible(true);
    }
    private BufferedImage emptyImage(BufferedImage a){
        BufferedImage temp = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < temp.getWidth(); i++) {
            for (int j = 0; j < temp.getHeight(); j++) {
                temp.setRGB(i, j, new Color(Color.TRANSLUCENT).getRGB());
            }
        }
        return temp;
    }
    private void popup(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    private static void createAndShowGUI() {
        JFrame frame = new NanoTiles();
        frame.setSize(500, 150);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(NanoTiles::createAndShowGUI);
    }

}