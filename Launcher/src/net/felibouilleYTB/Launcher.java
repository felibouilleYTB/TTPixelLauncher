package net.felibouilleYTB;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.internal.InternalLaunchProfile;
import fr.theshark34.openlauncherlib.internal.InternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

import java.io.File;
import java.util.Arrays;

public class Launcher {
    public static final GameVersion LC_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
    public static final GameInfos LC_INFOS = new GameInfos("ttpixel" , LC_VERSION, new GameTweak[] {GameTweak.FORGE});
    public static final File LC_DIR = LC_INFOS.getGameDir();


    private static AuthInfos authInfos;
    private static Thread t;
    public static void auth(String username, String password) throws AuthenticationException {
        Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
        AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
        authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());

    }
    public static void update() throws Exception {
        SUpdate su = new SUpdate("http://ttpixel.livehost.fr/update", LC_DIR);
        su.addApplication(new FileDeleter());
        t = new Thread() {
            private int val;
            private int max;
            public void run() {
                while(!this.isInterrupted()){
                    if(BarAPI.getNumberOfFileToDownload() == 0){
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verifying the files");
                        continue;
                    }
                    val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);
                    LauncherFrame.getInstance().getLauncherPanel().setInfoText("Downloading Files (" +
                    BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " + Swinger.percentage(val, max) + " % )");


                }
            }
        };
        t.start();

        su.start();
        t.interrupt();
    }
    public static void interuptThread() {
        t.interrupt();

    }
    public static void launch() throws LaunchException {

        ExternalLaunchProfile profile = new MinecraftLauncher().createExternalProfile(LC_INFOS, GameFolder.BASIC, authInfos);
        profile.getVmArgs().addAll(Arrays.asList(new String[] { "-Dforge.forceNoStencil=true" }));
        ExternalLauncher launcher = new ExternalLauncher(profile);
        profile.getVmArgs().addAll(Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
        LauncherFrame.getInstance().setVisible(false);
        launcher.launch();
        LauncherFrame.getInstance().setVisible(false);


    }
}
