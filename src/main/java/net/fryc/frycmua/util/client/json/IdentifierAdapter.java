package net.fryc.frycmua.util.client.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class IdentifierAdapter extends TypeAdapter<Identifier> {

    @Override
    public void write(JsonWriter out, Identifier value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public Identifier read(JsonReader in) throws IOException {
        return Identifier.of(in.nextString());
    }
}
