package com.company.model;

import org.junit.jupiter.api.Test;

public class BlockTest {
    @Test
    void testHash() {
        var block = new Block();
        block.setLedgerId(1L);
        block.setPrevHash(new byte[]{0});
        block.setMinedBy(new byte[]{1,2,3});
        System.out.println(block.toReadableString());
    }
}
