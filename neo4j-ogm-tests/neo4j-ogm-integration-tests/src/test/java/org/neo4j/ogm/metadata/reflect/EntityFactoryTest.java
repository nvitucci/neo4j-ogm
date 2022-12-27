/*
 * Copyright (c) 2002-2022 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.ogm.metadata.reflect;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.domain.social.Individual;
import org.neo4j.ogm.exception.core.MappingException;
import org.neo4j.ogm.metadata.MetaData;
import org.neo4j.ogm.response.model.NodeModel;

/**
 * @author Adam George
 */
public class EntityFactoryTest {

    private EntityFactory entityFactory;

    @BeforeEach
    public void setUp() {
        MetaData metadata = new MetaData("org.neo4j.ogm.domain.social", "org.neo4j.ogm.domain.canonical");
        this.entityFactory = new EntityFactory(metadata, new ReflectionEntityInstantiator(metadata));
    }

    @Test
    void shouldConstructObjectOfParticularTypeUsingItsDefaultZeroArgConstructor() {
        NodeModel personNodeModel = new NodeModel(-1L);
        personNodeModel.setLabels(new String[]{"Individual"});
        Individual sheila = this.entityFactory.newObject(personNodeModel);
        assertThat(sheila).isNotNull();
    }

    @Test
    void shouldHandleMultipleLabelsSafely() {
        NodeModel personNodeModel = new NodeModel(-1L);
        personNodeModel.setLabels(new String[]{"Female", "Individual", "Lass"});
        Individual ourLass = this.entityFactory.newObject(personNodeModel);
        assertThat(ourLass).isNotNull();
    }

    @Test
    void shouldBeAbleToConstructObjectWithNonPublicZeroArgConstructor() {
        NodeModel node = new NodeModel(163L);
        node.setLabels(new String[]{"ClassWithPrivateConstructor"});
        this.entityFactory.newObject(node);
    }

    @Test
    void shouldFailForGraphModelComponentWithNoTaxa() {
        assertThrows(MappingException.class, () -> {
            NodeModel node = new NodeModel(302L);
            node.setLabels(new String[0]);
            this.entityFactory.newObject(node);
        });
    }

    @Test
    void shouldConstructObjectIfExplicitlyGivenClassToInstantiate() {
        Individual instance = this.entityFactory.newObject(Individual.class, new HashMap<>());
        assertThat(instance).as("The resultant instance shouldn't be null").isNotNull();
    }
}
