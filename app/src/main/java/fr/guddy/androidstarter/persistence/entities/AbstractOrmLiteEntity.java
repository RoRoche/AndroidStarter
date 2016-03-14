package fr.guddy.androidstarter.persistence.entities;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;

public abstract class AbstractOrmLiteEntity {
    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    protected long _id;

    //region Getter
    public long getBaseId() {
        return _id;
    }
    //endregion
}
