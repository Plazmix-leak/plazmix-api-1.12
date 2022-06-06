package net.plazmix.game.mysql;

import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.user.GameUser;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class GameMysqlDatabase {

    public static final int NULL_INT_VALUE          = -1;
    public static final long NULL_LONG_VALUE        = -1;
    public static final byte NULL_BYTE_VALUE        = -1;
    public static final short NULL_SHORT_VALUE      = -1;
    public static final float NULL_FLOAT_VALUE      = -1;
    public static final double NULL_DOUBLE_VALUE    = -1;


    protected final String table;
    protected final boolean hasPrimary;

    protected final List<String> tableColumnNames = new ArrayList<>();

    private final Map<String, RemoteDatabaseRowType> tableColumnTypes = new HashMap<>();
    private final Map<String, GameMysqlResponseHandler> tableColumnHandlers = new HashMap<>();


    public abstract void initialize();
    public abstract void onJoinLoad(@NonNull GamePlugin plugin, @NonNull GameUser gameUser);

    public void onQuitSave(@NonNull GamePlugin plugin, @NonNull GameUser gameUser) {
        // can be realise
    }

    /**
     * Добавить секцию хранения данных игрока
     *
     * @param column   - ключ секции в дате игрока
     * @param responseHandler - обработчик даты для хранения даты
     */
    public final <S extends Serializable> void addColumn(@NonNull String column,
                                                         @NonNull RemoteDatabaseRowType rowType,
                                                         @NonNull GameMysqlResponseHandler responseHandler) {
        tableColumnNames.add(column);

        tableColumnTypes.put(column, rowType);
        tableColumnHandlers.put(column, responseHandler);
    }


    /**
     * Добавить секцию хранения данных игрока
     *
     * @param column - ключ секции в дате игрока
     */
    public final <S extends Serializable> void addColumn(@NonNull String column, @NonNull RemoteDatabaseRowType rowType) {
        addColumn(column, rowType, o -> null);
    }

    /**
     * Инициализация игровой базы данных
     */
    public final void initTableConnection() {
        addColumn("Id", RemoteDatabaseRowType.INT, GameUser::getPlayerId);
        initialize();

        StringBuilder tableStructureBuilder = new StringBuilder();
        {
            tableColumnNames.forEach(column -> {
                RemoteDatabaseRowType rowType = tableColumnTypes.get(column);

                tableStructureBuilder.append("`").append(column).append("`")
                        .append(" ").append(rowType.getQueryFormat()).append(" NOT NULL");

                if (hasPrimary && column.equalsIgnoreCase("Id")) {
                    tableStructureBuilder.append(" PRIMARY KEY");
                }

                tableStructureBuilder.append(", ");
            });
        }

        CoreConnector.getInstance().getMysqlConnection()
                .createTable(table, tableStructureBuilder.substring(0, tableStructureBuilder.length() - 2));
    }


    /**
     * Сохранить данные игрока в игровую
     * базу данных
     *
     * @param gameUser - игровой пользователь, которого сохранить
     */
    public final void insert(boolean sync, @NonNull GameUser gameUser, Object... queryObjects) {
        String insertQueryKeys;
        {
            insertQueryKeys = ("(`").concat(Joiner.on("`, `").join(tableColumnNames).concat("`)")).concat(" VALUES (")
                    .concat(StringUtils.repeat("?, ", tableColumnNames.size()));

            insertQueryKeys = insertQueryKeys.substring(0, insertQueryKeys.length() - 2).concat(")");
        }


        String duplicateKeyQuery;
        {
            StringBuilder stringBuilder = new StringBuilder();

            for (String column : tableColumnNames.stream().skip(1).collect(Collectors.toList())) {
                stringBuilder.append("`").append(column).append("`=?, ");
            }

            duplicateKeyQuery = stringBuilder.substring(0, stringBuilder.length() - 2);
        }


        Object[] queryArguments = new Object[tableColumnNames.size() * 2 - 1];

        if (queryObjects.length <= 0) {
            AtomicInteger indexCounter = new AtomicInteger(0);

            tableColumnNames.forEach(playerDataKey -> {

                int index = indexCounter.getAndIncrement();

                RemoteDatabaseRowType rowType = tableColumnTypes.get(playerDataKey);
                GameMysqlResponseHandler gameMysqlResponseHandler = tableColumnHandlers.get(playerDataKey);

                Object value = gameMysqlResponseHandler.handleResponse(gameUser);

                if (value == null) {
                    return;
                }

                queryArguments[index] = value;

                // для ON DUPLICATE KEY
                if (hasPrimary && index > 0) {
                    queryArguments[index + tableColumnTypes.size() - 1] = value;
                }
            });
        }

        Collection<Object> queryElementCollection = new LinkedList<>();

        if (queryObjects.length > 0) {
            queryElementCollection.add(gameUser.getPlayerId());
        }

        queryElementCollection.addAll(Arrays.asList(queryObjects.length > 0 ? queryObjects : queryArguments));

        CoreConnector.getInstance().getMysqlConnection().execute(!sync, "INSERT INTO `" + table + "` " + insertQueryKeys + (hasPrimary ? " ON DUPLICATE KEY UPDATE "
                + duplicateKeyQuery : ""), queryElementCollection.toArray());
    }

    /**
     * Выгрузить и инициализировать данные игрока
     * из игровой базу данных
     *
     * @param gameUser - игровой пользователь, которого сохранить
     */
    public final void loadPrimary(boolean sync, @NonNull GameUser gameUser, BiConsumer<String, Object> responseConsumer) {
        CoreConnector.getInstance().getMysqlConnection().executeQuery(!sync, "SELECT * FROM `" + table + "` WHERE `Id`=?", resultSet -> {

            if (!resultSet.next()) {
                return null;
            }

            tableColumnNames.forEach(column -> {

                try {
                    if (responseConsumer != null) {
                        responseConsumer.accept(toPlayerKey(column), resultSet.getObject(column));
                    }

                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }

            });

            return null;
        }, gameUser.getPlayerId());
    }

    /**
     * Выгрузить и инициализировать данные игрока
     * из игровой базу данных
     *
     * @param gameUser - игровой пользователь, которого сохранить
     */
    public final void loadAll(boolean sync, @NonNull GameUser gameUser, GameMysqlDatabaseLoader<ResultSet> databaseLoader) {
        CoreConnector.getInstance().getMysqlConnection().executeQuery(!sync, "SELECT * FROM `" + table + "` WHERE `Id`=?", resultSet -> {

            if (databaseLoader != null) {

                try {
                    databaseLoader.handleLoad(resultSet);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            return null;

        }, gameUser.getPlayerId());
    }

    public final void update(boolean sync, @NonNull GameUser gameUser, @NonNull String column, Object value,
                             @NonNull WhereQuery... whereQueries) {

        CoreConnector.getInstance().getMysqlConnection().execute(!sync, "UPDATE `" + table + "` SET `" + column + "`=? WHERE `Id`=?"

                        + (whereQueries.length > 0 ? " AND " : "")
                        + Joiner.on(" AND ").join(Arrays.stream(whereQueries).map(whereQuery -> "`" + whereQuery.getColumn() + "`=" + whereQuery.getValue())

                        .collect(Collectors.toList())),

                value, gameUser.getPlayerId());
    }

    public final boolean hasUserColumn(@NonNull GameUser gameUser) {
        AtomicBoolean exists = new AtomicBoolean();
        loadAll(true, gameUser, value -> exists.set(value.next()));

        return exists.get();
    }

    /**
     * Преобразовать название колонки в
     * базе данных в названия ключа данных
     * кеша игрока
     *
     * @param column - название колонки из базы
     */
    protected String toPlayerKey(@NonNull String column) {
        return column;
    }

}
