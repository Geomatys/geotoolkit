/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.filter.binding;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test bean accessor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class BeanBindingTest {

    public static class Person{
        
        private int age;
        private String name;
        private boolean human;

        public Person() {
        }
        
        public Person(int age, String name, boolean human) {
            this.age = age;
            this.name = name;
            this.human = human;
        }

        /**
         * @return the age
         */
        public int getAge() {
            return age;
        }

        /**
         * @param age the age to set
         */
        public void setAge(int age) {
            this.age = age;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the human
         */
        public boolean isHuman() {
            return human;
        }

        /**
         * @param human the human to set
         */
        public void setHuman(boolean human) {
            this.human = human;
        }
        
        
        
    }
    
    @Test
    public void testGetter(){
        final Person person = new Person(45,"marcel",true);

        final Binding binding = Bindings.getBinding(Person.class, "age");
        assertNotNull(binding);

        //test access
        assertEquals(Integer.valueOf(45), binding.get(person, "age", Object.class));
        assertEquals("marcel", binding.get(person, "name", Object.class));
        assertEquals(true, binding.get(person, "human", Object.class));

        //test convertion
        assertEquals("45", binding.get(person, "age", String.class));
    }

    @Test
    public void testSetter(){
        final Person person = new Person();

        final Binding binding = Bindings.getBinding(Person.class, "age");
        assertNotNull(binding);

        binding.set(person, "age", 45);
        binding.set(person, "name", "marcel");
        binding.set(person, "human", true);

        //test access
        assertEquals(Integer.valueOf(45), binding.get(person, "age", Object.class));
        assertEquals("marcel", binding.get(person, "name", Object.class));
        assertEquals(true, binding.get(person, "human", Object.class));

        //test convertion
        assertEquals("45", binding.get(person, "age", String.class));
    }

}
