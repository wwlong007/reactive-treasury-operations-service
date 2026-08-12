package com.acme.treasury;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
@AnalyzeClasses(packages="com.acme.treasury",importOptions=ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
 @ArchTest static final ArchRule layers=layeredArchitecture().consideringOnlyDependenciesInLayers()
  .layer("Domain").definedBy("..domain..")
  .layer("Application").definedBy("..application..")
  .layer("Persistence").definedBy("..r2dbc..")
  .layer("Web").definedBy("..web..")
  .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application","Persistence","Web")
  .whereLayer("Application").mayOnlyBeAccessedByLayers("Persistence","Web");
}

