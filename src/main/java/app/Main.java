package app;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsContext;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static final Map<WsContext, Thread> connections = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        byte[] whiteGrid = new byte[131072];
        Arrays.fill(whiteGrid, (byte) 255);
        ByteBuffer buffer = ByteBuffer.wrap(whiteGrid);

        Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
            config.routes.ws("/sim", ws -> {
                ws.onConnect(ctx -> {
                    Thread thread = Thread.ofVirtual().start(() -> {
                        try {
                            while (!Thread.currentThread().isInterrupted()) {
                                System.out.println("frame");
                                ctx.send(buffer.duplicate());
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    connections.put(ctx, thread);
                });
                ws.onClose(ctx -> {
                    Thread thread = connections.remove(ctx);
                    if (thread != null) {
                        thread.interrupt();
                        System.out.println("interrupt");
                    }
                });
            });
        }).start(8080);
    }
}