package fr.triinoxys.partybungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import com.google.common.io.ByteStreams;
import fr.triinoxys.partybungee.commands.PartyCmd;
import fr.triinoxys.partybungee.events.ConnectAndDisconnectEvents;
import fr.triinoxys.partybungee.utils.UpdaterV2;


public class Main extends Plugin{
	
	public static Configuration config;
	
	public Scoreboard sb;
    public Team admins;
    
    public static ProxyServer proxy;
    public static UpdaterV2 updater;
	
    @Override
	public void onEnable(){
		proxy = this.getProxy();
		
		
		//Commands
		proxy.getPluginManager().registerCommand(this, new PartyCmd());
		
		
		//Events
		proxy.getPluginManager().registerListener(this, new ConnectAndDisconnectEvents());
		
		
		//Updater
		try{
		    updater = new UpdaterV2(this);
		    updater.checkUpdate(true);
        }catch(IOException e1){
            e1.printStackTrace();
        }
		
		
		//Config
		if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
		
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()){
            try{
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                	OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            }catch (IOException e){
            	throw new RuntimeException("Impossible de cree le fichier de configuration.", e);
            }
        }
		
		try{
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onDisable(){
		try{
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
