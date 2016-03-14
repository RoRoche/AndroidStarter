package fr.guddy.androidstarter.mvp.repoDetail;

import fr.guddy.androidstarter.persistence.entities.RepoEntity;

public final class ModelRepoDetail {
    public final RepoEntity repo;

    public ModelRepoDetail(final RepoEntity poRepo) {
        repo = poRepo;
    }
}
