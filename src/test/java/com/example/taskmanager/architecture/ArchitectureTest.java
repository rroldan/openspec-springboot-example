package com.example.taskmanager.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.example.taskmanager")
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainDoesNotDependOnOuterLayers =
            noClasses().that().resideInAnyPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..adapters..", "..infrastructure..");

    @ArchTest
    static final ArchRule applicationDoesNotDependOnAdaptersOrInfrastructure =
            noClasses().that().resideInAnyPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapters..", "..infrastructure..");

    @ArchTest
    static final ArchRule adaptersDoNotDependOnInfrastructure =
            noClasses().that().resideInAnyPackage("..adapters..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..");

    @ArchTest
    static final ArchRule taskApplicationUsesPortsNotAdapters =
            noClasses().that().resideInAnyPackage("..application.service..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapters..");
}
