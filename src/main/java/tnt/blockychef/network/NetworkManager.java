package tnt.blockychef.network;

import tnt.blockychef.BlockyChef;
import tnt.tntlib.api.SimpleVersion;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.NetworkDispatcher;

@Network(modId = BlockyChef.MODID, versionComparation = SimpleVersion.ComparationType.MAJOR_MATCH)
public final class NetworkManager {

    @Network.Instance
    public static NetworkDispatcher DISPATCHER;
    @Network.Version
    public static final SimpleVersion VERSION = SimpleVersion.parse("1.0.0");
}
