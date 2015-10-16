/*
 * Copyright 2015 the original author or authors.
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
package org.gradle.platform.base.internal;

import org.apache.commons.lang.ObjectUtils;
import org.gradle.api.Nullable;
import org.gradle.platform.base.DependencySpec;
import org.gradle.platform.base.DependencySpecBuilder;

public class DefaultDependencySpec implements DependencySpec {
    private final String projectPath;
    private final String libraryName;
    private final boolean exported;

    public DefaultDependencySpec(String libraryName, String projectPath, boolean exported) {
        if (libraryName==null && projectPath==null) {
            throw new IllegalArgumentException("A dependency spec must have at least one of project or library name not null");
        }
        this.libraryName = libraryName;
        this.projectPath = projectPath;
        this.exported = exported;
    }

    @Override
    public String getProjectPath() {
        return projectPath;
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return libraryName;
    }

    @Override
    public boolean isExported() {
        return exported;
    }

    public static class Builder implements DependencySpecBuilder {
        private String projectPath;
        private String libraryName;
        private boolean exported;

        @Override
        public DependencySpecBuilder project(String path) {
            projectPath = path;
            return this;
        }

        @Override
        public DependencySpecBuilder library(String name) {
            libraryName = name;
            return this;
        }

        public DependencySpecBuilder exported(boolean value) {
            exported = value;
            return this;
        }

        @Override
        public DependencySpec build() {
            return new DefaultDependencySpec(libraryName, projectPath, exported);
        }

        @Override
        @Nullable
        public String getProjectPath() {
            return projectPath;
        }

        @Nullable
        @Override
        public String getLibraryName() {
            return libraryName;
        }

        @Override
        public boolean isExported() {
            return exported;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultDependencySpec that = (DefaultDependencySpec) o;

        return ObjectUtils.equals(projectPath, that.projectPath)
                && ObjectUtils.equals(libraryName, that.libraryName)
                && exported == that.exported;

    }

    @Override
    public int hashCode() {
        int result = projectPath != null ? projectPath.hashCode() : 0;
        result = 31 * result + (libraryName != null ? libraryName.hashCode() : 0);
        return result;
    }
}
