package fr.guddy.androidstarter.di.modules;

import com.mobandme.android.transformer.Transformer;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;

@Module
public class ModuleTransformer {
    public static final String TRANSFORMER_REPO = "TRANSFORMER_REPO";

    @Provides
    @Singleton
    @Named(TRANSFORMER_REPO)
    public Transformer provideTransformerRepo() {
        return new Transformer.Builder()
                .build(RepoEntity.class);
    }
}
