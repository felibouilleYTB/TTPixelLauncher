package net.felibouilleYTB.bootstrap;

import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ClasspathConstructor;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.openlauncherlib.util.explorer.ExploredDirectory;
import fr.theshark34.openlauncherlib.util.explorer.Explorer;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;

import java.io.File;

public class TTPixelBootstrap {
    private static SplashScreen splashScreen;
    private static SColoredBar bar;

    private static File LC_DIR = GameDirGenerator.createGameDir("ttpixel");
    private static final File LC_B_DIR = new File(LC_DIR, "Launcher");
    private static Thread barThread;

    public static void main(String[] args) {
        DisplaySplash();
        try {
            doUpdate();
        } catch (Exception e) {
            System.exit(0);
        }
        try {
            LaunchLauncher();
        } catch (LaunchException e) {
        }

    }

    private static void DisplaySplash() {
        Swinger.setResourcePath("/net/felibouilleYTB/bootstrap/resources/");
        splashScreen = new SplashScreen("TTPixel Launcher", Swinger.getResource("splash.png"));
        bar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
        bar.setBounds(0, 120, 200, 24);
        splashScreen.add(bar);
        splashScreen.setLayout(null);
        splashScreen.setIconImage(Swinger.getResource("logo.png"));
        splashScreen.setVisible(true);

    }

    private static void doUpdate() throws Exception {
        SUpdate su = new SUpdate("http://ttpixel.livehost.fr/bootstrap", LC_B_DIR);
        barThread = new Thread() {
            public void run() {
                while (!this.isInterrupted()) {
                    bar.setValue((int) BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    bar.setMaximum((int) BarAPI.getNumberOfTotalBytesToDownload() / 1000);
                }
            }
        };
        barThread.start();
        su.start();
        barThread.interrupt();

    }

    private static void LaunchLauncher() throws LaunchException {
        ClasspathConstructor constructor = new ClasspathConstructor();
        ExploredDirectory gameDir = Explorer.dir(LC_B_DIR);

        constructor.add(gameDir.sub("Libs").allRecursive());
        constructor.add(gameDir.get("launcher.jar"));

        ExternalLaunchProfile profile = new ExternalLaunchProfile("net.felibouilleYTB.LauncherFrame", constructor.make());
        ExternalLauncher launcher = new ExternalLauncher(profile);
        Process p = launcher.launch();
        try {
            splashScreen.setVisible(false);
            p.waitFor();
        } catch (InterruptedException ignored) {
        }
        //System.exit(0);
    }
}
