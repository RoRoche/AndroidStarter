package fr.guddy.androidstarter.mvp.repoList;

import java.io.Serializable;
import java.util.List;

import fr.guddy.androidstarter.persistence.entities.RepoEntity;

public final class ModelRepoList implements Serializable {

    public final List<RepoEntity> repos;

    public ModelRepoList(final List<RepoEntity> ploRepos) {
        repos = ploRepos;
    }
}
