package net.pl3x.servergui.api.gui.element;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.pl3x.servergui.api.Key;
import net.pl3x.servergui.api.gui.Point;
import net.pl3x.servergui.api.gui.Screen;
import net.pl3x.servergui.api.json.JsonObjectWrapper;
import net.pl3x.servergui.api.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Button extends AbstractElement {
    private String text;
    private Point size;

    private TriConsumer<Screen, Button, Player> onClick;

    public Button(@NotNull Key key, @Nullable String text, @Nullable Point pos, @Nullable Point size, @Nullable Point anchor, @Nullable Point offset, @Nullable Float scale, @Nullable Double zIndex) {
        super(key, "button", pos, anchor, offset, scale, zIndex);
        setText(text);
        setSize(size);
    }

    @Nullable
    public String getText() {
        return this.text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nullable
    public Point getSize() {
        return this.size;
    }

    public void setSize(float x, float y) {
        setSize(Point.of(x, y));
    }

    public void setSize(@Nullable Point size) {
        this.size = size;
    }

    public TriConsumer<Screen, Button, Player> onClick() {
        return this.onClick;
    }

    public void onClick(TriConsumer<Screen, Button, Player> run) {
        this.onClick = run;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonObjectWrapper json = new JsonObjectWrapper(super.toJson());
        json.addProperty("text", getText());
        json.addProperty("size", getSize());
        return json.getJsonObject();
    }

    @NotNull
    public static Button fromJson(@NotNull JsonObject json) {
        Preconditions.checkArgument(json.has("key"), "Key cannot be null");
        return new Button(
            Key.of(json.get("key").getAsString()),
            !json.has("text") ? null : json.get("text").getAsString(),
            !json.has("pos") ? null : Point.fromJson(json.get("pos").getAsJsonObject()),
            !json.has("size") ? null : Point.fromJson(json.get("size").getAsJsonObject()),
            !json.has("anchor") ? null : Point.fromJson(json.get("anchor").getAsJsonObject()),
            !json.has("offset") ? null : Point.fromJson(json.get("offset").getAsJsonObject()),
            !json.has("scale") ? null : json.get("scale").getAsFloat(),
            !json.has("zIndex") ? null : json.get("zIndex").getAsDouble()
        );
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Button other = (Button) o;
        return Objects.equals(getText(), other.getText())
            && Objects.equals(getSize(), other.getSize())
            && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getSize(), super.hashCode());
    }

    @Override
    public String toString() {
        return String.format("Button{%s}", getPropertiesAsString());
    }

    @Override
    @NotNull
    public String getPropertiesAsString() {
        return super.getPropertiesAsString()
            + ",text=" + getText()
            + ",size=" + getSize();
    }

    public static Builder builder(@NotNull String key) {
        return new Builder(key);
    }

    public static Builder builder(@NotNull Key key) {
        return new Builder(key);
    }

    public static class Builder extends AbstractBuilder<Builder> {
        private String text;
        private Point size;
        private TriConsumer<Screen, Button, Player> onClick;

        public Builder(@NotNull String key) {
            this(Key.of(key));
        }

        public Builder(@NotNull Key key) {
            super(key);
        }

        @Nullable
        public String getText() {
            return text;
        }

        @NotNull
        public Builder setText(@Nullable String text) {
            this.text = text;
            return this;
        }

        @Nullable
        public Point getSize() {
            return size;
        }

        @NotNull
        public Builder setSize(float x, float y) {
            return setSize(Point.of(x, y));
        }

        @NotNull
        public Builder setSize(@Nullable Point size) {
            this.size = size;
            return this;
        }

        @NotNull
        public Builder onClick(TriConsumer<Screen, Button, Player> run) {
            this.onClick = run;
            return this;
        }

        @Override
        @NotNull
        public Button build() {
            Button button = new Button(getKey(), getText(), getPos(), getSize(), getAnchor(), getOffset(), getScale(), getZIndex());
            button.onClick(this.onClick);
            return button;
        }
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);

        default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
            Objects.requireNonNull(after);
            return (a, b, c) -> {
                accept(a, b, c);
                after.accept(a, b, c);
            };
        }
    }
}
