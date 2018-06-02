package net.felibouilleYTB;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.colored.SColoredButton;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LauncherPanel extends JPanel implements SwingerEventListener {
    private Image background = Swinger.getResource("background.png");
    private Saver saver = new Saver(new File(Launcher.LC_DIR, "launcher.properties"));
    private JTextField usernameField = new JTextField(this.saver.get("username"));
    private JPasswordField passwordField = new JPasswordField();
    private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png"));
    private STexturedButton closeButton = new STexturedButton(Swinger.getResource("close.png"));
    private STexturedButton hideButton = new STexturedButton(Swinger.getResource("hide.png"));
    private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Color.CYAN);
    private JLabel infoLabel = new JLabel("Login and Click on Play", SwingConstants.CENTER);
    private RamSelector ramSelector = new RamSelector(new File(Launcher.LC_DIR, "ram.txt"));
    private SColoredButton ramButton = new SColoredButton(Swinger.getTransparentWhite(100),Swinger.getTransparentWhite(175));
    private JLabel ramText = new JLabel("Select the allocated ram for Minecraft");


    public LauncherPanel() {
        this.setLayout(null);
        usernameField.setFont(usernameField.getFont().deriveFont(20F));
        usernameField.setBounds(131, 226, 191, 33);
        usernameField.setOpaque(false);
        usernameField.setBorder(null);
        this.add(usernameField);
        passwordField.setFont(passwordField.getFont().deriveFont(20F));
        passwordField.setBounds(134, 369, 190, 33);
        passwordField.setOpaque(false);
        passwordField.setBorder(null);
        this.add(passwordField);

        closeButton.setBounds(880, 7);
        closeButton.addEventListener(this);
        this.add(closeButton);

        hideButton.setBounds(790, 7);
        hideButton.addEventListener(this);
        this.add(hideButton);

        playButton.setBounds(154, 448);
        playButton.addEventListener(this);
        this.add(playButton);

        progressBar.setBounds(0, 608 , 974, 624 );
        this.add(progressBar);

        infoLabel.setBounds(320, 490 , 400, 196);
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setFont(usernameField.getFont());
        this.add(infoLabel);

        this.ramButton.setBounds(114, 424, 400, 24);
        this.ramButton.addEventListener(this);
        this.add(ramButton);

        ramText.setBounds(114, 424, 400, 24);
        ramText.setHorizontalAlignment(SwingConstants.CENTER);
        ramText.setFont(usernameField.getFont());
        this.add(ramText);
    }
    public void onEvent(SwingerEvent e) {
        if (e.getSource() == playButton) {
            setFieldsEnabled(false);
            if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0){
                JOptionPane.showMessageDialog(this ,"Please Enter a valid username/email and a valid password", "Error", JOptionPane.ERROR_MESSAGE);
                setFieldsEnabled(true);
                return;
            }
        Thread t = new Thread(){
                public void run(){
                    try {
                        Launcher.auth(usernameField.getText(), passwordField.getText());
                    }catch (AuthenticationException e) {
                        JOptionPane.showMessageDialog(LauncherPanel.this, "Please enter a correct username and/ or password(" + e.getErrorModel().getErrorMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
                        setFieldsEnabled(true);
                        try {
                            Launcher.launch();
                        }catch (Exception e1){}

                        return;
                    }
                    saver.set("username", usernameField.getText());
                    try {
                        Launcher.update();
                    }catch (Exception e) {
                        JOptionPane.showMessageDialog(LauncherPanel.this, "Update Failed(" + e + ")", "Error", JOptionPane.ERROR_MESSAGE);
                        Launcher.interuptThread();
                        setFieldsEnabled(true);

                    }
                    try {
                        Launcher.launch();
                    }catch (LaunchException e) {
                        JOptionPane.showMessageDialog(LauncherPanel.this, "Launch Failed(" + e + ")", "Error", JOptionPane.ERROR_MESSAGE);
                        Launcher.interuptThread();
                        setFieldsEnabled(true);

                    }

                }
            };
            t.start();

        } else if (e.getSource() == closeButton)
            System.exit(0);
        else if (e.getSource() == ramButton)
            ramSelector.display();

                }
    public void paintComponent (Graphics g){
        super.paintComponent(g);
        g.drawImage(background, 0,0, this.getWidth(), this.getHeight(), this);



    }
    private void setFieldsEnabled(boolean enabled){
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        playButton.setEnabled(enabled);
    }
    public SColoredBar getProgressBar(){
        return progressBar;
    }
    public void setInfoText(String text){
        infoLabel.setText(text);
    }
    public RamSelector getRamSelector(){return ramSelector;
    }

}
