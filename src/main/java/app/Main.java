package app;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsContext;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import app.cellural.BinCell;
import app.cellural.Direction;
import app.cellural.automaton.Automaton2D;
import app.cellural.rule.GameOfLifeRule;
import app.data.Stream;
import app.data.Zipper;
import app.data.Zipper2D;

public class Main {
    private static final Map<WsContext, Thread> connections = new ConcurrentHashMap<>();
    private static final int WIDTH = 128;
    private static final int HEIGHT = 128;

    public static void main(String[] args) {
        Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
            config.routes.ws("/sim", ws -> {
                ws.onConnect(Main::handleConnect);
                ws.onClose(Main::handleClose);
            });
        }).start(8080);
    }

    private static void handleConnect(WsContext ctx) {
        Thread thread = Thread.ofVirtual().start(() -> runSimulation(ctx));
        connections.put(ctx, thread);
    }

    private static void handleClose(WsContext ctx) {
        Thread thread = connections.remove(ctx);
        if (thread != null) {
            thread.interrupt();
        }
    }

    private static void runSimulation(WsContext ctx) {
        try {
            Zipper2D<BinCell> board = createBoard();
            Automaton2D<BinCell> gameOfLife = new Automaton2D<>(board, new GameOfLifeRule());
            while (!Thread.currentThread().isInterrupted()) {
                byte[] frame = render(gameOfLife.state(), WIDTH, HEIGHT);

                ByteBuffer payload = ByteBuffer.allocate(8 + frame.length);
                payload.putInt(WIDTH);
                payload.putInt(HEIGHT);
                payload.put(frame);
                payload.flip();

                ctx.send(payload);

                gameOfLife = gameOfLife.step();
                Zipper2D<BinCell> croppedState = gameOfLife.state().crop(WIDTH / 2, HEIGHT / 2, BinCell.DEAD);
                gameOfLife = new Automaton2D<>(croppedState, new GameOfLifeRule());

                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Zipper2D<BinCell> createBoard() {
        Zipper<BinCell> initRow = createRandomRow();
        Zipper<Zipper<BinCell>> grid = new Zipper.Zip<>(
                Stream.repeatM(Main::createRandomRow),
                initRow,
                Stream.repeatM(Main::createRandomRow));

        return new Zipper2D.Zip2D<>(grid);
    }

    private static Zipper<BinCell> createRandomRow() {
        return new Zipper.Zip<>(
                Stream.repeatM(Main::randomCell),
                randomCell(),
                Stream.repeatM(Main::randomCell));
    }

    private static BinCell randomCell() {
        return Math.random() < 0.2 ? BinCell.ALIVE : BinCell.DEAD;
    }

    private static byte[] render(Zipper2D<BinCell> state, int width, int height) {
        byte[] buffer = new byte[(width * height) / 8];
        Zipper2D<BinCell> rowStart = state;

        for (int i = 0; i < height / 2; i++) {
            rowStart = rowStart.move(Direction.UP);
        }
        for (int i = 0; i < width / 2; i++) {
            rowStart = rowStart.move(Direction.LEFT);
        }

        int byteIndex = 0;
        for (int y = 0; y < height; y++) {
            Zipper2D<BinCell> current = rowStart;
            for (int x = 0; x < width; x += 8) {
                byte b = 0;
                for (int bit = 7; bit >= 0; bit--) {
                    if (current.extract() == BinCell.ALIVE) {
                        b |= (1 << bit);
                    }
                    current = current.move(Direction.RIGHT);
                }
                buffer[byteIndex++] = b;
            }
            rowStart = rowStart.move(Direction.DOWN);
        }
        return buffer;
    }
}