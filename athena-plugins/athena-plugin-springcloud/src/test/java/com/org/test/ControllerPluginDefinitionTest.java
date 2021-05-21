package com.org.test;

import junit.framework.TestCase;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.open.athena.plugin.springcloud.def.ControllerPluginDefinition;

@RunWith(JUnit4.class)
public class ControllerPluginDefinitionTest {

    @Test
    public void matcherTest() {
        ControllerPluginDefinition plugin = new ControllerPluginDefinition();
        ElementMatcher.Junction<TypeDescription> classMatcher = plugin.classMatcher();

        TestCase.assertTrue(!classMatcher.matches(typeDescription(Controller1.class)));
        TestCase.assertTrue(classMatcher.matches(typeDescription(Controller2.class)));
        TestCase.assertTrue(classMatcher.matches(typeDescription(Controller3.class)));
    }

    private TypeDescription typeDescription(Class<?> clz) {
        return TypePool.Default.ofSystemLoader().describe(clz.getName()).resolve();
    }

    public class Controller1 { }

    @Controller
    public class Controller2 { }

    @RestController
    public class Controller3 {

        @PostMapping
        public void m1() {}
    }
}
