package tnt.tntlib.core.network.manager;

import net.minecraftforge.network.NetworkDirection;
import tnt.tntlib.api.AnnotationHelper;
import tnt.tntlib.api.SimpleVersion;
import tnt.tntlib.api.TNTUtils;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.NetworkDispatcher;
import tnt.tntlib.api.network.message.ClientMessage;
import tnt.tntlib.api.network.message.HandledMessage;
import tnt.tntlib.api.network.message.ServerMessage;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AutomaticNetworkManager {

    private static final Map<String, AnnotatedNetworkHandler> HANDLERS = new HashMap<>();

    public static void init() {
        List<Class<?>> types = AnnotationHelper.findAnnotatedTypes(Network.class);
        for (Class<?> type : types) {
            Network manager = type.getAnnotation(Network.class);
            if (manager == null)
                throw new IllegalStateException();
            String modId = manager.modId();
            SimpleVersion.ComparationType comparationType = manager.versionComparation();
            SimpleVersion version = AnnotationHelper.getFieldValue(type, Network.Version.class, null, (f, v) -> {
                int modifiers = f.getModifiers();
                if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException("Network version field must be STATIC FINAL");
                }
                if (!f.getType().equals(SimpleVersion.class)) {
                    throw new IllegalArgumentException("Network version field must be of " + SimpleVersion.class.getSimpleName() + " type");
                }
                return true;
            });
            if (version == null) {
                throw new IllegalStateException("@NetworkManager type must contain exactly one valid @NetworkManager.Version field");
            }
            AnnotatedNetworkHandler handler = new AnnotatedNetworkHandler(modId, version, comparationType);
            Field instanceField = AnnotationHelper.getField(type, Network.Instance.class, (f, v) -> {
                int modifiers = f.getModifiers();
                if (!Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException("Network instance field must be STATIC");
                }
                if (!NetworkDispatcher.class.isAssignableFrom(f.getType())) {
                    throw new IllegalArgumentException("Network instance field must be of " + NetworkDispatcher.class.getSimpleName() + " type");
                }
                return true;
            });
            if (instanceField == null) {
                throw new IllegalStateException("@NetworkManager type must contain exactly one valid @NetworkManager.Instance field");
            }
            try {
                instanceField.setAccessible(true);
                instanceField.set(null, handler);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to attach instance to " + instanceField.getName() + " field");
            }
            HANDLERS.put(modId, handler);
        }

        List<Class<?>> messages = AnnotationHelper.findAnnotatedTypes(Network.Message.class);
        for (Class<?> type : messages) {
            Network.Message message = type.getAnnotation(Network.Message.class);
            if (message == null) {
                throw new IllegalStateException();
            }
            String modId = message.value();
            AnnotatedNetworkHandler handler = HANDLERS.get(modId);
            if (handler == null) {
                throw new IllegalStateException("No network handler found for modId " + modId);
            }
            if (!HandledMessage.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(type.getSimpleName() + " message must implement " + HandledMessage.class.getSimpleName() + " type");
            }
            if (ClientMessage.class.isAssignableFrom(type)) {
                handler.registerMessage(TNTUtils.unsafeCast(type), NetworkDirection.PLAY_TO_CLIENT);
            } else if (ServerMessage.class.isAssignableFrom(type)) {
                handler.registerMessage(TNTUtils.unsafeCast(type), NetworkDirection.PLAY_TO_SERVER);
            } else {
                throw new IllegalArgumentException(String.format("%s must implement either %s or %s type", type.getSimpleName(), ClientMessage.class.getSimpleName(), ServerMessage.class.getSimpleName()));
            }
        }
    }
}
