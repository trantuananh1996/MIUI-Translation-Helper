package com.anhtt.miui.translation.helper;

import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.internal.JGitText;

import java.io.File;
import java.text.MessageFormat;

public class RepositoryNotFoundException extends TransportException {
    private static final long serialVersionUID = 1L;

    public RepositoryNotFoundException(File location) {
        this(location.getPath());
    }

    public RepositoryNotFoundException(File location, Throwable why) {
        this(location.getPath(), why);
    }

    public RepositoryNotFoundException(String location) {
        super(message(location));
    }

    public RepositoryNotFoundException(String location, Throwable why) {
        super(message(location), why);
    }

    private static String message(String location) {
        return MessageFormat.format(JGitText.get().repositoryNotFound, location);
    }
}