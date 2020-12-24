package tudbut.mod.client.yac;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import tudbut.mod.client.yac.events.FMLEventHandler;
import tudbut.mod.client.yac.mods.*;
import tudbut.mod.client.yac.utils.FileRW;
import tudbut.mod.client.yac.utils.Module;
import tudbut.mod.client.yac.utils.ThreadManager;
import tudbut.mod.client.yac.utils.Utils;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

@Mod(modid = YAC.MODID, name = YAC.NAME, version = YAC.VERSION)
public class YAC {
    public static final String MODID = "yac";
    public static final String NAME = "YAC Client";
    public static final String VERSION = "vB1.0.0a";
    
    public static Module[] modules ;
    public static EntityPlayerSP player;
    public static Minecraft mc = Minecraft.getMinecraft();
    public static FileRW file;
    public static Map<String, String> cfg;
    public static String prefix = ",";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        try {
            file = new FileRW("config/yac.cfg");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("YAC by TudbuT");
        ThreadManager.run(() -> {
            JOptionPane.showMessageDialog(null, "YAC by TudbuT");
        });
        player = Minecraft.getMinecraft().player;
        try {
            cfg = Utils.stringToMap(file.getContent());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        modules = new Module[] {
                new AutoTotem(),
                new TPAParty(),
                new SafeTotem(),
                new Prefix(),
                new Team(),
                new TPATools(),
                new ChatSuffix(),
                new AutoConfig(),
                new ChatColor(),
                new Trap(),
                new LeavePos(),
                new ClickGUI(),
        };
    
        for (Module module : modules) {
            module.cfg = Utils.stringToMap(module.saveConfig());
        }
        
        MinecraftForge.EVENT_BUS.register(new FMLEventHandler());
        
        for (int i = 0; i < modules.length; i++) {
            try {
                logger.info(modules[i].toString());
                modules[i].saveConfig();
                if (cfg.containsKey(modules[i].toString())) {
                    modules[i].loadConfig(Utils.stringToMap(cfg.get(modules[i].getClass().getSimpleName())));
                }
            } catch (Exception e) {
                logger.warn("Couldn't load config of module " + modules[i].toString() + "!");
                logger.warn(e);
            }
        }
        prefix = cfg.getOrDefault("prefix", ",");
    
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        ThreadManager.run(() -> {
            while (true) {
                try {
                    try {
                        for (int i = 0; i < modules.length; i++) {
                            cfg.put(modules[i].getClass().getSimpleName(), modules[i].saveConfig());
                        }
                        cfg.put("prefix", prefix);
        
                        file.setContent(Utils.mapToString(cfg));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
