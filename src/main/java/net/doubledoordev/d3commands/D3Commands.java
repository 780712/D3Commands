package net.doubledoordev.d3commands;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.doubledoordev.d3commands.commands.*;
import net.doubledoordev.d3commands.event.PlayerDeathEventHandler;
import net.doubledoordev.d3commands.util.Location;
import net.doubledoordev.d3core.util.ID3Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

import static net.doubledoordev.d3commands.util.Constants.MODID;
import static net.doubledoordev.d3commands.util.Constants.NAME;

@Mod(modid = MODID, name = NAME, canBeDeactivated = false, acceptableRemoteVersions = "*")
public class D3Commands implements ID3Mod
{
    @Mod.Instance(MODID)
    public static D3Commands instance;

    private List<CommandEntry> commands = new ArrayList<>();

    private PlayerDeathEventHandler pdEventHandler = new PlayerDeathEventHandler();

    public Map<UUID, Location> deathlog = new HashMap<>();

    public Configuration configuration;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
        MinecraftForge.EVENT_BUS.register(pdEventHandler);
        //FMLCommonHandler.instance().bus().register(pdEventHandler); Do I need this?
    }

    @Override
    public void syncConfig()
    {
        configuration.setCategoryLanguageKey(MODID, "d3.cmd.config.cmd");
        configuration.addCustomCategoryComment(MODID, "Set any value to false to disable the command.");
        configuration.setCategoryRequiresWorldRestart(MODID, true);

        commands.add(new CommandEntry(new CommandTps(), configuration.getBoolean("tps", MODID, true, "A TPS command for all players, not just ops.")));
        commands.add(new CommandEntry(new CommandMem(), configuration.getBoolean("mem", MODID, true, "Shows server memory information.")));
        commands.add(new CommandEntry(new CommandTpx(), configuration.getBoolean("tpx", MODID, true, "Interdimensional TP command.")));
        commands.add(new CommandEntry(new CommandTop(), configuration.getBoolean("top", MODID, true, "Teleport yourself to the highest block above you.")));
        commands.add(new CommandEntry(new CommandKill(), configuration.getBoolean("kill", MODID, true, "Allow you to kill other players.")));
        commands.add(new CommandEntry(new CommandHeal(), configuration.getBoolean("heal", MODID, true, "Heal yourself or other players.")));
        commands.add(new CommandEntry(new CommandFeed(), configuration.getBoolean("feed", MODID, true, "Feed yourself or other players.")));
        commands.add(new CommandEntry(new CommandGetUUID(), configuration.getBoolean("getuuid", MODID, true, "Allows easy UUID grabbing.")));
        commands.add(new CommandEntry(new CommandFly(), configuration.getBoolean("fly", MODID, true, "Toggle fly mode.")));
        commands.add(new CommandEntry(new CommandGod(), configuration.getBoolean("god", MODID, true, "Toggle god mode.")));
        commands.add(new CommandEntry(new CommandBack(), configuration.getBoolean("back", MODID, true, "Teleport back to where you died the last time.")));
        commands.add(new CommandEntry(new CommandGm(), configuration.getBoolean("gm", MODID, true, "Shorter /gamemode command.")));
        commands.add(new CommandEntry(new CommandInvSee(), configuration.getBoolean("invsee", MODID, true, "Look at someone else's inventory")));
        commands.add(new CommandEntry(new CommandSpawn(), configuration.getBoolean("spawn", MODID, true, "Teleport to spawn")));

        if (Loader.isModLoaded("RandomThings"))
        {
            commands.add(new CommandEntry(new CommandKey(), configuration.getBoolean("key", MODID, true, "Give a RandomThings SpectreKey")));
        }

        if (configuration.hasChanged()) configuration.save();
    }

    @Override
    public void addConfigElements(List<IConfigElement> configElements)
    {
        configElements.add(new ConfigElement(configuration.getCategory(MODID.toLowerCase())));
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        for (CommandEntry e : commands)
        {
            if (e.isEnabled())
            {
                event.registerServerCommand(e.getCommand());
            }
        }
    }
}

