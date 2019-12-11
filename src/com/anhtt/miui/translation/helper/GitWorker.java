package com.anhtt.miui.translation.helper;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static com.anhtt.miui.translation.helper.Utils.delete;

public class GitWorker extends SwingWorker<String, String> {
    String sourceCompareFolder;
    Logger logger;
    private String url;

    public GitWorker(Logger logger, String sourceCompareFolder, String url) {
        this.sourceCompareFolder = sourceCompareFolder;
        this.url = url;
        this.logger = logger;
    }

    public void sendLog(String string) {
        publish(string);
    }

    @Override
    protected String doInBackground() {
        try {
            logger.info("Fetching repository: " + sourceCompareFolder);
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(sourceCompareFolder + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();


            FetchResult result = new Git(repository).fetch().call();
            System.out.println(result.getMessages());
            logger.info("Fetched from remote repository to local repository at " + repository.getDirectory());

        } catch (RepositoryNotFoundException ex) {
            try {
                File localPath = new File(sourceCompareFolder);
                delete(localPath);
                // then clone
                logger.info("Cloning from " + url + " to " + localPath);
                try (Git result = Git.cloneRepository()
                        .setURI(url)
                        .setDirectory(localPath)
                        .call()) {
                    // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
                    logger.info("Having repository: " + result.getRepository().getDirectory());
                    try (Git git = new Git(result.getRepository())) {
                        git.pull().call();
                    }
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        } catch (IOException | GitAPIException e1) {
            e1.printStackTrace();
        }

        return null;

    }


    @Override
    protected void process(List<String> chunks) {
        for (String string : chunks) logger.info(string);
    }

    @Override
    protected void done() {
        logger.info("Pulled from remote repository to local repository at " + sourceCompareFolder);
    }
}