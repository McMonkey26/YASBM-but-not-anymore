package com.pew.yetanotherskyblockmod.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pew.yetanotherskyblockmod.YASBM;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.math.Color;

@Config(name=YASBM.MODID)
// @Config.Gui.CategoryBackground(category = "general", background = "")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();
    public static class General {
        @ConfigEntry.Gui.Tooltip
        public boolean betterMusicEnabled = true;
        @ConfigEntry.Gui.Tooltip
        public boolean reforgeStopEnabled = true;
        @ConfigEntry.Gui.Tooltip
        public boolean wailaEnabled = true;

        @ConfigEntry.Gui.CollapsibleObject
        public FishingUtilsConfig fishingUtils = new FishingUtilsConfig();
        public static class FishingUtilsConfig {
            @ConfigEntry.Gui.Tooltip
            public boolean warnWhenClose = true;
            @ConfigEntry.Gui.Tooltip
            public boolean showOthers = false;

            public Color myLineColor = Color.ofRGBA(255, 255, 255, 255);
            public Color myParticleColor = Color.ofRGBA(255, 255, 255, 255);
        }
    }

    @ConfigEntry.Category("tools")
    @ConfigEntry.Gui.TransitiveObject
    public Tools tools = new Tools();
    public static class Tools {
        @ConfigEntry.Gui.Tooltip
        public Map<String,Integer> filter = new HashMap<>();
        /*
         * Bitfields for filter:
         *  - Censor: 1 << 0 (Allows messages to pass through)
         *  - Ignore: 1 << 1
         *  - Party Leave: 2 << 0
         *  - Party Report:2 << 1
         */

        @ConfigEntry.Gui.Excluded
        public List<String> ignored = new ArrayList<>();

        @ConfigEntry.Gui.Tooltip
        public Map<String,String> aliases = new HashMap<>();
        
        @ConfigEntry.Gui.Tooltip
        public Map<String,Integer> clean = new HashMap<>();
        
        @ConfigEntry.Gui.Tooltip
        public Map<String,String> emojis = new HashMap<String, String>() {{
            put("hi|hello|wave", "ヾ(＾∇＾)");
            put("bye|cya", "(^-^)/");
            put("fp|sigh|ugh|facepalm", "( ¬_ლ)");
            put("shrug", "¯\\_(ツ)_/¯");
            put("nice|yay|+1", "(b＾▽＾)b");
            put("sad|cry|sob", "|(╥_╥)\\");
            put("huh|hmm|confused", "Σ(-᷅_-᷄๑)?");
            put("mad|angry|grr", "p(╬ಠ益ಠ)/");
            put("cool|rad|shades", "(▀̿Ĺ̯▀̿ ̿)");
            put("bruh", "╭( ๐ _๐)╮");
            put("lenny", "( ͡° ͜ʖ ͡°)");
            put("blush|uwu", "≧◡≦");
            put("amogus|amongus|sus", "ඞ");
            put("yep|check", "✔");
            put("type|writing", "✎...");
            put("spell|magic", "('-')⊃━☆ﾟ.*･｡ﾟ");
            put("party", "ヽ(^◇^*)/");
        }};

        @ConfigEntry.Gui.CollapsibleObject
        public AFK afk = new AFK();

        public static class AFK {
            @ConfigEntry.Gui.Tooltip
            public List<String> reasons = new ArrayList<>() {{
                add("I'm afk!");
            }};

            @ConfigEntry.Gui.Tooltip
            public boolean joinLeave = true;

            @ConfigEntry.Gui.Tooltip
            public boolean guildReply= false;
            
            @ConfigEntry.Gui.Tooltip
            public String webhook = "";
        }
    }
    
    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.TransitiveObject
    public HUD hud = new HUD();
    public static class HUD {
        public boolean hideWitherbornEnabled = true;
        public boolean betterTablistEnabled = true;

        @ConfigEntry.Gui.CollapsibleObject
        public BetterMenus betterMenus = new BetterMenus();
        public static class BetterMenus {
            public boolean generalEnabled    = true;
            public boolean tradeMenuEnabled  = true;
            public boolean auctionMenuEnabled= true;
            public boolean bazaarMenuEnabled = true;
            public boolean storageMenuEnabled= true;
        }

        // custom bars, farming overlay, xp tracker
    }

    @ConfigEntry.Category("item")
    @ConfigEntry.Gui.TransitiveObject
    public Item item = new Item();
    public static class Item {
        @ConfigEntry.Gui.Tooltip
        public boolean itemLockEnabled = true;
        public boolean customizationEnabled = false;

        @ConfigEntry.Gui.CollapsibleObject
        public SBTooltip sbTooltip = new SBTooltip();
        public static class SBTooltip {
            public static enum ConfigState {
                OFF, KEYBIND, ON
            }
            public ConfigState missingEnchants   = ConfigState.OFF;
            public ConfigState stackingEnchants  = ConfigState.OFF;
            public ConfigState marketPrices      = ConfigState.OFF;
            public ConfigState npcPrices         = ConfigState.OFF;
            public ConfigState itemAge           = ConfigState.OFF;
            public ConfigState sbItemId          = ConfigState.OFF;
        }

        @ConfigEntry.Gui.Excluded
        public List<String> lockedUUIDs = new ArrayList<>();
    }

    @ConfigEntry.Category("dungeons")
    @ConfigEntry.Gui.TransitiveObject
    public Dungeons dungeons = new Dungeons();
    public static class Dungeons {
        // todo
    }

    public static void init() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}