package fr.triinoxys.partybungee.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import fr.triinoxys.partybungee.Main;


public class UpdaterV2{
    
    private static Main plugin;
    private static GHRepository repo;
    
    private static String name;
    private static String download_adress;
    private static String update_path;
    
    public UpdaterV2(Main instance){
        plugin = instance;
        
        name = plugin.getDescription().getName();
        
        try{
            repo = GitHub.connectAnonymously().getUser("TriiNoxYs").getRepository(name);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public String checkUpdate(boolean showInfos) throws IOException{
        name = plugin.getDescription().getName();
        String currVer = plugin.getDescription().getVersion();
        
        if(repo != null){
            String lastVer = repo.listReleases().iterator().next().getTagName().replaceAll("v", "");
            
            if(showInfos){
                if(compareVersions(currVer, lastVer) == true){
                    for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
                        if(p.hasPermission(name.toLowerCase() + ".update") || p.hasPermission(name.toLowerCase() + ".*") || p.hasPermission("*")){
                            p.sendMessage(new TextComponent(""));
                            p.sendMessage(new TextComponent("§6§l" + name + " §8§l>>> §a§lNew version available !"));
                            p.sendMessage(new TextComponent("§6§l" + name + " §8§l>>> §a§lCurrent: §c" + currVer));
                            p.sendMessage(new TextComponent("§6§l" + name + " §8§l>>> §a§lUpdate:  §6" + lastVer));
                            p.sendMessage(new TextComponent("§6§l" + name + " §8§l>>> §a§lType §6/" + name.toLowerCase() + " update§a§l to update !"));
                            p.sendMessage(new TextComponent(""));
                        }
                    } 
                }
            }
            return lastVer;
        }
        return currVer;
        
    }
    
    
    public void download(CommandSender sender) throws IOException, URISyntaxException{
        name = plugin.getDescription().getName();
        
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        
//        update_path = "plugins" + File.separator + repo.listReleases().iterator().next().getAssets().get(0).getName();
        update_path = "plugins" + File.separator + name + ".jar";
        download_adress = repo.listReleases().iterator().next().getAssets().get(0).getBrowserDownloadUrl();
        
        sender.sendMessage(new TextComponent("§8§lUpdating §6" + name + "§8..."));
        
        try{
            URL url = new URL (download_adress);
            out = new BufferedOutputStream(new FileOutputStream(update_path));
            conn = url.openConnection();
            in = conn.getInputStream();
            
            byte[] buffer = new byte[1024];
            int numRead;
            
            while((numRead = in.read(buffer)) != -1){
                out.write(buffer, 0, numRead);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if (in != null) in.close();
                if (out != null) out.close();
                sender.sendMessage(new TextComponent("§6§l" + name + "§a has been updated !"));
                sender.sendMessage(new TextComponent("§aYou can now restart the server."));
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
    
    
    public void updateCommand(CommandSender sender, String[] args) throws IOException, URISyntaxException{
        String lastVer = checkUpdate(false);
        String currVer = plugin.getDescription().getVersion();
        
        if((compareVersions(currVer, lastVer) == false) && ((args.length < 2) || (!args[1].equalsIgnoreCase("-force")))){
            if(sender.hasPermission(name.toLowerCase() + ".update") || sender.hasPermission(name.toLowerCase() + ".*") || sender.hasPermission("*")){
                sender.sendMessage(new TextComponent(" \n§a§l" + name + " is already updated."));
                sender.sendMessage(new TextComponent("§a§lCurrent version:§6 " + currVer));
                sender.sendMessage(new TextComponent("§a§lType §6/" + name.toLowerCase() + " update -force§a to force update !\n "));
            }
            else sender.sendMessage(new TextComponent("§cYou don't have permission to update the plugin."));
        }
        else download(sender);
    }
    
    
    private static boolean compareVersions(String currVer, String upVer){
        String[] currentSplits = currVer.split("\\.");
        String[] updateSplits = upVer.split("\\.");
        int count = currentSplits.length;
        
        for(int i = 0; i < count; i++){
            if(Integer.valueOf(updateSplits[i]) > Integer.valueOf(currentSplits[i])) return true;
            else if(Integer.valueOf(updateSplits[i]) < Integer.valueOf(currentSplits[i])) return false;
        }
        return false;
    }
    
}
