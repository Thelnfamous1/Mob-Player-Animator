package me.Thelnfamous1.mobplayeranimator.api.part;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public enum MPABodyPart implements StringRepresentable {
    BODY("body"), // "body" in the context of PlayerAnimator refers to the entire model
    HEAD("head"),
    TORSO("torso"),
    LEFT_ARM("leftArm"),
    LEFT_ITEM("leftItem"),
    RIGHT_ARM("rightArm"),
    RIGHT_ITEM("rightItem"),
    LEFT_LEG("leftLeg"),
    RIGHT_LEG("rightLeg");
    public static final StringRepresentable.EnumCodec<MPABodyPart> CODEC = StringRepresentable.fromEnum(MPABodyPart::values);

    private final String name;

    MPABodyPart(String name) {
        this.name = name;
    }

    @Nullable
    public static MPABodyPart byName(String name) {
        return CODEC.byName(name);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
