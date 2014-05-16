/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package fixture.todo.scenarios;

import com.google.common.base.Strings;

import fixture.todo.simple.ToDoItemsCreate;
import fixture.todo.simple.ToDoItemsDelete;

import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureResultList;
import org.apache.isis.applib.fixturescripts.FixtureScript.ExecutionContext;

public final class RecreateToDoItemsFor extends CompositeFixtureScript {
    public RecreateToDoItemsFor() {
        super(null, "recreate-specified");
    }
    
    @Override
    public String validateRun(String parameters) {
        return Strings.isNullOrEmpty(parameters) ? "Specify the owner of the ToDoItems to be recreated" : null;
    }
    
    @Override
    protected void execute(ExecutionContext executionContext) {
        execute("delete", ToDoItemsDelete.forUser(null), executionContext);
        execute("create", ToDoItemsCreate.forUser(null), executionContext);
    }
}