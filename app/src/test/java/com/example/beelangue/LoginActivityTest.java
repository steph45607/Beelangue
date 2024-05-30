package com.example.beelangue;

import junit.framework.TestCase;

import org.junit.Test;

public class LoginActivityTest extends TestCase {
    @Test
    public void testIsEmail() {
//        accepted email formats
        assertEquals(true, LoginActivity.isEmail("stephanie.staniswinata@binus.ac.id"));
        assertEquals(true, LoginActivity.isEmail("stephanie@binus.ac.id"));
        assertEquals(true, LoginActivity.isEmail("stephanie45607@gmail.com"));
//        rejected email format
        assertEquals(false, LoginActivity.isEmail("stephanie@@gmail.com"));
        assertEquals(false, LoginActivity.isEmail("stephanie.com"));
        assertEquals(false, LoginActivity.isEmail("stephanie"));
    }
}