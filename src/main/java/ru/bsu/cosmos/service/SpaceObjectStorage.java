package ru.bsu.cosmos.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ru.bsu.cosmos.model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Хранилище объектов в файловой системе (JSON).
 * Поддерживает полиморфную сериализацию иерархии SpaceObject
 * через ручной кастомный TypeAdapter (без сторонних библиотек).
 */
public class SpaceObjectStorage {

    private static final String TYPE_FIELD = "objectType";

    private final Path filePath;
    private final Gson gson;

    public SpaceObjectStorage(Path filePath) {
        this.filePath = filePath;
        this.gson = buildGson();
    }

    private static Gson buildGson() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(SpaceObject.class, new SpaceObjectAdapter())
            .create();
    }

    /** Сохраняет список объектов в JSON. */
    public void save(List<SpaceObject> objects) throws IOException {
        Files.createDirectories(filePath.getParent());
        Type listType = new TypeToken<List<SpaceObject>>(){}.getType();
        String json = gson.toJson(objects, listType);
        Files.writeString(filePath, json);
    }

    /** Загружает список объектов из JSON. */
    public List<SpaceObject> load() throws IOException {
        if (!Files.exists(filePath)) return new ArrayList<>();
        String json = Files.readString(filePath);
        if (json.isBlank()) return new ArrayList<>();
        Type listType = new TypeToken<List<SpaceObject>>(){}.getType();
        List<SpaceObject> result = gson.fromJson(json, listType);
        return result == null ? new ArrayList<>() : result;
    }

    public Path getFilePath() { return filePath; }

    // ===== Адаптер LocalDate =====
    static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;
        @Override public JsonElement serialize(LocalDate src, Type t, JsonSerializationContext c) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(FMT));
        }
        @Override public LocalDate deserialize(JsonElement json, Type t, JsonDeserializationContext c) {
            if (json == null || json.isJsonNull()) return null;
            return LocalDate.parse(json.getAsString(), FMT);
        }
    }

    // ===== Полиморфный адаптер SpaceObject =====
    static class SpaceObjectAdapter
            implements JsonSerializer<SpaceObject>, JsonDeserializer<SpaceObject> {

        private static final Map<String, Class<? extends SpaceObject>> NAME_TO_CLASS = new HashMap<>();
        static {
            NAME_TO_CLASS.put("STAR", Star.class);
            NAME_TO_CLASS.put("PLANET", Planet.class);
            NAME_TO_CLASS.put("SATELLITE", Satellite.class);
            NAME_TO_CLASS.put("ASTEROID", Asteroid.class);
            NAME_TO_CLASS.put("COMET", Comet.class);
            NAME_TO_CLASS.put("SPACECRAFT", Spacecraft.class);
        }

        @Override
        public JsonElement serialize(SpaceObject src, Type type, JsonSerializationContext ctx) {
            JsonElement element = ctx.serialize(src, src.getClass());
            element.getAsJsonObject().addProperty(TYPE_FIELD, src.getType().name());
            return element;
        }

        @Override
        public SpaceObject deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) {
            JsonObject obj = json.getAsJsonObject();
            JsonElement typeEl = obj.get(TYPE_FIELD);
            if (typeEl == null) throw new JsonParseException("Отсутствует поле " + TYPE_FIELD);
            String typeName = typeEl.getAsString();
            Class<? extends SpaceObject> clazz = NAME_TO_CLASS.get(typeName);
            if (clazz == null) throw new JsonParseException("Неизвестный тип: " + typeName);
            return ctx.deserialize(json, clazz);
        }
    }
}
