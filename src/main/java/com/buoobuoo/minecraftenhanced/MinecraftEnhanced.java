package com.buoobuoo.minecraftenhanced;

import com.buoobuoo.minecraftenhanced.command.CommandManager;
import com.buoobuoo.minecraftenhanced.core.ability.AbilityManager;
import com.buoobuoo.minecraftenhanced.core.area.AreaManager;
import com.buoobuoo.minecraftenhanced.core.block.CustomBlockManager;
import com.buoobuoo.minecraftenhanced.core.chat.ChatManager;
import com.buoobuoo.minecraftenhanced.core.damage.DamageManager;
import com.buoobuoo.minecraftenhanced.core.entity.EntityManager;
import com.buoobuoo.minecraftenhanced.core.entity.impl.ItemDropEntity;
import com.buoobuoo.minecraftenhanced.core.entity.interf.CustomEntity;
import com.buoobuoo.minecraftenhanced.core.event.DatabaseConnectEvent;
import com.buoobuoo.minecraftenhanced.core.event.listener.*;
import com.buoobuoo.minecraftenhanced.core.event.listener.mechanic.*;
import com.buoobuoo.minecraftenhanced.core.event.update.UpdateSecondEvent;
import com.buoobuoo.minecraftenhanced.core.event.update.UpdateTickEvent;
import com.buoobuoo.minecraftenhanced.core.inventory.CustomInventoryManager;
import com.buoobuoo.minecraftenhanced.core.item.CustomItemManager;
import com.buoobuoo.minecraftenhanced.core.item.additional.attributes.ItemAttributeManager;
import com.buoobuoo.minecraftenhanced.core.navigation.RouteManager;
import com.buoobuoo.minecraftenhanced.core.party.PartyManager;
import com.buoobuoo.minecraftenhanced.core.player.PlayerManager;
import com.buoobuoo.minecraftenhanced.core.quest.QuestManager;
import com.buoobuoo.minecraftenhanced.core.vfx.cinematic.SpectatorManager;
import com.buoobuoo.minecraftenhanced.permission.PermissionManager;
import com.buoobuoo.minecraftenhanced.persistence.MongoHook;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@Getter
public class MinecraftEnhanced extends JavaPlugin implements Listener{

    public static final String MAIN_WORLD_NAME = "world";

    private MongoHook mongoHook;

    private ProtocolManager protocolManager;
    private CustomBlockManager customBlockManager;
    private CustomItemManager customItemManager;
    private CustomInventoryManager customInventoryManager;
    private SpectatorManager spectatorManager;
    private PartyManager partyManager;
    private QuestManager questManager;
    private PlayerManager playerManager;
    private PermissionManager permissionManager;
    private ItemAttributeManager itemAttributeManager;
    private EntityManager entityManager;
    private DamageManager damageManager;
    private AbilityManager abilityManager;
    private ChatManager chatManager;
    private AreaManager areaManager;
    private RouteManager routeManager;

    private CommandManager commandManager;

    // ;(
    @Getter
    private static MinecraftEnhanced instance;

    @Override
    public void onEnable(){
        initManagers();
        initListeners();
        initTimers();

        instance = this;
        for(World world : Bukkit.getWorlds()){
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
        }
    }

    @Override
    public void onDisable(){
        entityManager.destroyAll();
        playerManager.saveAll();
        mongoHook.disable();
    }

    public void initManagers(){
        mongoHook = new MongoHook(this);

        routeManager = new RouteManager(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        customBlockManager = new CustomBlockManager(this);
        customItemManager = new CustomItemManager(this);
        customInventoryManager = new CustomInventoryManager(this);
        spectatorManager = new SpectatorManager(this);
        partyManager = new PartyManager(this);
        questManager = new QuestManager(this);
        playerManager = new PlayerManager(this);
        permissionManager = new PermissionManager(this);
        itemAttributeManager = new ItemAttributeManager(this);
        entityManager = new EntityManager(this);
        damageManager = new DamageManager(this);
        abilityManager = new AbilityManager(this);
        chatManager = new ChatManager(this);
        areaManager = new AreaManager(this);

        commandManager = new CommandManager(this);

        questManager.init();
    }

    public void initListeners(){
        registerEvents(
                this,
                customBlockManager,
                customItemManager,
                customInventoryManager,
                spectatorManager,
                playerManager,
                entityManager,
                abilityManager,
                chatManager,
                areaManager,
                damageManager,

                //mechanic listeners
                new EntityRegainHealthEventListener(this),
                new EntitySpawnEventListener(this),
                new EntityDamageEventListener(this),
                new EntityCombustEventListener(this),

                new PlayerManagerItemListener(this),
                new PlayerCreativeInteractEventListener(this),
                new PlayerDeathEventListener(this),

                new ProjectileHitEventListener(this),
                new ItemRequirementEventListener(this),
                new BlockGrowEventListener(this),
                new NpcTeamTagListener(this),
                new MoistureChangeEventListener(this)

        );

        //packet listener
        new PlayerInteractNpcPacketListener(this);
        new AnvilRenamePacketListener(this);
    }

    public void initTimers(){
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new UpdateSecondEvent());
            }

        }.runTaskTimer(this, 0, 20);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new UpdateTickEvent());
            }

        }.runTaskTimer(this, 0, 1);

    }

    //TEMP
    @EventHandler
    public void itemDrop(PlayerDropItemEvent event){
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        if(!customItemManager.getRegistry().isCustomItem(itemStack))
            return;

        item.setGravity(false);
        item.setVelocity(new Vector(0, 0, 0));

        item.setCustomName(itemStack.getItemMeta().getDisplayName());
        item.setCustomNameVisible(true);
    }

    @EventHandler
    public void dbConnect(DatabaseConnectEvent event){
        for(Player player : Bukkit.getOnlinePlayers()){
            playerManager.getProfile(player);
        }
    }

    public void registerEvents(Listener... listeners){
        for(Listener listener : listeners){
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public static World getMainWorld(){
        return Bukkit.getWorld(MAIN_WORLD_NAME);
    }
}
