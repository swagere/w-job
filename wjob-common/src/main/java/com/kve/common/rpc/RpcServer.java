package com.kve.common.rpc;

import java.io.IOException;

public interface RpcServer {
    public void stop();

    public void start() throws IOException;

    public void register (Class serverInterface, Class impl);

    public boolean isRunning();

    public int getPort();
}
