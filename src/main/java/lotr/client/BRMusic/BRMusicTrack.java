package lotr.client.BRMusic;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class BRMusicTrack implements ISound {
    private final ResourceLocation soundLocation;
    private final int priority;
    private final Supplier<Boolean> condition;

    public BRMusicTrack(String modID, String soundName, int priority, Supplier<Boolean> condition) {
        this.soundLocation = new ResourceLocation(modID, soundName);
        this.priority = priority;
        this.condition = condition;
    }

    @Override
    public ResourceLocation getPositionedSoundLocation() {
        return soundLocation;
    }

    @Override
    public boolean canRepeat() {
        return true;
    }

    @Override
    public int getRepeatDelay() {
        return 0;
    }

    @Override
    public float getVolume() {
        return 50;
    }

    @Override
    public float getPitch() {
        return 1;
    }

    @Override
    public float getXPosF() {
        return 0;
    }

    @Override
    public float getYPosF() {
        return 0;
    }

    @Override
    public float getZPosF() {
        return 0;
    }

    @Override
    public AttenuationType getAttenuationType() {
        return AttenuationType.NONE;
    }

    // Implement the rest of the ISound methods...

    public int getPriority() {
        return priority;
    }

    public boolean checkCondition() {
        return condition.get();
    }
}
