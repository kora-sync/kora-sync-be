package com.minierp.kora_sync.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Unit of measure of a product. \
        UN: unit. KG: kilogram. G: gram. L: liter. ML: milliliter. M: meter. CX: box. PCT: package. \
        UN, CX and PCT are counted in whole numbers; the others accept fractions (up to 3 decimal places).""")
public enum UnitOfMeasure {

    UN(false),
    KG(true),
    G(true),
    L(true),
    ML(true),
    M(true),
    CX(false),
    PCT(false);

    private final boolean fractional;

    UnitOfMeasure(boolean fractional) {
        this.fractional = fractional;
    }

    public boolean allowsFractions() {
        return fractional;
    }
}