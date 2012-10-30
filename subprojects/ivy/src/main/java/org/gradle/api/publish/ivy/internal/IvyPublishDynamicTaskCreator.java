/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.publish.ivy.internal;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ArtifactRepositoryContainer;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.IvyArtifactRepositoryInternal;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.ivy.IvyPublish;
import org.gradle.api.tasks.TaskContainer;

import static org.apache.commons.lang.WordUtils.capitalize;

/**
 * Dynamically creates tasks for each Ivy publication/repository pair in a publication set and repository set.
 */
public class IvyPublishDynamicTaskCreator {

    final private TaskContainer tasks;

    public IvyPublishDynamicTaskCreator(TaskContainer tasks) {
        this.tasks = tasks;
    }

    public void monitor(final PublicationContainer publications, final ArtifactRepositoryContainer repositories) {
        publications.all(new Action<Publication>() {
            public void execute(Publication publication) {
                for (ArtifactRepository repository : repositories) {
                    maybeCreate(publication, repository);
                }
            }
        });

        repositories.whenObjectAdded(new Action<ArtifactRepository>() {
            public void execute(ArtifactRepository repository) {
                for (Publication publication : publications) {
                    maybeCreate(publication, repository);
                }
            }
        });

        // Note: we aren't supporting removal of repositories or publications
        // Note: we also aren't considering that repos have a setName, so their name can change
        //       (though this is a violation of the Named contract)
    }

    private void maybeCreate(Publication publication, ArtifactRepository repository) {
        if (!(publication instanceof IvyPublicationInternal)) {
            return;
        }
        if (!(repository instanceof IvyArtifactRepositoryInternal)) {
            return;
        }

        IvyPublicationInternal publicationInternal = (IvyPublicationInternal) publication;
        IvyArtifactRepositoryInternal repositoryInternal = (IvyArtifactRepositoryInternal) repository;

        // TODO: this feels like it needs to be externalised
        String taskName = String.format("publish%sTo%s", capitalize(publicationInternal.getName()), capitalize(repositoryInternal.getName()));

        IvyPublish task = tasks.add(taskName, IvyPublish.class);
        task.setPublication(publicationInternal);
        task.setRepository(repositoryInternal);
        task.setGroup("publishing");
        task.setDescription(String.format("Publishes Ivy publication %s to Ivy repository %s", publication.getName(), repository.getName()));
    }

}
