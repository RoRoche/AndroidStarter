package fr.guddy.androidstarter.persistence.dao;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

import fr.guddy.androidstarter.persistence.entities.RepoEntity;

public class DAORepo extends AbstractBaseDAOImpl<RepoEntity> {
    //region Constructors matching super
    public DAORepo(final ConnectionSource poConnectionSource) throws SQLException {
        this(poConnectionSource, RepoEntity.class);
    }

    public DAORepo(final ConnectionSource poConnectionSource, final Class<RepoEntity> poDataClass) throws SQLException {
        super(poConnectionSource, poDataClass);
    }

    public DAORepo(final ConnectionSource poConnectionSource, final DatabaseTableConfig<RepoEntity> poTableConfig) throws SQLException {
        super(poConnectionSource, poTableConfig);
    }
    //endregion
}
