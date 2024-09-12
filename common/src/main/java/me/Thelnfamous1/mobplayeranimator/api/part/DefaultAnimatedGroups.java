package me.Thelnfamous1.mobplayeranimator.api.part;

import com.google.common.collect.Sets;

import java.util.Set;

public class DefaultAnimatedGroups {
    public static final Set<MPABodyPart> HEAD = Sets.immutableEnumSet(MPABodyPart.HEAD);
    public static final Set<MPABodyPart> TORSO = Sets.immutableEnumSet(MPABodyPart.TORSO);
    public static final Set<MPABodyPart> LEFT_ARM = Sets.immutableEnumSet(MPABodyPart.LEFT_ARM);
    public static final Set<MPABodyPart> RIGHT_ARM = Sets.immutableEnumSet(MPABodyPart.RIGHT_ARM);
    public static final Set<MPABodyPart> LEFT_LEG = Sets.immutableEnumSet(MPABodyPart.LEFT_LEG);
    public static final Set<MPABodyPart> RIGHT_LEG = Sets.immutableEnumSet(MPABodyPart.RIGHT_LEG);
    public static final Set<MPABodyPart> ARMS = Sets.immutableEnumSet(MPABodyPart.LEFT_ARM, MPABodyPart.RIGHT_ARM);

    static Set<MPABodyPart> guessAnimatedGroup(MPAPartPath partPath, Set<MPABodyPart> defaultGroup) {
        String lastChild = partPath.getLastChild();
        if(lastChild.contains("head")){
            return HEAD;
        } else if(lastChild.contains("body")){
            return TORSO;
        } else if(lastChild.contains("left_arm")){
            return LEFT_ARM;
        } else if(lastChild.contains("right_arm")){
            return RIGHT_ARM;
        } else if(lastChild.contains("left_leg")){
            return LEFT_LEG;
        } else if(lastChild.contains("right_leg")){
            return RIGHT_LEG;
        } else if(lastChild.contains("arms")){
            return ARMS;
        }
        return defaultGroup;
    }
}
