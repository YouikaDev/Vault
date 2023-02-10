package dev.youika.mysql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryResult implements AutoCloseable{

    private final Map<Integer, SQLSection> sectinosMap = new ConcurrentHashMap<>();
    // Cleanup only once.
    private boolean single;

    public QueryResult(ResultSet rs) throws SQLException {
        if (rs != null) {
            int point = 0;
            final ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                final SQLSection section = new SQLSection();
                for (int colum = 1; colum <= data.getColumnCount(); colum++) {
                    section.putValue(data.getColumnName(colum), rs.getObject(colum));
                }
                sectinosMap.put(point++, section);
            }

            rs.close();
        }
    }

    public List<SQLSection> all() {
        return sectinosMap.values();
    }

    public void cleanup() {
        if (!single) {
            single = true;
            sectinosMap.values().forEach(SQLSection::cleanup);
            sectinosMap.clear();
        }
    }

    @Override
    public void close() {
        cleanup();
    }

    public boolean isEmpty() {
        return sectinosMap.isEmpty();
    }
}
