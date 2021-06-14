/*
 * Sonar Pitest Plugin
 * Copyright (C) 2009-2016 Alexandre Victoor
 * alexvictoor@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.github.strykermutator;

import lombok.RequiredArgsConstructor;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import static io.github.strykermutator.StrykerConstants.*;

@RequiredArgsConstructor
public class StrykerRulesDefinition implements RulesDefinition {

    private final RulesDefinitionXmlLoader xmlLoader;

    public void define(Context context) {
        NewRepository repository = context
                .createRepository(RULE_REPOSITORY_KEY, CSHARP)
                .setName(REPOSITORY_NAME);

        this.xmlLoader.load(repository,
                getClass().getClassLoader().getResourceAsStream("rules.xml"),
                "UTF-8");

        repository.done();
    }
}
