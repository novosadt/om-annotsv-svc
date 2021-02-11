/*
 * Copyright (C) 2021  Tomas Novosad
 * VSB-TUO, Faculty of Electrical Engineering and Computer Science
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package cz.vsb.genetics.svc.parser;

import cz.vsb.genetics.svc.common.StructuralVariant;

import java.util.List;

public interface SvResultParser {
    void parseResultFile(String file, String delim) throws Exception;

    void printStructuralVariantStats();

    void setRemoveDuplicateVariants(boolean value);

    List<StructuralVariant> getTranslocations();

    List<StructuralVariant> getDuplications();

    List<StructuralVariant> getInversions();

    List<StructuralVariant> getDeletions();

    List<StructuralVariant> getInsertions();

    List<StructuralVariant> getCopyNumberVariations();

    List<StructuralVariant> getUnknown();
}
