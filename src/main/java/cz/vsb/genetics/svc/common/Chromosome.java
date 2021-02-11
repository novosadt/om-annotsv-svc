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


package cz.vsb.genetics.svc.common;

import org.apache.commons.lang3.StringUtils;

public enum Chromosome {
    chr1,
    chr2,
    chr3,
    chr4,
    chr5,
    chr6,
    chr7,
    chr8,
    chr9,
    chr10,
    chr11,
    chr12,
    chr13,
    chr14,
    chr15,
    chr16,
    chr17,
    chr18,
    chr19,
    chr20,
    chr21,
    chr22,
    chrX,
    chrY,
    chrM;

    public static Chromosome getChromosome(String label) {
        if (StringUtils.isBlank(label))
            return null;

        label = label.toLowerCase();

        if (label.contains("mt") || label.contains("m"))
            return chrM;

        if (label.contains("x"))
            return chrX;

        if (label.contains("y"))
            return chrY;

        if (label.contains("22"))
            return chr22;

        if (label.contains("21"))
            return chr21;

        if (label.contains("20"))
            return chr20;

        if (label.contains("19"))
            return chr19;

        if (label.contains("18"))
            return chr18;

        if (label.contains("17"))
            return chr17;

        if (label.contains("16"))
            return chr16;

        if (label.contains("15"))
            return chr15;

        if (label.contains("14"))
            return chr14;

        if (label.contains("13"))
            return chr13;

        if (label.contains("12"))
            return chr12;

        if (label.contains("11"))
            return chr11;

        if (label.contains("10"))
            return chr10;

        if (label.contains("9"))
            return chr9;

        if (label.contains("8"))
            return chr8;

        if (label.contains("7"))
            return chr7;

        if (label.contains("6"))
            return chr6;

        if (label.contains("5"))
            return chr5;

        if (label.contains("4"))
            return chr4;

        if (label.contains("3"))
            return chr3;

        if (label.contains("2"))
            return chr2;

        if (label.contains("1"))
            return chr1;

        return null;
    }
}
