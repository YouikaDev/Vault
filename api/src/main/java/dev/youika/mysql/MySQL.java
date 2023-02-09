package dev.youika.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.youika.mysql.factory.AsyncQueryFactory;
import dev.youika.mysql.factory.SyncQueryFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MySQL {

    private static final Consumer<Throwable> CATCHER = Throwable::printStackTrace;

    private final JavaPlugin plugin;

    private final ThreadPoolExecutor pool;
    private final HikariDataSource dataSource;

    private final SyncQueryFactory sync;
    private final AsyncQueryFactory async;

    public MySQL(
            JavaPlugin plugin,
            String user,
            String password,
            String database,
            String host,
            int port
    ) {
        this.plugin = plugin;

        pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                10L, TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );
        pool.allowCoreThreadTimeOut(true);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false", host, port, database));
        config.setUsername(user);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000);
        config.setAutoCommit(true);

        dataSource = new HikariDataSource(config);
        sync = new SyncQueryFactory(dataSource, CATCHER);
        async = new AsyncQueryFactory(dataSource, CATCHER, pool);
    }

    public void executeUpdate(String query) {
        pool.submit(() -> {
            try {
                try (Connection connection = dataSource.getConnection();
                     Statement statement = connection.createStatement()) {
                    statement.executeUpdate(query);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void executeQuery(String query, Consumer<QueryResult> callback) {
        pool.submit(() -> {
            try {
                try (Connection connection = dataSource.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet set = statement.executeQuery(query)) {
                    QueryResult result = new QueryResult(set);
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(result));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void shutdown() {
        pool.shutdown();
        dataSource.close();
    }

    public SyncQueryFactory sync() {
        return sync;
    }

    public AsyncQueryFactory async() {
        return async;
    }
}
