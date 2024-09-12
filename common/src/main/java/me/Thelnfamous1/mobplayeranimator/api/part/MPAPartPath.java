package me.Thelnfamous1.mobplayeranimator.api.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class MPAPartPath {
    public static final Codec<MPAPartPath> CODEC = Codec.STRING.comapFlatMap(MPAPartPath::read, MPAPartPath::getPath).stable();

    private final String path;
    private final LinkedList<String> linkedParts = new LinkedList<>();

    public MPAPartPath(String path){
        this.path = path;
        String[] parts = path.split("#");
        this.linkedParts.addAll(Arrays.asList(parts));
    }

    public static MPAPartPath of(String path){
        return new MPAPartPath(path);
    }

    public static DataResult<MPAPartPath> read(String path) {
        try {
            return DataResult.success(new MPAPartPath(path));
        } catch (Exception e) {
            return DataResult.error(() -> "Not a valid part path: " + path);
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getLastChild(){
        return this.linkedParts.getLast();
    }

    @Nullable
    public ModelPart findPart(ModelPart root){
        ModelPart current = root;
        for(String part : this.linkedParts){
            current = getChild(current, part);
            if(current == null) {
                return null;
            }
        }
        return current;
    }

    @Nullable
    private static ModelPart getChild(ModelPart part, String child){
        try{
            return part.getChild(child);
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MPAPartPath that = (MPAPartPath) o;
        return Objects.equals(this.path, that.path) && Objects.equals(this.linkedParts, that.linkedParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.linkedParts);
    }
}
