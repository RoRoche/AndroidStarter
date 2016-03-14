package fr.guddy.androidstarter.persistence.dao;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

import fr.guddy.androidstarter.persistence.entities.AbstractOrmLiteEntity;

public abstract class AbstractBaseDAOImpl<DataType extends AbstractOrmLiteEntity> extends RxBaseDaoImpl<DataType, Long> implements IOrmLiteEntityDAO<DataType> {
    //region Constructors matching super
    protected AbstractBaseDAOImpl(final Class<DataType> poDataClass) throws SQLException {
        super(poDataClass);
    }

    public AbstractBaseDAOImpl(final ConnectionSource poConnectionSource, final Class<DataType> poDataClass) throws SQLException {
        super(poConnectionSource, poDataClass);
    }

    public AbstractBaseDAOImpl(final ConnectionSource poConnectionSource, final DatabaseTableConfig<DataType> poTableConfig) throws SQLException {
        super(poConnectionSource, poTableConfig);
    }
    //endregion
}
