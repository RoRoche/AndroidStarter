package fr.guddy.androidstarter.persistence.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RawRowObjectMapper;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import fr.guddy.androidstarter.persistence.entities.AbstractOrmLiteEntity;
import rx.Observable;
import rx.functions.Func0;

public abstract class RxBaseDaoImpl<DataType extends AbstractOrmLiteEntity, IdType> extends BaseDaoImpl<DataType, IdType> implements IRxDao<DataType, IdType> {

    //region Matching constructor
    protected RxBaseDaoImpl(final Class<DataType> poDataClass) throws SQLException {
        super(poDataClass);
    }

    public RxBaseDaoImpl(final ConnectionSource poConnectionSource, final Class<DataType> poDataClass) throws SQLException {
        super(poConnectionSource, poDataClass);
    }

    public RxBaseDaoImpl(final ConnectionSource poConnectionSource, final DatabaseTableConfig<DataType> poTableConfig) throws SQLException {
        super(poConnectionSource, poTableConfig);
    }
    //endregion

    //region Implemented methods
    @Override
    public Observable<DataType> rxQueryForId(final IdType id) {
        final Func0<Observable<DataType>> loFunc = () -> {
            try {
                return Observable.just(queryForId(id));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<DataType> rxQueryForFirst(final PreparedQuery<DataType> preparedQuery) {
        final Func0<Observable<DataType>> loFunc = () -> {
            try {
                return Observable.just(queryForFirst(preparedQuery));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<List<DataType>> rxQueryForAll() {
        final Func0<Observable<List<DataType>>> loFunc = () -> {
            try {
                return Observable.just(queryForAll());
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<List<DataType>> rxQueryForEq(final String fieldName, final Object value) {
        final Func0<Observable<List<DataType>>> loFunc = () -> {
            try {
                return Observable.just(queryForEq(fieldName, value));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<List<DataType>> rxQueryForMatching(final DataType matchObj) {
        final Func0<Observable<List<DataType>>> loFunc = () -> {
            try {
                return Observable.just(queryForMatching(matchObj));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<List<DataType>> rxQueryForMatchingArgs(final DataType matchObj) {
        final Func0<Observable<List<DataType>>> loFunc = () -> {
            try {
                return Observable.just(queryForMatchingArgs(matchObj));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<List<DataType>> rxQueryForFieldValues(final Map<String, Object> fieldValues) {
        final Func0<Observable<List<DataType>>> loFunc = () -> {
            try {
                return Observable.just(queryForFieldValues(fieldValues));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<List<DataType>> rxQueryForFieldValuesArgs(final Map<String, Object> fieldValues) {
        final Func0<Observable<List<DataType>>> loFunc = () -> {
            try {
                return Observable.just(queryForFieldValuesArgs(fieldValues));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<DataType> rxQueryForSameId(final DataType data) {
        final Func0<Observable<DataType>> loFunc = () -> {
            try {
                return Observable.just(queryForSameId(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<List<DataType>> rxQuery(final PreparedQuery<DataType> preparedQuery) {
        final Func0<Observable<List<DataType>>> loFunc = () -> {
            try {
                return Observable.just(query(preparedQuery));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxCreate(final DataType data) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(create(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<DataType> rxCreateIfNotExists(final DataType data) {
        final Func0<Observable<DataType>> loFunc = () -> {
            try {
                return Observable.just(createIfNotExists(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<CreateOrUpdateStatus> rxCreateOrUpdate(final DataType data) {
        final Func0<Observable<CreateOrUpdateStatus>> loFunc = () -> {
            try {
                return Observable.just(createOrUpdate(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxUpdate(final DataType data) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(update(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxUpdateId(final DataType data, final IdType newId) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(updateId(data, newId));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxUpdate(final PreparedUpdate<DataType> preparedUpdate) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(update(preparedUpdate));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxRefresh(final DataType data) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(refresh(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxDelete(final DataType data) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(delete(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxDeleteById(final IdType id) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(deleteById(id));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxDelete(final Collection<DataType> datas) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(delete(datas));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxDeleteIds(final Collection<IdType> ids) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(deleteIds(ids));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxDelete(final PreparedDelete<DataType> preparedDelete) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(delete(preparedDelete));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<CloseableIterator<DataType>> rxIterator() {
        final Func0<Observable<CloseableIterator<DataType>>> loFunc = () -> Observable.just(iterator());
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<CloseableIterator<DataType>> rxIterator(final int resultFlags) {
        final Func0<Observable<CloseableIterator<DataType>>> loFunc = () -> Observable.just(iterator(resultFlags));
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<CloseableIterator<DataType>> rxIterator(final PreparedQuery<DataType> preparedQuery) {
        final Func0<Observable<CloseableIterator<DataType>>> loFunc = () -> {
            try {
                return Observable.just(iterator(preparedQuery));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<CloseableIterator<DataType>> rxIterator(final PreparedQuery<DataType> preparedQuery, final int resultFlags) {
        final Func0<Observable<CloseableIterator<DataType>>> loFunc = () -> {
            try {
                return Observable.just(iterator(preparedQuery, resultFlags));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<CloseableWrappedIterable<DataType>> rxGetWrappedIterable() {
        final Func0<Observable<CloseableWrappedIterable<DataType>>> loFunc = () -> Observable.just(getWrappedIterable());
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<CloseableWrappedIterable<DataType>> rxGetWrappedIterable(final PreparedQuery<DataType> preparedQuery) {
        final Func0<Observable<CloseableWrappedIterable<DataType>>> loFunc = () -> Observable.just(getWrappedIterable(preparedQuery));
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<GenericRawResults<String[]>> rxQueryRaw(final String query, final String... arguments) {
        final Func0<Observable<GenericRawResults<String[]>>> loFunc = () -> {
            try {
                return Observable.just(queryRaw(query, arguments));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public <UO> Observable<GenericRawResults<UO>> rxQueryRaw(final String query, final RawRowMapper<UO> mapper, final String... arguments) {
        final Func0<Observable<GenericRawResults<UO>>> loFunc = () -> {
            try {
                return Observable.just(queryRaw(query, mapper, arguments));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public <UO> Observable<GenericRawResults<UO>> rxQueryRaw(final String query, final com.j256.ormlite.field.DataType[] columnTypes, final RawRowObjectMapper<UO> mapper, final String... arguments) {
        final Func0<Observable<GenericRawResults<UO>>> loFunc = () -> {
            try {
                return Observable.just(queryRaw(query, columnTypes, mapper, arguments));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<GenericRawResults<Object[]>> rxQueryRaw(final String query, final com.j256.ormlite.field.DataType[] columnTypes, final String... arguments) {
        final Func0<Observable<GenericRawResults<Object[]>>> loFunc = () -> {
            try {
                return Observable.just(queryRaw(query, columnTypes, arguments));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Long> rxQueryRawValue(final String query, final String... arguments) {
        final Func0<Observable<Long>> loFunc = () -> {
            try {
                return Observable.just(queryRawValue(query, arguments));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxExecuteRaw(final String statement, final String... arguments) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(executeRaw(statement, arguments));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxExecuteRawNoArgs(final String statement) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(executeRawNoArgs(statement));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Integer> rxUpdateRaw(final String statement, final String... arguments) {
        final Func0<Observable<Integer>> loFunc = () -> {
            try {
                return Observable.just(updateRaw(statement, arguments));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public <CT> Observable<CT> rxCallBatchTasks(final Callable<CT> callable) throws Exception {
        final Func0<Observable<CT>> loFunc = () -> {
            try {
                return Observable.just(callBatchTasks(callable));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Boolean> rxObjectsEqual(final DataType data1, final DataType data2) {
        final Func0<Observable<Boolean>> loFunc = () -> {
            try {
                return Observable.just(objectsEqual(data1, data2));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<IdType> rxExtractId(final DataType data) {
        final Func0<Observable<IdType>> loFunc = () -> {
            try {
                return Observable.just(extractId(data));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Class<DataType>> rxGetDataClass() {
        final Func0<Observable<Class<DataType>>> loFunc = () -> Observable.just(getDataClass());
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<FieldType> rxFindForeignFieldType(final Class<?> clazz) {
        final Func0<Observable<FieldType>> loFunc = () -> Observable.just(findForeignFieldType(clazz));
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Boolean> rxIsUpdatable() {
        final Func0<Observable<Boolean>> loFunc = () -> Observable.just(isUpdatable());
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Boolean> rxIsTableExists() {
        final Func0<Observable<Boolean>> loFunc = () -> {
            try {
                return Observable.just(isTableExists());
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Long> rxCountOf() {
        final Func0<Observable<Long>> loFunc = () -> {
            try {
                return Observable.just(countOf());
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Long> rxCountOf(final PreparedQuery<DataType> preparedQuery) {
        final Func0<Observable<Long>> loFunc = () -> {
            try {
                return Observable.just(countOf(preparedQuery));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public <FT> Observable<ForeignCollection<FT>> rxGetEmptyForeignCollection(final String fieldName) {
        final Func0<Observable<ForeignCollection<FT>>> loFunc = () -> {
            try {
                return Observable.just(getEmptyForeignCollection(fieldName));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<ObjectCache> rxGetObjectCache() {
        final Func0<Observable<ObjectCache>> loFunc = () -> Observable.just(getObjectCache());
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<DataType> rxMapSelectStarRow(final DatabaseResults results) {
        final Func0<Observable<DataType>> loFunc = () -> {
            try {
                return Observable.just(mapSelectStarRow(results));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<GenericRowMapper<DataType>> rxGetSelectStarRowMapper() {
        final Func0<Observable<GenericRowMapper<DataType>>> loFunc = () -> {
            try {
                return Observable.just(getSelectStarRowMapper());
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<RawRowMapper<DataType>> rxGetRawRowMapper() {
        final Func0<Observable<RawRowMapper<DataType>>> loFunc = () -> Observable.just(getRawRowMapper());
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Boolean> rxIdExists(final IdType id) {
        final Func0<Observable<Boolean>> loFunc = () -> {
            try {
                return Observable.just(idExists(id));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<DatabaseConnection> rxStartThreadConnection() {
        final Func0<Observable<DatabaseConnection>> loFunc = () -> {
            try {
                return Observable.just(startThreadConnection());
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<Boolean> rxIsAutoCommit(final DatabaseConnection connection) {
        final Func0<Observable<Boolean>> loFunc = () -> {
            try {
                return Observable.just(isAutoCommit(connection));
            } catch (SQLException e) {
                return Observable.error(e);
            }
        };
        return Observable.defer(loFunc);
    }

    @Override
    public Observable<ConnectionSource> rxGetConnectionSource() {
        final Func0<Observable<ConnectionSource>> loFunc = () -> Observable.just(getConnectionSource());
        return Observable.defer(loFunc);
    }
    //endregion
}
