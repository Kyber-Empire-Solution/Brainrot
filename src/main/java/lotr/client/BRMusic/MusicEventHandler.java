package lotr.client.BRMusic;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lotr.common.util.Zone;
import lotr.common.util.ZoneLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MusicEventHandler {

    public static final MusicEventHandler INSTANCE = new MusicEventHandler();

    private List<ISound> playingSounds = new ArrayList<>();

    private final ArrayList<BRMusicTrack> menuMusic = new ArrayList<>();
    private final HashMap<ResourceLocation, BRMusicTrack> standardMusic = new HashMap<>();

    private BRMusicTrack currentTrack;

    public MusicEventHandler() {
        // Add your menu music
        menuMusic.add(new BRMusicTrack("lotr", "custom_menu1", 0, () -> true));
        menuMusic.add(new BRMusicTrack("lotr", "custom_menu2", 0, () -> true));
        //menuMusic.add(new BRMusicTrack("lotr", "custom_menu2", 0, () -> true));

        // Add your standard music
        standardMusic.put(new ResourceLocation("lotr", "combatmusic_1"), new BRMusicTrack("lotr", "combatmusic_1", 1, this::combatLogCheck));
        standardMusic.put(new ResourceLocation("lotr", "combatmusic_2"), new BRMusicTrack("lotr", "combatmusic_2", 1, this::combatLogCheck));
        standardMusic.put(new ResourceLocation("lotr", "combatmusic_3"), new BRMusicTrack("lotr", "combatmusic_3", 1, this::combatLogCheck));
        standardMusic.put(new ResourceLocation("lotr", "ambient_1"), new BRMusicTrack("lotr", "ambient_1", 0, () -> true));
        standardMusic.put(new ResourceLocation("lotr", "ambient_2"), new BRMusicTrack("lotr", "ambient_2", 0, () -> true));
        standardMusic.put(new ResourceLocation("lotr", "ambient_3"), new BRMusicTrack("lotr", "ambient_3", 0, () -> true));
        standardMusic.put(new ResourceLocation("lotr", "ambient_4"), new BRMusicTrack("lotr", "ambient_4", 0, () -> true));
        standardMusic.put(new ResourceLocation("lotr", "ambient_5"), new BRMusicTrack("lotr", "ambient_5", 0, () -> true));
//        standardMusic.put(new ResourceLocation("lotr", "calm_tavern_music"), new BRMusicTrack("lotr", "calm_tavern_music", 2, () -> calmZoneCheck(new Zone(-216, 96,464,-190,114,446))));
//        standardMusic.put(new ResourceLocation("lotr", "ambient_void"), new BRMusicTrack("lotr", "ambient_void", 2, () -> calmZoneCheck(new Zone(30000, 40,-5600,29750,105,-5729))));
//        standardMusic.put(new ResourceLocation("lotr", "dragon_boss_combat_music"), new BRMusicTrack("lotr", "dragon_boss_combat_music", 2, () -> combatLogZoneCheck(new Zone(-279,60,464,-305,102,434))));
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent17 event) {
        if (event.category == SoundCategory.MUSIC ) {

            if(!(event.sound instanceof BRMusicTrack)) {
                System.out.println(event.sound + " is cancelled");
                event.result = null;
            }
        }

    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.entity instanceof EntityPlayer && event.entity == mc.thePlayer) {
            List<ISound> soundsToStop = new ArrayList<>(playingSounds);
            for (ISound sound : soundsToStop) {
                stopMusic((BRMusicTrack) sound);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();


        BRMusicTrack hiTrack = getHighestPriorityTrack();

        if(currentTrack != null && mc.getSoundHandler().isSoundPlaying(currentTrack) && currentTrack.getPriority() < hiTrack.getPriority() ) {
            stopMusic(currentTrack);
            if(!mc.getSoundHandler().isSoundPlaying(hiTrack)) {
                playMusic(hiTrack);
            }
        }

        if(!(mc.currentScreen instanceof GuiMainMenu)) {
            for (BRMusicTrack track : menuMusic) {
                if (mc.getSoundHandler().isSoundPlaying(track)) {
                    mc.getSoundHandler().stopSound(track);
                }
            }
        }

        if(currentTrack != null && mc.getSoundHandler().isSoundPlaying(currentTrack) && !currentTrack.checkCondition()) {
            stopMusic(currentTrack);
        }


        if (mc.currentScreen instanceof GuiMainMenu) {
            // Play a random track from the menu music list
            if(!menuMusic.contains(currentTrack) || !mc.getSoundHandler().isSoundPlaying(currentTrack)) {
                stopMusic(currentTrack);
                playMusic(menuMusic.get(new Random().nextInt(menuMusic.size())));
            }
        } else {

            // Get all tracks whose condition is met
            List<BRMusicTrack> validTracks = standardMusic.values().stream()
                    .filter(BRMusicTrack::checkCondition)
                    .collect(Collectors.toList());

            if (!validTracks.isEmpty()) {
                // Get the highest priority of all valid tracks
                int highestPriority = validTracks.stream()
                        .mapToInt(BRMusicTrack::getPriority)
                        .max()
                        .getAsInt();

                // Get all tracks with the highest priority
                List<BRMusicTrack> highestPriorityTracks = validTracks.stream()
                        .filter(track -> track.getPriority() == highestPriority)
                        .collect(Collectors.toList());

                // Play a random track from the highest priority tracks
                if((currentTrack == null || !mc.getSoundHandler().isSoundPlaying(currentTrack)) && mc.thePlayer != null) {

                    playMusic(highestPriorityTracks.get(new Random().nextInt(highestPriorityTracks.size())));
                }
            }

        }
    }

    private void playMusic(BRMusicTrack music) {
        if (music != null && !playingSounds.contains(music)) {
            currentTrack = music;
            if (!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(currentTrack)) {
                Minecraft.getMinecraft().getSoundHandler().stopSounds();
                Minecraft.getMinecraft().getSoundHandler().playSound(currentTrack);
                playingSounds.add(music);
            }
        }
    }

    private void stopMusic(BRMusicTrack music) {
        Minecraft.getMinecraft().getSoundHandler().stopSound(music);
        currentTrack = null;
        playingSounds.remove(music);
    }

    public List<ISound> getPlayingSounds() {
        return playingSounds;
    }

    private boolean calmZoneCheck(Zone z) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            EntityPlayer ply = Minecraft.getMinecraft().thePlayer;

            return ZoneLib.isPlayerInZone(ply, z);
        }

        return false;
    }

    private boolean combatLogZoneCheck(Zone z) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            EntityPlayer ply = Minecraft.getMinecraft().thePlayer;

            return combatLogCheck() && ZoneLib.isPlayerInZone(ply, z);
        }

        return false;
    }

    private boolean combatLogCheck() {
//        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.isPotionActive(EffectRegister.getPotionId("combatLog"))) {
//            return true;
//        }
        return false;
    }

    private BRMusicTrack getHighestPriorityTrack() {
        // Get all tracks whose condition is met
        List<BRMusicTrack> validTracks = standardMusic.values().stream()
                .filter(BRMusicTrack::checkCondition)
                .collect(Collectors.toList());

        if (!validTracks.isEmpty()) {
            // Get the highest priority of all valid tracks
            int highestPriority = validTracks.stream()
                    .mapToInt(BRMusicTrack::getPriority)
                    .max()
                    .getAsInt();

            // Get all tracks with the highest priority
            List<BRMusicTrack> highestPriorityTracks = validTracks.stream()
                    .filter(track -> track.getPriority() == highestPriority)
                    .collect(Collectors.toList());

            // Return a random track from the highest priority tracks
            return highestPriorityTracks.get(new Random().nextInt(highestPriorityTracks.size()));
        } else {
            // If no conditions are met, return a random track
            return standardMusic.values().stream()
                    .skip(new Random().nextInt(standardMusic.size()))
                    .findFirst()
                    .orElse(null);
        }
    }
}