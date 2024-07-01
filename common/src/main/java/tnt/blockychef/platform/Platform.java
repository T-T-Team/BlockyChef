package tnt.blockychef.platform;

public interface Platform {

    Platform INSTANCE = JavaServiceLoader.loadService(Platform.class);

    Side getSide();
}
