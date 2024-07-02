package tnt.tntlib.core.network;

import tnt.tntlib.TNTLib;
import tnt.tntlib.api.SimpleVersion;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.NetworkDispatcher;

@Network(modId = TNTLib.MOD_ID)
public final class TNTNetworkManager {

    @Network.Instance
    public static NetworkDispatcher NETWORK;
    @Network.Version
    private static final SimpleVersion NETWORK_VERSION = SimpleVersion.parseString("1.0.0");
}
