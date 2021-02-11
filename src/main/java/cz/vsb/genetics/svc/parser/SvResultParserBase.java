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
import cz.vsb.genetics.svc.common.SvType;

import java.util.ArrayList;
import java.util.List;

public abstract class SvResultParserBase implements SvResultParser {
    protected final List<StructuralVariant> translocations = new ArrayList<>();
    protected final List<StructuralVariant> duplications = new ArrayList<>();
    protected final List<StructuralVariant> inversions = new ArrayList<>();
    protected final List<StructuralVariant> deletions = new ArrayList<>();
    protected final List<StructuralVariant> insertions = new ArrayList<>();
    protected final List<StructuralVariant> copyNumberVariations = new ArrayList<>();
    protected final List<StructuralVariant> unknown = new ArrayList<>();

    private boolean removeDuplicateVariants = false;

    protected void reset() {
        translocations.clear();
        duplications.clear();
        inversions.clear();
        deletions.clear();
        insertions.clear();
        copyNumberVariations.clear();
        unknown.clear();
    }

    @Override
    public List<StructuralVariant> getTranslocations() {
        return translocations;
    }

    @Override
    public List<StructuralVariant> getDuplications() {
        return duplications;
    }

    @Override
    public List<StructuralVariant> getInversions() {
        return inversions;
    }

    @Override
    public List<StructuralVariant> getDeletions() {
        return deletions;
    }

    @Override
    public List<StructuralVariant> getInsertions() {
        return insertions;
    }

    @Override
    public List<StructuralVariant> getCopyNumberVariations() {
        return copyNumberVariations;
    }

    @Override
    public List<StructuralVariant> getUnknown() {
        return unknown;
    }

    @Override
    public void setRemoveDuplicateVariants(boolean removeDuplicateVariants) {
        this.removeDuplicateVariants = removeDuplicateVariants;
    }

    protected void printStructuralVariantStats(String parserName) {
        System.out.println();
        System.out.println(parserName + " statistics:");
        System.out.println("Translocations (BND):\t" + getTranslocations().size());
        System.out.println("Duplications (DUP):\t\t" + getDuplications().size());
        System.out.println("Inversions (INV):\t\t" + getInversions().size());
        System.out.println("Deletions (DEL):\t\t" + getDeletions().size());
        System.out.println("Insertions (INS):\t\t" + getInsertions().size());
        System.out.println("Copy number var. (CNV):\t" + getCopyNumberVariations().size());
        System.out.println("Unknown SV type (CNV):\t" + getUnknown().size());
    }

    protected void addStructuralVariant(StructuralVariant variant, List<StructuralVariant> variants, SvType svType) {
        variant.setSvType(svType);

        if (!removeDuplicateVariants || !variants.contains(variant))
            variants.add(variant);
    }
}
