package com.example.beelangue;

import junit.framework.TestCase;

import org.junit.Test;

public class LoginActivityTest extends TestCase {
    @Test
    public void testDoubleDot() {
        assertEquals(true, LoginActivity.isEmail("staniswinata.stephanie@gmail.com"));
    }
    @Test
    public void testIsEmailNumbers() {
        assertEquals(true, LoginActivity.isEmail("stephanie45607@gmail.com"));
    }
    @Test
    public void testBinusEmail() {
        assertEquals(true, LoginActivity.isEmail("stephanie.staniswinata@binus.ac.id"));
    }
    @Test
    public void testEmailNormal() {
        assertEquals(true, LoginActivity.isEmail("stephanie@yahoo.com"));
    }

    @Test
    public void testDoubleAt(){
        assertEquals(false, LoginActivity.isEmail("stephanie@@gmail.com"));
    }
    @Test
    public void testNoDomain(){
        assertEquals(false, LoginActivity.isEmail("stephanie@.com"));
    }
    @Test
    public void testNoAtDomain(){
        assertEquals(false, LoginActivity.isEmail("stephanie.com"));
    }
    @Test
    public void testString(){
        assertEquals(false, LoginActivity.isEmail("stephanie"));
    }

}