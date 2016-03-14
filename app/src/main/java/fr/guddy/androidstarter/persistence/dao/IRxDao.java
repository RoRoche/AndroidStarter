package fr.guddy.androidstarter.persistence.dao;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RawRowObjectMapper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * The definition of the Database Access Objects that handle the reading and writing a class from the database. Kudos to
 * Robert A. for the general concept of this hierarchy.
 *
 * @param <T>  The class that the code will be operating on.
 * @param <ID> The class of the ID column associated with the class. The T class does not require an ID field. The class
 *             needs an ID parameter however so you can use Void or Object to satisfy the compiler.
 * @author romain.rochegude
 */
public interface IRxDao<T, ID> extends Dao<T, ID> {

    /**
     * Retrieves an object associated with a specific ID.
     *
     * @param id Identifier that matches a specific row in the database to find and return.
     * @return The object that has the ID field which equals id or null if no matches.
     * @throws SQLException on any SQL problems or if more than 1 item with the id are found in the database.
     */
    Observable<T> rxQueryForId(final ID id);

    /**
     * rxQuery for and return the first item in the object table which matches the PreparedQuery. See
     * {@link #queryBuilder()} for more information. This can be used to return the object that matches a single unique
     * column. You should use {@link #queryForId(Object)} if you want to rxQuery for the id column.
     *
     * @param preparedQuery rxQuery used to match the objects in the database.
     * @return The first object that matches the rxQuery.
     * @throws SQLException on any SQL problems.
     */
    Observable<T> rxQueryForFirst(final PreparedQuery<T> preparedQuery);

    /**
     * rxQuery for all of the items in the object table. For medium sized or large tables, this may load a lot of objects
     * into memory so you should consider using the {@link #iterator()} method instead.
     *
     * @return A list of all of the objects in the table.
     * @throws SQLException on any SQL problems.
     */
    Observable<List<T>> rxQueryForAll();

    /**
     * rxQuery for the items in the object table that match a simple where with a single field = value type of WHERE
     * clause. This is a convenience method for calling  QueryBuilder().where().eq(fieldName, value).query().
     *
     * @return A list of the objects in the table that match the fieldName = value;
     * @throws SQLException on any SQL problems.
     */
    Observable<List<T>> rxQueryForEq(final String fieldName, final Object value);

    /**
     * rxQuery for the rows in the database that match the object passed in as a parameter. Any fields in the matching
     * object that are not the default value (null, false, 0, 0.0, etc.) are used as the matching parameters with AND.
     * If you are worried about SQL quote escaping, you should use {@link #queryForMatchingArgs(Object)}.
     */
    Observable<List<T>> rxQueryForMatching(final T matchObj);

    /**
     * Same as {@link #queryForMatching(Object)} but this uses {@link SelectArg} and SQL ? arguments. This is slightly
     * more expensive but you don't have to worry about SQL quote escaping.
     */
    Observable<List<T>> rxQueryForMatchingArgs(final T matchObj);

    /**
     * rxQuery for the rows in the database that matches all of the field to value entries from the map passed in. If you
     * are worried about SQL quote escaping, you should use {@link #queryForFieldValuesArgs(Map)}.
     */
    Observable<List<T>> rxQueryForFieldValues(final Map<String, Object> fieldValues);

    /**
     * Same as {@link #queryForFieldValues(Map)} but this uses {@link SelectArg} and SQL ? arguments. This is slightly
     * more expensive but you don't have to worry about SQL quote escaping.
     */
    Observable<List<T>> rxQueryForFieldValuesArgs(final Map<String, Object> fieldValues);

    /**
     * rxQuery for a data item in the table that has the same id as the data parameter.
     */
    Observable<T> rxQueryForSameId(final T data);

    /**
     * rxQuery for the items in the object table which match the prepared rxQuery. See {@link #queryBuilder} for more
     * information.
     * <p>
     * <p>
     * <b>NOTE:</b> For medium sized or large tables, this may load a lot of objects into memory so you should consider
     * using the {@link #iterator(PreparedQuery)} method instead.
     * </p>
     *
     * @param preparedQuery rxQuery used to match the objects in the database.
     * @return A list of all of the objects in the table that match the rxQuery.
     * @throws SQLException on any SQL problems.
     */
    Observable<List<T>> rxQuery(final PreparedQuery<T> preparedQuery);

    /**
     * Create a new row in the database from an object. If the object being created uses
     * {@link DatabaseField#generatedId()} then the data parameter will be modified and set with the corresponding id
     * from the database.
     *
     * @param data The data item that we are creating in the database.
     * @return The number of rows updated in the database. This should be 1.
     */
    Observable<Integer> rxCreate(final T data);

    /**
     * This is a convenience method to creating a data item but only if the ID does not already exist in the table. This
     * extracts the id from the data parameter, does a {@link #queryForId(Object)} on it, returning the data if it
     * exists. If it does not exist {@link #create(Object)} will be called with the parameter.
     *
     * @return Either the data parameter if it was inserted (now with the ID field set via the create method) or the
     * data element that existed already in the database.
     */
    Observable<T> rxCreateIfNotExists(final T data);

    /**
     * This is a convenience method for creating an item in the database if it does not exist. The id is extracted from
     * the data parameter and a rxQuery-by-id is made on the database. If a row in the database with the same id exists
     * then all of the columns in the database will be updated from the fields in the data parameter. If the id is null
     * (or 0 or some other default value) or doesn't exist in the database then the object will be created in the
     * database. This also means that your data item <i>must</i> have an id field defined.
     *
     * @return Status object with the number of rows changed and whether an insert or update was performed.
     */
    Observable<CreateOrUpdateStatus> rxCreateOrUpdate(final T data);

    /**
     * Store the fields from an object to the database row corresponding to the id from the data parameter. If you have
     * made changes to an object, this is how you persist those changes to the database. You cannot use this method to
     * update the id field -- see {@link #updateId} .
     * <p>
     * <p>
     * NOTE: This will not save changes made to foreign objects or to foreign collections.
     * </p>
     *
     * @param data The data item that we are updating in the database.
     * @return The number of rows updated in the database. This should be 1.
     * @throws SQLException             on any SQL problems.
     * @throws IllegalArgumentException If there is only an ID field in the object. See the {@link #updateId} method.
     */
    Observable<Integer> rxUpdate(final T data);

    /**
     * Update the data parameter in the database to change its id to the newId parameter. The data <i>must</i> have its
     * current (old) id set. If the id field has already changed then it cannot be updated. After the id has been
     * updated in the database, the id field of the data parameter will also be changed.
     * <p>
     * <p>
     * <b>NOTE:</b> Depending on the database type and the id type, you may be unable to change the id of the field.
     * </p>
     *
     * @param data  The data item that we are updating in the database with the current id.
     * @param newId The <i>new</i> id that you want to update the data with.
     * @return The number of rows updated in the database. This should be 1.
     * @throws SQLException on any SQL problems.
     */
    Observable<Integer> rxUpdateId(final T data, final ID newId);

    /**
     * Update all rows in the table according to the prepared statement parameter. To use this, the
     * {@link UpdateBuilder} must have set-columns applied to it using the
     * {@link UpdateBuilder#updateColumnValue(String, Object)} or
     * {@link UpdateBuilder#updateColumnExpression(String, String)} methods.
     *
     * @param preparedUpdate A prepared statement to match database rows to be rxDeleted and define the columns to update.
     * @return The number of rows updated in the database.
     * @throws SQLException             on any SQL problems.
     * @throws IllegalArgumentException If there is only an ID field in the object. See the {@link #updateId} method.
     */
    Observable<Integer> rxUpdate(final PreparedUpdate<T> preparedUpdate);

    /**
     * Does a rxQuery for the data parameter's id and copies in each of the field values from the database to refresh the
     * data parameter. Any local object changes to persisted fields will be overwritten. If the database has been
     * updated this brings your local object up to date.
     *
     * @param data The data item that we are refreshing with fields from the database.
     * @return The number of rows found in the database that correspond to the data id. This should be 1.
     * @throws SQLException on any SQL problems or if the data item is not found in the table or if more than 1 item is found
     *                      with data's id.
     */
    Observable<Integer> rxRefresh(final T data);

    /**
     * Delete the database row corresponding to the id from the data parameter.
     *
     * @param data The data item that we are deleting from the database.
     * @return The number of rows updated in the database. This should be 1.
     * @throws SQLException on any SQL problems.
     */
    Observable<Integer> rxDelete(final T data);

    /**
     * Delete an object from the database that has an id.
     *
     * @param id The id of the item that we are deleting from the database.
     * @return The number of rows updated in the database. This should be 1.
     * @throws SQLException on any SQL problems.
     */
    Observable<Integer> rxDeleteById(final ID id);

    /**
     * Delete a collection of objects from the database using an IN SQL clause. The ids are extracted from the datas
     * parameter and used to remove the corresponding rows in the database with those ids.
     *
     * @param datas A collection of data items to be rxDeleted.
     * @return The number of rows updated in the database. This should be the size() of the collection.
     * @throws SQLException on any SQL problems.
     */
    Observable<Integer> rxDelete(final Collection<T> datas);

    /**
     * Delete the objects that match the collection of ids from the database using an IN SQL clause.
     *
     * @param ids A collection of data ids to be rxDeleted.
     * @return The number of rows updated in the database. This should be the size() of the collection.
     * @throws SQLException on any SQL problems.
     */
    Observable<Integer> rxDeleteIds(final Collection<ID> ids);

    /**
     * Delete the objects that match the prepared statement parameter.
     *
     * @param preparedDelete A prepared statement to match database rows to be rxDeleted.
     * @return The number of rows updated in the database.
     * @throws SQLException on any SQL problems.
     */
    Observable<Integer> rxDelete(final PreparedDelete<T> preparedDelete);

    /**
     * This satisfies the {@link Iterable} interface for the class and allows you to iterate through the objects in the
     * table using SQL. You can use code similar to the following:
     * <p>
     * <pre>
     * for (Account account : accountDao) { ... }
     * </pre>
     * <p>
     * <p>
     * <b>WARNING</b>: because the {@link Iterator#hasNext()}, {@link Iterator#next()}, etc. methods can only throw
     * {@link RuntimeException}, the code has to wrap any {@link SQLException} with {@link IllegalStateException}. Make
     * sure to catch {@link IllegalStateException} and look for a {@link SQLException} cause.
     * </p>
     * <p>
     * <p>
     * <b>WARNING</b>: The underlying results object will only be closed if you page all the way to the end of the
     * iterator using the for() loop or if you call {@link CloseableIterator#close()} directly. You can also call the
     * {@link #closeLastIterator()} if you are not iterating across this DAO in multiple threads.
     * </p>
     * <p>
     * <p>
     * <b>NOTE:</b> With this iterator you can only move forward through the object collection. See the
     * {@link #iterator(int)} method to create a cursor that can go both directions.
     * </p>
     *
     * @return An iterator of the class that uses SQL to step across the database table.
     * @throws IllegalStateException When it encounters a SQLException or in other cases.
     */
    Observable<CloseableIterator<T>> rxIterator();

    /**
     * Same as {@link #iterator()} but while specifying flags for the results. This is necessary with certain database
     * types. The resultFlags could be something like ResultSet.TYPE_SCROLL_INSENSITIVE or other values.
     * <p>
     * <p>
     * <b>WARNING:</b> Depending on the database type the underlying connection may never be freed -- even if you go all
     * of the way through the results. It is <i>strongly</i> recommended that you call the
     * {@link CloseableIterator#close()} method when you are done with the iterator.
     * </p>
     */
    Observable<CloseableIterator<T>> rxIterator(final int resultFlags);

    /**
     * Same as {@link #iterator()} but with a prepared rxQuery parameter. See {@link #queryBuilder} for more information.
     * You use it like the following:
     * <p>
     * <pre>
     *  QueryBuilder&lt;Account, String&gt; qb = accountDao.queryBuilder();
     * ... custom rxQuery builder methods
     * CloseableIterator&lt;Account&gt; iterator = partialDao.iterator(qb.prepare());
     * try {
     *     while (iterator.hasNext()) {
     *         Account account = iterator.next();
     *         ...
     *     }
     * } finish {
     *     iterator.close();
     * }
     * </pre>
     *
     * @param preparedQuery rxQuery used to iterate across a sub-set of the items in the database.
     * @return An iterator for T.
     * @throws SQLException on any SQL problems.
     */
    Observable<CloseableIterator<T>> rxIterator(final PreparedQuery<T> preparedQuery);

    /**
     * Same as {@link #iterator(PreparedQuery)} but while specifying flags for the results. This is necessary with
     * certain database types.
     */
    Observable<CloseableIterator<T>> rxIterator(final PreparedQuery<T> preparedQuery, final int resultFlags);

    /**
     * <p>
     * This makes a one time use iterable class that can be closed afterwards. The DAO itself is
     * {@link CloseableWrappedIterable} but multiple threads can each call this to get their own closeable iterable.
     * This allows you to do something like:
     * </p>
     * <p>
     * <pre>
     * CloseableWrappedIterable&lt;Foo&gt; wrappedIterable = fooDao.getWrappedIterable();
     * try {
     *   for (Foo foo : wrappedIterable) {
     *       ...
     *   }
     * } finally {
     *   wrappedIterable.close();
     * }
     * </pre>
     */
    Observable<CloseableWrappedIterable<T>> rxGetWrappedIterable();

    /**
     * Same as {@link #getWrappedIterable()} but with a prepared rxQuery parameter. See {@link #queryBuilder} or
     * {@link #iterator(PreparedQuery)} for more information.
     */
    Observable<CloseableWrappedIterable<T>> rxGetWrappedIterable(final PreparedQuery<T> preparedQuery);

    /**
     * <p>
     * Similar to the {@link #iterator(PreparedQuery)} except it returns a GenericRawResults object associated with the
     * SQL select rxQuery argument. Although you should use the {@link #iterator()} for most queries, this method allows
     * you to do special queries that aren't supported otherwise. Like the above iterator methods, you must call close
     * on the returned RawResults object once you are done with it. The arguments are optional but can be set with
     * strings to expand ? type of SQL.
     * </p>
     * <p>
     * <p>
     * You can use the {@link QueryBuilder#prepareStatementString()} method here if you want to build the rxQuery using
     * the structure of the  QueryBuilder.
     * </p>
     * <p>
     * <pre>
     *  QueryBuilder&lt;Account, Integer&gt; qb = accountDao.queryBuilder();
     * qb.where().ge(&quot;orderCount&quot;, 10);
     * results = accountDao.queryRaw(qb.prepareStatementString());
     * </pre>
     * <p>
     * <p>
     * If you want to use the  QueryBuilder with arguments to the raw rxQuery then you should do something like:
     * </p>
     * <p>
     * <pre>
     *  QueryBuilder&lt;Account, Integer&gt; qb = accountDao.queryBuilder();
     * // we specify a SelectArg here to generate a ? in the statement string below
     * qb.where().ge(&quot;orderCount&quot;, new SelectArg());
     * // the 10 at the end is an optional argument to fulfill the SelectArg above
     * results = accountDao.queryRaw(qb.prepareStatementString(), rawRowMapper, 10);
     * </pre>
     * <p>
     * <p>
     * <b>NOTE:</b> If you are using the {@link QueryBuilder#prepareStatementString()} to build your rxQuery, it may have
     * added the id column to the selected column list if the Dao object has an id you did not include it in the columns
     * you selected. So the results might have one more column than you are expecting.
     * </p>
     */
    Observable<GenericRawResults<String[]>> rxQueryRaw(final String query, final String... arguments);

    /**
     * Similar to the {@link #queryRaw(String, String...)} but this iterator returns rows that you can map yourself. For
     * every result that is returned by the database, the {@link RawRowMapper#mapRow(String[], String[])} method is
     * called so you can convert the result columns into an object to be returned by the iterator. The arguments are
     * optional but can be set with strings to expand ? type of SQL. For a simple implementation of a raw row mapper,
     * see {@link #getRawRowMapper()}.
     */
    <UO> Observable<GenericRawResults<UO>> rxQueryRaw(final String query, final RawRowMapper<UO> mapper, final String... arguments)
    ;

    /**
     * Similar to the {@link #queryRaw(String, RawRowMapper, String...)} but uses the column-types array to present an
     * array of object results to the mapper instead of strings. The arguments are optional but can be set with strings
     * to expand ? type of SQL.
     */
    <UO> Observable<GenericRawResults<UO>> rxQueryRaw(final String query, final DataType[] columnTypes, final RawRowObjectMapper<UO> mapper,
                                                      final String... arguments);

    /**
     * Similar to the {@link #queryRaw(String, String...)} but instead of an array of String results being returned by
     * the iterator, this uses the column-types parameter to return an array of Objects instead. The arguments are
     * optional but can be set with strings to expand ? type of SQL.
     */
    Observable<GenericRawResults<Object[]>> rxQueryRaw(final String query, final DataType[] columnTypes, final String... arguments)
    ;

    /**
     * Perform a raw rxQuery that returns a single value (usually an aggregate function like MAX or COUNT). If the rxQuery
     * does not return a single long value then it will throw a SQLException.
     */
    Observable<Long> rxQueryRawValue(final String query, final String... arguments);

    /**
     * Run a raw rxExecute SQL statement to the database. The arguments are optional but can be set with strings to expand
     * ? type of SQL. If you have no arguments, you may want to call {@link #executeRawNoArgs(String)}.
     *
     * @return number of rows affected.
     */
    Observable<Integer> rxExecuteRaw(final String statement, final String... arguments);

    /**
     * Run a raw rxExecute SQL statement on the database without any arguments. This may use a different mechanism to
     * rxExecute the rxQuery depending on the database backend.
     *
     * @return number of rows affected.
     */
    Observable<Integer> rxExecuteRawNoArgs(final String statement);

    /**
     * Run a raw update SQL statement to the database. The statement must be an SQL INSERT, UPDATE or DELETE
     * statement.The arguments are optional but can be set with strings to expand ? type of SQL.
     *
     * @return number of rows affected.
     */
    Observable<Integer> rxUpdateRaw(final String statement, final String... arguments);

    /**
     * Call the call-able that will perform a number of batch tasks. This is for performance when you want to run a
     * number of database operations at once -- maybe loading data from a file. This will turn off what databases call
     * "auto-commit" mode, run the call-able, and then re-enable "auto-commit". If auto-commit is not supported then a
     * transaction will be used instead.
     * <p>
     * <p>
     * <b>NOTE:</b> If neither auto-commit nor transactions are supported by the database type then this may just call
     * the callable. Also, "commit()" is <i>not</i> called on the connection at all. If "auto-commit" is disabled then
     * this will leave it off and nothing will have been persisted.
     * </p>
     * <p>
     * <p>
     * <b>NOTE:</b> Depending on your underlying database implementation and whether or not you are working with a
     * single database connection, this may synchronize internally to ensure that there are not race-conditions around
     * the transactions on the single connection. Android (for example) will synchronize. Also, you may also need to
     * synchronize calls to here and calls to {@link #setAutoCommit(DatabaseConnection, boolean)}.
     * </p>
     */
    <CT> Observable<CT> rxCallBatchTasks(final Callable<CT> callable) throws Exception;

    /**
     * Return true if the two parameters are equal. This checks each of the fields defined in the database to see if
     * they are equal. Useful for testing and debugging.
     *
     * @param data1 One of the data items that we are checking for equality.
     * @param data2 The other data item that we are checking for equality.
     */
    Observable<Boolean> rxObjectsEqual(final T data1, final T data2);

    /**
     * Returns the ID from the data parameter passed in. This is used by some of the internal queries to be able to
     * search by id.
     */
    Observable<ID> rxExtractId(final T data);

    /**
     * Returns the class of the DAO. This is used by internal rxQuery operators.
     */
    Observable<Class<T>> rxGetDataClass();

    /**
     * Returns the class of the DAO. This is used by internal rxQuery operators.
     */
    Observable<FieldType> rxFindForeignFieldType(final Class<?> clazz);

    /**
     * Returns true if we can call update on this class. This is used most likely by folks who are extending the base
     * dao classes.
     */
    Observable<Boolean> rxIsUpdatable();

    /**
     * Returns true if the table already exists otherwise false.
     */
    Observable<Boolean> rxIsTableExists();

    /**
     * Returns the number of rows in the table associated with the data class. Depending on the size of the table and
     * the database type, this may be expensive and take a while.
     */
    Observable<Long> rxCountOf();

    /**
     * Returns the number of rows in the table associated with the prepared rxQuery passed in. Depending on the size of
     * the table and the database type, this may be expensive and take a while.
     * <p>
     * <p>
     * <b>NOTE:</b> If the rxQuery was prepared with the {@link  QueryBuilder} then you should have called the
     * {@link  QueryBuilder#setCountOf(boolean)} with true before you prepared the rxQuery. You may instead want to use
     * {@link  QueryBuilder#countOf()} which makes it all easier.
     * </p>
     */
    Observable<Long> rxCountOf(final PreparedQuery<T> preparedQuery);

    /**
     * Like {@link #assignEmptyForeignCollection(Object, String)} but it returns the empty collection that you assign to
     * the appropriate field.
     * <p>
     * <p>
     * <b>NOTE:</b> May be deprecated in the future.
     * </p>
     */
    <FT> Observable<ForeignCollection<FT>> rxGetEmptyForeignCollection(final String fieldName);

    /**
     * Returns the current object-cache being used by the DAO or null if none.
     */
    Observable<ObjectCache> rxGetObjectCache();

    /**
     * Return the latest row from the database results from a rxQuery to select * (star).
     */
    Observable<T> rxMapSelectStarRow(final DatabaseResults results);

    /**
     * Return a row mapper that is suitable for mapping results from a rxQuery to select * (star).
     */
    Observable<GenericRowMapper<T>> rxGetSelectStarRowMapper();

    /**
     * Return a row mapper that is suitable for use with {@link #queryRaw(String, RawRowMapper, String...)}. This is a
     * bit experimental at this time. It most likely will _not_ work with all databases since the string output for each
     * data type is hard to forecast. Please provide feedback.
     */
    Observable<RawRowMapper<T>> rxGetRawRowMapper();

    /**
     * Returns true if an object exists that matches this ID otherwise false.
     */
    Observable<Boolean> rxIdExists(final ID id);

    /**
     * <p>
     * <b>WARNING:</b> This method is for advanced users only. It is only to support the
     * {@link #setAutoCommit(DatabaseConnection, boolean)} and other methods below. Chances are you should be using the
     * {@link #callBatchTasks(Callable)} instead of this method unless you know what you are doing.
     * </p>
     * <p>
     * <p>
     * This allocates a connection for this specific thread that will be used in all other DAO operations. The thread
     * <i>must</i> call {@link #endThreadConnection(DatabaseConnection)} once it is done with the connection. It is
     * highly recommended that a
     * <code>try { conn = dao.startThreadConnection(); ... } finally { dao.endThreadConnection(conn); }</code> type of
     * pattern be used here to ensure you do not leak connections.
     * </p>
     */
    Observable<DatabaseConnection> rxStartThreadConnection();

    /**
     * Return true if the database connection returned by the {@link #startThreadConnection()} is in auto-commit mode
     * otherwise false. This may not be supported by all database types.
     */
    Observable<Boolean> rxIsAutoCommit(final DatabaseConnection connection);

    /**
     * Return the associated ConnectionSource or null if none set on the DAO yet.
     */
    Observable<ConnectionSource> rxGetConnectionSource();
}